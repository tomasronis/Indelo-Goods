@echo off
echo ================================
echo Deploying to Vercel
echo ================================
echo.

echo Step 1: Building...
call npm run build
if errorlevel 1 (
    echo Build failed! Fix errors and try again.
    pause
    exit /b 1
)

echo.
echo Step 2: Deploying to Vercel...
echo.
echo If this is your first deployment:
echo 1. You'll be asked to login (browser will open)
echo 2. Select your scope/team
echo 3. Confirm project settings
echo.

npx vercel --prod

echo.
echo ================================
echo Deployment complete!
echo ================================
echo.
echo Next steps:
echo 1. Run: get-sha256.bat (in project root)
echo 2. Update: web\public\.well-known\assetlinks.json
echo 3. Redeploy: run this script again
echo.
pause
