# DROID INSPECTOR X ⚡
### Android System Intelligence & Hardware Diagnostics
**Développé par [@Bienvenu](https://github.com/bienvenu)**

---

## 📱 Présentation du Projet
**DROID INSPECTOR X** est une application Android native haut de gamme conçue pour l'analyse approfondie, l'exploration matérielle et le diagnostic informatif des smartphones et tablettes Android.

Conçue avec **Jetpack Compose**, **Material 3** et une direction artistique **Frosted Glass / Glassmorphism vert émeraude**, elle offre une expérience fluide, réactive, respectueuse de la vie privée et axée sur des mesures réelles sans artifice.

---

## 🛠️ Stack Technique (Phase 1 — Foundation)

| Composant | Technologie |
| :--- | :--- |
| **Langage** | Kotlin 2.0.21 |
| **Interface Graphique** | Jetpack Compose + Material 3 |
| **Architecture** | Clean Architecture + MVVM (Presentation / Domain / Data) |
| **Gestion d'État** | Kotlin Coroutines & StateFlow |
| **Navigation** | Navigation Compose |
| **Injection de Dépendances** | Dagger Hilt 2.52 |
| **Stockage Local** | Room Database & Jetpack DataStore |
| **Système de Build** | Gradle Kotlin DSL (`build.gradle.kts`) |
| **Intégration Continue** | GitHub Actions CI/CD (`.github/workflows/android-build.yml`) |
| **Cible OS** | Android 8.0 (API 26) minSdk ➔ Android 15 (API 35) targetSdk |

---

## 📁 Architecture Modulaire

```text
app/src/main/java/com/droidinspector/
├── DroidInspectorApplication.kt      // @HiltAndroidApp application entrypoint
├── MainActivity.kt                   // Single-Activity Compose Host
├── di/                               // Dagger Hilt Modules
│   └── AppModule.kt
├── core/
│   ├── theme/                        // Emerald & Frosted Glass Design System
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── navigation/                   // Type-safe Navigation Compose routes
│   │   ├── Screen.kt
│   │   ├── NavGraph.kt
│   │   └── BottomNavigationBar.kt
│   ├── components/                   // Reusable Glassmorphism UI components
│   │   ├── GlassCard.kt
│   │   ├── MetricGauge.kt
│   │   └── SectionHeader.kt
│   └── utils/
│       └── Extensions.kt
├── domain/
│   ├── model/                        // Pure domain entities
│   │   ├── DeviceInfo.kt
│   │   ├── BatteryInfo.kt
│   │   ├── HardwareInfo.kt
│   │   ├── SensorInfo.kt
│   │   └── StorageInfo.kt
│   └── repository/
│       └── DeviceRepository.kt
├── data/
│   └── repository/
│       └── DeviceRepositoryImpl.kt
└── feature/                          // Feature modules
    ├── dashboard/                    // Module 01: Dashboard
    ├── hardware/                     // Module 02: Hardware Explorer
    ├── battery/                      // Module 03: Battery Monitor
    ├── sensors/                      // Module 04: Sensor Lab
    ├── applications/                 // Module 05: App Manager
    ├── storage/                      // Module 06: Storage Analyzer
    ├── system/                       // Module 07: System Information
    ├── reports/                      // Module 08: Report Generator
    └── about/                        // Author Signature & Project Credits
```

---

## 🚀 Compilation & Lancement

### Prérequis
- **Android Studio Ladybug** (ou version plus récente)
- **JDK 17** ou supérieur
- Un appareil Android (API 26+) ou un émulateur Pixel Android 14/15

### Commandes Gradle
```bash
# Compiler le projet en mode Debug
./gradlew assembleDebug

# Lancer les tests unitaires
./gradlew testDebugUnitTest

# Générer l'APK Release
./gradlew assembleRelease
```

L'APK généré sera accessible dans : `app/build/outputs/apk/debug/app-debug.apk`.

---

## 🔒 Confidentialité & Permissions
- Toutes les opérations de diagnostic sont **100% locales**.
- Aucune donnée personnelle n'est envoyée à des tiers.
- Utilisation des API officielles Android (`SensorManager`, `BatteryManager`, `StorageManager`, `PackageManager`).

---

## ✍️ Signature Officielle
- **Auteur** : `@Bienvenu`
- **Application** : DROID INSPECTOR X
- **Version** : 1.0.0 (Phase 1 — Foundation)

## Phase 3 — vraies mesures

Les écrans **Hardware**, **Batterie** et **Stockage** affichent maintenant des valeurs lues sur l'appareil, sans permission supplémentaire. Quand Android n'expose pas une valeur (fréquences CPU, courant, cycles…), l'écran l'indique au lieu d'inventer un chiffre.

## Build

```bash
chmod +x gradlew
./gradlew assembleDebug
# → app/build/outputs/apk/debug/app-debug.apk
```

Requires JDK 17 and Android SDK platform 35. On GitHub, the workflow in `.github/workflows` builds the debug APK for you.
