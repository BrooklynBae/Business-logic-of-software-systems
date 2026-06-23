#!/usr/bin/env bash
set -euo pipefail

CAMUNDA_HOME="${CAMUNDA_HOME:-$HOME/camunda-run}"
CAMUNDA_PORT="${CAMUNDA_PORT:-8080}"

echo "Starting Camunda Run from ${CAMUNDA_HOME} on port ${CAMUNDA_PORT}"
cd "${CAMUNDA_HOME}"
export CAMUNDA_BPM_RUN_HTTP_PORT="${CAMUNDA_PORT}"
./start.sh
