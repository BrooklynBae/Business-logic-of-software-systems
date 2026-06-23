#!/usr/bin/env bash
set -euo pipefail

CAMUNDA_URL="${CAMUNDA_URL:-http://127.0.0.1:8080/engine-rest}"

curl -f "${CAMUNDA_URL}/engine"
echo
curl -f "${CAMUNDA_URL}/process-definition/key/airbnb-fast-booking"
echo
