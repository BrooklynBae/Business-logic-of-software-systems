#!/usr/bin/env bash
set -euo pipefail

CAMUNDA_URL="${CAMUNDA_URL:-http://127.0.0.1:8080/engine-rest}"
BPMN_FILE="${BPMN_FILE:-src/main/resources/bpmn/airbnb-fast-booking-lab4.bpmn}"

curl -f -X POST "${CAMUNDA_URL}/deployment/create" \
  -F "deployment-name=airbnb-fast-booking-lab4" \
  -F "enable-duplicate-filtering=true" \
  -F "deploy-changed-only=true" \
  -F "airbnb-fast-booking-lab4.bpmn=@${BPMN_FILE}"
