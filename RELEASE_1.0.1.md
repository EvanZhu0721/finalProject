# Typing Lane 1.0.1 Release Notes

## Changes

- Updated version branding from `1.0.0` to `1.0.1`.
- Fixed lane-word repetition:
  - A refreshed lane word no longer repeats the same lane's previous word.
  - The two lane words cannot be identical.
  - The two lane words avoid sharing the same first letter, reducing ambiguous typing starts.
- Added pre-run difficulty selection:
  - `1` Easy, target current typing speed 25 WPM.
  - `2` Normal, target current typing speed 40 WPM.
  - `3` Hard, target current typing speed 60 WPM.
- Difficulty now affects enemy movement speed only. Spawn pacing, enemy HP, XP, rewards, and upgrade economy stay identical across difficulties.
- HUD now shows selected difficulty and current typing speed in WPM.
- Removed progress-based lane-word length changes:
  - Lane command words now stay in a fixed 4-7 letter range.
  - Immediate lane threat no longer shortens or lengthens either lane word.
- Removed progress-based enemy density scaling:
  - Spawn cooldown no longer gets shorter over time or pressure.
  - Extra two-lane pressure spawns were removed.
  - Long-run pressure now leans harder into enemy HP max growth instead of raw enemy count.
- Added high-resolution and fullscreen support:
  - The window can now be resized while preserving the logical game layout.
  - `F11` toggles fullscreen without occupying any typing-letter key.
  - `--fullscreen` launches directly into fullscreen mode.
- Fixed fullscreen render stutter by rendering the neon game scene into a fixed logical frame buffer before scaling it to the window.
- Added `--perf` render benchmark for 3840 x 2160 fullscreen-style paint testing.
- Reworked the post-kill upgrade foundation:
  - Defeated non-Boss enemies now drop lane-bound XP balls instead of direct stat orbs.
  - XP balls are collected by entering that lane and approaching them; same-lane attraction helps nearby pickups.
  - Full XP stores a pending upgrade choice instead of interrupting typing immediately.
  - Space opens the pending 3-card upgrade menu when available; otherwise it keeps its emergency ram role.
  - Upgrade cards now roll rarity slot patterns first, then roll concrete buffs inside each slot.
  - High rarity is scaffolded as three build-defining slots: Weapon, Control, and Automation.
- Polished the neon UI layer:
  - Added lightweight animated neon grid and scanline backdrop.
  - Added smoother eased glow to active lanes, lane-word cards, the player, targets, XP balls, impacts, and the pressure meter.
  - Added compact target HP bars for faster combat readability.
  - Added a dedicated upper-right upgrade readiness panel with a pulsing Space prompt.
  - Simplified the upgrade readiness panel into icon, progress bar, and Space keycap states.
  - Removed the bottom explanatory message bar to keep the playfield clean.
  - Added F2 English / Chinese UI switching for HUD, menus, cards, overlays, and combat messages.
  - Kept typing words, HP, XP, WPM, Boss, key names, and target labels as compact English tokens for gameplay readability.
  - Removed a redundant bullet-core draw call while keeping the same projectile look.
- Increased same-lane attraction for XP balls to smooth pickups without removing lane commitment.

## Verification

- Smoke coverage includes version `1.0.1`, difficulty selection, F2 language switching, fixed progress-independent spawn density, fixed lane-word length, fullscreen state, high-resolution render scaling, word-repeat prevention, current typing speed calculation, XP pickup and attraction, upgrade menu selection, upgrade card application, and the existing combat/update loop checks.
- Render benchmark coverage can be run with `--perf`.
