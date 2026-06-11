# Typing Lane 1.2.0 Release Notes

## Highlights

Typing Lane 1.2.0 turns the project into a release-ready build: the old development naming is gone, the HUD is cleaner, Boss fights are more complete, and the weapon/gold-talent systems are much deeper.

## New And Changed

- Renamed the main entry point to `TypingLane` and updated launch scripts to remove development-era naming.
- Added a release packaging script that builds an executable jar and a Windows portable zip with bundled runtime.
- Added dynamic bottom HUD cards for active weapon and talent values, with cleaner large-number presentation.
- Moved version, language, and current weapon to the top HUD with simplified release styling.
- Expanded Boss behavior: two-lane body, ranged laser warnings, lower-health barrage attacks, freeze restrictions, and defeat flash/explosion sequence.
- Added gold talents from `goldBoosts.md`, including drones, overflow shot, magnet pickup, undying totem, adrenaline, melee, and red-eye damage.
- Added weapon-level progression for red weapons, with Boss reward choices supporting current-weapon upgrades, random weapons, and gold talents.
- Updated red weapons with level mechanics for Basic Gun, Laser Gun, Autocannon, Dry-Ice Bullet, and Homing Shotgun.
- Polished damage numbers, projectile trails, freeze visuals, melee Boss ram feedback, and shotgun color spread.
- Rebalanced drones to a maximum of three active drones.
- Made Autocannon level 2 return to base fire rate twice as quickly as before.

## Build And Run

```powershell
.\run-typing-lane.ps1
.\run-typing-lane.ps1 --smoke
.\package-release.ps1
```

The portable release artifact is:

```text
dist\TypingLane-1.2.0-windows-portable.zip
```

Unzip it and run `Play Typing Lane.bat`.

## Verification

- `.\run-typing-lane.ps1 --smoke`
- `.\package-release.ps1`
