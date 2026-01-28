# Deploy Web App to Vercel - Quick Guide

## Current Status

✅ Web app builds successfully
✅ Stripe API versions updated
✅ assetlinks.json created (needs your SHA-256 fingerprint)
❌ Not deployed yet (need to log into Vercel)

## Option 1: Deploy via Vercel Dashboard (Easiest - 5 minutes)

### Step 1: Push to GitHub

```bash
# In project root
git add .
git commit -m "Add Android App Links support and fix Stripe API version"
git push
```

### Step 2: Deploy on Vercel

1. Go to [vercel.com](https://vercel.com) and sign in with GitHub
2. Click "Add New Project"
3. Import your `IndeloGoods` repository
4. **Important**: Set **Root Directory** to `web`
5. Add environment variables (get values from `web/.env.local`):
   - `NEXT_PUBLIC_SUPABASE_URL`: Your Supabase project URL
   - `NEXT_PUBLIC_SUPABASE_ANON_KEY`: Your Supabase anon/public key
   - `NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY`: Your Stripe publishable key
   - `STRIPE_SECRET_KEY`: Your Stripe secret key
6. Click "Deploy"
7. Wait 2-3 minutes for deployment to complete
8. You'll get a URL like `https://your-project.vercel.app`

### Step 3: Add Custom Domain (Optional)

If you want to use `indelogoods.com`:

1. In Vercel dashboard → Settings → Domains
2. Add `indelogoods.com`
3. Follow DNS setup instructions from Vercel
4. Wait for DNS propagation (can take up to 48 hours, usually faster)

## Option 2: Deploy via CLI (For Advanced Users)

```bash
# In web directory
cd web

# Login to Vercel (opens browser)
npx vercel login

# Deploy
npx vercel --prod
```

The CLI will guide you through the setup. When it asks for environment variables, add the ones from `.env.local`.

## After Deployment

### 1. Verify assetlinks.json is accessible

Visit: `https://your-domain/.well-known/assetlinks.json`

You should see the JSON file with placeholder fingerprints.

### 2. Get Your App's SHA-256 Fingerprint

Run in project root:
```bash
.\get-sha256.bat
```

Or manually:
```bash
./gradlew signingReport
```

Look for the SHA-256 line and copy the fingerprint.

### 3. Update assetlinks.json

Edit `web/public/.well-known/assetlinks.json` and replace:
```json
"YOUR_DEBUG_SHA256_FINGERPRINT_HERE"
```

With your actual fingerprint (example):
```json
"AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF:00:11:22:33:44:55:66:77:88:99"
```

### 4. Redeploy

Push the changes to GitHub, and Vercel will automatically redeploy.

Or if using CLI:
```bash
cd web
npx vercel --prod
```

### 5. Test!

1. Scan a QR code on your phone
2. **With app installed**: Opens in app ✨
3. **Without app**: Opens web page 🌐

## What I Fixed

- ✅ Updated Stripe API version from `2024-11-20.acacia` to `2025-12-15.clover` in 3 files
- ✅ Added Suspense boundary to `/success` page to fix Next.js build error
- ✅ Created `.well-known/assetlinks.json` for Android App Links
- ✅ Web app now builds successfully

## Files Changed

```
web/
├── src/app/api/create-checkout-session/route.ts (Stripe API version)
├── src/app/api/create-payment-intent/route.ts (Stripe API version)
├── src/app/api/webhooks/stripe/route.ts (Stripe API version)
├── src/app/success/page.tsx (Added Suspense boundary)
└── public/.well-known/assetlinks.json (NEW - for Android App Links)
```

## Next Steps

1. **Deploy to Vercel** using Option 1 or 2 above
2. **Get SHA-256 fingerprint** using `get-sha256.bat`
3. **Update assetlinks.json** with your fingerprint
4. **Redeploy** to update the file
5. **Test** on a real device

## Troubleshooting

### Build fails
```bash
cd web
npm run build
```
Should complete successfully. If not, check error messages.

### Can't log into Vercel
Make sure you have a GitHub account and sign in with GitHub on vercel.com

### Environment variables not working
Make sure all 4 environment variables are added in Vercel dashboard under Settings → Environment Variables

### Domain not working
- Check DNS settings in your domain registrar
- Wait for DNS propagation (can take 24-48 hours)
- Verify SSL certificate is issued (automatic on Vercel)

## Need Help?

- [Vercel Documentation](https://vercel.com/docs)
- [Next.js Deployment](https://nextjs.org/docs/deployment)
- [Android App Links](https://developer.android.com/training/app-links)
