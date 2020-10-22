#!/usr/bin/env bash
set -o errexit -o nounset -o pipefail

workdir="$(
  cd "$(dirname "$0")"
  pwd
)"
service_name='archilab-keycloak'

docker stack deploy \
  -c "${workdir}/docker/docker-compose.yml" \
  -c "${workdir}/docker/docker-compose.prod.yml" \
  "${service_name}"
