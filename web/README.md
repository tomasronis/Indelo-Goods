# Indelo Goods Web

Next.js web frontend for Indelo Goods product QR code pages.

## Purpose

This web app handles universal links (`https://indelogoods.com/product/{id}`) for product QR codes. When users scan a QR code:

1. **With the app installed**: The page attempts to deep link to the Android app
2. **Without the app**: Shows a beautiful product detail page with option to download the app

## Features

- Server-side rendered product pages for fast loading and SEO
- Retro/pixel aesthetic matching the Android app
- Product details, certifications, ingredients, and allergens
- Deep linking to Android app
- Responsive design for mobile and desktop

## Setup

1. Install dependencies:
```bash
npm install
```

2. Create `.env.local` file (copy from `.env.local.example`):
```bash
cp .env.local.example .env.local
```

3. Add your Supabase credentials to `.env.local`:
```
NEXT_PUBLIC_SUPABASE_URL=your_supabase_url
NEXT_PUBLIC_SUPABASE_ANON_KEY=your_supabase_anon_key
```

4. Run the development server:
```bash
npm run dev
```

5. Open [http://localhost:3000](http://localhost:3000) in your browser

## Project Structure

```
web/
├── src/
│   ├── app/
│   │   ├── layout.tsx          # Root layout with metadata
│   │   ├── page.tsx             # Home page
│   │   ├── globals.css          # Global styles (Tailwind + custom)
│   │   ├── not-found.tsx        # 404 page
│   │   └── product/
│   │       └── [id]/
│   │           └── page.tsx     # Dynamic product detail page
│   ├── components/
│   │   └── ProductDetailClient.tsx  # Client component for product page
│   └── lib/
│       └── supabase.ts          # Supabase client and data fetching
├── public/                      # Static assets
├── package.json
├── tsconfig.json
├── tailwind.config.js           # Theme colors (mustard, ketchup, bun, charcoal)
└── next.config.js
```

## Deployment

### Vercel (Recommended)

1. Push your code to GitHub
2. Import the project in Vercel
3. Set the root directory to `web`
4. Add environment variables in Vercel dashboard:
   - `NEXT_PUBLIC_SUPABASE_URL`
   - `NEXT_PUBLIC_SUPABASE_ANON_KEY`
5. Deploy!

Vercel will automatically set up:
- Continuous deployment from your main branch
- Preview deployments for pull requests
- Custom domain support

### Custom Domain Setup

After deployment, configure your domain:

1. Add your domain in Vercel dashboard (e.g., `indelogoods.com`)
2. Update DNS records as instructed by Vercel
3. Update Android app's `AndroidManifest.xml` to match:
   ```xml
   <data
       android:scheme="https"
       android:host="indelogoods.com"
       android:pathPrefix="/product" />
   ```

## Design System

Colors (matching Android app):
- **Mustard**: `#F4C430` - Primary action color
- **Ketchup**: `#E63946` - Accent color
- **Bun**: `#FFF8DC` - Background color
- **Charcoal**: `#2B2D42` - Text color

Typography:
- Heading font: Press Start 2P (pixel font)
- Body font: System default

Components:
- `.pixel-border` - Retro bordered containers
- `.pixel-button` - Chunky 3D button effect
- `.badge-*` - Certification badges

## Features to Add

- [ ] Producer profile pages
- [ ] Product catalog/browse view
- [ ] Analytics tracking (Google Analytics, Plausible, etc.)
- [ ] App download links (Play Store, App Store)
- [ ] Shopping cart preview
- [ ] Related products
- [ ] Social sharing (Open Graph, Twitter Cards)

## Notes

- Product data is fetched from Supabase using the same database as the Android app
- Deep linking attempts to open the Android app, then falls back to download prompt
- All product pages are server-side rendered for SEO and fast initial load
- Image optimization handled by Next.js Image component
