# Typing Lane 1.0.0 Release Notes

## Release Goal

Typing Lane 1.0.0 packages the game as a complete local Java Swing game: clear start state, pause/restart controls, visible versioning, weapon build parity, upgrade-orb decisions, and repeatable smoke verification.

## Major Changes

- Added visible `v1.0.0` branding in the window title and HUD.
- Added a start overlay. Press Enter to begin the run.
- Added pause support. Press Esc to pause or resume; pressure, enemies, bullets, and spawns freeze while paused.
- Removed letter-key system shortcuts so typing letters can never pause or restart the run.
- Added restart-from-pause with F5 and game-over restart with Enter.
- Added session high score display.
- Reduced dead upgrades:
  - Pierce builds now also benefit from DMG upgrades.
  - Continuous/Base builds convert PCE upgrades into damage and heat.
  - Random upgrade rolls lightly prefer useful options for the current weapon.
- Improved growth parity:
  - Boss hunter now affects Pierce shots against tanks and bosses.
  - Clear stacks now improves both direct damage and Pierce count.
- Clarified the Continuous weapon identity:
  - Regular continuous bullets are now much faster but deal much lower chip damage.
  - Correct lane-word input opens a short 0.5 second surge window.
  - Surge bullets use brighter yellow-green pulse effects and deal higher damage during that window.
  - Regular green chip bullets are smaller and lower-glow so the high fire rate is less visually noisy.
- Increased enemy HP ramp over time so high fire-rate builds face stronger late-run targets sooner.
- Raised target durability: non-Boss targets are about 1.5x HP, while Boss targets are 2x HP.
- Reduced per-frame allocation pressure by caching draw resources and only allocating pierce hit-tracking lists for bullets that actually need them.
- Improved pressure readability:
  - A threatened lane can receive a shorter rescue word.
  - The safer opposite lane can become a longer, greedier choice at higher pressure.
- Launcher script now compiles and runs from its own folder even when called from the repo root.
- Smoke test now covers release gates, input edge cases, letter-key shortcut rejection, pause freeze, reset behavior, reward choice validation, weapon parity, bullets, orbs, ramming, and pressure scaling.

## Acceptance Checklist

- `run-typing-lane.ps1 --smoke` passes from `finalProject`.
- `finalProject\run-typing-lane.ps1 --smoke` passes from the repo root.
- `finalProject\run-typing-lane.bat --smoke` passes from the repo root.
- Manual play can start, pause, resume, restart, choose Boss rewards, collect upgrade orbs, and reach rising pressure without word prompts being covered by enemies.
