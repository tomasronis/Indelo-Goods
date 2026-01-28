# Android App Links Setup - Quick Start Guide

## Problem

When scanning QR codes on a phone without the app installed, it tries to open the app (and fails) instead of opening the web page.

## Solution

Android App Links! This allows URLs to:
- **Open the app** if it's installed
- **Open the web page** if it's not installed

## What I've Set Up

1. ✅ Created `web/public/.well-known/assetlinks.json` - tells Android which app can handle indelogoods.com links
2. ✅ Created helper script `get-sha256.bat` - makes it easy to get your app's fingerprint
3. ✅ QR codes already generate HTTPS URLs (`https://indelogoods.com/product/{id}`)
4. ✅ AndroidManifest.xml already has universal link support with `android:autoVerify="true"`

## Next Steps (5 minutes)

### Step 1: Get Your SHA-256 Fingerprint

Run this in your project root:

```bash
# Windows
.\get-sha256.bat

# Mac/Linux
./gradlew signingReport
```

Look for output like this:
```
Variant: debug
Config: debug
Store: C:\Users\YourName\.android\debug.keystore
Alias: AndroidDebugKey
MD5: AA:BB:CC:...
SHA1: 11:22:33:...
SHA-256: AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99
```

**Copy the SHA-256 value** (the long string with colons like `AA:BB:CC:...`)

### Step 2: Update assetlinks.json

1. Open `web/public/.well-known/assetlinks.json`
2. Replace `YOUR_DEBUG_SHA256_FINGERPRINT_HERE` with your actual SHA-256 fingerprint
3. For now, you can remove the second placeholder or leave it for your production build later

Example:
```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "com.indelo.goods",
    "sha256_cert_fingerprints": [
      "AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99"
    ]
  }
}]
```

### Step 3: Deploy to Your Web Host

Deploy your web frontend with the updated assetlinks.json file:

```bash
cd web
vercel --prod
# or however you deploy your web app
```

The file **must** be accessible at:
```
https://indelogoods.com/.well-known/assetlinks.json
```

### Step 4: Test It!

1. **Clear your app data**: Settings → Apps → Indelo Goods → Storage → Clear Data
2. **Reinstall the app** to force Android to re-verify App Links
3. **Scan a QR code** or click a product link
4. **Magic!** ✨
   - If app is installed → Opens in app
   - If app is NOT installed → Opens in browser

## Testing Scenarios

### Test 1: App Installed
1. Have the app installed on your phone
2. Scan a product QR code (or click `https://indelogoods.com/product/xxx`)
3. **Expected**: App opens directly to product page

### Test 2: App Not Installed
1. Uninstall the app
2. Scan the same QR code
3. **Expected**: Web page opens in browser
4. User can view product and click "Download App" button

## Troubleshooting

### "App still doesn't open automatically"

Try these in order:

1. **Verify the JSON file is accessible**:
   - Visit `https://indelogoods.com/.well-known/assetlinks.json` in browser
   - Should see your JSON with correct fingerprint

2. **Clear app data and reinstall**:
   ```
   Settings → Apps → Indelo Goods → Storage → Clear Data
   Then uninstall and reinstall the app
   ```

3. **Wait a few minutes**: Android verifies App Links in the background, can take a few minutes

4. **Check for typos**: Make sure SHA-256 fingerprint is exactly correct (including colons)

5. **Reset default app handling**:
   ```
   Settings → Apps → Indelo Goods → Open by default → Clear defaults
   ```

### "Get SHA-256 script doesn't work"

Run manually:
```bash
./gradlew signingReport
```

Or directly with keytool (Windows):
```bash
keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Mac/Linux:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

## Production Checklist

When you're ready to release:

- [ ] Generate a release keystore for signing your production builds
- [ ] Get the SHA-256 fingerprint from your **release** keystore
- [ ] Add the production fingerprint to assetlinks.json (keep debug too for testing)
- [ ] Deploy updated assetlinks.json to production
- [ ] Test with production build

## How It Works

1. **QR Code contains**: `https://indelogoods.com/product/123`
2. **Android sees HTTPS URL**: Checks if any app can handle it
3. **Looks up**: `https://indelogoods.com/.well-known/assetlinks.json`
4. **Verifies**: App signature matches the fingerprints in the file
5. **Decision**:
   - Match found + app installed → Open in app
   - No match OR no app → Open in browser

## More Info

See `web/public/.well-known/README.md` for detailed documentation.

## Need Help?

- [Android App Links Docs](https://developer.android.com/training/app-links)
- [Test Your Links](https://developers.google.com/digital-asset-links/tools/generator)
- Check logcat: `adb logcat | grep -i "AppLinks"`
