'use client'

import { useEffect, useState } from 'react'
import { useParams } from 'next/navigation'
import Link from 'next/link'
import { createClient } from '@supabase/supabase-js'

interface Shop {
  id: string
  name: string
  description?: string
  address?: string
  city?: string
  state?: string
  zip_code?: string
  business_type?: string
  phone?: string
  email?: string
}

interface Product {
  id: string
  name: string
  brand?: string
  description?: string
  short_description?: string
  image_url?: string
  retail_price?: number
  currency?: string
}

export default function ShopDetailPage() {
  const params = useParams()
  const shopId = params.id as string

  const [shop, setShop] = useState<Shop | null>(null)
  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function fetchShopData() {
      const supabase = createClient(
        process.env.NEXT_PUBLIC_SUPABASE_URL!,
        process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
      )

      // Fetch shop details
      const { data: shopData, error: shopError } = await supabase
        .from('shops')
        .select('*')
        .eq('id', shopId)
        .single()

      if (shopError) {
        console.error('Error fetching shop:', shopError)
        setLoading(false)
        return
      }

      setShop(shopData)

      // Fetch products available at this shop
      const { data: productsData, error: productsError } = await supabase
        .from('products')
        .select('*')
        .contains('available_shop_ids', [shopId])
        .limit(50)

      if (productsError) {
        console.error('Error fetching products:', productsError)
      } else {
        setProducts(productsData || [])
      }

      setLoading(false)
    }

    fetchShopData()
  }, [shopId])

  if (loading) {
    return (
      <div className="min-h-screen bg-bun flex items-center justify-center">
        <div className="text-center">
          <div className="text-6xl mb-4 animate-bounce">🌭</div>
          <p className="text-charcoal font-bold">Loading shop...</p>
        </div>
      </div>
    )
  }

  if (!shop) {
    return (
      <div className="min-h-screen bg-bun flex items-center justify-center p-4">
        <div className="pixel-border bg-white p-12 text-center max-w-md">
          <div className="text-6xl mb-4">❌</div>
          <h1 className="text-2xl font-bold mb-4 text-charcoal">Shop Not Found</h1>
          <p className="text-charcoal/70 mb-6">
            This shop doesn't exist or has been removed.
          </p>
          <Link href="/shops" className="pixel-button inline-block">
            Back to Shops
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-bun p-8">
      <div className="max-w-6xl mx-auto">
        {/* Back Link */}
        <Link href="/shops" className="inline-block mb-6 text-ketchup hover:underline">
          ← Back to Shops
        </Link>

        {/* Shop Header */}
        <div className="pixel-border bg-white p-8 mb-8">
          <div className="flex items-start gap-6">
            <div className="text-6xl">🏪</div>
            <div className="flex-1">
              <h1 className="text-4xl font-bold mb-2 text-charcoal">{shop.name}</h1>

              {shop.business_type && (
                <p className="text-lg text-mustard font-bold mb-4">
                  {shop.business_type}
                </p>
              )}

              {shop.description && (
                <p className="text-lg text-charcoal/70 mb-6">{shop.description}</p>
              )}

              {/* Location */}
              {(shop.address || shop.city || shop.state) && (
                <div className="mb-4">
                  <h3 className="font-bold text-charcoal mb-2">📍 Location</h3>
                  <div className="text-charcoal/70">
                    {shop.address && <div>{shop.address}</div>}
                    <div>
                      {shop.city}
                      {shop.city && shop.state && ', '}
                      {shop.state} {shop.zip_code}
                    </div>
                  </div>
                </div>
              )}

              {/* Contact */}
              {(shop.phone || shop.email) && (
                <div className="mb-4">
                  <h3 className="font-bold text-charcoal mb-2">📞 Contact</h3>
                  <div className="text-charcoal/70">
                    {shop.phone && <div>Phone: {shop.phone}</div>}
                    {shop.email && <div>Email: {shop.email}</div>}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Products Available Here */}
        <div>
          <h2 className="text-3xl font-bold mb-6 text-charcoal">
            Products Available at This Shop
          </h2>

          {products.length === 0 ? (
            <div className="pixel-border bg-white p-12 text-center">
              <div className="text-6xl mb-4">📦</div>
              <h3 className="text-2xl font-bold mb-2 text-charcoal">No Products Yet</h3>
              <p className="text-charcoal/70">
                This shop hasn't listed any products yet. Check back soon!
              </p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {products.map((product) => (
                <Link
                  key={product.id}
                  href={`/product/${product.id}`}
                  className="pixel-border bg-white p-6 hover:shadow-lg transition-shadow"
                >
                  {/* Product Image */}
                  {product.image_url ? (
                    <img
                      src={product.image_url}
                      alt={product.name}
                      className="w-full h-48 object-cover mb-4"
                    />
                  ) : (
                    <div className="w-full h-48 bg-mustard/20 flex items-center justify-center mb-4">
                      <span className="text-6xl">🥫</span>
                    </div>
                  )}

                  {/* Brand */}
                  {product.brand && (
                    <p className="text-sm text-mustard font-bold mb-1">
                      {product.brand}
                    </p>
                  )}

                  {/* Product Name */}
                  <h3 className="text-xl font-bold mb-2 text-charcoal">
                    {product.name}
                  </h3>

                  {/* Short Description */}
                  {product.short_description && (
                    <p className="text-sm text-charcoal/70 mb-4 line-clamp-2">
                      {product.short_description}
                    </p>
                  )}

                  {/* Price */}
                  {product.retail_price && (
                    <p className="text-lg font-bold text-ketchup">
                      ${product.retail_price.toFixed(2)} {product.currency || 'USD'}
                    </p>
                  )}
                </Link>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
