'use client'

import { useEffect, useState } from 'react'
import Link from 'next/link'
import { createClient } from '@supabase/supabase-js'

interface Shop {
  id: string
  name: string
  description?: string
  address?: string
  city?: string
  state?: string
  business_type?: string
}

export default function ShopsPage() {
  const [shops, setShops] = useState<Shop[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function fetchShops() {
      const supabase = createClient(
        process.env.NEXT_PUBLIC_SUPABASE_URL!,
        process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
      )

      const { data, error } = await supabase
        .from('shops')
        .select('*')
        .order('created_at', { ascending: false })
        .limit(50)

      if (error) {
        console.error('Error fetching shops:', error)
        setLoading(false)
        return
      }

      setShops(data || [])
      setLoading(false)
    }

    fetchShops()
  }, [])

  if (loading) {
    return (
      <div className="min-h-screen bg-bun flex items-center justify-center">
        <div className="text-center">
          <div className="text-6xl mb-4 animate-bounce">🌭</div>
          <p className="text-charcoal font-bold">Loading shops...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-bun p-8">
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="text-center mb-12">
          <Link href="/" className="inline-block mb-6 text-ketchup hover:underline">
            ← Back to Home
          </Link>
          <h1 className="text-4xl font-bold mb-4 text-charcoal">Find Shops</h1>
          <p className="text-xl text-charcoal/70">
            Discover local shops carrying Indelo Goods products
          </p>
        </div>

        {/* Shops Grid */}
        {shops.length === 0 ? (
          <div className="pixel-border bg-white p-12 text-center">
            <div className="text-6xl mb-4">🏪</div>
            <h2 className="text-2xl font-bold mb-2 text-charcoal">No Shops Yet</h2>
            <p className="text-charcoal/70 mb-6">
              Check back soon! Shops are joining the platform.
            </p>
            <Link href="/" className="pixel-button inline-block">
              Back to Home
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {shops.map((shop) => (
              <div
                key={shop.id}
                className="pixel-border bg-white p-6 hover:shadow-lg transition-shadow"
              >
                {/* Shop Icon */}
                <div className="text-4xl mb-4">🏪</div>

                {/* Shop Info */}
                <h3 className="text-xl font-bold mb-2 text-charcoal">
                  {shop.name}
                </h3>

                {shop.business_type && (
                  <p className="text-sm text-mustard font-bold mb-2">
                    {shop.business_type}
                  </p>
                )}

                {shop.description && (
                  <p className="text-sm text-charcoal/70 mb-4 line-clamp-3">
                    {shop.description}
                  </p>
                )}

                {/* Location */}
                {(shop.address || shop.city || shop.state) && (
                  <div className="text-sm text-charcoal/60 mb-4">
                    <div className="flex items-start gap-2">
                      <span>📍</span>
                      <div>
                        {shop.address && <div>{shop.address}</div>}
                        {(shop.city || shop.state) && (
                          <div>
                            {shop.city}
                            {shop.city && shop.state && ', '}
                            {shop.state}
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                )}

                {/* View Button */}
                <button className="w-full py-2 px-4 bg-ketchup text-white font-bold hover:bg-ketchup/90 transition-colors">
                  View Shop
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
