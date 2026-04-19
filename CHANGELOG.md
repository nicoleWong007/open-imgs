# Changelog

All notable changes to OpenImgs will be documented in this file.

## [1.0.0] - 2026-04-19

### Added

**Shared Module (KMP)**
- Domain models: Photo, Album, DuplicateGroup, SearchResult, StorageCategory, PremiumFeature, PremiumStatus
- SQLDelight schema: photos, albums, album_photos, duplicate_groups, duplicate_photos, premium_status tables
- Platform photo access via expect/actual: PhotoProvider (Android MediaStore + iOS PhotoKit stubs)
- ThumbnailLoader with platform-native thumbnail generation
- DatabaseDriverFactory for SQLDelight (Android SQLite + iOS Native)
- PhotoRepository with paginated loading, favorites, date range queries, hash/embedding storage
- AlbumRepository with CRUD, photo membership, cover photo management
- DuplicateRepository with group management and status tracking
- PremiumManager with freemium feature gating (5 albums, 10 searches/mo, 5 deletes/day)
- PerceptualHasher: DCT-based pHash + difference hash with Hamming distance scoring
- DuplicateDetector: combined pHash/dHash similarity with configurable threshold
- SearchEngine: EmbeddingProvider interface (CLIP/ONNX-ready), cosine similarity, float↔byte conversion
- StorageAnalyzer: photo categorization (screenshots, large videos), storage reports with potential savings

**Android App (Jetpack Compose)**
- Material 3 theme with dynamic color, dark mode support, WCAG AA secondary text (#6E6E73)
- 5-tab bottom navigation: Photos, Albums, Search, Clean, Settings
- Gallery screen with photo grid (Coil image loading), loading/error/empty states
- Albums screen with album grid, FAB for creation, premium limit enforcement
- Search screen with SearchBar and debounced input
- Clean screen with Duplicates/Storage segmented tabs
- Settings screen with premium upgrade, usage counters, about section
- Onboarding screen with permission request flow
- ProGuard rules for release builds
- GitHub Actions CI workflow (shared build + Android APK)

**iOS App (SwiftUI)**
- App entry point with permission-gated navigation
- 5-tab TabView: Photos, Albums, Search, Clean, Settings
- Gallery view with PHAsset grid, async thumbnail loading
- Albums view with PHAssetCollection integration, create album flow
- Search view with searchable modifier
- Clean view with Duplicates/Storage segmented picker
- Settings view with premium upgrade, usage tracking
- Onboarding flow (3-page trust sequence + photo permission)
- AccentColor asset catalog (light + dark variants)
