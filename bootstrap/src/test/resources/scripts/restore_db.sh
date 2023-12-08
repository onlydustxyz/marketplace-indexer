#!/bin/sh
SCRIPT_DIR=$(dirname "$0")

if psql --username=test --dbname=marketplace_db -f "$SCRIPT_DIR"/clean.sql
then
  pg_restore --username=test --dbname=marketplace_db --data-only --disable-triggers "$SCRIPT_DIR"/backup
fi

