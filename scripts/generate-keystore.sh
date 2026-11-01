#!/usr/bin/env bash
# Генерирует постоянный ключ подписи приложения (release-keystore.jks).
# ОДИН ключ на весь жизненный цикл приложения — иначе обновления не станут
# устанавливаться поверх старой версии (Android требует совпадения сертификата).
#
# Использование:
#   cd app
#   RELEASE_STORE_PASSWORD=... RELEASE_KEY_PASSWORD=... ../scripts/generate-keystore.sh
#
# Полученный файл НЕ коммитьте в git (уже в .gitignore).
# Закодируйте его в base64 и положите в GitHub Secret RELEASE_STORE_FILE:
#   base64 -i app/release-keystore.jks | pbcopy   (macOS)
#   base64 -w0 app/release-keystore.jks            (Linux)

set -euo pipefail

KEYSTORE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/app"
KEYSTORE="$KEYSTORE_DIR/release-keystore.jks"
ALIAS="${RELEASE_KEY_ALIAS:-release}"
STORE_PASS="${RELEASE_STORE_PASSWORD:-changeit}"
KEY_PASS="${RELEASE_KEY_PASSWORD:-changeit}"

if [[ -f "$KEYSTORE" ]]; then
    echo "Keystore already exists: $KEYSTORE"
    exit 0
fi

if ! command -v keytool >/dev/null 2>&1; then
    echo "keytool not found. Install JDK." >&2
    exit 1
fi

keytool -genkeypair \
    -v \
    -keystore "$KEYSTORE" \
    -alias "$ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -dname "CN=GitExplorerBox, O=Git-explorer-box, L=Minsk, S=Minsk City, C=BY" \
    -storepass "$STORE_PASS" \
    -keypass "$KEY_PASS"

echo ""
echo "Готово: $KEYSTORE (alias: $ALIAS, срок действия: 10000 дней)"
echo "Сохраните пароли в GitHub Secrets: RELEASE_STORE_PASSWORD, RELEASE_KEY_PASSWORD"
echo "Файл ключа (base64) — в секрет RELEASE_STORE_FILE. Never commit the .jks file."
