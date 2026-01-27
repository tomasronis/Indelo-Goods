# Deployment Guide - Indelo Goods Web

This guide walks you through deploying the Indelo Goods web frontend for product QR code pages.

## Prerequisites

- Supabase project with products table set up
- GitHub account (for Vercel deployment)
- Domain name (optional, for custom domain)

## Step 1: Environment Variables

You'll need the following from your Supabase project:

1. Go to your Supabase project dashboard
2. Navigate to Settings > API
3. Copy the following values:
   - **Project URL** (e.g., `https://abcdefgh.supabase.co`)
   - **Anon/Public Key** (the `anon` `public` key)

## Step 2: Deploy to Vercel

### Option A: Deploy via Vercel Dashboard

1. Push your code to GitHub
2. Go to [vercel.com](https://vercel.com) and sign in with GitHub
3. Click "Add New Project"
4. Import your repository
5. Configure project:
   - **Framework Preset**: Next.js
   - **Root Directory**: `web`
   - **Build Command**: `npm run build`
   - **Output Directory**: `.next`
6. Add environment variables:
   - `NEXT_PUBLIC_SUPABASE_URL`: Your Supabase URL
   - `NEXT_PUBLIC_SUPABASE_ANON_KEY`: Your Supabase anon key
7. Click "Deploy"

### Option B: Deploy via Vercel CLI

```bash
# Install Vercel CLI
npm i -g vercel

# Navigate to web directory
cd web

# Login to Vercel
vercel login

# Deploy
vercel

# Follow prompts:
# - Set up and deploy? Yes
# - Which scope? (select your account)
# - Link to existing project? No
# - What's your project's name? indelo-goods-web
# - In which directory is your code located? ./
# - Want to modify settings? Yes
#   - Build Command: npm run build
#   - Output Directory: .next
#   - Development Command: npm run dev

# Add environment variables
vercel env add NEXT_PUBLIC_SUPABASE_URL
# Paste your Supabase URL when prompted

vercel env add NEXT_PUBLIC_SUPABASE_ANON_KEY
# Paste your Supabase anon key when prompted

# Deploy to production
vercel --prod
```

## Step 3: Configure Custom Domain

### Add Domain in Vercel

1. Go to your project in Vercel dashboard
2. Navigate to Settings > Domains
3. Add your domain (e.g., `indelogoods.com`)
4. Follow DNS configuration instructions

### Update DNS Records

Add the following DNS records to your domain provider:

**For apex domain** (indelogoods.com):
```
Type: A
Name: @
Value: 76.76.21.21
```

**For www subdomain**:
```
Type: CNAME
Name: www
Value: cname.vercel-dns.com
```

### Verify Domain

1. Wait for DNS propagation (can take up to 48 hours, usually faster)
2. Vercel will automatically verify and issue SSL certificate
3. Your site will be live at `https://indelogoods.com`

## Step 4: Update Android App

Update `AndroidManifest.xml` to handle universal links from your domain:

```xml
<activity android:name=".MainActivity">
    <!-- Existing deep link -->
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
            android:scheme="indelogoods"
            android:host="product" />
    </intent-filter>

    <!-- Universal link (HTTPS) -->
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
            android:scheme="https"
            android:host="indelogoods.com"
            android:pathPrefix="/product" />
    </intent-filter>
</activity>
```

## Step 5: Configure Android App Links

For seamless deep linking (app opens automatically when link is clicked):

1. Create `.well-known/assetlinks.json` in your web app:

```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "com.indelo.goods",
    "sha256_cert_fingerprints": [
      "YOUR_APP_SIGNING_CERTIFICATE_SHA256_FINGERPRINT"
    ]
  }
}]
```

2. Get your SHA256 fingerprint:
```bash
# Debug keystore
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Release keystore
keytool -list -v -keystore /path/to/release.keystore -alias your_alias
```

3. Add the assetlinks.json to your Next.js app:
   - Create `web/public/.well-known/assetlinks.json`
   - Paste the content with your actual SHA256 fingerprint
   - Redeploy

4. Verify App Links:
   - Visit `https://indelogoods.com/.well-known/assetlinks.json`
   - Should return your JSON file

## Step 6: Test Deep Linking

1. Generate a QR code for a test product:
   - URL: `https://indelogoods.com/product/YOUR_PRODUCT_ID`

2. Scan the QR code on your Android device:
   - **With app installed**: Should open directly in app
   - **Without app**: Should show web page with download button

3. Test the deep link fallback:
   - Click "Open in App" button on web page
   - Should attempt to open app or show download prompt

## Monitoring & Analytics

### Add Analytics (Optional)

1. **Google Analytics**:
```typescript
// Add to web/src/app/layout.tsx
import Script from 'next/script'

export default function RootLayout({ children }) {
  return (
    <html>
      <head>
        <Script
          src="https://www.googletagmanager.com/gtag/js?id=GA_MEASUREMENT_ID"
          strategy="afterInteractive"
        />
        <Script id="google-analytics" strategy="afterInteractive">
          {`
            window.dataLayer = window.dataLayer || [];
            function gtag(){dataLayer.push(arguments);}
            gtag('js', new Date());
            gtag('config', 'GA_MEASUREMENT_ID');
          `}
        </Script>
      </head>
      <body>{children}</body>
    </html>
  )
}
```

2. **Vercel Analytics**:
```bash
npm install @vercel/analytics
```

```typescript
// Add to web/src/app/layout.tsx
import { Analytics } from '@vercel/analytics/react'

export default function RootLayout({ children }) {
  return (
    <html>
      <body>
        {children}
        <Analytics />
      </body>
    </html>
  )
}
```

## Troubleshooting

### Deep Links Not Working

- Verify `AndroidManifest.xml` has correct intent filters
- Check that assetlinks.json is accessible
- Clear Android app cache and reinstall
- Verify domain is using HTTPS

### Environment Variables Not Loading

- Ensure variables start with `NEXT_PUBLIC_`
- Redeploy after adding variables
- Check Vercel dashboard > Settings > Environment Variables

### Product Images Not Loading

- Verify Supabase storage bucket is public
- Check CORS settings in Supabase
- Update `next.config.js` with correct hostname patterns

### 404 on Product Pages

- Verify product ID exists in database
- Check Supabase credentials are correct
- Look at server logs in Vercel dashboard

## Costs

- **Vercel**: Free tier includes:
  - Unlimited deployments
  - 100 GB bandwidth/month
  - Serverless functions
  - Custom domains
  - SSL certificates

- **Supabase**: Free tier includes:
  - 500 MB database
  - 1 GB file storage
  - 50 MB bandwidth/day

Both should be sufficient for initial launch. Scale up as needed.

## Next Steps

- Set up monitoring/alerting
- Add error tracking (Sentry)
- Implement SEO optimizations
- Add social sharing cards
- Create producer profile pages
- Build product catalog/browse view
