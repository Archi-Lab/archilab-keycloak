#!/usr/bin/env bash
set -o errexit -o nounset -o pipefail

service_name='archilab-keycloak'

docker stack rm "${service_name}"
