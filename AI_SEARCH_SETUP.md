# AI Search Setup Guide

## 🤖 Claude AI-Powered Search

Your app now has AI-powered search using Claude (Anthropic's AI)! Users can search using natural language like:
- "organic drinks"
- "gluten-free snacks"
- "vegan products under 500ml"
- "kosher beverages"

## 📝 Setup Instructions

### 1. Get an Anthropic API Key

1. Go to [https://console.anthropic.com](https://console.anthropic.com)
2. Sign up or log in
3. Navigate to **API Keys** section
4. Click **Create Key**
5. Copy your API key (starts with `sk-ant-...`)

### 2. Add API Key to local.properties

Open `local.properties` in your project root and add:

```properties
# Existing keys
SUPABASE_URL=your_supabase_url
SUPABASE_ANON_KEY=your_supabase_key

# Add this new line
ANTHROPIC_API_KEY=sk-ant-your-api-key-here
```

**Important:** Never commit `local.properties` to git (it's already in `.gitignore`)

### 3. Sync Gradle

After adding the API key:
1. Click **Sync Now** in Android Studio
2. Rebuild the project

### 4. Test AI Search

1. Run the app
2. Navigate to Browse Products (as a Shop user)
3. Type a natural language query in the search bar
4. Claude will analyze your query and return matching products! 🎉

## 💰 Pricing

Anthropic charges per API call:
- **Input**: ~$3 per million tokens
- **Output**: ~$15 per million tokens
- **Average search**: ~$0.01 - $0.05 per query

**Tip:** The app has a fallback to simple text search if API calls fail or you don't have an API key set up.

## 🔧 How It Works

1. User types a natural language query
2. App sends product catalog + query to Claude
3. Claude analyzes which products match
4. Results are filtered and displayed
5. If API fails, fallback to simple text search

## 🎯 Example Queries That Work Great

- "vegan beverages"
- "organic and non-GMO products"
- "kosher snacks"
- "gluten-free options"
- "products with less than 200 calories"
- "Mexican salsas and sauces"
- "refreshing summer drinks"

## 🛡️ Error Handling

The app gracefully handles:
- Missing API key → Falls back to text search
- API failures → Falls back to text search
- Rate limits → Shows all products
- Network errors → Shows all products

## 📊 Monitoring Usage

Check your usage at: [https://console.anthropic.com/settings/usage](https://console.anthropic.com/settings/usage)

---

**Note:** The AI search is live! Just add your API key and you're ready to go! 🚀
