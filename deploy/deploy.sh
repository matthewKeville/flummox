#!/bin/bash

# TODO 
#   there should be a development artifact path that we can wild card
#   This needs to adapt when version changes

################################################################################
# Guard
################################################################################

if test "$(basename "$(pwd)")" != "flummox"; then
  echo "Please run from flummox root (currently: ""$(pwd)"" )"
  exit 1
fi

# DEV_SERVER
# DEV_SERVER_USER
# DEV_SERVER_SSH_PORT
# DEV_SERVER_SQL_PORT
# DEV_SERVER_SQL_SA
# DEV_SERVER_SQL_SA_PASS
# DEV_SERVER_DB
source ./deploy/secrets.sh

if [ -z "$DEV_SERVER" ]; then
  echo "DEV_SERVER is empty"
  exit 1
fi
if [ -z "$DEV_SERVER_USER" ]; then
  echo "DEV_SERVER_USER is empty"
  exit 1
fi
if [ -z "$DEV_SERVER_SSH_PORT" ]; then
  echo "DEV_SERVER_SSH_PORT is empty"
  exit 1
fi
if [ -z "$DEV_SERVER_SQL_PORT" ]; then
  echo "DEV_SERVER_SQL_PORT is empty"
  exit 1
fi
if [ -z "$DEV_SERVER_SQL_SA_PASS" ]; then
  echo "DEV_SERVER_SQL_SA_PASS is empty"
  exit 1
fi
if [ -z "$DEV_SERVER_SQL_SA" ]; then
  echo "DEV_SERVER_SQL_SA is empty"
  exit 1
fi
if [ -z "$DEV_SERVER_DB" ]; then
  echo "DEV_SERVER_DB is empty"
  exit 1
fi

# See TODO
ARTIFACT_PATH='./target/flummox-0.0.1-SNAPSHOT.jar'

################################################################################
# Build Bundle
################################################################################

npm run build-production
if $? ; then
  echo " error building webpack bundle "
  exit 1
fi

################################################################################
# Build WAR
################################################################################

if ! mvn package; then
  echo " error building WAR "
  exit 1
fi

scp -P "$DEV_SERVER_SSH_PORT" "$ARTIFACT_PATH" "$DEV_SERVER_USER"@"$DEV_SERVER":"./"

################################################################################
# DB Reset (Nukes DB)
################################################################################

# Future me, implement a migration and have a nuclear option as a flag

# create/reset database
mysql -h "$DEV_SERVER" -P "$DEV_SERVER_SQL_PORT" "$DEV_SERVER_DB" -u "$DEV_SERVER_SQL_SA" -p"$DEV_SERVER_SQL_SA_PASS" < ./src/main/resources/schema.sql
EXIT_CODE=$?

if [ $EXIT_CODE = 0 ]; then
  echo "recreated database"
else
  echo "failed to recreate database"
fi

################################################################################
# Start/Restart Application
################################################################################

deploy/restart.sh
