#!/bin/bash

SCRIPT_DIR=$(readlink -f "$0" | xargs dirname)
. "$SCRIPT_DIR"/utils.sh

unset -v ENV
COMMIT=HEAD

usage() {
  echo "Usage: $0 [ -e ENV ] [ -c COMMIT ] [-h]"
  echo "  -e ENV: develop, staging, production"
  echo "  -c COMMIT: commit ref (default: HEAD)"
  echo "  -h: show usage"
  echo ""
}

check_args() {
    if [[ -z $ENV ]]; then
      exit_error "❌ Invalid arguments, you must specify an environment"
    fi
}

print_status() {
  case $1 in
    completed)
      echo "✅"
      ;;
    in_progress)
      echo "🔄"
      ;;
    *)
      echo "❌"
      ;;
  esac
}

spinner() {
  ticks=$1
  count=0
  spin='-\|/'

  while [ $count -lt "$ticks" ]; do
    sleep 0.1
    (( count++ ))
    ((i = count % 4))
    printf "\r${spin:$i:1}"
  done
}

actuator_url() {
  case "$ENV" in
    production)
      echo "https://indexer.onlydust.com/actuator/info"
      ;;
    *)
      echo "https://$ENV-indexer.onlydust.com/actuator/info"
      ;;
  esac
}

check_deployment() {
  runs=$(gh api "$RUNS_URL")

  echo "$runs" | jq -r '.workflow_runs[] | "\(.name),\(.status),\(.head_branch)"' | while IFS=',' read -r name status head_branch
  do
    echo "$name [$head_branch] $(print_status "$status")"
  done

  deployed_sha=$(curl --silent "$(actuator_url)" | jq -r .git.commit.id)
  if [ "$deployed_sha" == "$SHORT_SHA" ]; then
    echo "🚀 Deployed!"
    return 1
  else
    echo "🕒 Waiting for deployment in $ENV..."
    return 0
  fi
}

wait_for_deployment() {
  while check_deployment; do
    spinner 20
    clear
  done
}

while getopts "e:c:h" o; do
    case "${o}" in
        e)
            ENV=${OPTARG}
            ((ENV == "develop" || ENV == "staging" || ENV == "production")) || usage
            ;;
        c)
            COMMIT=${OPTARG}
            ;;
        h|*)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

REPO_OWNER_NAME=$(git remote get-url upstream | sed 's@https://github.com/\(.*\).git@\1@')
SHA=$(git --no-pager rev-parse "$COMMIT")
SHORT_SHA=$(echo "$SHA" | cut -c-7)
RUNS_URL="repos/$REPO_OWNER_NAME/actions/runs?head_sha=$SHA"

check_args
wait_for_deployment