#!/usr/bin/env bash
set -euo pipefail

APP_URL="${APP_URL:-http://127.0.0.1:8082}"
TOKEN="${TOKEN:?Set TOKEN from 01-login-user.sh response}"
PROCESS_INSTANCE_ID="${PROCESS_INSTANCE_ID:?Set process instance id}"

curl -s -X POST "${APP_URL}/api/bpm/reservations/${PROCESS_INSTANCE_ID}/cancel" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"reason":"Cancelled during Lab 4 test"}'
echo
