import { createClient } from '@supabase/supabase-js'

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL!
const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!

export const supabase = createClient(supabaseUrl, supabaseAnonKey)

export interface Product {
  id: string
  name: string
  brand?: string
  description?: string
  short_description?: string
  wholesale_price: number
  retail_price?: number
  currency: string
  volume_ml?: number
  weight_g?: number
  serving_size?: string
  servings_per_container?: number
  units_per_case: number
  ingredients?: string
  allergens?: string
  is_organic: boolean
  is_non_gmo: boolean
  is_vegan: boolean
  is_gluten_free: boolean
  is_kosher: boolean
  other_certifications?: string
  sku?: string
  upc?: string
  in_stock: boolean
  image_url?: string
  additional_images?: string[]
  category_id?: string
  tags?: string[]
  producer_id: string
  country_of_origin?: string
  shelf_life_days?: number
  storage_instructions?: string
  created_at?: string
  updated_at?: string
}

export interface ProducerProfile {
  id: string
  company_name?: string
  brand_name?: string
  bio?: string
  background?: string
  inspiration?: string
  goals?: string
  website_url?: string
  logo_url?: string
  cover_image_url?: string
  location?: string
  founded_year?: number
  specialty?: string
  certifications?: string[]
  created_at?: string
  updated_at?: string
}

export async function getProduct(productId: string): Promise<Product | null> {
  const { data, error } = await supabase
    .from('products')
    .select('*')
    .eq('id', productId)
    .single()

  if (error) {
    console.error('Error fetching product:', error)
    return null
  }

  return data
}

export async function getProducerProfile(producerId: string): Promise<ProducerProfile | null> {
  const { data, error } = await supabase
    .from('producer_profiles')
    .select('*')
    .eq('id', producerId)
    .single()

  if (error) {
    console.error('Error fetching producer profile:', error)
    return null
  }

  return data
}
