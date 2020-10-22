#!/usr/bin/env bash
set -o errexit -o nounset -o pipefail

workdir="$(
  cd "$(dirname "$0")"
  pwd
)"
tag='archilab-keycloak'

docker build \
  --file "${workdir}/docker/Dockerfile" \
  --tag "${tag}" \
  "${workdir}/context"
