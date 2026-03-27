#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
TOWN="${TOWN:-Moscow}"
PLACE_ID="${PLACE_ID:-1}"
OWNER_ID="${OWNER_ID:-1}"
USER_ID="${USER_ID:-1}"
RESERVATION_ID="${RESERVATION_ID:-1}"
ARRIVAL="${ARRIVAL:-2026-04-10}"
DEPARTURE="${DEPARTURE:-2026-04-15}"
GUESTS_AMOUNT="${GUESTS_AMOUNT:-2}"
PETS_AMOUNT="${PETS_AMOUNT:-0}"
AGREED_TO_RESERVATION="${AGREED_TO_RESERVATION:-true}"
PAYMENT_TYPE="${PAYMENT_TYPE:-NOW}"
PAYMENT_METHOD="${PAYMENT_METHOD:-CARD}"
PHOTO_URL="${PHOTO_URL:-https://example.com/photo.jpg}"

echo "1) Поиск жилья по городу"
curl -sS "$BASE_URL/places/town/$TOWN" \
  -H 'Accept: application/json'
echo -e "\n\n2) Поиск популярных мест по рейтингу"
curl -sS "$BASE_URL/places/rating" \
  -H 'Accept: application/json'
echo -e "\n\n3) Просмотр карточки жилья"
curl -sS "$BASE_URL/places/$PLACE_ID" \
  -H 'Accept: application/json'
echo -e "\n\n4) Создание черновика бронирования"
curl -sS -X POST "$BASE_URL/reservation" \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d "{\"arrival\":\"$ARRIVAL\",\"departure\":\"$DEPARTURE\",\"guestsAmount\":$GUESTS_AMOUNT,\"agreedToReservation\":$AGREED_TO_RESERVATION,\"petsAmount\":$PETS_AMOUNT,\"idOwner\":$OWNER_ID,\"idPlace\":$PLACE_ID}"
echo -e "\n\n5) Проверка черновика брони"
curl -sS "$BASE_URL/reservation/$RESERVATION_ID" \
  -H 'Accept: application/json'
echo -e "\n\n6) Корректировка дат бронирования"
curl -sS -X PATCH "$BASE_URL/reservation/$RESERVATION_ID/dates" \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d "{\"arrival\":\"$ARRIVAL\",\"departure\":\"$DEPARTURE\"}"
echo -e "\n\n7) Добавление данных оплаты"
curl -sS -X PATCH "$BASE_URL/reservation/$RESERVATION_ID/payment" \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d "{\"paymentType\":\"$PAYMENT_TYPE\",\"paymentMethod\":\"$PAYMENT_METHOD\"}"
echo -e "\n\n8) Запуск обработки оплаты"
curl -sS -X POST "$BASE_URL/reservation/$RESERVATION_ID/payment/process" \
  -H 'Accept: application/json'
echo -e "\n\n9) Финальная проверка брони"
curl -sS "$BASE_URL/reservation/$RESERVATION_ID" \
  -H 'Accept: application/json'
echo -e "\n\n10) Получение пользователя"
curl -sS "$BASE_URL/user/$USER_ID" \
  -H 'Accept: application/json'
echo -e "\n\n11) Обновление фото пользователя"
curl -sS -X PATCH "$BASE_URL/user/$USER_ID/photo" \
  -H 'Content-Type: text/plain' \
  -H 'Accept: application/json' \
  --data "$PHOTO_URL"
echo

