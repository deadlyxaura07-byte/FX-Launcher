# FX Launcher — Android MVP

FX Launcher is a native Android home-screen launcher prototype with a dark, premium FX look.

## Working in this MVP
- Android Home app entry point (choose FX Launcher in Android's default Home app setting).
- Live clock and date.
- FX Clock, FX Security and FX Verse quick-launch cards: open a matching app if installed.
- Installed-app drawer, sorted by app name.
- Shortcut to Android Home settings and a simple About dialog.
- GitHub Actions workflow that builds a debug APK and uploads it as an artifact.

## Build the APK from your Android tablet
1. Open the repository's **Actions** tab.
2. Open **FX Launcher APK Builder**.
3. Tap **Run workflow** (or open the latest run triggered by a commit).
4. Wait for the job to finish with a green check.
5. Open the successful run and download **FX-Launcher-APK** under Artifacts.
6. Extract the artifact ZIP and install `app-debug.apk`. Android may ask you to allow installs from that browser/file manager.

## Important scope and Android limits
This is an MVP, not a fully autonomous AI operating system. The current build does not yet contain AI provider connections, internet research, voice input, autonomous code generation, or cross-app screen control. Android only permits background/screen automation through supported APIs and user-granted permissions; it cannot safely or legitimately bypass all OS restrictions.

The FX product cards do not bundle other apps. They launch a matching installed app when one is found, or offer the app drawer.

## Technical
- Kotlin + Android SDK
- Gradle Kotlin DSL
- Package: `com.fx.launcher`
- Minimum Android: API 26 (Android 8.0)
- Debug APK is for testing; it is not a signed Play Store release.
