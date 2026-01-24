# Indelo Goods - Project Guide

## Overview

Indelo Goods is a goods/inventory management application. The first version targets Android, with iOS planned for a later phase.

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
│   ├── navigation/     # App navigation
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

## Requirements

### Authentication
- [ ] Phone number OTP sign up/sign in (primary)
- [ ] User type selection (Shop, Shopper, Producer)
- [ ] Sign out
- [ ] Email/password (fallback, future)
- [ ] OAuth providers (future)

### Product Management (Producer)
- [ ] Create product with full details (basic info, pricing, specs, certifications)
- [ ] Multi-step product creation form
- [ ] Edit existing products
- [ ] Delete products
- [ ] View product list (own products)
- [ ] Image upload for products

### Product Discovery (Shop & Shopper)
- [ ] Browse all products
- [ ] View product details
- [ ] Search products
- [ ] Filter by category
- [ ] Filter by certifications (organic, vegan, etc.)

### Categories
- [ ] List categories
- [ ] CRUD operations

### Future Features
- [ ] Barcode scanning
- [ ] Image upload for products
- [ ] Real-time inventory updates
- [ ] Reporting/analytics
- [ ] iOS app

## Dependencies

Key dependencies and their purposes:
- `supabase-kt` - Supabase Kotlin SDK (BOM version 2.6.1)
- `ktor-client-android` - HTTP client for Supabase
- `navigation-compose` - Jetpack Compose navigation
- `lifecycle-viewmodel-compose` - ViewModel integration with Compose

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
