# Flovex Launcher

A cross-platform Minecraft launcher application built with Kotlin and Compose Multiplatform.

## Current project status

This repository starts with the application foundation. The launcher UI is an early scaffold; Microsoft authentication, Minecraft installation/launching, mod management, and FPS optimization are planned modules and are not yet implemented.

## Targets

- Windows, macOS, and Linux through the Compose Desktop target.
- Android through the Android application target.
- iOS and other platforms are not enabled yet.

## Requirements

- JDK 17 or newer
- Gradle 8.13 or compatible wrapper
- Android SDK for Android builds

## Build

After generating or installing a Gradle wrapper:

- Desktop: `gradle :composeApp:run`
- Android debug APK: `gradle :composeApp:assembleDebug`

## Architecture

- `commonMain`: shared UI and application state.
- `desktopMain`: desktop entry point and future desktop integrations.
- `androidMain`: Android entry point and future Android integrations.

## Planned milestones

1. Launcher shell and navigation.
2. Local profile management.
3. Official Microsoft/Minecraft authentication and entitlement checks.
4. Minecraft version metadata, downloads, and game launching.
5. Fabric/Forge/NeoForge installation and mod management.
6. Safe, reversible performance profiles using compatible optimization mods.
7. Platform-specific packaging and automated tests.

The launcher must use official authentication and must not bypass Minecraft ownership checks. Local profiles are for offline-mode environments only.
