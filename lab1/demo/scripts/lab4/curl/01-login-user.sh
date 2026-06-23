#!/usr/bin/env bash
set -euo pipefail

APP_URL="${APP_URL:-http://127.0.0.1:8082}"

curl -s -X POST "${APP_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"KaragodinaKs@yandex.ru","password":"password123"}'
echo
