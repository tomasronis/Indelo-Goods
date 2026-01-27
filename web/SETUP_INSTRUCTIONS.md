# Setup Instructions - New Features

This document guides you through completing the setup for the new features added to Indelo Goods.

## What Was Added

### 1. ✅ Producer Profile Link on Product Web Page
- The product page now displays a link to the producer's profile
- Shows "By [Producer Name]" with a clickable link
- Links to `/producer/{producerId}` (you'll need to create this page later)

### 2. ✅ Producer Profile Creation in Android App
- Producers can now create/edit their profile from the Android app
- Added "Edit Profile" button (person icon) in the Producer Home screen top bar
- Profile fields include:
  - Company Name, Brand Name
  - Location, Founded Year, Specialty
  - Website URL
  - Bio, Background, Inspiration, Goals
- All data syncs to Supabase `producer_profiles` table

### 3. ✅ Purchase Here Button on Web
- Added "Purchase Here" button on product web page
- Shows price directly on the button
- Opens a checkout modal with:
  - Product summary
  - Quantity selector
  - Email input
  - Total with fee breakdown
  - "Complete Purchase" button (ready for Stripe integration)

## Required Setup Steps

### Step 1: Create Producer Profiles Table in Supabase

**IMPORTANT:** You must run this migration before the features will work.

1. Go to your Supabase project dashboard
2. Navigate to **SQL Editor**
3. Open the file `migrations/add_producer_profiles.sql`
4. Copy the entire SQL content
5. Paste it into the Supabase SQL Editor
6. Click **Run** to execute the migration

This creates the `producer_profiles` table with proper Row Level Security (RLS) policies.

### Step 2: Test the Android App

1. Open the Android app in Android Studio
2. Run the app
3. Sign in as a Producer
4. Click the **person icon** in the top-right corner of the Producer Home screen
5. Fill out your profile information
6. Click **Save Profile**
7. Your profile is now stored in Supabase!

### Step 3: Test the Web Page

1. The dev server should still be running at http://localhost:3000
2. Visit a product page (if you don't have one, create a product in the Android app first)
3. You should now see:
   - "By [Producer Name]" link (if profile exists)
   - "Purchase Here" button
   - Clicking "Purchase Here" opens the checkout modal
   - You can adjust quantity and enter email
   - Click "Complete Purchase" (currently shows placeholder - Stripe integration next step)

## Next Steps: Stripe Integration

The checkout UI is ready. To complete the integration:

### For Stripe Checkout:

1. Install Stripe SDK:
```bash
cd web
npm install @stripe/stripe-js stripe
```

2. Create API route for Stripe Checkout Session:

```typescript
// web/src/app/api/create-checkout-session/route.ts
import { NextRequest, NextResponse } from 'next/server'
import Stripe from 'stripe'

const stripe = new Stripe(process.env.STRIPE_SECRET_KEY!, {
  apiVersion: '2024-11-20.acacia',
})

export async function POST(req: NextRequest) {
  const { productId, quantity, email } = await req.json()

  // Fetch product from your database
  // Calculate pricing with splits

  const session = await stripe.checkout.sessions.create({
    payment_method_types: ['card'],
    line_items: [
      {
        price_data: {
          currency: 'usd',
          product_data: {
            name: 'Product Name',
          },
          unit_amount: price * 100, // Stripe uses cents
        },
        quantity,
      },
    ],
    mode: 'payment',
    success_url: `${req.headers.get('origin')}/success?session_id={CHECKOUT_SESSION_ID}`,
    cancel_url: `${req.headers.get('origin')}/product/${productId}`,
    customer_email: email,
  })

  return NextResponse.json({ sessionId: session.id })
}
```

3. Update the checkout modal to redirect to Stripe:

```typescript
// In CheckoutModal component
import { loadStripe } from '@stripe/stripe-js'

const stripePromise = loadStripe(process.env.NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY!)

const handleCheckout = async () => {
  setIsProcessing(true)

  const response = await fetch('/api/create-checkout-session', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      productId: product.id,
      quantity,
      email,
    }),
  })

  const { sessionId } = await response.json()
  const stripe = await stripePromise

  if (stripe) {
    await stripe.redirectToCheckout({ sessionId })
  }
}
```

4. Set up Stripe webhooks for payment confirmation
5. Implement payment splits (90% producer, 4.5% shop, 5.5% platform)

## Troubleshooting

### "Could not find table 'producer_profiles'" error

**Solution:** Run the migration SQL from Step 1 above.

### Producer profile link not showing

**Possible causes:**
1. Migration not run yet (run Step 1)
2. Producer hasn't created their profile yet (use Android app to create)
3. Check browser console for errors

### Checkout modal not opening

**Solution:** Check browser console for JavaScript errors. Make sure the page compiled without errors.

## Files Created/Modified

### Android App
- **New Files:**
  - `ProducerProfile.kt` (model)
  - `ProducerProfileRepository.kt` (repository)
  - `ProducerProfileViewModel.kt` (ViewModel)
  - `ProducerProfileScreen.kt` (UI screen)
- **Modified:**
  - `AppNavigation.kt` (added profile route)
  - `ProducerHomeScreen.kt` (added Edit Profile button)

### Web App
- **New Files:**
  - `migrations/add_producer_profiles.sql` (database migration)
  - `web/SETUP_INSTRUCTIONS.md` (this file)
- **Modified:**
  - `web/src/lib/supabase.ts` (added ProducerProfile interface and fetch function)
  - `web/src/app/product/[id]/page.tsx` (fetch producer profile)
  - `web/src/components/ProductDetailClient.tsx` (added producer link + checkout modal)

### Database
- **New Table:** `producer_profiles`
  - Stores producer information (company name, bio, background, inspiration, goals, etc.)
  - RLS enabled (producers can edit own profile, everyone can view)

## Support

If you encounter any issues:
1. Check that the migration was run successfully
2. Verify the dev server is running without errors
3. Check browser console and server logs for error messages
4. Make sure your `.env.local` file has correct Supabase credentials
