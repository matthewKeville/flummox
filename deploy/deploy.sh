#!/bin/bash

if test "$(basename "$(pwd)")" != "ReBoggled"; then
  echo "Please run from ReBoggled root (currently: ""$(pwd)"" )"
  exit 1
fi

if ! mvn package; then
  exit 1
fi


# This needs to adapt when version changes
# copy the artifact to DEV_SERVER_USER home 
DEV_SERVER=dream-land
DEV_SERVER_USER=reboggled-dev
DEV_SERVER_SQL_SA=reboggled_dev_sa
DEV_SERVER_DB=reboggled_dev_db
ARTIFACT_PATH='./target/ReBoggled-0.0.1-SNAPSHOT.jar'

# get user password


scp "$ARTIFACT_PATH" "$DEV_SERVER_USER"@"$DEV_SERVER":"./"

# whiptail prints the answer to STDERR (2) , but we want it in STOUT, so we juggle the streams
DEV_SERVER_SQL_SA_PASS=$(whiptail --passwordbox "Enter password for $DEV_SERVER_SQL_SA@$DEV_SERVER for $DEV_SERVER_DB" 8 78 --title " SQL SA PASSWORD " 3>&1 1>&2 2>&3)
echo $DEV_SERVER_SQL_SA_PASS
if [ -z "$DEV_SERVER_SQL_SA_PASS" ]; then
  echo "no password supplied, aborting..."
  exit 1
fi

# create/reset database
mysql -h "$DEV_SERVER" "$DEV_SERVER_DB" -u "$DEV_SERVER_SQL_SA" -p"$DEV_SERVER_SQL_SA_PASS" < ./src/main/resources/schema.sql
EXIT_CODE=$?

if [ $EXIT_CODE = 0 ]; then
  echo "recreated database"
else
  echo "failed to recreate database"
fi

# create dev dataset
# kill existing instance if any
ssh reboggled-dev@dream-land 'kill `pgrep -f ReBoggled`'
ssh reboggled-dev@dream-land 'java -jar -Dspring.profiles.active=dev  ReBoggled-0.0.1-SNAPSHOT.jar --create-dev-data=true'
clear
echo " all done ! "
