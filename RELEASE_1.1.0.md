# Typing Lane 1.1.0 Release Notes

## Compared With 1.0.1

Typing Lane 1.1.0 focuses on combat feel, high-rarity weapon identity, smoother timing, higher-resolution rendering, and stronger regression coverage. This document records the gameplay and technical changes made after `1.0.1`.

## Major Gameplay Changes

- Changed typing input to read direct keyboard key codes from `keyPressed` instead of using `keyTyped` text input, so IME composition no longer affects gameplay letters.
- Kept IME disabled for the game panel and made letter handling accept only physical A-Z key codes during active play.
- Reworked lane switching so attacks wait until the lane-switch animation finishes.
- Changed lane switching to a faster nonlinear eased animation for a smoother, less mechanical movement feel.
- Expanded the lane word bank while preserving the fixed 4-7 character word-length constraint.
- Added word-bank validation to reject duplicate words and out-of-range word lengths in smoke tests.

## Timing, Resolution, And Scaling

- Raised the fixed logic cadence to 120 FPS.
- Changed the render timer cadence to 8 ms.
- Added `logicTicks(...)` and `gameplayStep(...)` scaling so enemy movement, bullet movement, cooldowns, particle lifetime, XP movement, status effects, and pressure pacing keep their old gameplay speed under the faster logic loop.
- Moved the logical game coordinate space to 2560 x 1440.
- Kept the render buffer at 2560 x 1440 and scaled the full scene into the window.
- Added coordinate, font, and stroke scaling helpers so UI, bullets, enemies, particles, lanes, XP, and effects stay proportional after the coordinate-space change.
- Changed the default window size to 1600 x 900.
- Changed the minimum window size to 960 x 540, instead of forcing the window to open at the full render resolution.
- Updated the launcher window minimum-size behavior to use the new minimum window constants.

## Weapon And Combat Changes

- Reworked primary weapon firing into a mutually exclusive weapon path.
- Fixed high-rarity weapons firing an unintended extra basic bullet.
- Fixed Laser Gun, Dry-Ice Bullet, and Homing Shotgun so each replaces the basic typed shot instead of stacking a hidden basic shot.
- Kept Autocannon as an independent automatic weapon; typed words still overcharge it.
- Kept Autocannon bullets on the older non-particle trail style by design.
- Changed basic bullets to a cleaner line-shaped trail.
- Added particle-trail support for manual special projectiles.
- Added color-matched damage numbers for different bullet types.
- Made damage numbers larger, more exaggerated, and slower to fade, using a livelier pop style and `Comic Sans MS`.
- Added enemy death break particles, with larger bursts for bosses and sturdier enemies.

## High-Rarity Weapons

- Added `Dry-Ice Bullet`.
  - Fires an icy irregular pentagon projectile.
  - Projectile model is larger than the original basic bullet and continuously rotates after being fired.
  - Uses polygon particle trajectory trails.
  - Deals splash damage in a small area.
  - Slows enemies in the splash area.
  - Freezes the same target after every third Dry-Ice hit.
  - Adds a pale-blue expanding pulse on hit.
- Reworked `Piercing Rail` into `Laser Gun`.
  - Keeps the old piercing mechanism and the 20% damage decay per pierced hit.
  - Changes the visual model from a traveling projectile into an instant red beam.
  - Adds a red aura, hot white beam core, muzzle flare, small glints, and red spark particles.
  - Beam fades out by lowering transparency over a short lifetime.
- Added `Homing Shotgun`.
  - Fires a large fan of pellets from typed words.
  - Starts at 15 pellets.
  - Trigger Tuning adds more pellets and raises total shotgun damage.
  - Pellets travel outward first, then arm after a short distance.
  - Armed pellets steer toward the nearest living enemy, including enemies in the other lane.
  - Each pellet has a faint pink particle trail.

## Upgrade And Balance Changes

- Added high-rarity pool entries for Dry-Ice Bullet and Homing Shotgun.
- Updated the high-rarity card text for Laser Gun, Dry-Ice Bullet, and Homing Shotgun in English and Chinese.
- Added upgrade-cap filtering so capped HP, damage, and Trigger Tuning cards stop being offered.
- Capped max-HP upgrade growth through `MAX_HP_UPGRADE_BONUS`.
- Capped additive damage growth through `MAX_DAMAGE_BONUS_PERCENT`.
- Capped Trigger Tuning growth through `MAX_TRIGGER_TUNING_BONUS_PERCENT`.
- Changed Trigger Tuning text so it no longer promises continuous-fire-rate scaling.
- Made low base-damage attacks still gain at least +1 damage when a positive percentage damage bonus would otherwise round down to 0.
- Reworked Combo Calibrator from a periodic extra shot into direct damage bonuses at combo 3, 5, and 10.
- Tightened Error Reset so it requires completing the same word after a mismatch instead of any fast follow-up word.
- Clarified several upgrade descriptions around non-Boss guards, Backspace-triggered bonuses, same-lane XP pulls, and lane-change collision protection.
- Changed Boss Breaker to rely on its level-based damage bonus path instead of the removed separate `bossDamageBonus` counter.
- Added Crossfeed priming on completed f/t/k words and expiration over time.

## Visual And Effect Changes

- Added particle trajectory trails for supported manual bullets.
- Added a dedicated bullet trail particle class.
- Added a dedicated Dry-Ice hit pulse class.
- Added rotating irregular pentagon shape data to Dry-Ice bullets.
- Added two-dimensional bullet state for homing pellets.
- Added beam-resolved state and fade lifetime for Laser Gun beams.
- Added ice slow and freeze state to targets.
- Added render interpolation for particle and projectile Y positions, not just X positions.
- Added target freeze outline rendering.
- Updated slow/mark/freeze effect rendering to scale correctly at 2560 x 1440.
- Scaled the neon backdrop, lane cards, player, targets, XP orbs, bullets, particles, HUD, and overlays through the new coordinate helpers.

## Bug Fixes

- Fixed Dry-Ice Bullet firing a basic bullet in addition to the dry-ice projectile.
- Fixed Laser Gun/Piercing Rail firing a basic bullet in addition to the piercing weapon.
- Fixed Homing Shotgun firing a basic bullet in addition to the shotgun fan.
- Fixed movement speeds becoming effectively doubled after the logic frame-rate change.
- Fixed projectile ranges and lifetimes after the 120 FPS logic change.
- Fixed particle movement and lifetime after the 120 FPS logic change.
- Fixed target movement, pressure timing, Boss lane-change timing, XP attraction, cooldowns, message timing, and status durations after the 120 FPS logic change.
- Fixed fullscreen/window scaling assumptions that made the default window open too large.
- Fixed Error Reset triggering from the wrong completed word after a mismatch.
- Fixed upgrade offers continuing to show capped upgrades.

## Verification

- Expanded smoke coverage for:
  - 120 FPS fixed logic cadence.
  - 8 ms render timer cadence.
  - 2560 x 1440 logical and render resolution.
  - 1600 x 900 default window size.
  - Decoupled minimum window size.
  - Fixed-update frame clock behavior.
  - Speed preservation after the logic-FPS change.
  - Direct keyboard-code input and ignored `keyTyped` gameplay input.
  - Word-bank length and duplicate validation.
  - Basic bullet line-trail style.
  - Autocannon old-style trails.
  - Laser Gun instant beam behavior and no hidden basic bullet.
  - Homing Shotgun pellet fan, arming, steering, particle trails, and no hidden basic bullet.
  - Dry-Ice Bullet replacement behavior, rotation, splash, slow, pulse, and freeze.
  - Upgrade cap filtering.
  - Error Reset same-word requirement.
  - Combo Calibrator damage thresholds.
  - Crossfeed priming, use, and expiration.
- Latest verification commands:

```text
javac *.java
java -cp . TypingLaneDemo --smoke
java -cp . TypingLaneDemo --perf
```

- Latest observed render benchmark:

```text
Render benchmark 2560x1440: 9.61 ms/frame, 104.0 FPS equivalent
```

## Changed Files Since 1.0.1

- Modified `BreakParticle.java`
- Modified `Bullet.java`
- Modified `BulletKind.java`
- Added `BulletTrailParticle.java`
- Modified `GamePanel.java`
- Added `IcePulse.java`
- Modified `Impact.java`
- Modified `SmokeTest.java`
- Modified `Target.java`
- Modified `TypingLaneDemo.java`
- Modified `UpgradeEffect.java`
