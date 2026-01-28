# Deploy Web App to Azure Static Web Apps

## What You'll Need

- Azure account (free tier works great!)
- GitHub repository (already done ✅)
- 10 minutes

## Why Azure Static Web Apps?

- ✅ Free tier (100GB bandwidth/month)
- ✅ Automatic HTTPS and custom domains
- ✅ Supports Next.js API routes
- ✅ Auto-deploys from GitHub
- ✅ Perfect for your use case

## Step-by-Step Deployment

### Step 1: Create Azure Static Web App

1. Go to [Azure Portal](https://portal.azure.com)
2. Click "Create a resource"
3. Search for "Static Web App" and select it
4. Click "Create"

### Step 2: Configure Basic Settings

**Basics tab:**
- **Subscription**: Select your subscription
- **Resource Group**: Create new or select existing (e.g., "indelo-goods-rg")
- **Name**: `indelo-goods-web` (or your preferred name)
- **Plan type**: Free (or Standard if you need more)
- **Region**: Choose closest to your users (e.g., East US, West Europe)

**Deployment details:**
- **Source**: GitHub
- **Sign in with GitHub**: Authenticate your GitHub account
- **Organization**: Your GitHub username
- **Repository**: `Indelo-Goods`
- **Branch**: `claude/add-documentation-guidelines-2S8uq` (or `main` after merging)

**Build details:**
- **Build Presets**: Custom
- **App location**: `/web`
- **Api location**: (leave empty)
- **Output location**: `.next`

Click "Review + create" then "Create"

### Step 3: Wait for Deployment

Azure will:
1. Create a GitHub Actions workflow in your repo
2. Automatically build and deploy your app
3. Provide you with a URL like `https://your-app.azurestaticapps.net`

This takes 3-5 minutes.

### Step 4: Add Environment Variables

1. In Azure Portal, go to your Static Web App
2. Click "Configuration" in the left menu
3. Add the following environment variables:

**From your `web/.env.local` file:**
- `NEXT_PUBLIC_SUPABASE_URL`: Your Supabase project URL
- `NEXT_PUBLIC_SUPABASE_ANON_KEY`: Your Supabase anon key
- `NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY`: Your Stripe publishable key
- `STRIPE_SECRET_KEY`: Your Stripe secret key

4. Click "Save"
5. The app will automatically redeploy with the new environment variables

### Step 5: Configure Custom Domain (Optional)

If you own `indelogoods.com`:

1. In your Static Web App, click "Custom domains"
2. Click "+ Add"
3. Choose "Custom domain on other DNS"
4. Enter `indelogoods.com`
5. Follow the DNS configuration instructions:

**Add these DNS records at your domain provider:**

For root domain (indelogoods.com):
```
Type: CNAME
Name: @
Value: your-app.azurestaticapps.net
TTL: 3600
```

For www subdomain:
```
Type: CNAME
Name: www
Value: your-app.azurestaticapps.net
TTL: 3600
```

6. Wait for DNS propagation (up to 48 hours, usually faster)
7. Azure will automatically provision SSL certificate

### Step 6: Verify assetlinks.json

Once deployed, verify the App Links file is accessible:

Visit: `https://your-app.azurestaticapps.net/.well-known/assetlinks.json`

You should see:
```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "com.indelo.goods",
    "sha256_cert_fingerprints": [
      "CA:C1:2B:5D:30:E1:EF:B3:2A:63:A8:24:D3:4D:E9:8B:5B:25:4F:9B:4A:30:74:EA:13:C3:9E:C5:C3:07:B5:AE"
    ]
  }
}]
```

### Step 7: Update Android App (If Using Custom Domain)

If you configured a custom domain, update `AndroidManifest.xml`:

```xml
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data
        android:scheme="https"
        android:host="your-custom-domain.com"
        android:pathPrefix="/product" />
</intent-filter>
```

And update QR code generation in `QrCodeGenerator.kt` if needed.

## Testing

### Test 1: Web Page Loads
Visit: `https://your-app.azurestaticapps.net/product/some-product-id`

Should show product page or 404 if product doesn't exist.

### Test 2: App Links Work
1. Clear app data and reinstall your Android app
2. Scan a QR code or click a product link
3. **With app installed**: Opens in app ✨
4. **Without app**: Opens web page 🌐

## GitHub Actions Workflow

Azure automatically created a workflow file at:
`.github/workflows/azure-static-web-apps-*.yml`

This workflow:
- Triggers on push to your branch
- Builds your Next.js app
- Deploys to Azure Static Web Apps

Every time you push to GitHub, your site automatically updates!

## Monitoring and Logs

View deployment logs:
1. Go to GitHub → Actions tab
2. Click on the latest workflow run
3. Expand "Build and Deploy" to see logs

View app logs in Azure:
1. Azure Portal → Your Static Web App
2. Click "Log stream" or "Application Insights"

## Costs

**Free tier includes:**
- 100 GB bandwidth/month
- 2 custom domains
- Automatic SSL certificates
- Unlimited API calls

**Should be free** for your initial launch. Only upgrade if you exceed limits.

## Troubleshooting

### Build fails in GitHub Actions

Check the workflow file (`.github/workflows/azure-static-web-apps-*.yml`):
- Ensure `app_location: '/web'`
- Ensure `output_location: '.next'`

### Environment variables not working

- Make sure all variables are added in Azure Portal → Configuration
- Click "Save" after adding
- Wait for automatic redeployment (2-3 minutes)

### assetlinks.json returns 404

Make sure the file is at: `web/public/.well-known/assetlinks.json`

Azure Static Web Apps serves files from the `public` directory.

### Custom domain not working

- Verify DNS records are correct (check with `nslookup` or `dig`)
- Wait for DNS propagation (can take 24-48 hours)
- Check Azure Portal → Custom domains for validation status

### App Links not opening app

1. Verify assetlinks.json is accessible via HTTPS
2. Clear app data and reinstall
3. Check SHA-256 fingerprint matches exactly
4. Wait a few minutes for Android to verify

## Next Steps After Deployment

1. ✅ Verify web app loads
2. ✅ Check assetlinks.json is accessible
3. ✅ Test QR codes on real device
4. ✅ Set up custom domain (optional)
5. ✅ Configure monitoring/alerts
6. ✅ Set up production release keystore for release builds

## Additional Resources

- [Azure Static Web Apps Documentation](https://docs.microsoft.com/en-us/azure/static-web-apps/)
- [Next.js on Azure](https://docs.microsoft.com/en-us/azure/static-web-apps/deploy-nextjs)
- [Android App Links](https://developer.android.com/training/app-links)

## Need Help?

Check Azure deployment logs in:
- GitHub Actions tab
- Azure Portal → Your app → Deployments
- Azure Portal → Your app → Log stream
