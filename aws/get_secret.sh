#!/bin/bash

SCRIPT_DIR=$(readlink -f "$0" | xargs dirname)
. "$SCRIPT_DIR"/utils.sh

unset -v AWS_PROFILE
unset -v SECRET_NAME

usage() {
  echo "Usage: $0 --profile AWS_PROFILE [secret_name]"
  echo "  -p | --profile AWS_PROFILE: develop, staging, perf, production"
  echo "  -h: show usage"
  echo ""
}

_aws() {
  aws --profile "$AWS_PROFILE" "$@"
}

check_args() {
  if [[ -z $AWS_PROFILE ]]; then
    exit_error "❌ Invalid arguments, you must specify an AWS profile"
  fi
}

aws_login() {
  if ! _aws sts get-caller-identity &> /dev/null; then
    log_info "AWS login required"
    _aws sso login

    if ! _aws sts get-caller-identity &> /dev/null; then
      exit_error "❌ AWS login failed"
    fi
  fi
}

while [[ $# -gt 0 ]]; do
  case $1 in
    --profile|-p)
      AWS_PROFILE=$2
      shift 2
      ;;
    --help | -h)
      usage
      exit 0
      ;;
    *)
      SECRET_NAME=$1
      shift
      ;;
  esac
done

check_commands aws jq
check_args

aws_login

if [[ -n $SECRET_NAME ]]; then
  filter="?contains(@.Name, '$SECRET_NAME') == \`true\`"
fi

secrets=$(_aws secretsmanager list-secrets --query "SecretList[$filter].ARN" --output text)

for secret_arn in $secrets; do
  secret=$(_aws secretsmanager get-secret-value --secret-id "$secret_arn")
  secret_name=$(echo "$secret" | jq -r '.Name')
  secret_value=$(echo "$secret" | jq -r '.SecretString')

  log_info "== $secret_name =="
  if [[ -n $secret_value && $(echo "$secret_value" | grep -c '{') -gt 0 ]]; then
    echo "$secret_value" | jq -r .
  else
    echo "$secret_value"
  fi

done
