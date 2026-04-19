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
# Build shared module
./gradlew :shared:build

# Run Android
./gradlew :androidApp:installDebug

# iOS: open iosApp/OpenImgs.xcodeproj in Xcode
```

## Conventions

- Package: `com.openimgs.shared.*` for shared code
- Platform code in `com.openimgs.shared.platform` (expect/actual)
- SQLDelight schemas in `shared/src/commonMain/sqldelight/`
- Tests mirror source structure in `commonTest/`
- 8pt spacing grid for UI
- WCAG AA contrast minimum (#6E6E73 for secondary text)
- Dark mode supported at launch

## Design System

See DESIGN.md for color tokens, typography, and spacing.

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
