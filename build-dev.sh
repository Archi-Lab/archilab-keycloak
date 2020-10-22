#!/usr/bin/env bash
set -o errexit -o nounset -o pipefail

workdir="$(
  cd "$(dirname "$0")"
  pwd
)"
tag='archilab-keycloak:dev'

docker build \
  --file "${workdir}/docker/Dockerfile.dev" \
  --tag "${tag}" \
  "${workdir}/context"
