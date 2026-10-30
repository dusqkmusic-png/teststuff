@echo off
cd /d "%~dp0"
"runtime\bin\java.exe" -Xms128m -Xmx1024m -Dfile.encoding=UTF-8 -cp "asterfall.jar;lib\*" game.Desktop > launch-log.txt 2>&1
if errorlevel 1 (
 echo Asterfall could not start. Opening the log.
 notepad launch-log.txt
 pause
)
