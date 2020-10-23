#!/usr/bin/env bash
set -o errexit -o nounset -o pipefail

if [ $# -lt 2]; then
  echo "Please supply backup filename as first argument and database user \
    as second."
  exit 1
fi

if [ $# -gt 2 ]; then
  echo "Too many arguments. Only supply the backup filename and database user."
  exit 1
fi

file="$1"

if [ ! -f "$file" ]; then
    echo "$file does not exist!"
    exit 1
fi

workdir="$(
  cd "$(dirname "$0")"
  pwd
)"

stack_name='archilab-keycloak'
service_name='postgres'
db_user="$2"
db_name='keycloak'
instance_number=1
stack_service="${stack_name}_${service_name}"
stack_service_instance="${stack_service}.${instance_number}"
task_id="$(docker service ps ${stack_service} -q --no-trunc | head -n1)"

{
  docker exec -i "${stack_service_instance}.${task_id}" \
    psql --username "${db_user}" "${db_name}"
} <"${file}"
