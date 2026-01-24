# Indelo Goods

Android application for Indelo Goods.

## Requirements

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34 (API Level 34)
- Minimum SDK: 26 (Android 8.0 Oreo)

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: Modern Android architecture
- **Build System**: Gradle with Kotlin DSL

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/tomasronis/Indelo-Goods.git
cd Indelo-Goods
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
│   │   │   │   ├── ui/theme/          # Compose theming
│   │   │   │   ├── IndeloGoodsApplication.kt
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── MainScreen.kt
│   │   │   ├── res/                   # Resources
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                      # Unit tests
│   │   └── androidTest/               # Instrumentation tests
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Building for Release

To create a release build:

```bash
./gradlew assembleRelease
```

Note: You'll need to configure signing for release builds.

## Future Plans

- iOS version (planned)
