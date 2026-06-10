$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$exitCode = 0
Push-Location $scriptDir
try {

$javac = (Get-Command javac -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty Source)
$java = (Get-Command java -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty Source)

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
            $java = Join-Path (Split-Path $candidate -Parent) "java.exe"
            break
        }
    }
}

if (-not $javac -or -not (Test-Path $javac)) {
    Write-Host "javac was not found on PATH or in the VS Code Java extension JDK."
    Write-Host "Install a JDK, then run this script again."
    Write-Host "Source file: TypingLaneDemo.java"
    $exitCode = 1
} else {
    Write-Host "Using compiler: $javac"
    & $javac TypingLaneDemo.java
    $exitCode = $LASTEXITCODE
    if ($exitCode -eq 0) {
        & $java TypingLaneDemo @args
        $exitCode = $LASTEXITCODE
    }
}
} finally {
    Pop-Location
}

exit $exitCode
