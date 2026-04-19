# OpenImgs — Local-First Photo Manager

## Project Overview

Kotlin Multiplatform photo management app for iOS and Android. Shared business logic in Kotlin, native UI per platform (SwiftUI + Jetpack Compose). Local-first, no cloud sync.

## Architecture

```
shared/           — KMP shared module
  commonMain/     — Pure Kotlin: domain models, repositories, search, dedup, storage
  iosMain/        — iOS platform implementations (PhotoKit)
  androidMain/    — Android platform implementations (MediaStore)
androidApp/       — Android app (Jetpack Compose)
iosApp/           — iOS app (SwiftUI)
```

## Tech Stack

- **Language**: Kotlin 2.1.20
- **Database**: SQLDelight 2.0.2
- **Coroutines**: kotlinx-coroutines 1.10.2
- **Serialization**: kotlinx-serialization 1.7.3
- **Android UI**: Jetpack Compose + Material 3
- **iOS UI**: SwiftUI
- **IAP**: RevenueCat KMP (to be added)
- **ML Search**: ONNX Runtime + CLIP (to be added)
- **Image Loading**: Coil (Android), platform-native (iOS)

## Build & Run

```bash
# Build shared module (both Android + iOS targets)
./gradlew :shared:compileDebugKotlinAndroid :shared:compileKotlinIosSimulatorArm64

# Build Android app
./gradlew :androidApp:compileDebugKotlin

# Full build + test
./gradlew :shared:build

# Android debug APK
./gradlew :androidApp:assembleDebug

# iOS: open iosApp/OpenImgs.xcodeproj in Xcode
```

### Build Requirements

- JDK 17+ (current: Java 21)
- Android SDK with build-tools 36.1.0, platform android-36
- `local.properties` with `sdk.dir` pointing to Android SDK
- Gradle 8.11.1 (wrapper included)

## Code Conventions

### Package Structure

- `com.openimgs.shared.*` for shared code
- `com.openimgs.shared.platform` for expect/actual declarations
- `com.openimgs.shared.domain.model` for data classes
- `com.openimgs.shared.domain.repository` for repository interfaces
- `com.openimgs.shared.data.repository` for SQLDelight implementations
- `com.openimgs.shared.data.database` for DatabaseHelper/DriverFactory
- `com.openimgs.shared.data.dedup` for duplicate detection (pHash/dHash)
- `com.openimgs.shared.data.search` for search engine (CLIP/ONNX)
- `com.openimgs.shared.data.storage` for storage analysis
- `com.openimgs.shared.data.premium` for freemium management
- `com.openimgs.android.ui.*` for Android Compose screens
- SQLDelight schemas in `shared/src/commonMain/sqldelight/`

### Kotlin Style

- All domain models use `@Serializable` from kotlinx.serialization
- Timestamps use `kotlinx.datetime.Instant` (NOT `java.util.Date` or `System.currentTimeMillis()`)
- Pure Kotlin in commonMain — no `java.*` imports allowed (use `Float.fromBits()` not `java.lang.Float`)
- SQLDelight generated types: use the generated names exactly (`duplicatesQueries` not `duplicateGroupsQueries`, `premiumQueries` not `premiumStatusQueries`)
- Repositories follow interface in `domain.repository` → implementation in `data.repository` pattern
- Free tier constants live in `PremiumManager.Companion`

### UI Style

- 8pt spacing grid for UI
- WCAG AA contrast minimum (#6E6E73 for secondary text, NOT #8E8E93)
- Dark mode supported at launch
- Material 3 on Android, native SwiftUI on iOS
- No custom icon set — SF Symbols (iOS), Material Symbols (Android)

## Navigation

5-tab bottom bar: Photos | Albums | Search | Clean | Settings

## Feature Gating (Freemium)

| Feature | Free | Premium |
|---------|------|---------|
| Gallery | Unlimited | Unlimited |
| Albums | 5 max | Unlimited |
| Smart Search | 10/month | Unlimited |
| Duplicate Delete | 5/day | Unlimited |
| Storage Analysis | View only | Batch cleanup |

Pricing: $2.99/month or $19.99/year

## Design System

See DESIGN.md for color tokens, typography, and spacing.

## Known Incomplete Areas

These are intentionally stubbed and need future work:

- **iOS PhotoProvider**: `iosMain` has empty stubs — actual PhotoKit integration happens in SwiftUI layer via `PHAsset`/`PHImageManager`
- **SearchEngine vector search**: `searchByImage()` and `searchBySimilarity()` return empty lists — needs ONNX Runtime integration
- **DuplicateDetector.computeHashForPhoto()**: Returns `(0L, 0L)` — needs platform pixel data → hasher pipeline
- **RevenueCat IAP**: PremiumManager logic exists but no SDK wiring yet
- **iOS Xcode project**: No `.xcodeproj` generated yet — needs `./gradlew :shared:generateXcodeProject` or manual setup

## CI

GitHub Actions workflow at `.github/workflows/ci.yml`:
- `build-shared`: macOS runner, compiles Android + iOS targets, runs tests
- `build-android`: Ubuntu runner, assembles debug APK
- `lint`: Android lint check
