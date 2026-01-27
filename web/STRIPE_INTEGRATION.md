# Stripe Integration Guide - Indelo Goods

This guide walks you through integrating Stripe payments into your Indelo Goods platform.

## Overview

Your business model requires:
- **Shopper purchases** → Pay via Stripe
- **Payment splits:**
  - 90% → Producer
  - 4.5% → Shop (where QR code was scanned)
  - 5.5% → Indelo Goods (platform)

For this, you'll use:
- **Stripe Checkout** - For shopper purchases on web
- **Stripe Connect** - For splitting payments between Producer, Shop, and Platform

---

## Step 1: Create Stripe Account

1. Go to [stripe.com](https://stripe.com)
2. Click **Start now** (or **Sign in** if you have an account)
3. Fill in your business information
4. Verify your email address

---

## Step 2: Get Your API Keys

### Development (Test) Keys

1. Log into [Stripe Dashboard](https://dashboard.stripe.com)
2. Make sure you're in **Test mode** (toggle in the top-right)
3. Go to **Developers** → **API keys**
4. You'll see two keys:
   - **Publishable key** (starts with `pk_test_`)
   - **Secret key** (starts with `sk_test_`)

### Add to Environment Variables

**Web App (`web/.env.local`):**
```env
NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY=pk_test_your_publishable_key_here
STRIPE_SECRET_KEY=sk_test_your_secret_key_here
```

**Important:**
- `NEXT_PUBLIC_` prefix makes the key available in browser (publishable key only!)
- Secret key should NEVER be exposed to the browser
- Keep both keys in `.env.local` (already gitignored)

---

## Step 3: Install Stripe Dependencies

### For Web App

```bash
cd web
npm install @stripe/stripe-js stripe
```

- `@stripe/stripe-js` - Client-side Stripe.js library (for browser)
- `stripe` - Server-side Stripe SDK (for API routes)

### For Android App (Optional - for in-app purchases)

Add to `app/build.gradle.kts`:
```kotlin
dependencies {
    implementation("com.stripe:stripe-android:20.37.5")
}
```

---

## Step 4: Create Checkout API Route

Create server-side API route to create Stripe Checkout sessions.

**Create file:** `web/src/app/api/create-checkout-session/route.ts`

```typescript
import { NextRequest, NextResponse } from 'next/server'
import Stripe from 'stripe'
import { createClient } from '@supabase/supabase-js'

const stripe = new Stripe(process.env.STRIPE_SECRET_KEY!, {
  apiVersion: '2024-11-20.acacia',
})

const supabase = createClient(
  process.env.NEXT_PUBLIC_SUPABASE_URL!,
  process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
)

export async function POST(req: NextRequest) {
  try {
    const { productId, quantity, email, shopId } = await req.json()

    // Fetch product from Supabase
    const { data: product, error } = await supabase
      .from('products')
      .select('*')
      .eq('id', productId)
      .single()

    if (error || !product) {
      return NextResponse.json(
        { error: 'Product not found' },
        { status: 404 }
      )
    }

    const unitPrice = product.retail_price || product.wholesale_price
    const totalAmount = unitPrice * quantity

    // Create Checkout Session
    const session = await stripe.checkout.sessions.create({
      payment_method_types: ['card'],
      line_items: [
        {
          price_data: {
            currency: product.currency || 'usd',
            product_data: {
              name: product.name,
              description: product.short_description || product.description,
              images: product.image_url ? [product.image_url] : [],
            },
            unit_amount: Math.round(unitPrice * 100), // Stripe uses cents
          },
          quantity,
        },
      ],
      mode: 'payment',
      success_url: `${req.headers.get('origin')}/success?session_id={CHECKOUT_SESSION_ID}`,
      cancel_url: `${req.headers.get('origin')}/product/${productId}`,
      customer_email: email,
      metadata: {
        productId,
        producerId: product.producer_id,
        shopId: shopId || 'unknown',
        quantity: quantity.toString(),
      },
    })

    return NextResponse.json({ sessionId: session.id })
  } catch (error: any) {
    console.error('Stripe checkout error:', error)
    return NextResponse.json(
      { error: error.message },
      { status: 500 }
    )
  }
}
```

---

## Step 5: Update Checkout Modal

Update `web/src/components/ProductDetailClient.tsx`:

```typescript
import { loadStripe } from '@stripe/stripe-js'

// At the top of the file, outside the component
const stripePromise = loadStripe(process.env.NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY!)

// Inside CheckoutModal component, update handleCheckout:
const handleCheckout = async () => {
  setIsProcessing(true)

  try {
    // Create checkout session
    const response = await fetch('/api/create-checkout-session', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        productId: product.id,
        quantity,
        email,
        shopId: 'shop-id-from-qr-scan', // TODO: Get from QR scan context
      }),
    })

    const { sessionId, error } = await response.json()

    if (error) {
      alert('Error: ' + error)
      setIsProcessing(false)
      return
    }

    // Redirect to Stripe Checkout
    const stripe = await stripePromise
    if (stripe) {
      const { error: stripeError } = await stripe.redirectToCheckout({ sessionId })

      if (stripeError) {
        alert('Payment error: ' + stripeError.message)
        setIsProcessing(false)
      }
    }
  } catch (error: any) {
    alert('Error: ' + error.message)
    setIsProcessing(false)
  }
}
```

---

## Step 6: Create Success Page

**Create file:** `web/src/app/success/page.tsx`

```typescript
'use client'

import { useEffect, useState } from 'react'
import { useSearchParams } from 'next/navigation'
import Link from 'next/link'

export default function SuccessPage() {
  const searchParams = useSearchParams()
  const sessionId = searchParams.get('session_id')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (sessionId) {
      // Optional: Verify the session with your backend
      setLoading(false)
    }
  }, [sessionId])

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="text-6xl mb-4">⏳</div>
          <p>Processing your order...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <div className="pixel-border bg-white max-w-md w-full p-8 text-center">
        <div className="text-6xl mb-4">🎉</div>
        <h1 className="text-3xl font-bold mb-4 text-charcoal">Thank You!</h1>
        <p className="text-lg mb-6 text-charcoal/80">
          Your order has been confirmed. You'll receive an email confirmation shortly.
        </p>
        <div className="space-y-3">
          <Link href="/" className="pixel-button block">
            Back to Home
          </Link>
          <p className="text-sm text-charcoal/60">
            Order ID: {sessionId?.slice(0, 20)}...
          </p>
        </div>
      </div>
    </div>
  )
}
```

---

## Step 7: Set Up Webhooks (Payment Confirmation)

Webhooks let Stripe notify your server when payments succeed.

### Create Webhook Handler

**Create file:** `web/src/app/api/webhooks/stripe/route.ts`

```typescript
import { NextRequest, NextResponse } from 'next/server'
import Stripe from 'stripe'
import { createClient } from '@supabase/supabase-js'

const stripe = new Stripe(process.env.STRIPE_SECRET_KEY!, {
  apiVersion: '2024-11-20.acacia',
})

const webhookSecret = process.env.STRIPE_WEBHOOK_SECRET!

const supabase = createClient(
  process.env.NEXT_PUBLIC_SUPABASE_URL!,
  process.env.SUPABASE_SERVICE_ROLE_KEY! // Use service role for admin operations
)

export async function POST(req: NextRequest) {
  const body = await req.text()
  const signature = req.headers.get('stripe-signature')!

  let event: Stripe.Event

  try {
    event = stripe.webhooks.constructEvent(body, signature, webhookSecret)
  } catch (err: any) {
    console.error('Webhook signature verification failed:', err.message)
    return NextResponse.json({ error: err.message }, { status: 400 })
  }

  // Handle the event
  switch (event.type) {
    case 'checkout.session.completed':
      const session = event.data.object as Stripe.Checkout.Session

      // Create order in database
      const metadata = session.metadata!

      // TODO: Create order record in Supabase
      // TODO: Send confirmation email
      // TODO: Notify producer of new order

      console.log('Payment succeeded:', session.id)
      console.log('Metadata:', metadata)

      break

    case 'payment_intent.succeeded':
      const paymentIntent = event.data.object as Stripe.PaymentIntent
      console.log('PaymentIntent succeeded:', paymentIntent.id)
      break

    default:
      console.log(`Unhandled event type: ${event.type}`)
  }

  return NextResponse.json({ received: true })
}
```

### Configure Webhook in Stripe Dashboard

1. Go to **Developers** → **Webhooks**
2. Click **Add endpoint**
3. Endpoint URL: `https://your-domain.com/api/webhooks/stripe`
   - For local testing: Use [Stripe CLI](https://stripe.com/docs/stripe-cli) or [ngrok](https://ngrok.com)
4. Select events to listen for:
   - `checkout.session.completed`
   - `payment_intent.succeeded`
5. Click **Add endpoint**
6. Copy the **Signing secret** (starts with `whsec_`)
7. Add to `.env.local`:
   ```env
   STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret_here
   ```

---

## Step 8: Implement Payment Splits with Stripe Connect

For your business model (90% producer, 4.5% shop, 5.5% platform), you need **Stripe Connect**.

### Overview of Stripe Connect

- **Platform Account** (Indelo Goods) - Your main Stripe account
- **Connected Accounts** (Producers & Shops) - Sub-accounts that receive payouts

### Implementation Steps

1. **Enable Stripe Connect**
   - Go to Stripe Dashboard → **Settings** → **Connect**
   - Click **Get started**
   - Choose **Platform or marketplace** type

2. **Onboard Connected Accounts**
   - Producers and Shops need to create Stripe Connect accounts
   - Use [Stripe Connect Onboarding](https://stripe.com/docs/connect/onboarding)

3. **Update Checkout to Use Connect**

```typescript
// In create-checkout-session/route.ts
const session = await stripe.checkout.sessions.create({
  // ... existing config
  payment_intent_data: {
    application_fee_amount: Math.round(totalAmount * 100 * 0.055), // 5.5% platform fee
    transfer_data: {
      destination: producerStripeAccountId, // Producer gets 90%
    },
  },
})

// After payment succeeds, transfer to shop
await stripe.transfers.create({
  amount: Math.round(totalAmount * 100 * 0.045), // 4.5% to shop
  currency: 'usd',
  destination: shopStripeAccountId,
  transfer_group: session.id,
})
```

---

## Step 9: Test the Integration

### Test Mode

1. Use test credit cards:
   - Success: `4242 4242 4242 4242`
   - Decline: `4000 0000 0000 0002`
   - Any future expiry date, any CVC

2. Test the flow:
   - Visit product page
   - Click "Purchase Here"
   - Enter email and test card
   - Complete checkout
   - Should redirect to success page

### Monitor in Stripe Dashboard

- Go to **Payments** to see test transactions
- Check **Logs** for API calls
- View **Webhooks** for webhook deliveries

---

## Step 10: Go Live

When ready for production:

1. **Switch to Live Mode**
   - Toggle from Test to Live mode in Stripe Dashboard
   - Get live API keys (start with `pk_live_` and `sk_live_`)

2. **Update Environment Variables**
   ```env
   NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY=pk_live_...
   STRIPE_SECRET_KEY=sk_live_...
   ```

3. **Activate Your Account**
   - Complete business verification in Stripe Dashboard
   - Provide business details, tax info, bank account

4. **Update Webhook Endpoint**
   - Point to production URL
   - Update signing secret

---

## Pricing & Fees

**Stripe Charges:**
- 2.9% + $0.30 per successful card charge (US)
- No setup fees, monthly fees, or hidden costs

**Your Platform Fees:**
- You keep 5.5% of each transaction
- Minus Stripe's 2.9% + $0.30
- Net platform revenue ≈ 2.6% per transaction

**Example Transaction ($10 product):**
- Shopper pays: $10.00
- Stripe fee: $0.59 (2.9% + $0.30)
- Producer gets: $9.00 (90%)
- Shop gets: $0.45 (4.5%)
- Platform gets: $0.55 (5.5%)
- Platform net: -$0.04 (need to account for Stripe fees)

**Important:** Adjust your platform fee or pricing to account for Stripe fees.

---

## Troubleshooting

### Common Errors

**"No such customer"**
- Make sure you're using the correct API keys for the environment (test vs live)

**"Webhook signature verification failed"**
- Check that `STRIPE_WEBHOOK_SECRET` is correct
- Ensure you're using raw body (not parsed JSON)

**"Invalid API key"**
- Verify keys in `.env.local`
- Restart dev server after changing env vars

### Testing Webhooks Locally

Use Stripe CLI:
```bash
# Install Stripe CLI
# Download from https://stripe.com/docs/stripe-cli

# Login
stripe login

# Forward webhooks to local server
stripe listen --forward-to localhost:3000/api/webhooks/stripe

# Test a webhook
stripe trigger checkout.session.completed
```

---

## Next Steps

1. ✅ Set up Stripe account
2. ✅ Install dependencies
3. ✅ Create checkout API route
4. ✅ Update checkout modal
5. ✅ Create success page
6. ✅ Set up webhooks
7. ⏳ Test with test cards
8. ⏳ Implement Stripe Connect for payment splits
9. ⏳ Add producer/shop onboarding flow
10. ⏳ Go live!

---

## Resources

- [Stripe Checkout Documentation](https://stripe.com/docs/checkout)
- [Stripe Connect Guide](https://stripe.com/docs/connect)
- [Stripe API Reference](https://stripe.com/docs/api)
- [Next.js + Stripe Example](https://github.com/vercel/next.js/tree/canary/examples/with-stripe-typescript)
- [Stripe Testing](https://stripe.com/docs/testing)

---

## Support

If you need help:
- [Stripe Support](https://support.stripe.com/)
- [Stripe Discord Community](https://discord.gg/stripe)
- Check Stripe Dashboard logs for detailed error messages
