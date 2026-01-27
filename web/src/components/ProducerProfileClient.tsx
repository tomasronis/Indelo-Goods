'use client'

import { ProducerProfile } from '@/lib/supabase'
import { useEffect, useState } from 'react'
import { createClient } from '@supabase/supabase-js'
import Image from 'next/image'
import Link from 'next/link'

interface Props {
  profile: ProducerProfile
  producerId: string
}

interface Product {
  id: string
  name: string
  image_url?: string
  retail_price?: number
  wholesale_price: number
  currency: string
  short_description?: string
}

const supabase = createClient(
  process.env.NEXT_PUBLIC_SUPABASE_URL!,
  process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
)

export default function ProducerProfileClient({ profile, producerId }: Props) {
  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadProducts()
  }, [producerId])

  const loadProducts = async () => {
    try {
      const { data, error } = await supabase
        .from('products')
        .select('id, name, image_url, retail_price, wholesale_price, currency, short_description')
        .eq('producer_id', producerId)
        .limit(12)

      if (error) {
        console.error('Error loading products:', error)
      } else {
        setProducts(data || [])
      }
    } catch (error) {
      console.error('Error:', error)
    } finally {
      setLoading(false)
    }
  }

  const formatPrice = (price: number, currency: string = 'USD') => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency,
    }).format(price)
  }

  return (
    <main className="min-h-screen bg-bun">
      {/* Header */}
      <div className="bg-mustard py-4 px-4 md:px-8">
        <div className="max-w-6xl mx-auto">
          <Link href="/" className="text-charcoal hover:underline inline-flex items-center gap-2 mb-4">
            ← Back to Home
          </Link>
        </div>
      </div>

      {/* Producer Info */}
      <div className="max-w-6xl mx-auto p-4 md:p-8">
        <div className="pixel-border bg-white p-6 md:p-8 mb-8">
          <div className="flex flex-col md:flex-row gap-6 mb-6">
            {/* Logo */}
            <div className="flex-shrink-0">
              {profile.logo_url ? (
                <div className="w-32 h-32 relative">
                  <Image
                    src={profile.logo_url}
                    alt={profile.company_name || 'Producer logo'}
                    fill
                    className="object-contain"
                  />
                </div>
              ) : (
                <div className="w-32 h-32 bg-mustard/30 rounded flex items-center justify-center">
                  <span className="text-6xl">🌭</span>
                </div>
              )}
            </div>

            {/* Basic Info */}
            <div className="flex-1">
              <h1 className="text-4xl font-bold text-charcoal mb-2">
                {profile.company_name || profile.brand_name || 'Producer'}
              </h1>

              {profile.brand_name && profile.company_name && profile.brand_name !== profile.company_name && (
                <p className="text-xl text-charcoal/70 mb-3">{profile.brand_name}</p>
              )}

              <div className="flex flex-wrap gap-4 text-sm text-charcoal/60 mb-4">
                {profile.location && (
                  <span className="flex items-center gap-1">
                    📍 {profile.location}
                  </span>
                )}
                {profile.founded_year && (
                  <span className="flex items-center gap-1">
                    📅 Founded {profile.founded_year}
                  </span>
                )}
                {profile.specialty && (
                  <span className="flex items-center gap-1">
                    ⭐ {profile.specialty}
                  </span>
                )}
              </div>

              {profile.website_url && (
                <a
                  href={profile.website_url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-ketchup hover:underline inline-flex items-center gap-1"
                >
                  🌐 Visit Website →
                </a>
              )}
            </div>
          </div>

          {/* Bio */}
          {profile.bio && (
            <div className="mb-6">
              <h2 className="text-2xl font-bold text-charcoal mb-3">About</h2>
              <p className="text-charcoal/80 leading-relaxed whitespace-pre-wrap">
                {profile.bio}
              </p>
            </div>
          )}

          {/* Story Sections */}
          <div className="grid md:grid-cols-3 gap-6">
            {profile.background && (
              <div>
                <h3 className="text-lg font-bold text-ketchup mb-2">Background</h3>
                <p className="text-charcoal/70 text-sm leading-relaxed whitespace-pre-wrap">
                  {profile.background}
                </p>
              </div>
            )}

            {profile.inspiration && (
              <div>
                <h3 className="text-lg font-bold text-ketchup mb-2">Inspiration</h3>
                <p className="text-charcoal/70 text-sm leading-relaxed whitespace-pre-wrap">
                  {profile.inspiration}
                </p>
              </div>
            )}

            {profile.goals && (
              <div>
                <h3 className="text-lg font-bold text-ketchup mb-2">Goals</h3>
                <p className="text-charcoal/70 text-sm leading-relaxed whitespace-pre-wrap">
                  {profile.goals}
                </p>
              </div>
            )}
          </div>

          {/* Certifications */}
          {profile.certifications && profile.certifications.length > 0 && (
            <div className="mt-6 pt-6 border-t-2 border-charcoal/20">
              <h3 className="text-lg font-bold text-charcoal mb-3">Certifications</h3>
              <div className="flex flex-wrap gap-2">
                {profile.certifications.map((cert, index) => (
                  <span
                    key={index}
                    className="px-3 py-1 bg-mustard/30 text-charcoal text-sm font-medium rounded"
                  >
                    {cert}
                  </span>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Products Section */}
        <div>
          <h2 className="text-3xl font-bold text-charcoal mb-6">Our Products</h2>

          {loading ? (
            <div className="text-center py-12">
              <div className="text-6xl mb-4 animate-bounce">🌭</div>
              <p className="text-charcoal/60">Loading products...</p>
            </div>
          ) : products.length === 0 ? (
            <div className="pixel-border bg-white p-12 text-center">
              <div className="text-6xl mb-4">🥫</div>
              <p className="text-xl text-charcoal/60">No products yet</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {products.map((product) => (
                <Link
                  key={product.id}
                  href={`/product/${product.id}`}
                  className="pixel-border bg-white hover:shadow-lg transition-shadow"
                >
                  <div className="aspect-square bg-bun relative">
                    {product.image_url ? (
                      <Image
                        src={product.image_url}
                        alt={product.name}
                        fill
                        className="object-contain p-4"
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center text-6xl">
                        🥫
                      </div>
                    )}
                  </div>
                  <div className="p-4">
                    <h3 className="font-bold text-charcoal mb-2 line-clamp-2">
                      {product.name}
                    </h3>
                    {product.short_description && (
                      <p className="text-sm text-charcoal/60 mb-3 line-clamp-2">
                        {product.short_description}
                      </p>
                    )}
                    <p className="text-lg font-bold text-ketchup">
                      {formatPrice(
                        product.retail_price || product.wholesale_price,
                        product.currency
                      )}
                    </p>
                  </div>
                </Link>
              ))}
            </div>
          )}
        </div>
      </div>
    </main>
  )
}
