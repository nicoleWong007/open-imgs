# OpenImgs Design System

## Typography

| Role | iOS | Android | Size |
|------|-----|---------|------|
| Heading | SF Pro Display Medium | Roboto Medium | 20pt |
| Body | SF Pro Text Regular | Roboto Regular | 16pt |
| Caption | SF Pro Text Light | Roboto Light | 12pt |

Single weight hierarchy. No expressive type. Utility app.

## Colors

### Light Mode

| Token | Hex | Usage |
|-------|-----|-------|
| background | #FFFFFF | Screen background |
| surface | #F2F2F7 | Cards, sheets |
| text-primary | #000000 | Main text |
| text-secondary | #6E6E93 | Captions, metadata (WCAG AA 4.5:1) |
| accent | #007AFF | Buttons, links, active tab |
| destructive | #FF3B30 | Delete actions |
| success | #34C759 | Confirmations, freed space |
| premium | #FF9500 | Premium CTAs, upgrade prompts |

### Dark Mode

| Token | Hex |
|-------|-----|
| background | #000000 |
| surface | #1C1C1E |
| text-primary | #FFFFFF |
| text-secondary | #8E8E93 |
| accent | #0A84FF |
| destructive | #FF453A |
| success | #30D158 |
| premium | #FF9F0A |

## Spacing

8pt grid system. Values: 4, 8, 12, 16, 24, 32, 48pt.

| Spacing | Usage |
|---------|-------|
| 4pt | Tight padding (icon-text gap) |
| 8pt | Standard padding |
| 12pt | Section gap (compact) |
| 16pt | Card padding, screen margins |
| 24pt | Section gap (regular) |
| 32pt | Screen header padding |
| 48pt | Large vertical gaps |

## Grid

- Phone (375-428pt): 4 columns (default), pinch to 3 or 5
- Tablet (768-1024pt): 6 columns, pinch to 4-8

## Cards

- Corner radius: 12pt
- Shadow: none (flat surface)
- Background: surface color
- Only used when card IS the interaction (album covers, clean categories)

## Icons

- iOS: SF Symbols
- Android: Material Symbols
- No custom icon set

## Touch Targets

Minimum 44x44pt. Tab bar icons: 48x48pt. Photo thumbnails: 88x88pt minimum.

## Gesture Vocabulary

| Gesture | Action |
|---------|--------|
| Tap | Open |
| Long press | Select mode |
| Swipe left | Delete |
| Swipe right | Favorite |
| Pinch | Zoom grid |
| Swipe up (detail) | Metadata sheet |

## Accessibility

- Dynamic Type / scalable fonts supported
- VoiceOver / TalkBack labels on all interactive elements
- Reduce Motion: disable fade-in, instant transitions
- RTL language support: flip layout direction
