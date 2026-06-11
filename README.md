# Typing Lane

Typing Lane is a two-lane typing action game built with Java Swing. Type the lane word to move, fire, collect XP, and assemble a build through blue upgrades, gold talents, and Boss-only red weapons.

## Run

```powershell
.\run-typing-lane.ps1
```

Useful launch flags:

- `--fullscreen`: start in fullscreen.
- `--smoke`: run the release smoke test.
- `--perf`: run the render benchmark.

The PowerShell launcher discovers the VS Code Java extension JDK when Java is not on `PATH`.

## Package

```powershell
.\package-release.ps1
```

Release artifacts are written to `dist/`:

- `TypingLane-1.2.0.jar`
- `TypingLane-1.2.0-windows-portable.zip`

The portable zip includes a bundled runtime and a `Play Typing Lane.bat` launcher.

## Version

Current release: `1.2.0`.
