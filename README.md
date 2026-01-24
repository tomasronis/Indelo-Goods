# Indelo Goods

Android application for Indelo Goods.

## Requirements

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34 (API Level 34)
- Minimum SDK: 26 (Android 8.0 Oreo)
- Supabase project (for backend)

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Backend**: Supabase (PostgreSQL, Auth, Storage, Realtime)
- **Networking**: Ktor client
- **Serialization**: Kotlinx Serialization
- **Architecture**: MVVM with Repository pattern
- **Build System**: Gradle with Kotlin DSL

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/tomasronis/Indelo-Goods.git
cd Indelo-Goods
```

### Configure Supabase

1. Create a project at [supabase.com](https://supabase.com)
2. Get your project URL and anon key from Settings > API
3. Copy `local.properties.example` to `local.properties`:

```bash
cp local.properties.example local.properties
```

4. Edit `local.properties` with your Supabase credentials:

```properties
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key
```

> **Note:** `local.properties` is gitignored and should never be committed to version control.

### Database Setup

Create the following table in your Supabase SQL Editor:

```sql
-- Products table
CREATE TABLE products (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    quantity INTEGER DEFAULT 0,
    image_url TEXT,
    category_id UUID REFERENCES categories(id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Categories table (optional)
CREATE TABLE categories (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable Row Level Security
ALTER TABLE products ENABLE ROW LEVEL SECURITY;
ALTER TABLE categories ENABLE ROW LEVEL SECURITY;

-- Create policies (adjust as needed for your use case)
CREATE POLICY "Allow read access to all users" ON products FOR SELECT USING (true);
CREATE POLICY "Allow read access to all users" ON categories FOR SELECT USING (true);
```

### Build the Project

```bash
./gradlew build
```

### Run the App

Open the project in Android Studio and run on an emulator or physical device.

Or via command line:

```bash
./gradlew installDebug
```

## Project Structure

```
Indelo-Goods/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/indelo/goods/
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/           # Data models
│   │   │   │   │   ├── repository/      # Repositories
│   │   │   │   │   └── supabase/        # Supabase client
│   │   │   │   ├── ui/
│   │   │   │   │   ├── auth/            # Authentication screens
│   │   │   │   │   ├── navigation/      # Navigation
│   │   │   │   │   └── theme/           # Compose theming
│   │   │   │   ├── IndeloGoodsApplication.kt
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── MainScreen.kt
│   │   │   ├── res/                     # Resources
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                        # Unit tests
│   │   └── androidTest/                 # Instrumentation tests
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Features

- User authentication (sign up, sign in, sign out)
- Product management (CRUD operations)
- Real-time updates support
- Light/dark theme

## Building for Release

To create a release build:

```bash
./gradlew assembleRelease
```

Note: You'll need to configure signing for release builds.

## Future Plans

- iOS version (planned)
