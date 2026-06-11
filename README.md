# Folio 🌿

*Your books. Your voice. Your people.*

**Folio** is a social reading app built around voice notes, constellation-style
discovery, and communities that feel like book clubs, not forums. Calm by
design: warm oatmeal, sage, clay terracotta, and dusty lavender — nothing
flashes or shouts.

This repo contains **two implementations** of the same design:

| | What | Where | How to see it |
|---|---|---|---|
| 🌐 | Interactive web prototype | `index.html` | Open in any browser — zero setup |
| 🤖 | Native Android app (Kotlin + Jetpack Compose) | `android/` | Open in Android Studio and press Run |

---

## 🌐 Web prototype — see it in 10 seconds

No build step, no dependencies:

```
open index.html        # macOS
xdg-open index.html    # Linux
```

Or serve it: `python3 -m http.server` → `http://localhost:8000`.
Renders in a phone frame on desktop, full-screen on mobile. Use it as the
living design reference.

## 🤖 Android app — the real thing

Built with the recommended modern stack: **Kotlin, Jetpack Compose,
Material 3, Navigation Compose**. No XML layouts.

### Run it

1. Open **Android Studio** (Ladybug or newer).
2. **File → Open…** and select the `android/` folder.
3. Let Gradle sync (first sync downloads dependencies).
4. Pick an emulator or a connected device and press **▶ Run**.

Requirements: JDK 17+, Android SDK 35 (Android Studio installs both for you).
Min SDK is 26 (Android 8.0).

### Project layout

```
android/app/src/main/java/com/folio/app/
├── MainActivity.kt          # entry point, theme state (dark/light toggle)
├── FolioApp.kt              # Scaffold + bottom nav + Navigation Compose graph
├── data/Data.kt             # models + sample books, users, feed, chats…
└── ui/
    ├── theme/Theme.kt       # the calm palette, Material 3 color schemes, type
    ├── components/          # BookCover, VoicePill, RatingStars, avatars…
    ├── map/NeuralMap.kt     # the neural book map (Compose Canvas)
    └── screens/             # Feed, Discover, Book detail, Record, Library,
                             # Communities, Messages, Profile, Onboarding
```

### What's implemented (full Folio v1.0 spec)

1. **Feed** — cover cards, tap-to-rate stars, expandable reviews, emoji
   reactions, playable voice pills, Friends/Everyone toggle.
2. **Search & book detail** — live search grid, collapsible synopsis,
   rating breakdown bars, comments with replies, chapter-tagged voice notes.
3. **Voice notes** — recording screen with animated waveform, live timer,
   Private / Friends / Public visibility. (Playback/record are simulated —
   wiring `MediaRecorder` is the natural next step.)
4. **Profile & shelves** — stats, favourite genres, three shelf rails.
5. **Neural book map** — deep `#0d0d14` Compose Canvas: breathing nodes with
   per-node sine phase, pulsing center ring, particles along edges, filter
   chips with smooth fades, drag-to-pan, pinch-to-zoom, and a slide-up card
   whose "View book" hops to the next constellation, forever.
6. **Direct messages** — inbox with unread dots; text, shared book cards,
   and voice pills in threads.
7. **Communities** — feed / shared shelf / live chat tabs + creation flow
   (open vs invite-only).
8. **Library folders** — preview mosaics, 3-column grid, edit mode with
   remove (never deletes books), "Add book" tile, visibility cycling.
9. **Profile customization** — curated bookish banner swatches **plus a real
   photo picker** (`ActivityResultContracts.GetContent`), avatar with white
   ring pinned bottom-left.
10. **Onboarding** — four warm, fully skippable screens; no dark patterns.

### Design notes

- **Serif for book titles, sans-serif for UI** (system families; drop
  Fraunces/Outfit into `res/font/` to match the web prototype exactly).
- **First-class dark mode** — ◐ in the top bar, defaults to system.
- Book covers are generated from each book's two palette colors — no image
  assets required.
- No ads, no dark patterns, no forced contacts access. Ever.
