# Folio 🌿

*Your books. Your voice. Your people.*

A high-fidelity interactive prototype of **Folio** — a social reading app built
around voice notes, constellation-style discovery, and communities that feel
like book clubs, not forums.

## Run it

No build step, no dependencies. Just open the file:

```
open index.html        # macOS
xdg-open index.html    # Linux
```

Or serve it locally: `python3 -m http.server` and visit `http://localhost:8000`.

It renders inside a phone frame on desktop and goes full-screen on mobile.

## What's inside

Every section of the Folio v1.0 spec is implemented:

1. **Feed** — vertical card feed with covers, inline-tappable star ratings,
   expandable reviews, emoji reactions, and playable voice-note pills.
   Friends / Everyone scope toggle.
2. **Search & book detail** — real-time search grid; detail pages with
   collapsible synopsis, community rating breakdown, comment threads with
   replies, and chapter-tagged voice notes.
3. **Voice notes** — recording flow with animated waveform, live timer, and
   Private / Friends / Public visibility.
4. **Profile & shelves** — stats, favourite genres, and the three default
   shelves as horizontal cover rails.
5. **Neural book map** — full-bleed `#0d0d14` canvas at the bottom of every
   book page. Breathing nodes with per-node phase offsets, a pulsing center
   ring, particles drifting along edges, connection-type filter chips
   (theme / style / reader overlap), drag-to-pan, pinch/scroll zoom, and a
   slide-up card whose "View book" button lets you hop between maps forever.
6. **Direct messages** — inbox with unread dots; threads mixing text, shared
   book cards, and voice pills.
7. **Communities** — feed / shared shelf / live chat tabs, plus a full
   creation flow (name, description, open vs invite-only, seeded shelf).
8. **Library folders** — preview mosaics, 3-column grids, edit mode with
   remove, an "Add book" tile, and visibility cycling. Deleting a grouping
   never deletes books.
9. **Profile customization** — banner sheet with a curated bookish palette,
   a real photo picker for custom banners, and reset-to-default.
10. **Onboarding** — four warm, fully skippable screens: welcome, genre
    chips, shelf seeding, and optional friend-finding.

## Design notes

- **Calm by default.** Warm oatmeal, sage, clay terracotta, and dusty
  lavender; soft shadows; nothing flashes or shouts.
- **Serif for books, sans for UI** — Fraunces + Outfit, per the spec's
  typography direction.
- **First-class dark mode** — toggle in the top bar (◐), defaults to your
  system preference, persisted in `localStorage`.
- **Soft failures, inviting empty states** — "Your shelf is quiet. Let's
  change that."
- No ads, no dark patterns, no forced contacts access. Ever.
