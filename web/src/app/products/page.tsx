'use client'

import { useEffect, useState } from 'react'
import Link from 'next/link'
import { createClient } from '@supabase/supabase-js'

interface Product {
  id: string
  name: string
  brand?: string
  short_description?: string
  retail_price?: number
  wholesale_price: number
  currency: string
  image_url?: string
  is_organic: boolean
  is_vegan: boolean
  is_gluten_free: boolean
}

export default function ProductsPage() {
  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function fetchProducts() {
      const supabase = createClient(
        process.env.NEXT_PUBLIC_SUPABASE_URL!,
        process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
      )

      const { data, error } = await supabase
        .from('products')
        .select('*')
        .eq('in_stock', true)
        .order('created_at', { ascending: false })
        .limit(50)

      if (error) {
        console.error('Error fetching products:', error)
        setLoading(false)
        return
      }

      setProducts(data || [])
      setLoading(false)
    }

    fetchProducts()
  }, [])

  if (loading) {
    return (
      <div className="min-h-screen bg-bun flex items-center justify-center">
        <div className="text-center">
          <div className="text-6xl mb-4 animate-bounce">🌭</div>
          <p className="text-charcoal font-bold">Loading products...</p>
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
          <h1 className="text-4xl font-bold mb-4 text-charcoal">Browse Products</h1>
          <p className="text-xl text-charcoal/70">
            Discover quality canned goods and beverages from local producers
          </p>
        </div>

        {/* Products Grid */}
        {products.length === 0 ? (
          <div className="pixel-border bg-white p-12 text-center">
            <div className="text-6xl mb-4">🥫</div>
            <h2 className="text-2xl font-bold mb-2 text-charcoal">No Products Yet</h2>
            <p className="text-charcoal/70">
              Check back soon! Producers are adding products.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {products.map((product) => (
              <Link
                key={product.id}
                href={`/product/${product.id}`}
                className="pixel-border bg-white hover:shadow-lg transition-shadow"
              >
                {/* Product Image */}
                {product.image_url ? (
                  <img
                    src={product.image_url}
                    alt={product.name}
                    className="w-full h-48 object-cover"
                  />
                ) : (
                  <div className="w-full h-48 bg-bun flex items-center justify-center">
                    <span className="text-6xl">🥫</span>
                  </div>
                )}

                {/* Product Info */}
                <div className="p-6">
                  {product.brand && (
                    <p className="text-sm text-mustard font-bold mb-1">
                      {product.brand}
                    </p>
                  )}
                  <h3 className="text-xl font-bold mb-2 text-charcoal">
                    {product.name}
                  </h3>
                  {product.short_description && (
                    <p className="text-sm text-charcoal/70 mb-4 line-clamp-2">
                      {product.short_description}
                    </p>
                  )}

                  {/* Certifications */}
                  <div className="flex flex-wrap gap-2 mb-4">
                    {product.is_organic && (
                      <span className="text-xs px-2 py-1 bg-mustard/20 text-charcoal rounded">
                        🌱 Organic
                      </span>
                    )}
                    {product.is_vegan && (
                      <span className="text-xs px-2 py-1 bg-mustard/20 text-charcoal rounded">
                        🌿 Vegan
                      </span>
                    )}
                    {product.is_gluten_free && (
                      <span className="text-xs px-2 py-1 bg-mustard/20 text-charcoal rounded">
                        🌾 Gluten-Free
                      </span>
                    )}
                  </div>

                  {/* Price */}
                  <div className="text-2xl font-bold text-ketchup">
                    ${(product.retail_price || product.wholesale_price).toFixed(2)}
                    <span className="text-sm text-charcoal/60 ml-1">
                      {product.currency.toUpperCase()}
                    </span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
