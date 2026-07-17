#!/usr/bin/env bash
# Usage: run-emulator.sh <avd-name> [-no-window]
set -euo pipefail

ANDROID_SDK="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
AVD_NAME="${1:?Usage: run-emulator.sh <avd-name> [-no-window]}"
shift || true

"$ANDROID_SDK/emulator/emulator" -avd "$AVD_NAME" -netdelay none -netspeed full "$@" &
EMULATOR_PID=$!
echo "Launched emulator '$AVD_NAME' (pid $EMULATOR_PID)"

"$ANDROID_SDK/platform-tools/adb" wait-for-device

echo "Waiting for boot to complete..."
until [[ "$("$ANDROID_SDK/platform-tools/adb" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" == "1" ]]; do
    sleep 2
done

echo "Emulator '$AVD_NAME' is booted and ready."
