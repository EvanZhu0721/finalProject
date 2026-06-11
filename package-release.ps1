$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Push-Location $scriptDir
try {
    $entryFile = Join-Path $scriptDir "TypingLane.java"
    $versionText = [System.IO.File]::ReadAllText($entryFile, [System.Text.Encoding]::UTF8)
    $versionMatch = [regex]::Match($versionText, 'VERSION\s*=\s*"([^"]+)"')
    if (-not $versionMatch.Success) {
        throw "Could not read VERSION from TypingLane.java."
    }
    $version = $versionMatch.Groups[1].Value

    $javac = (Get-Command javac -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty Source)
    if (-not $javac) {
        $extensionRoots = @(
            Join-Path $env:USERPROFILE ".vscode\extensions"
            Join-Path $env:USERPROFILE ".vscode-server\extensions"
        )
        foreach ($root in $extensionRoots) {
            if (-not (Test-Path $root)) {
                continue
            }
            $candidate = Get-ChildItem -Path $root -Directory -Filter "redhat.java-*-win32-x64" |
                ForEach-Object { Get-ChildItem -Path (Join-Path $_.FullName "jre") -Directory -ErrorAction SilentlyContinue } |
                ForEach-Object { Join-Path $_.FullName "bin\javac.exe" } |
                Where-Object { Test-Path $_ } |
                Select-Object -First 1
            if ($candidate) {
                $javac = $candidate
                break
            }
        }
    }

    if (-not $javac -or -not (Test-Path $javac)) {
        throw "javac was not found on PATH or in the VS Code Java extension JDK."
    }

    $javaBin = Split-Path $javac -Parent
    $jar = Join-Path $javaBin "jar.exe"
    if (-not (Test-Path $jar)) {
        throw "jar.exe was not found next to javac."
    }
    $runtimeRoot = Split-Path $javaBin -Parent

    $buildDir = Join-Path $scriptDir "build"
    $classesDir = Join-Path $buildDir "classes"
    $manifestPath = Join-Path $buildDir "MANIFEST.MF"
    $distDir = Join-Path $scriptDir "dist"
    $jarName = "TypingLane-$version.jar"
    $jarPath = Join-Path $distDir $jarName
    $portableDir = Join-Path $distDir "TypingLane-$version-windows-portable"
    $zipPath = Join-Path $distDir "TypingLane-$version-windows-portable.zip"

    if (Test-Path $buildDir) {
        Remove-Item -LiteralPath $buildDir -Recurse -Force
    }
    New-Item -ItemType Directory -Force -Path $classesDir | Out-Null
    New-Item -ItemType Directory -Force -Path $distDir | Out-Null
    if (Test-Path $jarPath) {
        Remove-Item -LiteralPath $jarPath -Force
    }
    if (Test-Path $portableDir) {
        Remove-Item -LiteralPath $portableDir -Recurse -Force
    }
    if (Test-Path $zipPath) {
        Remove-Item -LiteralPath $zipPath -Force
    }

    $sources = Get-ChildItem -Path $scriptDir -Filter "*.java" -File | ForEach-Object { $_.FullName }
    & $javac -encoding UTF-8 -d $classesDir @sources
    if ($LASTEXITCODE -ne 0) {
        throw "javac failed with exit code $LASTEXITCODE."
    }

    "Manifest-Version: 1.0`r`nMain-Class: TypingLane`r`n" |
        Set-Content -LiteralPath $manifestPath -Encoding ASCII -NoNewline
    & $jar cfm $jarPath $manifestPath -C $classesDir .
    if ($LASTEXITCODE -ne 0) {
        throw "jar failed with exit code $LASTEXITCODE."
    }

    New-Item -ItemType Directory -Force -Path $portableDir | Out-Null
    Copy-Item -LiteralPath $jarPath -Destination (Join-Path $portableDir $jarName)
    Copy-Item -LiteralPath (Join-Path $scriptDir "README.md") -Destination $portableDir -ErrorAction SilentlyContinue
    Copy-Item -LiteralPath (Join-Path $scriptDir "RELEASE_1.2.0.md") -Destination $portableDir -ErrorAction SilentlyContinue

    $launcherPath = Join-Path $portableDir "Play Typing Lane.bat"
    @"
@echo off
setlocal
set "APP_DIR=%~dp0"
if exist "%APP_DIR%runtime\bin\javaw.exe" (
    start "" "%APP_DIR%runtime\bin\javaw.exe" -jar "%APP_DIR%$jarName" %*
) else (
    start "" javaw -jar "%APP_DIR%$jarName" %*
)
endlocal
"@ | Set-Content -LiteralPath $launcherPath -Encoding ASCII

    $consoleLauncherPath = Join-Path $portableDir "Run With Console.bat"
    @"
@echo off
setlocal
set "APP_DIR=%~dp0"
if exist "%APP_DIR%runtime\bin\java.exe" (
    "%APP_DIR%runtime\bin\java.exe" -jar "%APP_DIR%$jarName" %*
) else (
    java -jar "%APP_DIR%$jarName" %*
)
pause
endlocal
"@ | Set-Content -LiteralPath $consoleLauncherPath -Encoding ASCII

    if (Test-Path $runtimeRoot) {
        Copy-Item -LiteralPath $runtimeRoot -Destination (Join-Path $portableDir "runtime") -Recurse
    }

    Compress-Archive -LiteralPath $portableDir -DestinationPath $zipPath -Force

    Write-Host "Built release artifacts:"
    Write-Host "  $jarPath"
    Write-Host "  $zipPath"
} finally {
    Pop-Location
}
