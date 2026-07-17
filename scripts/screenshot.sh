#!/usr/bin/env bash
# Usage: screenshot.sh <output-path>
set -euo pipefail

ANDROID_SDK="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
OUTPUT_PATH="${1:?Usage: screenshot.sh <output-path>}"
DEVICE_PATH="/sdcard/alarmity_screenshot.png"

"$ANDROID_SDK/platform-tools/adb" shell screencap -p "$DEVICE_PATH"
"$ANDROID_SDK/platform-tools/adb" pull "$DEVICE_PATH" "$OUTPUT_PATH"
"$ANDROID_SDK/platform-tools/adb" shell rm "$DEVICE_PATH"

echo "Saved screenshot to $OUTPUT_PATH"
