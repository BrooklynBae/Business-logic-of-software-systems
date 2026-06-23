#!/usr/bin/env bash
set -euo pipefail

APP_URL="${APP_URL:-http://127.0.0.1:8082}"
TOKEN="${TOKEN:?Set TOKEN from 01-login-user.sh response}"

curl -s "${APP_URL}/api/bpm/tasks/my" \
  -H "Authorization: Bearer ${TOKEN}"
echo
