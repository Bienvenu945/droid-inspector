# DROID INSPECTOR X
### Android System Intelligence & Diagnostics
**Développé par @Bienvenu**

Application professionnelle native Jetpack Compose & Material 3 sous architecture Clean MVVM modulaire.
Prête pour compilation sur Android Studio et automatisation avec GitHub Actions CI.
## Build

```bash
chmod +x gradlew
./gradlew assembleDebug
# → app/build/outputs/apk/debug/app-debug.apk
```

Requires JDK 17 and Android SDK platform 35. On GitHub, the workflow in `.github/workflows` builds the debug APK for you.
