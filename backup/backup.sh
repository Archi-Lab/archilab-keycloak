#!/usr/bin/env bash
set -o errexit -o nounset -o pipefail

if [ $# -eq 0 ]
  then
    echo "Please supply database user as first argument."
    exit 1
fi

if [ $# -gt 1 ]
  then
    echo "Too many arguments. Only supply the database user."
    exit 1
fi

workdir="$(
  cd "$(dirname "$0")"
  pwd
)"

stack_name='archilab-keycloak'
keycloak_service_name='keycloak'
db_service_name='postgres'
db_name='keycloak'
db_user="$1"
instance_number=1
keycloak_stack_service="${stack_name}_${keycloak_service_name}"
keycloak_stack_service_instance="${keycloak_stack_service}.${instance_number}"
keycloak_task_id="$(docker service ps ${keycloak_stack_service} -q --no-trunc | head -n1)"
db_stack_service="${stack_name}_${db_service_name}"
db_stack_service_instance="${db_stack_service}.${instance_number}"
db_task_id="$(docker service ps ${db_stack_service} -q --no-trunc | head -n1)"
current_date="$(date +%Y-%m-%d_%H-%M-%S)"
backup_dir=${workdir}/${current_date}

mkdir --parents "${backup_dir}"

docker cp "${keycloak_stack_service_instance}.${keycloak_task_id}:/opt/jboss/keycloak" \
  "${backup_dir}/"

{
  docker exec "${db_stack_service_instance}.${db_task_id}" \
    pg_dump --username="${db_user}" --clean "${db_name}"
} >"${backup_dir}/keycloak.sql"
