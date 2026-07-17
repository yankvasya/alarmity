#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

ANDROID_SDK="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
APPLICATION_ID="com.yankvasya.alarmity"
LAUNCHER_ACTIVITY="$APPLICATION_ID/$APPLICATION_ID.MainActivity"

./gradlew :app:assembleDebug

APK_PATH="$(find app/build/outputs/apk/debug -name '*.apk' | head -1)"
if [[ -z "$APK_PATH" ]]; then
  echo "No APK found after build" >&2
  exit 1
fi

"$ANDROID_SDK/platform-tools/adb" install -r "$APK_PATH"
"$ANDROID_SDK/platform-tools/adb" shell am start -n "$LAUNCHER_ACTIVITY"
