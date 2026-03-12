# EasyCart

EasyCart is a production-grade, feature-rich Android food delivery application designed to provide a seamless and intuitive ordering experience, connecting users with local culinary favorites.

---

## 📖 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Environment Variables](#environment-variables)
  - [Running Locally](#running-locally)
- [Configuration](#configuration)
- [Usage Examples](#usage-examples)
- [Testing](#testing)
- [Security](#security)
---

## Overview

EasyCart simplifies the food ordering process through a modern, responsive mobile interface. It leverages real-time synchronization for menus and orders, integrated location services for precise delivery, and a robust multi-factor authentication system. Built with scalability and performance in mind, it serves as a complete solution for the food delivery ecosystem.

---

## Features

- **Multi-Factor Authentication**: Supports Secure Email/Password, Google Sign-In, and Phone OTP verification via Firebase.
- **Dynamic Product Discovery**: Real-time menu updates, category filtering, and a "Popular Items" section.
- **Location-Aware Services**: Integrated Google Maps SDK for precise delivery address selection and real-time location fetching.
- **Advanced Navigation**: Fluid fragment transitions using the Jetpack Navigation Component.
- **Cart & Order Management**: Real-time price calculation, item persistence, and comprehensive order history tracking.
- **Interactive UI**: Custom image sliders for promotions and Material Design 3 components for a premium feel.

---

## Architecture

The project follows a **Clean Architecture** approach combined with the **MVVM (Model-View-ViewModel)** pattern to ensure scalability, maintainability, and testability.

- **View**: Activities and Fragments using **ViewBinding** for type-safe interaction.
- **ViewModel**: Manages UI-related data and communication with the Repository/Data layer.
- **Model**: Data structures and Firebase integration logic.

---

## Tech Stack

- **Platform**: Android (Min SDK 24, Target SDK 36)
- **Language**: Java 11
- **Dependency Management**: Gradle (KTS)
- **Backend Services**: Firebase Authentication, Firebase Realtime Database
- **APIs**: Google Maps SDK, Google Play Services Location
- **UI Components**: Material Components, ViewBinding, ImageSlideshow
- **Navigation**: Jetpack Navigation Component

---

## Project Structure

```text
app/src/main/java/com/example/miniproject/
├── adapter/             # RecyclerView adapters for data binding
├── Fragment/            # UI Fragments (Home, Cart, Search, History, Profile)
├── MainActivity.java    # Initial host activity
├── HomeActivity.java    # Primary dashboard activity hosting fragments
├── LoginActivity.java   # Authentication logic
├── SignupActivity.java  # User registration logic
├── OtpActivity.java     # Phone verification management
├── SplashScreen.java    # Application entry branding
└── ...                  # Specialized activities for Location and Maps
```

---

## Getting Started

### Prerequisites

- **Android Studio** (Ladybug or newer recommended)
- **JDK 11**
- **Android Device/Emulator** (API Level 24+)
- **Firebase Account** for backend services

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/EasyCart.git
   ```
2. Open the project in Android Studio.
3. Allow Gradle to sync and download all necessary dependencies.

### Environment Variables

The project uses `local.properties` to manage sensitive information. Add your API keys here:

```properties
# local.properties
MAPS_API_KEY=your_google_maps_api_key_here
```

### Running Locally

1. Connect your Android device via USB or start an emulator.
2. Select the `app` configuration in Android Studio.
3. Click **Run** or press `Shift + F10`.

---

## Configuration

### Firebase Integration
1. Create a project on the [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app with package `com.example.miniproject`.
3. Download `google-services.json` and place it in the `app/` directory.
4. Enable **Email/Password**, **Google**, and **Phone** providers in Authentication.
5. Initialize **Realtime Database** and set appropriate security rules.

---

## Usage Examples

### Navigation Setup
To add a new screen, define the destination in `res/navigation/navigation.xml` and ensure the `id` matches the corresponding item in `res/menu/menu.xml` for automatic handling by `NavigationUI`.

---

## Testing

- **Unit Tests**: Located in `app/src/test/java`. Run via `./gradlew test`.
- **Instrumentation Tests**: Located in `app/src/androidTest/java`. Run via `./gradlew connectedAndroidTest`.

---

## Security

- **Secrets Management**: Sensitive API keys are managed via the `secrets-gradle-plugin` and never committed to version control.
- **Secure Auth**: Implements multi-factor authentication to protect user accounts.
- **Obfuscation**: R8 is configured for release builds to protect the source code.

---

## Performance & Scalability

- **Efficient List Rendering**: RecyclerView with optimized ViewHolder patterns and DiffUtil.
- **Real-time Synchronization**: Firebase Realtime Database provides low-latency data updates.
- **Resource Management**: Vector drawables and optimized image assets to reduce APK size.




