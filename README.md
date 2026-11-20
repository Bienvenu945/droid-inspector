# DROID INSPECTOR X
### Android System Intelligence & Diagnostics
**Développé par [@Bienvenu](https://github.com/bienvenu)**

---

## Présentation du Projet
**DROID INSPECTOR X** est une application Android native professionnelle conçue pour l'analyse approfondie, l'exploration matérielle et le diagnostic informatif des appareils Android (Android 8.0 API 26 à Android 15 API 35).

Construite avec **Kotlin**, **Jetpack Compose**, **Material 3**, **Dagger Hilt** et une direction artistique **Emerald Frosted Glass (Glassmorphism)**, elle fonctionne **100 % localement** sans aucune collecte ou transmission de données.

---

## Modules Fonctionnels Complets (Phases 1 à 9)

1. **Module 01 · Dashboard** : Identification de l'appareil (`Build`), version Android & API, jauges circulaires animées temps réel (Batterie, RAM `ActivityManager.MemoryInfo`, Stockage `StatFs`), résumé CPU/écran et raccourcis directs.
2. **Module 02 · Hardware Explorer** : Fabricant, modèle, produit, architecture CPU & ABIs, fréquences par cœur (`/sys/devices/system/cpu/`), RAM détaillée, résolution/DPI/taux de rafraîchissement/HDR, OpenGL ES & Vulkan, fonctionnalités matérielles.
3. **Module 03 · Battery Monitor** : Surveillance temps réel via `ACTION_BATTERY_CHANGED` & `BatteryManager` (pourcentage, état de charge, source AC/USB/Sans fil, santé, technologie, température °C, tension mV, courant/compteur).
4. **Module 04 · Sensor Lab** : Détection complète via `SensorManager` (Accéléromètre, Gyroscope, Magnétomètre, Luminosité, Proximité, Baromètre et autres sondes), valeurs temps réel, inclinomètre visuel interactif et signalement clair des capteurs absents.
5. **Module 05 · App Manager** : Liste des applications accessibles via `PackageManager` (respectant `<queries>`), recherche, filtrage (Toutes / Utilisateur / Système), tri (Nom / Taille / Date), icônes, fiche détaillée avec permissions déclarées.
6. **Module 06 · Storage Analyzer** : Espace total/utilisé/disponible (`StatFs`), taille de bloc et explorateur de dossiers & fichiers basé sur **Storage Access Framework (`OpenDocumentTree` / `DocumentsContract`)** avec fil d'Ariane et recherche.
7. **Module 07 · System Information** : Version Android, niveau API, correctif de sécurité, noyau Linux, runtime ART, numéro de build, bootloader, empreinte, état de chiffrement (`DevicePolicyManager`), SELinux et vérifications d'intégrité.
8. **Module 08 · Report Generator** : Générateur de rapport PDF natif A4 (`PdfDocument`) personnalisable par module, enregistrement via SAF (`CreateDocument`) et partage via `FileProvider`, avec pied de page officiel `DROID INSPECTOR X — @Bienvenu`.
9. **Module 09 · À propos & Paramètres** : Signature officielle `@Bienvenu`, bascule Thème Sombre / Thème Clair persistée dans **Jetpack DataStore**, intervalle de rafraîchissement configurable et charte de confidentialité.

---

## Compilation & Automatisation GitHub Actions

```bash
# Exécuter les tests unitaires
./gradlew testDebugUnitTest

# Générer l'APK Debug
./gradlew assembleDebug

# Générer l'APK Release
./gradlew assembleRelease
```

Le workflow `.github/workflows/android-build.yml` exécute automatiquement les tests, génère l'APK Debug et l'APK Release (avec signature automatique si les secrets `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD` sont configurés).

---

## Signature Officielle
- **Application** : `DROID INSPECTOR X`
- **Package** : `com.droidinspector.app`
- **Auteur** : `@Bienvenu`
