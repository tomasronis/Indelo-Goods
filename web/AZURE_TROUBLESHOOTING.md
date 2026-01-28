# Azure App Service Troubleshooting

## Current Issue: 403 Forbidden Error

### Root Cause
Azure App Service is not starting the Next.js application correctly. This typically means:
1. Missing or incorrect startup command
2. Missing environment variables
3. App build issues

---

## Solution Steps (In Order)

### Step 1: Configure Startup Command

1. Go to [Azure Portal](https://portal.azure.com)
2. Navigate to **indelo-goods** App Service
3. Click **Configuration** (left sidebar, under Settings)
4. Click **General settings** tab
5. Find **Startup Command** field
6. Enter exactly:
   ```
   npm start
   ```
7. Click **Save** at the top
8. Click **Continue** to restart the app
9. Wait 2-3 minutes for restart

### Step 2: Verify Environment Variables

While in Configuration:

1. Click **Application settings** tab
2. Verify these 4 variables exist:
   - `NEXT_PUBLIC_SUPABASE_URL`
   - `NEXT_PUBLIC_SUPABASE_ANON_KEY`
   - `NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY`
   - `STRIPE_SECRET_KEY`

**If missing, add them:**
- Click **+ New application setting**
- Name: `NEXT_PUBLIC_SUPABASE_URL`
- Value: `https://ftojjmukocngssmjgedd.supabase.co`
- Click **OK**
- Repeat for all 4 variables
- Click **Save** at top

### Step 3: Check App Service Plan

1. In your App Service, look for **App Service Plan** in the Overview
2. Verify:
   - **Operating System**: Linux ✅
   - **Runtime**: Node 18 LTS ✅
   - **Pricing tier**: Basic B1 or higher ✅

**If using Windows:**
- You need to recreate the App Service with Linux
- Windows requires different configuration (web.config, IIS)

### Step 4: Verify Deployment Succeeded

1. Go to **Deployment Center** (left sidebar)
2. Click **Logs** tab
3. Check the latest deployment:
   - Should show "Success" status
   - If failed, click to view error logs

**If deployment failed:**
- Go to GitHub Actions: https://github.com/tomasronis/Indelo-Goods/actions
- Click the latest workflow run
- Check for build errors

### Step 5: Check Application Logs

1. In Azure Portal, go to your App Service
2. Click **Log stream** (left sidebar, under Monitoring)
3. Wait for logs to appear
4. Look for:
   - `> indelo-goods-web@0.1.0 start`
   - `> next start`
   - `Ready on http://0.0.0.0:8080` or similar

**Common errors:**
- `npm ERR! missing script: start` → Build didn't complete
- `Error: Cannot find module 'next'` → Dependencies not installed
- `EADDRINUSE` → Port already in use (restart app)

---

## Quick Fixes

### Fix 1: Restart the App

1. Go to App Service **Overview**
2. Click **Restart** at the top
3. Wait 2-3 minutes
4. Check site again

### Fix 2: Redeploy from GitHub

1. Go to GitHub Actions
2. Click **Actions** tab
3. Select **Build and deploy Node.js app to Azure Web App**
4. Click **Run workflow** dropdown
5. Select your branch
6. Click **Run workflow**
7. Wait 5 minutes for deployment

### Fix 3: Check PORT Configuration

Azure assigns a dynamic PORT. Verify your app uses it:

1. In **Configuration** → **Application settings**
2. Check if `PORT` variable exists
3. If not, don't add it (Azure sets it automatically)
4. If exists, remove it (let Azure set it)

---

## Verification Checklist

After making changes, verify:

- [ ] Startup command is set to `npm start`
- [ ] 4 environment variables are configured
- [ ] Latest deployment succeeded (green checkmark)
- [ ] Log stream shows "Ready" or "Listening" message
- [ ] Site loads at `https://indelogoods.com` or `https://indelo-goods.azurewebsites.net`

---

## Advanced Debugging

### Check Build Output

The `.next` folder must exist after deployment:

1. Go to **Development Tools** → **Advanced Tools** → **Go →**
2. Click **Debug console** → **CMD** or **Bash**
3. Navigate to `/home/site/wwwroot/`
4. Check if `.next` folder exists
5. Check if `node_modules` folder exists

**If missing:**
- Build failed during deployment
- Check GitHub Actions logs
- Verify `npm run build` completes successfully

### Check Node.js Version

1. In **Log stream**, look for Node.js version
2. Should be `v18.x.x`
3. If different:
   - Go to **Configuration** → **General settings**
   - Set **Stack**: Node.js
   - Set **Version**: 18 LTS

### Manual Test (SSH)

1. Go to **Development Tools** → **SSH**
2. Run commands:
   ```bash
   cd /home/site/wwwroot
   ls -la
   cat package.json
   npm start
   ```
3. Look for errors

---

## Still Not Working?

### Option A: Check Azure Status
- Visit [Azure Status](https://status.azure.com/)
- Check for outages in your region

### Option B: Review GitHub Actions Logs
- GitHub → Actions → Latest run → View logs
- Look for failed steps

### Option C: Contact Support
- Azure Portal → Help + support → New support request
- Or post in [Azure Forums](https://docs.microsoft.com/en-us/answers/products/azure)

---

## Expected Behavior (When Working)

1. Visit `https://indelogoods.com`
2. See your Next.js app homepage
3. No errors in browser console
4. Fast page loads
5. Images load correctly

---

## Commit These Fixes

Don't forget to commit the changes:

```bash
git add .
git commit -m "Fix Azure App Service configuration for custom domain"
git push
```

Then wait for GitHub Actions to redeploy (~5 minutes).
