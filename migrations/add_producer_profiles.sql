-- Migration: Add Producer Profiles Table
-- Run this in your Supabase SQL Editor

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

-- Enable RLS for producer profiles
ALTER TABLE producer_profiles ENABLE ROW LEVEL SECURITY;

-- Producers can manage their own profile
CREATE POLICY "Producers can CRUD own profile" ON producer_profiles
    FOR ALL USING (auth.uid() = id);

-- Everyone can view producer profiles
CREATE POLICY "Producer profiles are viewable by everyone" ON producer_profiles
    FOR SELECT USING (true);
