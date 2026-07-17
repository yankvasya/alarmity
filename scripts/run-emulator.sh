#!/usr/bin/env bash
# Usage: run-emulator.sh <avd-name> [-no-window]
set -euo pipefail

ANDROID_SDK="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
AVD_NAME="${1:?Usage: run-emulator.sh <avd-name> [-no-window]}"
shift || true
LOG_FILE="${TMPDIR:-/tmp}/alarmity-emulator-$AVD_NAME.log"

# nohup + disown fully detach the emulator so it survives after this script's
# own process (and process group) is torn down by whatever launched it.
nohup "$ANDROID_SDK/emulator/emulator" -avd "$AVD_NAME" -netdelay none -netspeed full "$@" \
    > "$LOG_FILE" 2>&1 < /dev/null &
EMULATOR_PID=$!
disown
echo "Launched emulator '$AVD_NAME' (pid $EMULATOR_PID), logging to $LOG_FILE"

"$ANDROID_SDK/platform-tools/adb" wait-for-device

echo "Waiting for boot to complete..."
until [[ "$("$ANDROID_SDK/platform-tools/adb" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" == "1" ]]; do
    sleep 2
done

echo "Emulator '$AVD_NAME' is booted and ready."
