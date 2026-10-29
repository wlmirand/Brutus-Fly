# Brutus Fly

A native Android client for [Firefly III](https://www.firefly-iii.org/) to browse bank accounts and track your finances through a modern, intuitive interface.

Connect to your own Firefly III server, choose an account, and explore your transactions and categories.

## Features

- **Accounts:** view account balances and select an account to explore its activity.
- **Transactions:** browse transactions grouped by date, with daily totals kept separate by currency.
- **Search and filters:** search by description, category, or account, and filter by income, expenses, category, and calendar period.
- **Categories:** browse and search categories, with customizable icons saved locally on your device.
- **Pagination:** load additional accounts, transactions, and categories as you scroll, with refresh and retry actions.
- **Appearance:** Material 3 interface with light and dark themes.

Search and filters apply only to pages already loaded. The current interface focuses on browsing accounts, transactions, and categories; it does not create or edit financial records on the server.

## Getting started

### Requirements

- Android 13 (API 33) or newer.
- A Firefly III server reachable from your device over HTTPS, with a valid TLS certificate.
- A Firefly III personal access token.

### Connect your server

1. Open the app and enter your Firefly III installation URL, such as `https://firefly.example.com/`.
2. Enter your personal access token.
3. Tap **Verify and save**. The app checks the server before saving the connection settings.
4. Select an account in **Accounts**, then open **Transactions** to view its activity.

Use the installation root URL, not the `/api/v1` endpoint. Installations in subdirectories, such as `https://example.com/firefly/`, are supported.

HTTP connections, invalid TLS certificates, and authenticated redirects are not supported. Use the final HTTPS URL of your installation.

## Security

- Personal access tokens are encrypted with AES-GCM using a key managed by Android Keystore.
- Connection settings and local preferences are stored with DataStore.
- Authenticated requests are restricted to the configured HTTPS origin and installation path.
- Android backup is disabled for the app.

Changing the server URL or token clears the saved account selection and local category icon assignments. Keep your access token private and never include it in issues, screenshots, or commits.

## Built with

- Kotlin and coroutines
- Jetpack Compose and Material 3
- Navigation 3 and ViewModel
- Hilt for dependency injection
- Retrofit, OkHttp, and Kotlin Serialization
- DataStore Preferences and Android Keystore

## Build from source

Open the repository in an Android Studio version compatible with the Android Gradle Plugin version declared in [`gradle/libs.versions.toml`](gradle/libs.versions.toml), then sync the project.

The project compiles against **Android SDK 37**. Gradle's daemon JVM criteria specify **JDK 25**, while Java and Kotlin compilation target **JVM 21**. Ensure the required SDK and JDK are available, and configure your Android SDK location through Android Studio or `local.properties`.

To build a debug APK:

```sh
./gradlew :app:assembleDebug
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

## Project structure

| Module | Responsibility |
| --- | --- |
| `app` | Screens, navigation, domain models, use cases, repositories, and Firefly III integration |
| `core/common` | Shared mapper and use case contracts |
| `core/designsystem` | Reusable Compose components, themes, and design tokens |
| `core/network` | Shared network result models and Retrofit call adapters |
| `core/ui` | Shared ViewModel and MVI infrastructure |

Feature implementation notes are available in [`docs/`](docs/).

## Feedback

Bug reports and feature suggestions are welcome through GitHub Issues. Include reproduction steps and relevant device information, but remove tokens and sensitive financial data.
