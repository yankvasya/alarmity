# Alarmity

A native Android alarm clock app built with Kotlin and Jetpack Compose. Personal-use project, distributed via GitHub Releases (no Play Store).

## Stack

- Kotlin, Jetpack Compose, Material 3 with dynamic color
- MVVM + Repository, Hilt for DI, Room for persistence, Coroutines/Flow
- `AlarmManager` for exact-time scheduling, foreground service + full-screen intent for ringing
- Dismiss actions are pluggable via a `DismissMission` interface — ships with a simple tap dismiss and a math-problem dismiss, with room to add shake-to-dismiss, QR scans, etc. later without touching the core alarm-firing logic

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

`.github/workflows/build-alpha.yml` builds an alpha APK and publishes it as a GitHub Release on every push to `main`:

1. Builds `assembleDebug` with JDK 17 and Gradle caching.
2. Versions automatically: `versionName` is `0.1.<run number>-alpha`, computed from the `GITHUB_RUN_NUMBER` environment variable that Actions sets for every run (local/dev builds fall back to `0.1.0-dev`). Every build gets a unique, incrementing, readable version without manual bumping.
3. Generates a changelog from conventional commit messages (`feat:`, `fix:`, everything else) since the last `v*` tag, grouped into `### Added` / `### Fixed` / `### Changed`.
4. Publishes a pre-release GitHub Release tagged `v0.1.<run number>-alpha`, with the APK attached and the generated changelog as the release body.

To trigger a build manually (e.g. without pushing to `main`), go to the repo's **Actions** tab → **Build Alpha** → **Run workflow**.

## License

GPL-3.0 — see [LICENSE](LICENSE). You're free to use, modify, and redistribute this code, but any distributed derivative work must also be open source under the same license.
