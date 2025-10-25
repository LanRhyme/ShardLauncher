# ShardLauncher

[中文版本 (README.md)](README.md)

[![Development Build Status](https://github.com/LanRhyme/ShardLauncher/actions/workflows/development.yml/badge.svg?branch=master)](https://github.com/LanRhyme/ShardLauncher/actions/workflows/development.yml)

ShardLauncher is a modern Minecraft Java Edition Android launcher application built with Jetpack Compose. It offers a contemporary user interface and rich customization options.

## Core Features

*   **Multiple Login Methods:** Supports Microsoft and offline login.
*   **Game Management:** Automatically downloads and manages Minecraft game files, including clients, assets, and libraries.
*   **Highly Customizable UI:**
    *   Dark mode and multiple theme colors
    *   Customizable launcher background (images or videos)
    *   Adjustable UI animation speed and scaling
    *   Customizable sidebar position
*   **Version Check:** Option to enable fetching information about the latest Minecraft versions.

## Technology Stack

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose
*   **Design Language:** Material Design 3
*   **Key Dependencies:**
    *   `androidx.navigation:navigation-compose` for page navigation
    *   `io.coil-kt:coil-compose` for image loading
    *   `com.squareup.retrofit2:retrofit` for network requests
    *   `androidx.media3:media3-exoplayer` for video background playback

## Build and Run

### Requirements

*   Android Studio (latest stable version recommended)
*   Android SDK (API 35 or higher)
*   JDK 11

### Build Steps

1.  Clone the repository:
    ```bash
    git clone https://github.com/LanRhyme/ShardLauncher.git
    ```
2.  Open the project in Android Studio.
3.  Wait for Gradle sync to complete.
4.  Connect an Android device or start an emulator.
5.  Click the "Run" button (green triangle) in Android Studio or use the shortcut `Shift + F10` to build and install the app.

## Development Conventions

*   **UI Development:** Uses Jetpack Compose for declarative UI development.
*   **State Management:** Utilizes Compose's `State` and `ViewModel` (if used) to manage UI state.
*   **Navigation:** Uses `androidx.navigation:navigation-compose` for single-Activity, multi-Composable page navigation.
*   **Theming:** Employs the Material Design 3 theme system, supporting dark mode and multiple theme color customizations.
*   **Settings Storage:** Uses `SharedPreferences` (encapsulated via `SettingsRepository`) for persisting user settings.

## Project Structure

```
ShardLauncher/
├── app/                  # Main application module
│   ├── src/main/
│   │   ├── java/         # Kotlin source code
│   │   │   └── com.lanrhyme.shardlauncher/
│   │   │       ├── MainActivity.kt          # Application entry point
│   │   │       ├── ShardLauncherApp.kt      # Application class
│   │   │       ├── api/                     # Network API interface definitions
│   │   │       ├── common/                  # Common data classes (e.g., SidebarPosition)
│   │   │       ├── data/                    # Data layer (e.g., SettingsRepository)
│   │   │       ├── model/                   # Data models
│   │   │       ├── ui/                      # UI layer
│   │   │       │   ├── components/          # Reusable UI components
│   │   │       │   ├── navigation/          # Navigation related
│   │   │       │   ├── settings/            # Settings interface
│   │   │       │   ├── downloads/           # Download interface
│   │   │       │   ├── account/             # Account interface
│   │   │       │   ├── home/                # Home interface
│   │   │       │   ├── notification/        # Notification system
│   │   │       │   ├── developeroptions/    # Developer options
│   │   │       │   ├── crash/               # Crash handling
│   │   │       │   ├── custom/              # Custom XAML parser
│   │   │       │   ├── theme/               # Theme definitions
│   │   │       │   └── LocalSettingsProvider.kt # Local settings provider
│   │   │       └── utils/                   # Utility classes
│   │   └── res/          # Resource files (images, strings, themes, etc.)
│   └── build.gradle.kts  # Application module build script
├── gradle/               # Gradle Wrapper and version configuration
├── build.gradle.kts      # Project-level build script
├── settings.gradle.kts   # Project settings
├── gradle.properties     # Gradle properties
└── README.md             # Project description
```