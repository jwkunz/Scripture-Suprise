# Scripture Surprise

![Scripture Surprise logo](docs/images/scripture_suprise_logo.png)

An Android app that shows a surprise scripture verse, with filters for choosing which verses appear. Built with Kotlin, Jetpack Compose, Room, DataStore and Hilt.

## Get it

Debug APKs are attached to each [release](https://github.com/jwkunz/Scripture-Suprise/releases). Every push also builds one in CI: open the run under the **Actions** tab and download the `scripture-surprise-apk` artifact.

## Repository layout

```
VERSION                  the one place the project version lives (X.Y.Z)
Android/                 Gradle project (app module: com.example.scripturesuprise)
docs/                    images and documentation
.github/workflows/       ci.yml (version check, debug APK build, tagged releases)
```

## Build

Requires JDK 21 and the Android SDK.

```
cd Android
./gradlew :app:assembleDebug
```

The APK lands in `Android/app/build/outputs/apk/debug/app-debug.apk`. The app's `versionName` and `versionCode` are derived from `VERSION`.

## Releasing

Update `VERSION`, commit, then tag and push:

```
git tag vX.Y.Z && git push origin main vX.Y.Z
```

CI checks the tag matches `VERSION`, builds the debug APK and publishes it as a GitHub release.

## License

MIT, see [LICENSE](LICENSE).
