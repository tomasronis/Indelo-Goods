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
      // Payment was successful
      setLoading(false)
    } else {
      setLoading(false)
    }
  }, [sessionId])

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-bun">
        <div className="text-center">
          <div className="text-6xl mb-4 animate-bounce">⏳</div>
          <p className="text-charcoal font-bold">Processing your order...</p>
        </div>
      </div>
    )
  }

  if (!sessionId) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-bun p-4">
        <div className="pixel-border bg-white max-w-md w-full p-8 text-center">
          <div className="text-6xl mb-4">❌</div>
          <h1 className="text-3xl font-bold mb-4 text-charcoal">Something went wrong</h1>
          <p className="text-lg mb-6 text-charcoal/80">
            We couldn't find your order. Please try again or contact support.
          </p>
          <Link href="/" className="pixel-button block">
            Back to Home
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-bun p-4">
      <div className="pixel-border bg-white max-w-md w-full p-8 text-center">
        <div className="text-6xl mb-4">🎉</div>
        <h1 className="text-3xl font-bold mb-4 text-charcoal">Thank You!</h1>
        <p className="text-lg mb-6 text-charcoal/80">
          Your order has been confirmed. You'll receive an email confirmation shortly.
        </p>

        <div className="bg-bun p-4 rounded mb-6">
          <p className="text-sm text-charcoal/60 mb-2">Order ID</p>
          <p className="text-xs font-mono text-charcoal break-all">
            {sessionId}
          </p>
        </div>

        <div className="space-y-3">
          <Link href="/" className="pixel-button block">
            Back to Home
          </Link>
          <p className="text-xs text-charcoal/60">
            Questions? Contact support@indelogoods.com
          </p>
        </div>

        <div className="mt-8 pt-6 border-t-2 border-charcoal/20">
          <p className="text-sm text-charcoal/60">
            🌭 Thank you for supporting local producers!
          </p>
        </div>
      </div>
    </div>
  )
}
