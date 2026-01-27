import { Metadata } from 'next'
import { notFound } from 'next/navigation'
import { getProduct, getProducerProfile } from '@/lib/supabase'
import ProductDetailClient from '@/components/ProductDetailClient'

interface Props {
  params: { id: string }
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const product = await getProduct(params.id)

  if (!product) {
    return {
      title: 'Product Not Found - Indelo Goods',
    }
  }

  return {
    title: `${product.name} - Indelo Goods`,
    description: product.short_description || product.description || `${product.name} by ${product.brand || 'Indelo Goods'}`,
    openGraph: {
      title: product.name,
      description: product.short_description || product.description || '',
      images: product.image_url ? [product.image_url] : [],
    },
  }
}

export default async function ProductPage({ params }: Props) {
  const product = await getProduct(params.id)

  if (!product) {
    notFound()
  }

  const producerProfile = await getProducerProfile(product.producer_id)

  return <ProductDetailClient product={product} producerProfile={producerProfile} />
}
