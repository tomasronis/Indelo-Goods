# Deploy to Azure App Service - Step by Step

## What is Azure App Service?

Azure App Service is a fully managed platform for hosting web apps, including Next.js with full server-side rendering and API route support.

**Cost:** ~$13/month (Basic B1 tier) - required for production apps

## Step 1: Create Azure App Service

1. Go to [Azure Portal](https://portal.azure.com)
2. Click **"Create a resource"**
3. Search for **"Web App"** and click **Create**

### Configure Basic Settings:

**Basics tab:**
- **Subscription**: Your Azure subscription
- **Resource Group**: Create new or use existing (e.g., `indelo-goods-rg`)
- **Name**: `indelo-goods` (this becomes `indelo-goods.azurewebsites.net`)
- **Publish**: **Code**
- **Runtime stack**: **Node 18 LTS**
- **Operating System**: **Linux**
- **Region**: Choose closest to your users (e.g., East US, West US, West Europe)

**Pricing:**
- Click **"Explore pricing plans"**
- Select **Basic B1** ($13.14/month)
  - 1.75 GB RAM
  - 10 GB storage
  - Perfect for starting out
- Or **Standard S1** if you need more ($55/month)

**Deployment:**
- **Continuous deployment**: Enable
- **GitHub Account**: Connect your GitHub account
- **Organization**: Your GitHub username
- **Repository**: `Indelo-Goods`
- **Branch**: `claude/add-documentation-guidelines-2S8uq` (or `main` after merging)

Click **"Review + create"** then **"Create"**

Azure will:
1. Create the App Service (2-3 minutes)
2. Set up GitHub Actions deployment automatically
3. Give you a URL: `https://indelo-goods.azurewebsites.net`

## Step 2: Configure Environment Variables

1. In Azure Portal, go to your App Service
2. Click **"Configuration"** in the left menu (under Settings)
3. Click **"+ New application setting"** for each variable:

**Add these 4 variables:**

```
NEXT_PUBLIC_SUPABASE_URL = https://ftojjmukocngssmjgedd.supabase.co
NEXT_PUBLIC_SUPABASE_ANON_KEY = sb_publishable_4UBidGG9V8RxnsNKDvf3hQ_oCNCi09u
NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY = [your Stripe publishable key]
STRIPE_SECRET_KEY = [your Stripe secret key]
```

4. Click **"Save"** at the top
5. Click **"Continue"** to restart the app

## Step 3: Configure Deployment Settings

Azure should auto-create a GitHub Actions workflow, but let's verify:

1. In your App Service, click **"Deployment Center"**
2. You should see:
   - Source: GitHub
   - Organization: Your username
   - Repository: Indelo-Goods
   - Branch: Your branch name

3. If not set up, click **"Settings"** and configure:
   - Source: **GitHub**
   - Authenticate with GitHub
   - Select your repository and branch
   - Click **"Save"**

## Step 4: Verify Deployment

1. Go to **GitHub Actions**: https://github.com/tomasronis/Indelo-Goods/actions
2. You should see a new workflow: **"Build and deploy Node.js app to Azure Web App"**
3. Wait for it to complete (3-5 minutes)
4. Visit: `https://indelo-goods.azurewebsites.net` (or your chosen name)

## Step 5: Update Android App (Optional)

If you want to use your custom domain later:

Update `QrCodeGenerator.kt`:
```kotlin
fun generateProductWebUrl(productId: String): String {
    return "https://indelo-goods.azurewebsites.net/product/$productId"
}
```

## Configure Custom Domain (Optional)

### If you own indelogoods.com:

1. In your App Service, click **"Custom domains"**
2. Click **"+ Add custom domain"**
3. Enter: `indelogoods.com`
4. Azure will show you DNS records to add

**Add these DNS records at your domain provider:**

```
Type: CNAME
Name: www
Value: indelo-goods.azurewebsites.net
TTL: 3600

Type: TXT
Name: asuid
Value: [Azure will provide this]
TTL: 3600
```

For apex domain (indelogoods.com):
```
Type: A
Name: @
Value: [Azure will provide IP]
TTL: 3600

Type: TXT
Name: asuid
Value: [Azure will provide this]
TTL: 3600
```

5. Click **"Validate"** then **"Add"**
6. Wait for DNS propagation (up to 48 hours, usually faster)
7. Azure automatically provisions SSL certificate (free)

## Troubleshooting

### App shows "Service Unavailable"

Check logs:
1. Go to your App Service
2. Click **"Log stream"** in the left menu
3. Watch for errors

Common fixes:
- Make sure Node.js version is **18 LTS**
- Check environment variables are set
- Verify GitHub Actions deployment succeeded

### Build fails in GitHub Actions

Check the workflow logs:
1. GitHub → Actions tab
2. Click the failed run
3. Expand "Build and deploy" step

Common issues:
- Missing environment variables (add in App Service Configuration)
- Wrong Node.js version (should be 18)

### Environment variables not working

1. Go to App Service → Configuration
2. Verify all 4 variables are listed
3. Click **"Save"** and **"Continue"** to restart

### Custom domain not working

- Verify DNS records with `nslookup indelogoods.com`
- Wait for DNS propagation (can take 24-48 hours)
- Check validation status in Azure Portal

## Monitoring & Logs

### View Application Logs

1. App Service → **"Log stream"**
2. Or click **"Logs"** → **"App Service logs"**
3. Enable **"Application Logging"** if needed

### View Deployment Logs

- GitHub Actions tab shows build/deploy logs
- App Service → **"Deployment Center"** → **"Logs"**

### Set Up Alerts (Optional)

1. App Service → **"Alerts"**
2. Create alert for:
   - High CPU usage
   - High memory usage
   - HTTP 5xx errors

## Scaling (When You Grow)

Start with **Basic B1** ($13/month), upgrade later:

- **Standard S1** ($55/month) - Auto-scaling, custom domains, more power
- **Premium P1V2** ($96/month) - Even more resources

To scale:
1. App Service → **"Scale up (App Service plan)"**
2. Select new tier
3. Click **"Apply"**

## Cost Management

**Current cost: ~$13/month** for Basic B1

To minimize costs:
- Use **Basic B1** tier (cheapest for production)
- Stop the app when not in use (dev/testing only)
- Set up budget alerts in Azure

## Comparison: App Service vs Static Web Apps

| Feature | Static Web Apps (Current) | App Service |
|---------|--------------------------|-------------|
| **Cost** | Free | ~$13/month |
| **Next.js SSR** | ❌ Limited | ✅ Full support |
| **API Routes** | ⚠️ Limited | ✅ Full support |
| **Setup Complexity** | Medium | Easy |
| **Performance** | Good | Great |
| **Our Use Case** | ❌ Not ideal | ✅ Perfect |

## Next Steps

1. ✅ Create App Service in Azure Portal
2. ✅ Configure environment variables
3. ✅ Wait for deployment (3-5 minutes)
4. ✅ Test your site
5. ⏭️ Configure custom domain (optional)
6. ⏭️ Update Android app with new URL (optional)

## Need Help?

- [Azure App Service Documentation](https://docs.microsoft.com/en-us/azure/app-service/)
- [Deploy Next.js to Azure](https://docs.microsoft.com/en-us/azure/app-service/quickstart-nodejs)
- Check deployment logs in GitHub Actions
- Check application logs in Azure Portal → Log stream
