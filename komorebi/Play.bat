@echo off
cd /d "%~dp0"
java -jar Neighborhood.jar %*
if errorlevel 1 pause
