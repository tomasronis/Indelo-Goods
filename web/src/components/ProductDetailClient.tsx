'use client'

import { Product, ProducerProfile } from '@/lib/supabase'
import Image from 'next/image'
import { useState, useEffect } from 'react'
import Link from 'next/link'
import { loadStripe, StripeElementsOptions } from '@stripe/stripe-js'
import {
  Elements,
  PaymentElement,
  useStripe,
  useElements
} from '@stripe/react-stripe-js'

// Initialize Stripe
const stripePromise = loadStripe(process.env.NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY!)

interface Props {
  product: Product
  producerProfile: ProducerProfile | null
}

export default function ProductDetailClient({ product, producerProfile }: Props) {
  const [showCheckout, setShowCheckout] = useState(false)
  const [quantity, setQuantity] = useState(1)

  const handleOpenInApp = () => {
    // Try to open in app using deep link
    const deepLink = `indelogoods://product/${product.id}`
    window.location.href = deepLink

    // Fallback to app store after a delay if app doesn't open
    setTimeout(() => {
      // TODO: Add actual Play Store link when available
      alert('Download the Indelo Goods app to purchase this product!')
    }, 1500)
  }

  const handlePurchase = () => {
    setShowCheckout(true)
  }

  const formatPrice = (price: number, currency: string = 'USD') => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency,
    }).format(price)
  }

  const certifications = [
    { key: 'is_organic', label: 'Organic', class: 'badge-organic' },
    { key: 'is_vegan', label: 'Vegan', class: 'badge-vegan' },
    { key: 'is_gluten_free', label: 'Gluten Free', class: 'badge-gluten-free' },
    { key: 'is_non_gmo', label: 'Non-GMO', class: 'badge-non-gmo' },
    { key: 'is_kosher', label: 'Kosher', class: 'badge-kosher' },
  ]

  const activeCertifications = certifications.filter(
    cert => product[cert.key as keyof Product]
  )

  return (
    <main className="min-h-screen p-4 md:p-8">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <a href="/" className="text-ketchup hover:underline inline-flex items-center gap-2">
            ← Back to Home
          </a>
        </div>

        {/* Product Card */}
        <div className="pixel-border bg-white p-6 md:p-8">
          <div className="grid md:grid-cols-2 gap-8">
            {/* Product Image */}
            <div className="relative aspect-square bg-bun rounded-lg overflow-hidden">
              {product.image_url ? (
                <Image
                  src={product.image_url}
                  alt={product.name}
                  fill
                  className="object-contain"
                  priority
                />
              ) : (
                <div className="w-full h-full flex items-center justify-center text-6xl">
                  🥫
                </div>
              )}
            </div>

            {/* Product Info */}
            <div className="flex flex-col">
              {/* Producer Link */}
              {producerProfile && (
                <Link
                  href={`/producer/${product.producer_id}`}
                  className="text-sm text-ketchup hover:underline mb-2 font-medium inline-flex items-center gap-1"
                >
                  By {producerProfile.company_name || producerProfile.brand_name || 'Producer'}
                  <span>→</span>
                </Link>
              )}

              {product.brand && (
                <p className="text-sm text-charcoal/60 mb-2">{product.brand}</p>
              )}

              <h1 className="text-3xl md:text-4xl font-bold mb-4 text-charcoal">
                {product.name}
              </h1>

              {product.short_description && (
                <p className="text-lg mb-4 text-charcoal/80">
                  {product.short_description}
                </p>
              )}

              {/* Certifications */}
              {activeCertifications.length > 0 && (
                <div className="flex flex-wrap gap-2 mb-6">
                  {activeCertifications.map(cert => (
                    <span key={cert.key} className={`badge ${cert.class}`}>
                      {cert.label}
                    </span>
                  ))}
                </div>
              )}

              {/* Pricing */}
              <div className="mb-6">
                {product.retail_price && (
                  <div className="text-3xl font-bold text-ketchup mb-2">
                    {formatPrice(product.retail_price, product.currency)}
                  </div>
                )}
                {product.volume_ml && (
                  <p className="text-sm text-charcoal/60">
                    {product.volume_ml}ml
                  </p>
                )}
              </div>

              {/* CTA Buttons */}
              <button
                onClick={handlePurchase}
                className="pixel-button w-full mb-3"
                style={{ backgroundColor: '#E63946' }}
              >
                Purchase Here - {formatPrice(product.retail_price || product.wholesale_price, product.currency)}
              </button>

              <button
                onClick={handleOpenInApp}
                className="w-full mb-4 px-6 py-3 border-2 border-charcoal bg-white hover:bg-bun transition-colors font-bold"
              >
                Open in App
              </button>

              <p className="text-xs text-center text-charcoal/60">
                Available at participating shops
              </p>
            </div>
          </div>

          {/* Description */}
          {product.description && (
            <div className="mt-8 pt-8 border-t-4 border-charcoal">
              <h2 className="text-2xl font-bold mb-4">About This Product</h2>
              <p className="text-charcoal/80 leading-relaxed whitespace-pre-wrap">
                {product.description}
              </p>
            </div>
          )}

          {/* Ingredients & Nutrition */}
          {(product.ingredients || product.allergens) && (
            <div className="mt-8 pt-8 border-t-4 border-charcoal">
              <h2 className="text-2xl font-bold mb-4">Ingredients & Allergens</h2>

              {product.ingredients && (
                <div className="mb-4">
                  <h3 className="font-bold mb-2">Ingredients:</h3>
                  <p className="text-charcoal/80">{product.ingredients}</p>
                </div>
              )}

              {product.allergens && (
                <div className="mb-4">
                  <h3 className="font-bold mb-2">Allergens:</h3>
                  <p className="text-charcoal/80">{product.allergens}</p>
                </div>
              )}

              {product.serving_size && (
                <div className="grid grid-cols-2 gap-4 mt-4">
                  <div>
                    <p className="text-sm text-charcoal/60">Serving Size</p>
                    <p className="font-bold">{product.serving_size}</p>
                  </div>
                  {product.servings_per_container && (
                    <div>
                      <p className="text-sm text-charcoal/60">Servings</p>
                      <p className="font-bold">{product.servings_per_container}</p>
                    </div>
                  )}
                </div>
              )}
            </div>
          )}

          {/* Additional Info */}
          <div className="mt-8 pt-8 border-t-4 border-charcoal grid md:grid-cols-2 gap-4 text-sm">
            {product.country_of_origin && (
              <div>
                <p className="text-charcoal/60">Country of Origin</p>
                <p className="font-bold">{product.country_of_origin}</p>
              </div>
            )}
            {product.sku && (
              <div>
                <p className="text-charcoal/60">SKU</p>
                <p className="font-bold">{product.sku}</p>
              </div>
            )}
            {product.shelf_life_days && (
              <div>
                <p className="text-charcoal/60">Shelf Life</p>
                <p className="font-bold">{product.shelf_life_days} days</p>
              </div>
            )}
            {product.storage_instructions && (
              <div>
                <p className="text-charcoal/60">Storage</p>
                <p className="font-bold">{product.storage_instructions}</p>
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="mt-8 text-center text-sm text-charcoal/60">
          <p>
            Part of the Indelo Goods network • Supporting local producers
          </p>
        </div>
      </div>

      {/* Checkout Modal */}
      {showCheckout && (
        <CheckoutModal
          product={product}
          quantity={quantity}
          onQuantityChange={setQuantity}
          onClose={() => setShowCheckout(false)}
          producerProfile={producerProfile}
        />
      )}
    </main>
  )
}

interface CheckoutModalProps {
  product: Product
  quantity: number
  onQuantityChange: (quantity: number) => void
  onClose: () => void
  producerProfile: ProducerProfile | null
}

function CheckoutModal({ product, quantity, onQuantityChange, onClose, producerProfile }: CheckoutModalProps) {
  const [email, setEmail] = useState('')
  const [clientSecret, setClientSecret] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  const unitPrice = product.retail_price || product.wholesale_price
  const total = unitPrice * quantity

  const formatPrice = (price: number, currency: string = 'USD') => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency,
    }).format(price)
  }

  // Create PaymentIntent when component mounts or quantity changes
  useEffect(() => {
    if (email && email.includes('@')) {
      createPaymentIntent()
    }
  }, [quantity])

  const createPaymentIntent = async () => {
    if (!email || !email.includes('@')) {
      return
    }

    setIsLoading(true)

    try {
      const response = await fetch('/api/create-payment-intent', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          productId: product.id,
          quantity,
          email,
          shopId: 'direct',
        }),
      })

      const data = await response.json()

      if (!response.ok || data.error) {
        throw new Error(data.error || 'Failed to initialize payment')
      }

      setClientSecret(data.clientSecret)
    } catch (error: any) {
      console.error('Payment intent error:', error)
      alert('Error: ' + error.message)
    } finally {
      setIsLoading(false)
    }
  }

  const handleEmailSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    createPaymentIntent()
  }

  const appearance: any = {
    theme: 'stripe',
    variables: {
      colorPrimary: '#E63946',
      colorBackground: '#ffffff',
      colorText: '#2B2D42',
      colorDanger: '#E63946',
      fontFamily: 'system-ui, sans-serif',
      borderRadius: '4px',
    },
  }

  const options: StripeElementsOptions = {
    clientSecret,
    appearance,
  }


  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50" onClick={onClose}>
      <div className="pixel-border bg-white max-w-md w-full p-6 max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
        <div className="flex justify-between items-start mb-4">
          <h2 className="text-2xl font-bold text-charcoal">Checkout</h2>
          <button
            onClick={onClose}
            className="text-charcoal hover:text-ketchup text-2xl leading-none"
          >
            ×
          </button>
        </div>

        {/* Product Summary */}
        <div className="mb-6">
          <div className="flex gap-4 mb-4">
            {product.image_url ? (
              <div className="w-20 h-20 relative bg-bun rounded overflow-hidden flex-shrink-0">
                <Image
                  src={product.image_url}
                  alt={product.name}
                  fill
                  className="object-contain"
                />
              </div>
            ) : (
              <div className="w-20 h-20 bg-bun rounded flex items-center justify-center text-3xl flex-shrink-0">
                🥫
              </div>
            )}
            <div className="flex-1">
              <h3 className="font-bold text-charcoal">{product.name}</h3>
              {producerProfile && (
                <p className="text-sm text-charcoal/60">
                  {producerProfile.company_name || producerProfile.brand_name}
                </p>
              )}
              <p className="text-sm text-charcoal/60">
                {formatPrice(unitPrice, product.currency)} each
              </p>
            </div>
          </div>

          {/* Quantity Selector */}
          <div className="flex items-center gap-4">
            <label className="font-bold text-charcoal">Quantity:</label>
            <div className="flex items-center gap-2">
              <button
                onClick={() => onQuantityChange(Math.max(1, quantity - 1))}
                className="w-8 h-8 border-2 border-charcoal bg-bun hover:bg-mustard font-bold"
                disabled={!!clientSecret}
              >
                −
              </button>
              <span className="w-12 text-center font-bold">{quantity}</span>
              <button
                onClick={() => onQuantityChange(quantity + 1)}
                className="w-8 h-8 border-2 border-charcoal bg-bun hover:bg-mustard font-bold"
                disabled={!!clientSecret}
              >
                +
              </button>
            </div>
          </div>
        </div>

        {/* Email Input */}
        {!clientSecret ? (
          <form onSubmit={handleEmailSubmit} className="mb-6">
            <label className="block font-bold text-charcoal mb-2">
              Email Address
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="your@email.com"
              className="w-full px-4 py-2 border-2 border-charcoal focus:border-mustard outline-none mb-4"
              required
            />
            <button
              type="submit"
              disabled={isLoading || !email}
              className="pixel-button w-full"
              style={{
                backgroundColor: isLoading || !email ? '#ccc' : '#F4C430',
                cursor: isLoading || !email ? 'not-allowed' : 'pointer',
                color: '#2B2D42'
              }}
            >
              {isLoading ? 'Loading...' : 'Continue to Payment'}
            </button>
          </form>
        ) : (
          <>
            {/* Total */}
            <div className="border-t-2 border-charcoal pt-4 mb-6">
              <div className="flex justify-between items-center text-lg mb-2">
                <span className="font-bold">Total:</span>
                <span className="font-bold text-ketchup text-2xl">
                  {formatPrice(total, product.currency)}
                </span>
              </div>
              <p className="text-xs text-charcoal/60">
                Includes 90% to producer, 4.5% to shop, 5.5% platform fee
              </p>
            </div>

            {/* Stripe Payment Form */}
            <Elements stripe={stripePromise} options={options}>
              <PaymentForm
                product={product}
                email={email}
                onSuccess={onClose}
              />
            </Elements>
          </>
        )}

        <p className="text-xs text-center text-charcoal/60 mt-4">
          🔒 Powered by Stripe • Secure checkout
        </p>
      </div>
    </div>
  )
}

// Separate component for payment form (must be inside Elements provider)
function PaymentForm({
  product,
  email,
  onSuccess
}: {
  product: Product
  email: string
  onSuccess: () => void
}) {
  const stripe = useStripe()
  const elements = useElements()
  const [isProcessing, setIsProcessing] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!stripe || !elements) {
      return
    }

    setIsProcessing(true)
    setErrorMessage('')

    try {
      const { error, paymentIntent } = await stripe.confirmPayment({
        elements,
        confirmParams: {
          return_url: `${window.location.origin}/success`,
          receipt_email: email,
        },
        redirect: 'if_required',
      })

      if (error) {
        setErrorMessage(error.message || 'Payment failed')
        setIsProcessing(false)
      } else if (paymentIntent && paymentIntent.status === 'succeeded') {
        // Payment successful!
        window.location.href = `/success?session_id=${paymentIntent.id}`
      }
    } catch (err: any) {
      setErrorMessage(err.message || 'An error occurred')
      setIsProcessing(false)
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <PaymentElement />

      {errorMessage && (
        <div className="mt-4 p-3 bg-red-100 border-2 border-red-500 text-red-700 text-sm">
          {errorMessage}
        </div>
      )}

      <button
        type="submit"
        disabled={!stripe || isProcessing}
        className="pixel-button w-full mt-6"
        style={{
          backgroundColor: !stripe || isProcessing ? '#ccc' : '#E63946',
          cursor: !stripe || isProcessing ? 'not-allowed' : 'pointer'
        }}
      >
        {isProcessing ? 'Processing...' : 'Pay Now'}
      </button>
    </form>
  )
}
