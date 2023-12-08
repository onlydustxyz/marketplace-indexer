#!/bin/sh
SCRIPT_DIR=$(dirname "$0")

pg_dump --username=test --dbname=marketplace_db --format=c --data-only --file="$SCRIPT_DIR"/backup
