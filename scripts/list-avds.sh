#!/usr/bin/env bash
set -euo pipefail

ANDROID_SDK="${ANDROID_HOME:-$HOME/Library/Android/sdk}"

"$ANDROID_SDK/emulator/emulator" -list-avds
