# Azure Startup Command for Standalone Next.js

After this deployment completes, update the Azure startup command:

## In Azure Portal

1. Go to **indelo-goods** App Service
2. **Configuration** → **General settings**
3. Change **Startup Command** to:
   ```
   node server.js
   ```
4. Click **Save** and **Continue**

## Why Standalone Mode?

Next.js standalone mode creates a self-contained server that:
- Includes only necessary dependencies
- Runs with `node server.js` (no npm needed)
- Smaller deployment size
- Faster startup time
- No Oryx/node_modules extraction issues

## Current Status

The deployment will build a standalone server and deploy it to Azure.
