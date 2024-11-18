#!/bin/bash

SCRIPT_DIR=$(readlink -f "$0" | xargs dirname)
. "$SCRIPT_DIR"/utils.sh

unset -v FROM_BRANCH
unset -v TO_BRANCH
unset -v GIT_FORCE
unset -v MAINTENANCE
unset -v MAINTENANCE_REPO_DIR

REMOTE=promote-origin

GIT_REPO_URL=https://github.com/onlydustxyz/marketplace-indexer.git
ENV_FILES="**/application.yaml Procfile*"

check_args() {
    if [[ -z $FROM_BRANCH || -z $TO_BRANCH ]]; then
      exit_error "Invalid arguments, you must specify at least --staging or --production flag"
    fi
}

check_cwd() {
    if ! root_dir=$(git rev-parse --show-toplevel); then
      exit_error "You are not in a git directory"
    fi

    if [ "$(pwd)" != "$root_dir" ]; then
      exit_error "Please run this script from the root directory: $root_dir"
    fi
}

delete_remote() {
    if [ "$(git remote | grep -c $REMOTE)" -gt 0 ]; then
      git remote remove $REMOTE
    fi
}

create_remote() {
    delete_remote
    if ! git remote add $REMOTE "$GIT_REPO_URL" -f; then
      exit_error "Unable add remote."
    fi
}

check_env_vars_diff() {
    log_info "Checking diff in environment variables"
    GIT_DIFF_CMD="git diff $to_commit..$from_commit -- $ENV_FILES"
    DIFF=$(eval "$GIT_DIFF_CMD")
    if [ -n "$DIFF" ]; then
        execute "$GIT_DIFF_CMD"
        log_warning "Some diff have been found, make sure to update the environment variables 🧐"
    else
        log_success "No diff found, you are good to go 🥳"
    fi
}

git_push() {
    LOCAL_BRANCH=promote

    log_info "Pushing diff on git"

    if [ "$(git branch | grep -c $REMOTE)" -gt 0 ]; then
      git branch -D "$LOCAL_BRANCH"
    fi

    if ! git checkout -b "$LOCAL_BRANCH" "$REMOTE/$FROM_BRANCH"; then
      exit_error "Unable to checkout $FROM_BRANCH to $LOCAL_BRANCH."
    fi

    if ! git push "$GIT_FORCE" "$REMOTE" "$LOCAL_BRANCH:$TO_BRANCH"; then
      exit_error "Unable to push $FROM_BRANCH to $TO_BRANCH. Please rebase then try again."
    fi

    git checkout -
    git branch -D $LOCAL_BRANCH
}

deploy() {
    from_commit=$REMOTE/$FROM_BRANCH
    to_commit=$REMOTE/$TO_BRANCH

    print "Checking diff from $to_commit to $from_commit"

    log_info "Checking diff to be loaded in $TO_BRANCH"
    git log --color --graph --pretty=format:'%Cred%h%Creset %s %Cgreen(%cr) %C(bold blue)<%an>%Creset' $to_commit..$from_commit | tee

    echo
    if ask "OK to continue"; then
      check_env_vars_diff
    else
      exit_error "Aborting"
    fi

    echo
    if ask "OK to continue"; then
        git_push
    fi
}

activate_maintenance() {
  log_info "Activating maintenance mode"
  MAINTENANCE_REPO_DIR=$(mktemp -d)

  (
    cd "$MAINTENANCE_REPO_DIR" || exit_error "Unable to change directory"
    execute git clone https://github.com/onlydustxyz/od-maintenance-page.git .

    if [ "$(npx wrangler whoami | grep -c 'You are not authenticated')" -eq 1 ]; then
      execute npx wrangler login
    fi
    execute npx wrangler deploy --env $TO_BRANCH
  )
}

deactivate_maintenance() {
  if ! ask "Do you want to deactivate maintenance mode"; then
    log_warning Skipping maintenance deactivation, to deactivate maintenance, use the following command:
    log_warning "(cd $MAINTENANCE_REPO_DIR && npx wrangler delete --env $TO_BRANCH)"
    return
  fi

  log_info "Deactivating maintenance"
  (
      cd "$MAINTENANCE_REPO_DIR" || exit_error "Unable to change directory"
      execute npx wrangler delete --env $TO_BRANCH
    )

  [ -d "$MAINTENANCE_REPO_DIR" ] && rm -rf "$MAINTENANCE_REPO_DIR"
}

usage() {
  echo "Usage: $0 [ --staging | --production ] [--force] [--maintenance]"
  echo "  --staging         Promote to staging"
  echo "  --production      Promote to production"
  echo "  --force           Force push to GitHub"
  echo "  --maintenance     Activate maintenance mode during deployment"
  echo ""
}

while [[ $# -gt 0 ]]; do
  case $1 in
    --staging)
      FROM_BRANCH=main
      TO_BRANCH=staging
      shift
      ;;
    --production)
      FROM_BRANCH=staging
      TO_BRANCH=production
      shift
      ;;
    --force)
      GIT_FORCE=--force
      shift
      ;;
    --maintenance)
      MAINTENANCE=1
      shift
      ;;
    --help | -h)
      usage
      exit 0
      ;;
    *)
      exit_error "Error: unrecognized option '$1'"
      ;;
  esac
done

check_args
check_command git
check_command aws
check_command npm
check_cwd
create_remote

if [ -n "$MAINTENANCE" ]; then
  activate_maintenance
fi

deploy

if [ -n "$MAINTENANCE" ]; then
  deactivate_maintenance
fi

delete_remote

exit_success
