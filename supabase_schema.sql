-- Indelo Goods Database Schema
-- Run this script in your Supabase SQL Editor to set up all tables

-- ============================================================================
-- CATEGORIES TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS categories (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    parent_id UUID REFERENCES categories(id),
    image_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable RLS
ALTER TABLE categories ENABLE ROW LEVEL SECURITY;

-- Everyone can view categories
CREATE POLICY "Categories are viewable by everyone" ON categories
    FOR SELECT USING (true);

-- Seed categories for canned food/beverages
INSERT INTO categories (name, description) VALUES
    ('Canned Vegetables', 'Preserved vegetables in cans'),
    ('Canned Fruits', 'Preserved fruits in cans'),
    ('Canned Beans & Legumes', 'Beans, lentils, and legumes'),
    ('Soups & Broths', 'Ready-to-eat soups and cooking broths'),
    ('Canned Meats & Seafood', 'Preserved meats and seafood'),
    ('Sauces & Condiments', 'Sauces, dressings, and condiments'),
    ('Sodas & Soft Drinks', 'Carbonated and soft drinks'),
    ('Juices', 'Fruit and vegetable juices'),
    ('Energy Drinks', 'Energy and sports drinks'),
    ('Sparkling Water', 'Carbonated water'),
    ('Ready-to-Drink Tea & Coffee', 'Bottled tea and coffee beverages'),
    ('Craft Beverages', 'Artisanal and craft drinks'),
    ('Pickled & Fermented', 'Pickled vegetables and fermented foods'),
    ('Other', 'Other food and beverage products')
ON CONFLICT DO NOTHING;

-- ============================================================================
-- USER PROFILES TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    user_type TEXT NOT NULL CHECK (user_type IN ('SHOP', 'SHOPPER', 'PRODUCER')),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- ============================================================================
-- PRODUCER PROFILES TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS producer_profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    company_name TEXT,
    brand_name TEXT,
    bio TEXT,
    background TEXT,
    inspiration TEXT,
    goals TEXT,
    website_url TEXT,
    logo_url TEXT,
    cover_image_url TEXT,
    location TEXT,
    founded_year INTEGER,
    specialty TEXT,
    certifications TEXT[],
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable RLS
ALTER TABLE user_profiles ENABLE ROW LEVEL SECURITY;

-- Users can manage their own profile
CREATE POLICY "Users can CRUD own profile" ON user_profiles
    FOR ALL USING (auth.uid() = id);

-- Enable RLS for producer profiles
ALTER TABLE producer_profiles ENABLE ROW LEVEL SECURITY;

-- Producers can manage their own profile
CREATE POLICY "Producers can CRUD own profile" ON producer_profiles
    FOR ALL USING (auth.uid() = id);

-- Everyone can view producer profiles
CREATE POLICY "Producer profiles are viewable by everyone" ON producer_profiles
    FOR SELECT USING (true);

-- ============================================================================
-- SHOPS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS shops (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,

    -- Owner
    owner_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,

    -- Location
    address TEXT,
    city TEXT,
    state TEXT,
    zip_code TEXT,
    country TEXT,
    region TEXT,

    -- Contact
    phone TEXT,
    email TEXT,

    -- Business Info
    business_type TEXT,
    tax_id TEXT,

    -- Metadata
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable RLS
ALTER TABLE shops ENABLE ROW LEVEL SECURITY;

-- Shop owners can manage their own shops
CREATE POLICY "Shop owners can CRUD own shops" ON shops
    FOR ALL USING (auth.uid() = owner_id);

-- Everyone can view shops
CREATE POLICY "Shops are viewable by everyone" ON shops
    FOR SELECT USING (true);

-- ============================================================================
-- PRODUCTS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS products (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Basic Info
    name TEXT NOT NULL,
    brand TEXT,
    description TEXT,
    short_description TEXT,

    -- Pricing
    wholesale_price DECIMAL(10,2) NOT NULL,
    retail_price DECIMAL(10,2),
    currency TEXT DEFAULT 'USD',

    -- Product Specifications
    volume_ml INTEGER,
    weight_g INTEGER,
    serving_size TEXT,
    servings_per_container INTEGER,

    -- Packaging
    units_per_case INTEGER DEFAULT 1,
    case_dimensions TEXT,
    case_weight_kg DECIMAL(6,2),

    -- Ingredients & Nutrition
    ingredients TEXT,
    nutrition_facts JSONB,
    allergens TEXT,

    -- Certifications
    is_organic BOOLEAN DEFAULT FALSE,
    is_non_gmo BOOLEAN DEFAULT FALSE,
    is_vegan BOOLEAN DEFAULT FALSE,
    is_gluten_free BOOLEAN DEFAULT FALSE,
    is_kosher BOOLEAN DEFAULT FALSE,
    other_certifications TEXT,

    -- Inventory & Ordering
    sku TEXT,
    upc TEXT,
    minimum_order_quantity INTEGER DEFAULT 1,
    lead_time_days INTEGER,
    in_stock BOOLEAN DEFAULT TRUE,
    stock_quantity INTEGER,

    -- Media
    image_url TEXT,
    additional_images TEXT[],

    -- Categorization
    category_id UUID REFERENCES categories(id),
    tags TEXT[],

    -- Availability (granular: by city and shop)
    available_cities TEXT[],
    available_shop_ids UUID[],

    -- Producer Info
    producer_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    country_of_origin TEXT,
    shelf_life_days INTEGER,
    storage_instructions TEXT,

    -- Metadata
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable RLS
ALTER TABLE products ENABLE ROW LEVEL SECURITY;

-- Producers can manage their own products
CREATE POLICY "Producers can CRUD own products" ON products
    FOR ALL USING (auth.uid() = producer_id);

-- Everyone can view products
CREATE POLICY "Products are viewable by everyone" ON products
    FOR SELECT USING (true);

-- Create indexes for common queries
CREATE INDEX IF NOT EXISTS idx_products_producer_id ON products(producer_id);
CREATE INDEX IF NOT EXISTS idx_products_category_id ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_products_available_cities ON products USING GIN(available_cities);

-- ============================================================================
-- ORDERS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS orders (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Relationships
    shop_id UUID NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    producer_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,

    -- Order Info
    status TEXT DEFAULT 'pending',
    total_amount DECIMAL(10,2) NOT NULL,
    currency TEXT DEFAULT 'USD',

    -- Shipping
    shipping_address TEXT,
    shipping_status TEXT,
    tracking_number TEXT,

    -- Notes
    notes TEXT,

    -- Metadata
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    fulfilled_at TIMESTAMPTZ,

    -- Constraint for valid status values
    CONSTRAINT valid_status CHECK (status IN ('pending', 'confirmed', 'shipped', 'delivered', 'cancelled'))
);

-- Enable RLS
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;

-- Shop owners can view/manage orders for their shops
CREATE POLICY "Shop owners can CRUD orders for their shops" ON orders
    FOR ALL USING (
        shop_id IN (SELECT id FROM shops WHERE owner_id = auth.uid())
    );

-- Producers can view and update orders for their products
CREATE POLICY "Producers can view and update orders" ON orders
    FOR SELECT USING (auth.uid() = producer_id);

CREATE POLICY "Producers can update orders" ON orders
    FOR UPDATE USING (auth.uid() = producer_id);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_orders_shop_id ON orders(shop_id);
CREATE INDEX IF NOT EXISTS idx_orders_producer_id ON orders(producer_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);

-- ============================================================================
-- ORDER ITEMS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS order_items (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Relationships
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,

    -- Item Details
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,

    -- Product Info (cached for historical reference)
    product_name TEXT,
    product_image_url TEXT,

    -- Metadata
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable RLS
ALTER TABLE order_items ENABLE ROW LEVEL SECURITY;

-- Inherit permissions from orders table
CREATE POLICY "Order items inherit order permissions" ON order_items
    FOR ALL USING (
        order_id IN (
            SELECT id FROM orders WHERE
            shop_id IN (SELECT id FROM shops WHERE owner_id = auth.uid())
            OR producer_id = auth.uid()
        )
    );

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);

-- ============================================================================
-- SHOPPER PREFERENCES TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS shopper_preferences (
    user_id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    favorite_categories TEXT[] DEFAULT '{}',
    notifications_enabled BOOLEAN DEFAULT FALSE,

    -- Metadata
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable RLS
ALTER TABLE shopper_preferences ENABLE ROW LEVEL SECURITY;

-- Users can manage their own preferences
CREATE POLICY "Shoppers can CRUD own preferences" ON shopper_preferences
    FOR ALL USING (auth.uid() = user_id);

-- ============================================================================
-- SHOPPER SUBSCRIPTIONS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS shopper_subscriptions (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,

    -- Subscription Status
    status TEXT DEFAULT 'inactive',
    stripe_subscription_id TEXT,

    -- Billing Period
    current_period_start TIMESTAMPTZ,
    current_period_end TIMESTAMPTZ,

    -- Usage Tracking
    products_used_this_month INTEGER DEFAULT 0,

    -- Metadata
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),

    -- Constraints
    CONSTRAINT valid_subscription_status CHECK (status IN ('inactive', 'active', 'cancelled', 'past_due', 'trial')),
    CONSTRAINT max_three_products CHECK (products_used_this_month <= 3)
);

-- Enable RLS
ALTER TABLE shopper_subscriptions ENABLE ROW LEVEL SECURITY;

-- Users can manage their own subscriptions
CREATE POLICY "Shoppers can CRUD own subscriptions" ON shopper_subscriptions
    FOR ALL USING (auth.uid() = user_id);

-- Create index
CREATE INDEX IF NOT EXISTS idx_shopper_subscriptions_user_id ON shopper_subscriptions(user_id);

-- ============================================================================
-- MONTHLY PRODUCT SELECTIONS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS monthly_product_selections (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Relationships
    subscription_id UUID NOT NULL REFERENCES shopper_subscriptions(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    shop_id UUID REFERENCES shops(id) ON DELETE SET NULL,

    -- Selection Info
    month TEXT NOT NULL,
    redeemed BOOLEAN DEFAULT FALSE,
    redeemed_at TIMESTAMPTZ,

    -- Metadata
    created_at TIMESTAMPTZ DEFAULT NOW(),

    -- Ensure unique selections per month
    UNIQUE (subscription_id, month, product_id)
);

-- Enable RLS
ALTER TABLE monthly_product_selections ENABLE ROW LEVEL SECURITY;

-- Users can manage their own selections
CREATE POLICY "Shoppers can CRUD own selections" ON monthly_product_selections
    FOR ALL USING (
        subscription_id IN (
            SELECT id FROM shopper_subscriptions WHERE user_id = auth.uid()
        )
    );

-- Create indexes for month-based queries
CREATE INDEX IF NOT EXISTS idx_monthly_selections_month ON monthly_product_selections(subscription_id, month);
CREATE INDEX IF NOT EXISTS idx_monthly_selections_subscription_id ON monthly_product_selections(subscription_id);

-- ============================================================================
-- STORAGE BUCKETS FOR PRODUCT IMAGES
-- ============================================================================

-- Create storage bucket for product images (if not exists)
INSERT INTO storage.buckets (id, name, public)
VALUES ('products', 'products', true)
ON CONFLICT (id) DO NOTHING;

-- Storage policies
CREATE POLICY "Authenticated users can upload product images" ON storage.objects
    FOR INSERT WITH CHECK (bucket_id = 'products' AND auth.role() = 'authenticated');

CREATE POLICY "Anyone can view product images" ON storage.objects
    FOR SELECT USING (bucket_id = 'products');

CREATE POLICY "Users can update their own product images" ON storage.objects
    FOR UPDATE USING (bucket_id = 'products' AND auth.uid()::text = (storage.foldername(name))[1]);

CREATE POLICY "Users can delete their own product images" ON storage.objects
    FOR DELETE USING (bucket_id = 'products' AND auth.uid()::text = (storage.foldername(name))[1]);

-- ============================================================================
-- DONE!
-- ============================================================================

-- Verify tables were created
SELECT
    schemaname,
    tablename,
    tableowner
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY tablename;
