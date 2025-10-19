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
