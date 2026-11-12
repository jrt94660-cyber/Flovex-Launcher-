# Flovex Launcher

A cross-platform Minecraft launcher application built with Kotlin and Compose Multiplatform.

## Current project status

The application shell and FlovexSMP server monitor are implemented. Minecraft account authentication, game installation/launching, mod management, and FPS optimization remain under development; this is not yet a complete playable launcher.

## FlovexSMP server

The Home screen and Server page display the configured server:

- Address: `flovexsmp.mc-host.com`
- Port: `12999`
- Status: Minecraft Java Edition Server List Ping (not merely a TCP port check).
- Refresh: automatic every 30 seconds while the app is open, plus a manual Refresh button.
- When reachable, the monitor shows server version and online/max player count when supplied by the server.

A failed ping is shown as OFFLINE; connection timeout or an invalid response is surfaced in the detail text. Server status is not guaranteed while the launcher is closed.

## Targets

- Windows, macOS, and Linux through the Compose Desktop target.
- Android through the Android application target.
- iOS and other platforms are not enabled yet.

## Requirements

- JDK 17 or newer
- Gradle 8.14 or compatible wrapper
- Android SDK for Android builds

## Build

- Desktop: `gradle :composeApp:run`
- Android debug APK: `gradle :composeApp:assembleDebug`

## Architecture

- `commonMain`: shared UI and server monitor presentation.
- `desktopMain`: desktop entry point and Java Edition server ping.
- `androidMain`: Android entry point and Java Edition server ping.

## Planned milestones

1. Launcher shell and FlovexSMP server status monitor.
2. Local profile management.
3. Official Microsoft/Minecraft authentication and entitlement checks.
4. Minecraft version metadata, downloads, and game launching.
5. Fabric/Forge/NeoForge installation and mod management.
6. Safe, reversible performance profiles using compatible optimization mods.
7. Platform-specific packaging and automated tests.

The launcher must use official authentication and must not bypass Minecraft ownership checks. Local profiles are for offline-mode environments only.
