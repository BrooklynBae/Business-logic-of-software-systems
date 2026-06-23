#!/usr/bin/env bash
set -euo pipefail

APP_URL="${APP_URL:-http://127.0.0.1:8082}"
TOKEN="${TOKEN:?Set TOKEN from 01-login-user.sh response}"
TASK_ID="${TASK_ID:?Set Camunda task id}"

curl -s -X POST "${APP_URL}/api/bpm/tasks/${TASK_ID}/complete" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "town": "Saint Petersburg",
    "arrival": "2026-07-01",
    "departure": "2026-07-05",
    "guestsAmount": 2,
    "petsAmount": 0,
    "idPlace": 1,
    "selectedPlaceId": 1,
    "confirmedByUser": true,
    "coverLetter": "Lab 4 Camunda booking"
  }'
echo
