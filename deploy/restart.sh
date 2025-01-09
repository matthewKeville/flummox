#!/bin/bash

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


################################################################################
# remote start 
################################################################################

echo "building session"

ssh -p "$DEV_SERVER_SSH_PORT" "$DEV_SERVER_USER"@"$DEV_SERVER" 'tmux has-session -t flummox'
if [ !$? ]; then
  ssh -p "$DEV_SERVER_SSH_PORT" "$DEV_SERVER_USER"@"$DEV_SERVER" 'tmux kill-session -t flummox'
fi

ssh -p "$DEV_SERVER_SSH_PORT" "$DEV_SERVER_USER"@"$DEV_SERVER" 'tmux new-session -d -s flummox -n flummox -c /home/flummox/'

echo "launching flummox"
ssh -p "$DEV_SERVER_SSH_PORT" "$DEV_SERVER_USER"@"$DEV_SERVER" $'tmux send-keys -t \'=flummox:=flummox\' \'echo "hi"\' Enter'
ssh -p "$DEV_SERVER_SSH_PORT" "$DEV_SERVER_USER"@"$DEV_SERVER" $'tmux send-keys -t \'=flummox:=flummox\' \'java -Dserver.port=8080 -jar -Dspring.profiles.active=prod flummox-*.jar\' Enter'
