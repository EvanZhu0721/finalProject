@echo off
setlocal
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-typing-lane-demo.ps1" %*
if errorlevel 1 pause
endlocal
