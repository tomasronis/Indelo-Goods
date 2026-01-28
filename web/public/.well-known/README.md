# Android App Links Setup

This directory contains the `assetlinks.json` file needed for Android App Links to work properly.

## What Are Android App Links?

Android App Links allow your HTTPS URLs to automatically open your app when it's installed, and fallback to the web page when it's not installed. This means:

- **App installed**: QR code → opens directly in the app
- **App NOT installed**: QR code → opens web page in browser

## Setup Instructions

### Step 1: Get Your App's SHA-256 Fingerprints

You need to get the SHA-256 certificate fingerprints for both your debug and release builds.

#### For Debug Build (Development/Testing)

Run this command in your project root:

```bash
# Windows (PowerShell)
keytool -list -v -keystore $env:USERPROFILE\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android

# Mac/Linux
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Or use Gradle (works on all platforms):

```bash
./gradlew signingReport
```

Look for the line that says "SHA256:" and copy the fingerprint (it looks like `AA:BB:CC:DD:...`).

#### For Release Build (Production)

If you have a release keystore:

```bash
keytool -list -v -keystore /path/to/your/release.keystore -alias your_alias_name
```

You'll be prompted for your keystore password.

### Step 2: Update assetlinks.json

Open `assetlinks.json` in this directory and replace the placeholder fingerprints with your actual SHA-256 fingerprints:

```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "com.indelo.goods",
    "sha256_cert_fingerprints": [
      "AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99",
      "11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF:00"
    ]
  }
}]
```

**Important**:
- Include BOTH debug and release fingerprints
- Remove any placeholder fingerprints
- Format: colon-separated pairs (e.g., `AA:BB:CC:...`)

### Step 3: Deploy to Production

After updating the fingerprints:

1. Commit the changes to git
2. Push to your repository
3. Deploy to Vercel (or your hosting provider)

The file must be accessible at:
```
https://indelogoods.com/.well-known/assetlinks.json
```

### Step 4: Verify Setup

1. Visit `https://indelogoods.com/.well-known/assetlinks.json` in your browser
2. You should see your JSON file with the correct fingerprints
3. Use Google's test tool: https://developers.google.com/digital-asset-links/tools/generator

### Step 5: Test Deep Linking

1. **Clear app data** on your test device (Settings → Apps → Indelo Goods → Storage → Clear Data)
2. **Reinstall the app** to ensure Android re-verifies the App Links
3. Scan a QR code or click a link like `https://indelogoods.com/product/some-id`
4. The app should open automatically (if installed)

## Troubleshooting

### App doesn't open automatically when clicking links

- Verify the assetlinks.json file is accessible at the URL above
- Make sure you've added BOTH debug and release fingerprints
- Clear app data and reinstall the app
- Wait a few minutes after deployment for Android to re-verify
- Check AndroidManifest.xml has `android:autoVerify="true"` on the intent filter

### How to reset App Link handling on Android

1. Go to Settings → Apps → Indelo Goods
2. Tap "Open by default" or "Set as default"
3. Tap "Clear defaults" if available
4. Uninstall and reinstall the app

### Still not working?

Android App Links verification can be finicky. If it's not working:

1. Double-check all SHA-256 fingerprints are correct
2. Ensure the JSON file has no syntax errors
3. Make sure your domain is using HTTPS (not HTTP)
4. Try on a different device or Android version
5. Check Android logcat for verification errors:
   ```bash
   adb logcat | grep -i "AppLinks"
   ```

## Production Checklist

Before going live:

- [ ] Get SHA-256 fingerprint for release keystore
- [ ] Update assetlinks.json with production fingerprint
- [ ] Deploy to production domain
- [ ] Verify file is accessible at https://indelogoods.com/.well-known/assetlinks.json
- [ ] Test with production build on real device
- [ ] Test both scenarios (app installed vs not installed)

## References

- [Android App Links Documentation](https://developer.android.com/training/app-links)
- [Digital Asset Links](https://developers.google.com/digital-asset-links)
- [App Links Assistant in Android Studio](https://developer.android.com/studio/write/app-link-indexing)
