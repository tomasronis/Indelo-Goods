@echo off
echo ================================
echo Getting SHA-256 Fingerprints
echo ================================
echo.

echo Attempting to get Debug build fingerprint...
echo.

gradlew.bat signingReport

echo.
echo ================================
echo Look for "SHA256:" in the output above
echo Copy the fingerprint and update:
echo web\public\.well-known\assetlinks.json
echo ================================
echo.
pause
