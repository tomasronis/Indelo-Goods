# Indelo Goods - Project Guide

## Overview

Indelo Goods is a goods/inventory management application connecting Producers, Shops, and Shoppers through QR code-based commerce. The first version targets Android, with iOS planned for a later phase.

### Business Model

**Revenue Flow:**
1. **Shoppers** (consumers) scan QR codes on products in physical shops → purchase via Stripe
2. **Payment Split** on each sale:
   - 4.5% → Shop (where QR code was scanned)
   - 5.5% → Indelo Goods (platform fee)
   - 90% → Producer (product manufacturer)

**B2B Wholesale Orders:**
- Shops order inventory from Producers **at no cost** (consignment model)
- Shops only make money when their customers scan QR codes and purchase
- Producers review and fulfill wholesale orders, shipping products to shops

**Account Requirements:**
- **Shops**: Bank account via Stripe Connect to receive 4.5% payouts
- **Producers**:
  - Bank account via Stripe Connect to receive 90% payouts
  - Credit card for $50/month SaaS subscription fee

## User Types

Three distinct user roles:

| Type | Description |
|------|-------------|
| **Shop** | Retail stores that sell goods to shoppers |
| **Shopper** | Consumers who browse and purchase goods |
| **Producer** | Manufacturers/suppliers who provide goods to shops |

## Design System

### Visual Style
- **Aesthetic:** Retro/pixelated old-school look
- **Colors:** Simple 2-4 color palette
- **Mascot:** Dancing hotdog (used on auth screens)
- **Typography:** Clean, readable (pixel fonts optional for accents)

### Auth UX Goals
- Super slick and easy sign-up/login
- Phone-based OTP for speed and simplicity
- Minimal friction to get users started

## Tech Stack

### Android (v1)
- **Language:** Kotlin
- **UI:** Jetpack Compose with Material 3
- **Architecture:** MVVM with Repository pattern
- **Backend:** Supabase
  - PostgreSQL database
  - Authentication (email/password)
  - Realtime subscriptions
  - Storage for images/files
- **Networking:** Ktor client
- **Serialization:** Kotlinx Serialization
- **Build:** Gradle with Kotlin DSL

### iOS (planned)
- To be determined (likely SwiftUI with same Supabase backend)

## Project Structure

```
app/src/main/java/com/indelo/goods/
├── data/
│   ├── model/          # Data classes (@Serializable)
│   ├── repository/     # Data access layer
│   └── supabase/       # Supabase client configuration
├── ui/
│   ├── auth/           # Authentication screens
│   ├── cart/           # Shopping cart (consumer checkout)
│   ├── components/     # Reusable UI components (DancingHotdog, etc.)
│   ├── navigation/     # App navigation
│   ├── producer/       # Producer screens (product management)
│   ├── public/         # Public screens (product detail, producer profile)
│   ├── shop/           # Shop screens (B2B ordering)
│   └── theme/          # Colors, typography, theming
├── IndeloGoodsApplication.kt
├── MainActivity.kt
└── MainScreen.kt
```

## Project Documentation

### FORtomas.md

For every project, write a detailed **FORtomas.md** file that explains the whole project in plain language.

This file should cover:

- **Technical architecture** - How is everything structured? What's the big picture?
- **Codebase structure** - How are the various parts connected? What talks to what?
- **Technologies used** - What tools, frameworks, and libraries power this thing?
- **Technical decisions** - Why did we choose X over Y? What trade-offs did we make?
- **Lessons learned** - This is the gold:
  - Bugs we ran into and how we fixed them
  - Potential pitfalls and how to avoid them in the future
  - New technologies we explored
  - How good engineers think and work
  - Best practices we discovered along the way

**Writing style:** Make it engaging to read! Don't write boring technical documentation that reads like a textbook. Use analogies and anecdotes where appropriate to make concepts more understandable and memorable. Think of it as explaining the project to a smart colleague over coffee, not writing an API reference.

## Coding Practices

### General
- Use Kotlin idioms (data classes, sealed classes, extension functions)
- Prefer immutability (`val` over `var`)
- Use `Result<T>` for operations that can fail
- Keep UI logic in ViewModels, business logic in repositories

### Compose
- Use `Modifier` as first optional parameter
- Extract reusable composables
- Use `remember` and `rememberSaveable` appropriately
- Preview composables with `@Preview`

### Supabase
- All Supabase operations through repository classes
- Use `withContext(Dispatchers.IO)` for database calls
- Handle errors gracefully with Result type
- Data classes must be `@Serializable` with `@SerialName` for snake_case columns

### Naming Conventions
- Files: PascalCase (e.g., `ProductRepository.kt`)
- Classes/Objects: PascalCase
- Functions/Variables: camelCase
- Database columns: snake_case (mapped via `@SerialName`)
- Composables: PascalCase (they're like components)

## Security

- **Never commit secrets** to version control
- Supabase credentials stored in `local.properties` (gitignored)
- Use BuildConfig for runtime access to secrets
- Enable Row Level Security (RLS) on all Supabase tables

## Database Schema

### Products (for canned food/beverage producers)
```sql
CREATE TABLE products (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Basic Info
    name TEXT NOT NULL,
    brand TEXT,
    description TEXT,
    short_description TEXT,

    -- Pricing
    wholesale_price DECIMAL(10,2) NOT NULL,  -- Price for shops
    retail_price DECIMAL(10,2),               -- Suggested retail price
    currency TEXT DEFAULT 'USD',

    -- Product Specifications
    volume_ml INTEGER,                        -- Volume in milliliters
    weight_g INTEGER,                         -- Weight in grams
    serving_size TEXT,                        -- e.g., "240ml", "1 can"
    servings_per_container INTEGER,

    -- Packaging
    units_per_case INTEGER DEFAULT 1,
    case_dimensions TEXT,                     -- e.g., "12x8x6 inches"
    case_weight_kg DECIMAL(6,2),

    -- Ingredients & Nutrition
    ingredients TEXT,
    nutrition_facts JSONB,                    -- Stored as JSON object
    allergens TEXT,                           -- e.g., "Contains: Soy, Wheat"

    -- Certifications
    is_organic BOOLEAN DEFAULT FALSE,
    is_non_gmo BOOLEAN DEFAULT FALSE,
    is_vegan BOOLEAN DEFAULT FALSE,
    is_gluten_free BOOLEAN DEFAULT FALSE,
    is_kosher BOOLEAN DEFAULT FALSE,
    other_certifications TEXT,

    -- Inventory & Ordering
    sku TEXT,                                 -- Stock Keeping Unit
    upc TEXT,                                 -- Universal Product Code (barcode)
    minimum_order_quantity INTEGER DEFAULT 1,
    lead_time_days INTEGER,
    in_stock BOOLEAN DEFAULT TRUE,
    stock_quantity INTEGER,

    -- Media
    image_url TEXT,
    additional_images TEXT[],                 -- Array of image URLs

    -- Categorization
    category_id UUID REFERENCES categories(id),
    tags TEXT[],                              -- e.g., ["beverage", "sparkling"]

    -- Availability (granular: by city and shop)
    available_cities TEXT[],                  -- Cities where product is available
    available_shop_ids UUID[],                -- Specific shops that can order this product

    -- Producer Info
    producer_id UUID REFERENCES auth.users(id),
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
```

### Categories
```sql
CREATE TABLE categories (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    parent_id UUID REFERENCES categories(id),
    image_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Seed categories for canned food/beverages
INSERT INTO categories (name) VALUES
    ('Canned Vegetables'),
    ('Canned Fruits'),
    ('Canned Beans & Legumes'),
    ('Soups & Broths'),
    ('Canned Meats & Seafood'),
    ('Sauces & Condiments'),
    ('Sodas & Soft Drinks'),
    ('Juices'),
    ('Energy Drinks'),
    ('Sparkling Water'),
    ('Ready-to-Drink Tea & Coffee'),
    ('Craft Beverages'),
    ('Pickled & Fermented'),
    ('Other');
```

### User Profiles
```sql
CREATE TABLE user_profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id),
    user_type TEXT NOT NULL CHECK (user_type IN ('SHOP', 'SHOPPER', 'PRODUCER')),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable RLS
ALTER TABLE user_profiles ENABLE ROW LEVEL SECURITY;

-- Users can manage their own profile
CREATE POLICY "Users can CRUD own profile" ON user_profiles
    FOR ALL USING (auth.uid() = id);
```

### Shops
```sql
CREATE TABLE shops (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,

    -- Owner
    owner_id UUID NOT NULL REFERENCES auth.users(id),

    -- Location
    address TEXT,
    city TEXT,
    state TEXT,
    zip_code TEXT,
    country TEXT,
    region TEXT,                               -- Geographic region for product availability

    -- Contact
    phone TEXT,
    email TEXT,

    -- Business Info
    business_type TEXT,                        -- e.g., "Cafe", "Restaurant", "Retail Store"
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
```

### Orders
```sql
CREATE TABLE orders (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Relationships
    shop_id UUID NOT NULL REFERENCES shops(id),
    producer_id UUID REFERENCES auth.users(id),

    -- Order Info
    status TEXT DEFAULT 'pending',             -- pending, confirmed, shipped, delivered, cancelled
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
    fulfilled_at TIMESTAMPTZ
);

-- Enable RLS
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;

-- Shop owners can view/manage orders for their shops
CREATE POLICY "Shop owners can CRUD orders for their shops" ON orders
    FOR ALL USING (
        shop_id IN (SELECT id FROM shops WHERE owner_id = auth.uid())
    );

-- Producers can view orders for their products
CREATE POLICY "Producers can view orders" ON orders
    FOR SELECT USING (auth.uid() = producer_id);
```

### Order Items
```sql
CREATE TABLE order_items (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Relationships
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id),

    -- Item Details
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,         -- Price at time of order
    subtotal DECIMAL(10,2) NOT NULL,           -- quantity * unit_price

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
```

### Storage Buckets
```sql
-- Create storage bucket for product images
INSERT INTO storage.buckets (id, name, public) VALUES ('products', 'products', true);

-- Enable RLS on storage
CREATE POLICY "Authenticated users can upload product images" ON storage.objects
    FOR INSERT WITH CHECK (bucket_id = 'products' AND auth.role() = 'authenticated');

CREATE POLICY "Anyone can view product images" ON storage.objects
    FOR SELECT USING (bucket_id = 'products');

CREATE POLICY "Users can update their own product images" ON storage.objects
    FOR UPDATE USING (bucket_id = 'products' AND auth.uid()::text = (storage.foldername(name))[1]);

CREATE POLICY "Users can delete their own product images" ON storage.objects
    FOR DELETE USING (bucket_id = 'products' AND auth.uid()::text = (storage.foldername(name))[1]);
```

### Shopper Preferences
```sql
CREATE TABLE shopper_preferences (
    user_id UUID PRIMARY KEY REFERENCES auth.users(id),
    favorite_categories TEXT[] DEFAULT '{}',      -- Array of category names
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
```

### Shopper Subscriptions
```sql
CREATE TABLE shopper_subscriptions (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth.users(id),

    -- Subscription Status
    status TEXT DEFAULT 'inactive',              -- inactive, active, cancelled, past_due, trial
    stripe_subscription_id TEXT,

    -- Billing Period
    current_period_start TIMESTAMPTZ,
    current_period_end TIMESTAMPTZ,

    -- Usage Tracking
    products_used_this_month INTEGER DEFAULT 0,  -- Max 3 per month

    -- Metadata
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT max_three_products CHECK (products_used_this_month <= 3)
);

-- Enable RLS
ALTER TABLE shopper_subscriptions ENABLE ROW LEVEL SECURITY;

-- Users can manage their own subscriptions
CREATE POLICY "Shoppers can CRUD own subscriptions" ON shopper_subscriptions
    FOR ALL USING (auth.uid() = user_id);
```

### Monthly Product Selections
```sql
CREATE TABLE monthly_product_selections (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Relationships
    subscription_id UUID NOT NULL REFERENCES shopper_subscriptions(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id),
    shop_id UUID REFERENCES shops(id),           -- Where product was redeemed

    -- Selection Info
    month TEXT NOT NULL,                          -- Format: YYYY-MM
    redeemed BOOLEAN DEFAULT FALSE,
    redeemed_at TIMESTAMPTZ,

    -- Metadata
    created_at TIMESTAMPTZ DEFAULT NOW(),

    -- Ensure 3 selections per month
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

-- Create index for month-based queries
CREATE INDEX idx_monthly_selections_month ON monthly_product_selections(subscription_id, month);
```

## Requirements

### Authentication
- [x] Phone number OTP sign up/sign in (primary)
- [x] User type selection (Shop, Shopper, Producer)
- [x] User type persistence in Supabase
- [x] Sign out
- [ ] Email/password (fallback, future)
- [ ] OAuth providers (future)

### Product Management (Producer)
- [x] Create product with full details (basic info, pricing, specs, certifications)
- [x] Multi-step product creation form (6 steps)
- [x] Edit existing products
- [x] Delete products
- [x] View product list (own products)
- [x] Image upload for products (Supabase Storage)
- [x] QR code generation for products
- [x] Display QR codes for shoppers to scan

### Product Discovery (Shop & Shopper)
- [x] View product details (public, no auth required)
- [x] Browse producer profiles
- [x] View all products from a producer
- [x] Deep link support (scan QR code → product page)
- [ ] Browse all products
- [ ] Search products
- [ ] Filter by category
- [ ] Filter by certifications (organic, vegan, etc.)

### Shopping Cart & Checkout
- [x] Shopping cart functionality (add, remove, update quantity)
- [x] Checkout screen with cart summary
- [x] Web checkout UI with quantity selector and email input
- [x] "Purchase Here" button on product web pages
- [x] Checkout modal with payment breakdown (90% producer, 4.5% shop, 5.5% platform)
- [ ] Stripe payment integration (checkout UI ready, needs API integration)
- [ ] Order management
- [ ] Order history

### Deep Links & QR Codes
- [x] Deep link handling (`indelogoods://product/{id}`)
- [x] Universal links support (`https://indelogoods.com/product/{id}`)
- [x] QR code generation with ZXing
- [x] Public product pages (accessible via QR scan)
- [x] Web frontend for universal links (Next.js app in `web/` directory)

### Shop B2B Ordering
- [x] Create and manage multiple shop locations
- [x] Shop list with retro styling and dancing hotdog empty state
- [x] Multi-step shop creation form (Basic Info, Location, Contact)
- [x] Browse products available in shop's city/region
- [x] Granular product availability (by city and specific shop ID)
- [x] Add products to wholesale order cart
- [x] Adjust order quantities (case units)
- [x] View order summary with total
- [x] Place B2B wholesale orders (free inventory - consignment model)
- [x] Delivery address confirmation
- [x] Dancing hotdog Easter eggs throughout shop UI
- [ ] View order history
- [ ] Track order status

### Producer Order Management
- [x] View incoming orders from shops
- [x] Order details (items, quantities, delivery address, notes)
- [x] Accept/confirm orders
- [x] Mark orders as shipped
- [x] Mark orders as delivered
- [x] Status badges (pending, confirmed, shipped, delivered, cancelled)
- [x] Quick actions dashboard on producer home (all cards now functional)
- [x] Dancing hotdog empty state

### Producer Dashboard & Analytics
- [x] Inventory tracking screen (placeholder - ready for implementation)
- [x] Sales analytics screen (placeholder - ready for implementation)
- [x] Payout history screen (placeholder - ready for Stripe Connect)
- [x] Subscription management screen (placeholder - ready for Stripe billing)
- [x] Navigation from producer home to all dashboard features
- [x] Producer profile creation/editing (company info, bio, background, inspiration, goals)
- [x] Producer profile link on product web pages
- [ ] Implement inventory tracking (count delivered orders by shop)
- [ ] Implement sales analytics (integrate with shopper purchase data)
- [ ] Implement Stripe Connect for payouts
- [ ] Implement Stripe subscription billing ($50/month)

### Shopper Experience
- [x] Shopper home screen with subscription banner
- [x] Category preferences (14 food/beverage categories with emoji chips)
- [x] Notification opt-in for new products in favorite categories
- [x] $49/month subscription screen
- [x] Subscription status display (active/inactive)
- [x] Monthly product selection screen (select 3 products to try)
- [x] Product selection progress tracking (0-3 selected)
- [x] Navigation between all shopper screens
- [x] Dancing hotdog placeholders for coming soon features
- [ ] Implement Stripe subscription billing ($49/month)
- [ ] Implement product catalog for monthly selection
- [ ] Implement product redemption at shops
- [ ] Personalized product feed based on preferences
- [ ] Push notifications for new products

### Categories
- [ ] List categories
- [ ] CRUD operations

### Stripe Connect & Payments
- [ ] Stripe checkout for shoppers (QR code purchases)
- [ ] Stripe Connect account setup for Shops (receive 4.5% payouts)
- [ ] Stripe Connect account setup for Producers (receive 90% payouts)
- [ ] Producer subscription billing ($50/month via saved card)
- [ ] Payment split implementation (4.5% Shop, 5.5% Indelo, 90% Producer)
- [ ] Payout management and tracking

### Future Features
- [ ] Barcode scanning (UPC/EAN)
- [ ] Real-time inventory updates
- [ ] Reporting/analytics for producers
- [ ] Producer order management dashboard
- [ ] iOS app
- [ ] Web app (Next.js)

## Dependencies

Key dependencies and their purposes:
- `supabase-kt` - Supabase Kotlin SDK (BOM version 2.6.1)
  - `postgrest-kt` - Database queries
  - `auth-kt` - Authentication
  - `storage-kt` - File storage for product images
  - `realtime-kt` - Real-time subscriptions (future use)
- `ktor-client-android` - HTTP client for Supabase
- `navigation-compose` - Jetpack Compose navigation with deep link support
- `lifecycle-viewmodel-compose` - ViewModel integration with Compose
- `coil-compose` - Image loading and caching (v2.5.0)
- `zxing:core` - QR code generation (v3.5.3)
- `stripe-android` - Stripe payment SDK (v20.37.5) - for future checkout

## Testing

- Unit tests in `app/src/test/`
- Instrumentation tests in `app/src/androidTest/`
- Use `junit` for unit tests
- Use `espresso` for UI tests

## Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install debug on device
./gradlew installDebug

# Run tests
./gradlew test
```
