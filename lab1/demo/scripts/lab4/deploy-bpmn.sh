#!/usr/bin/env bash
set -euo pipefail

CAMUNDA_URL="${CAMUNDA_URL:-http://127.0.0.1:8080/engine-rest}"
BPMN_FILE="${BPMN_FILE:-src/main/resources/bpmn/airbnb-fast-booking-lab4.bpmn}"
FORMS_DIR="${FORMS_DIR:-src/main/resources/forms}"

curl -f -X POST "${CAMUNDA_URL}/deployment/create" \
  -F "deployment-name=airbnb-fast-booking-lab4" \
  -F "enable-duplicate-filtering=true" \
  -F "deploy-changed-only=true" \
  -F "airbnb-fast-booking-lab4.bpmn=@${BPMN_FILE}" \
  -F "start-booking.form=@${FORMS_DIR}/start-booking.form" \
  -F "search-accommodation.form=@${FORMS_DIR}/search-accommodation.form" \
  -F "select-accommodation.form=@${FORMS_DIR}/select-accommodation.form" \
  -F "confirm-booking.form=@${FORMS_DIR}/confirm-booking.form" \
  -F "admin-moderate-reservation.form=@${FORMS_DIR}/admin-moderate-reservation.form" \
  -F "owner-approve-booking.form=@${FORMS_DIR}/owner-approve-booking.form" \
  -F "payment.form=@${FORMS_DIR}/payment.form" \
  -F "cancel-booking.form=@${FORMS_DIR}/cancel-booking.form"
