# GoService

A vehicle service-log and maintenance-reminder app for Indonesia, built with
**Kotlin Multiplatform**: one shared Kotlin core drives a **native UI per
platform** — Jetpack Compose on Android, SwiftUI on iOS.

Track your vehicles, log every service, monitor wearable components, and get
reminded before the next service is due — all stored locally on the device.

> **Scope:** Indonesia-only. Locale, currency, and distance unit are fixed to
> `id-ID`, IDR (Rp), and kilometres; there are no pickers for these.

---

## Features

- **Vehicles** — add motorcycles/cars with nickname, brand, model, year,
  plate, colour, and odometer. Odometer is protected against accidental
  rollback (smaller-than-current needs explicit confirmation).
- **Service records** — log type, date, odometer, workshop, cost (IDR), notes,
  and related components. Logging a higher odometer automatically advances the
  vehicle's odometer. History is **paged from SQLite** with search, per-vehicle
  filter, sorting, and a filtered total-cost summary.
- **Tracked components** — track wearable parts from a catalog (or add custom
  ones). The app derives the next-service schedule (by km and/or date) and
  shows an urgency badge: **Aman / Segera / Terlewat** (OK / Soon / Overdue).
- **Reminders** — trigger by kilometres, by date, or both; snooze, complete,
  or dismiss. Completing a reminder or servicing a component automatically
  closes the open reminder and opens the next cycle. Local notifications fire
  ahead of the due point.
- **Backup & restore** — export a single-file CSV (per-section, per-time-range)
  and share it; import merges by id so re-importing never duplicates data.
- **Onboarding** — guided first-run flow (profile → vehicle type → first
  vehicle → notification permission), resumable and skippable.
- **Profile & settings** — editable profile (name, avatar colour, email) and a
  toggle for service-reminder notifications.

A manual-QA test scenario for the whole app lives in
[`docs/qa/`](./docs/qa/GoService_Test_Scenario_QA.xlsx).

---

## Tech stack

| Area | Choice |
| --- | --- |
| Language | Kotlin 2.3.21 (Multiplatform) |
| Shared core | `:shared` framework consumed by both apps (domain + data + ViewModels + DI) |
| Android UI | Jetpack Compose 1.11 + Material 3 + Navigation Compose |
| iOS UI | SwiftUI (consumes the shared `Shared` framework) |
| Targets | Android (minSdk 24, target/compile 36), iOS (arm64 + simulator arm64) |
| DI | Koin |
| Persistence | Room (KMP, via KSP) on bundled SQLite |
| Async | Kotlinx Coroutines / Flow |
| Date/time | Kotlinx Datetime |
| Serialization | Kotlinx Serialization (JSON) |
| Notifications | WorkManager (Android) + `UNUserNotificationCenter` (iOS), behind a shared scheduler |
| Ads | Yandex Mobile Ads (banner / interstitial / native / app-open) |

---

## Architecture

Clean Architecture, organised by **feature module**. Everything except the UI —
domain, data, presentation (ViewModels), and DI — lives in `:shared`, so both
platforms drive the same logic and state. Each platform then renders its own
native UI on top: Compose on Android, SwiftUI on iOS.

```
shared/src/commonMain/.../feature/<name>/
├── domain/        # models, repository interfaces, use cases (pure Kotlin)
├── data/          # Room DAOs/entities, repository implementations, mappers
├── presentation/  # ViewModels + UI state
└── di/            # Koin module
```

Features: `onboarding`, `profile`, `vehicle`, `service`, `component`,
`reminder`, `backup`, `settings`, `tip`. Cross-cutting helpers live under
`core/` (value types like `Money`/`Distance`/`HexColor`, `AppClock`, paging,
sync metadata, result types, DI).

```
GoService/
├── shared/        # KMP module: domain + data + presentation + DI (the app's brain)
├── composeApp/    # Android app — Jetpack Compose UI + Android entry point
└── iosApp/        # iOS app — SwiftUI UI; open in Xcode
```

---

## Getting started

### Requirements

- JDK 11+
- Android Studio (latest stable) with the Kotlin Multiplatform plugin
- For iOS: macOS + Xcode

Set the Android SDK location in `local.properties`:

```properties
sdk.dir=/path/to/Android/sdk
```

### Run the Android app

Use the run configuration in your IDE, or build from the terminal:

```shell
# macOS / Linux
./gradlew :composeApp:assembleDebug

# Windows
.\gradlew.bat :composeApp:assembleDebug
```

### Run the iOS app

Open [`iosApp/iosApp.xcodeproj`](./iosApp) in Xcode and run. Xcode builds the
`Shared` Kotlin framework as part of its build phase, so no separate Gradle
step is required.

### Tests

```shell
./gradlew :shared:allTests        # KMP common tests
./gradlew :composeApp:testDebugUnitTest
```

---

## Project conventions

- Commit messages are written in **English**.
- Indonesia-only product scope (see note at the top) — do not add locale,
  currency, or distance-unit settings.
