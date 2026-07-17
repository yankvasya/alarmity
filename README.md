# Alarmity

A native Android alarm clock app built with Kotlin and Jetpack Compose. Personal-use project, distributed via GitHub Releases (no Play Store).

## Stack

- Kotlin, Jetpack Compose, Material 3 with dynamic color
- MVVM + Repository, Hilt for DI, Room for persistence, Coroutines/Flow
- `AlarmManager` for exact-time scheduling, foreground service + full-screen intent for ringing
- Dismiss actions are pluggable via a `DismissMission` interface — phase 1 ships a simple tap dismiss, with room to add math problems, shake-to-dismiss, QR scans, etc. later without touching the core alarm-firing logic

## Development

### Emulator scripts

```
scripts/list-avds.sh                    # list available AVDs
scripts/run-emulator.sh <avd-name>      # boot an AVD and wait for it to be ready
scripts/build-install-launch.sh         # assembleDebug, install, and launch on a connected device/emulator
scripts/screenshot.sh <output-path>     # pull a screenshot from the connected device/emulator
```

These reference `~/Library/Android/sdk` by default; set `ANDROID_HOME` to override.

## CI/CD

<!-- Filled in once the release pipeline is wired up. -->
