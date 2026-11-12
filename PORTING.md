# Flovex Android port

This branch replaces the previous Compose Multiplatform prototype as the Android launcher implementation. It uses the Android Minecraft Java launcher engine from Zalith Launcher 2 rather than attempting to recreate its runtime, native libraries, version installer, loader integrations, renderer support, and game process management.

## Pinned upstream

- Repository: https://github.com/ZalithLauncher/ZalithLauncher2
- Commit: `4607f153fcce149a8e0ad39dc93fc447589d9f17`
- License: GPL-3.0
- Required attribution: retain upstream copyright and third-party notices; identify this app as an unofficial modified version.

## Flovex changes to implement on this branch

1. Device-local Flovex account registration and login. A username is unique on this device; registration stores a salted password hash, never plaintext. A local-only account cannot reserve a name globally across devices.
2. A first-run Android Storage Access Framework folder grant, with the chosen game directory named `fl`, and persisted URI permissions.
3. Add the FlovexSMP Java server entry: `flovexsmp.mc-host.com:12999`.
4. Preserve the upstream launcher engine and its game, version, loader, mod, resource-pack, control, and renderer capabilities.

## Build

The GitHub Actions workflow checks out the pinned upstream commit, applies the Flovex branding overlay, builds the arm64 debug APK, and uploads it as an artifact. The first port build is a base-engine validation; it is not considered feature-complete until the account gate, SAF folder integration, and server preset have been implemented and tested.

The app is Android-only on this branch. The previous desktop prototype remains on `main`.
