# Flovex Launcher

Flovex Launcher is a Kotlin Multiplatform desktop and Android application for Minecraft Java Edition.

## Implemented so far

- Dark launcher shell with Home, Server, Versions, Mods, Performance, Accounts, and Settings navigation.
- FlovexSMP Java Edition server monitor at `flovexsmp.mc-host.com:12999`.
- Minecraft Java Server List Ping, with automatic 30-second refresh while the app is open and a manual refresh action.
- Mojang official version manifest catalogue, with release/snapshot filtering.
- Local offline profile creation, selection, removal, and persistence on desktop and Android.

## Not implemented yet

The app is still an early development build and is not yet a complete playable launcher.

- Microsoft OAuth device-code sign-in, Xbox/XSTS authentication, Minecraft token exchange, and ownership/entitlement checks.
- Downloading and installing Minecraft client versions, libraries, assets, and Java runtimes.
- Launching the game and managing Java arguments/RAM.
- Fabric, Forge, NeoForge, Quilt, modpacks, resource packs, and shaders.
- FPS optimization profiles and compatibility checks.
- Android Java Edition runtime/launch support.

## Planned account system

- Flovex accounts use globally unique usernames reserved in a central account database.
- Registration requires a password; existing usernames require the matching password to sign in.
- Passwords must be stored as salted password hashes (never plaintext), with server-side rate limiting and secure session handling.
- A central authentication service is required for accounts to work across devices. Local-only profiles cannot provide global username reservations.
- Offline Minecraft identity is still not Mojang/Microsoft verified. The Flovex authentication service and compatible game server must enforce the account-to-player identity mapping; launcher-only checks are not sufficient to prevent impersonation.

## Planned game customization

- Mod manager: import local .jar mods, enable/disable, remove, and organize by game profile, Minecraft version, and mod loader.
- Resource-pack manager: import .zip packs, enable/disable, remove, and reorder pack priority.
- Controls editor: remap keyboard and mouse bindings, adjust supported sensitivity settings, and restore defaults.
- Compatibility checks for Minecraft version, mod loader, and conflicting mods.
- Keep mods, resource packs, and control settings isolated per game profile.
- Android touch controls and desktop keyboard/mouse controls need platform-specific handling.

## Server monitor

- Address: `flovexsmp.mc-host.com`
- Port: `12999`
- Uses Minecraft Java Edition Server List Ping, not only a TCP port test.
- Refreshes automatically every 30 seconds while the app is open.
- Shows version and player count when the server includes those values in its status response.
- Status cannot be monitored while the launcher is closed.

## Platform targets

- Windows, macOS, and Linux: Compose Desktop/JVM.
- Android: Compose Android application.
- iOS is not configured.

## Build

Requirements: JDK 17+, Gradle 8.14, and Android SDK for Android packaging.

```bash
gradle :composeApp:compileKotlinDesktop
gradle :composeApp:run
gradle :composeApp:assembleDebug
```

GitHub Actions compiles the desktop target and attempts to package an Android debug APK on pushes to `main`.

## Authentication and account safety

Local profiles are offline display-name profiles only. They do not authenticate with Microsoft, prove Minecraft ownership, or bypass online-mode authentication. The completed launcher will use official Microsoft/Minecraft authentication and verify game entitlement before online play.
