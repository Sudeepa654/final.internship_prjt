# Surya Shakti Solar Monitor

Surya Shakti is a full Jetpack Compose Android app for solar-powered homes. It uses a yellow/black high-contrast theme, Room local database, MVVM architecture, Repository pattern, Coroutines, Flow, and Material 3.

## Features

- Login, register, and forgot password screens.
- Local Room authentication using email and password.
- User profile stored locally with name, email, home location, solar capacity, and electricity unit rate.
- Luxury-style dashboard with solar/grid circular progress, daily generation, consumption, battery, savings, independence score, export, and suggestions.
- Generation log with date, weather, solar generation, and battery level.
- Weather simulation:
  - Sunny: high generation
  - Cloudy: medium generation
  - Rainy: low generation
- Consumption tracker with previous/current meter readings and manual units consumed.
- Automatic grid usage, solar usage, net usage, exported-to-grid, and savings calculation.
- 30-day savings report with totals, a simple chart, recent entries, and exported energy.
- Smart suggestions for high sun, low battery, high consumption, and over-generation.
- Bottom navigation: Dashboard, Add Log, Reports, Profile.

## Project Structure

- `app/src/main/java/com/example/suryashaktimain/MainActivity.kt`: app entry point and dependency setup.
- `data/local/UserEntity.kt`: Room users table.
- `data/local/EnergyLogEntity.kt`: Room energy logs table.
- `data/local/UserDao.kt`: user queries for register, login, and password reset.
- `data/local/EnergyLogDao.kt`: energy log insert and report queries.
- `data/local/AppDatabase.kt`: Room database class.
- `data/repository/SolarRepository.kt`: database access layer used by ViewModels.
- `domain/EnergyCalculator.kt`: all solar, grid, export, savings, report, and suggestion formulas.
- `domain/WeatherCondition.kt`: weather options used by simulation.
- `viewmodel/AuthViewModel.kt`: login/register/reset/logout UI state.
- `viewmodel/EnergyViewModel.kt`: dashboard, log saving, and report UI state.
- `viewmodel/SolarViewModelFactory.kt`: creates ViewModels with the repository.
- `navigation/SuryaShaktiApp.kt`: authentication navigation plus bottom navigation.
- `ui/auth`: login, register, and forgot password screens.
- `ui/dashboard`: dashboard screen.
- `ui/logs`: generation log and consumption tracker screen.
- `ui/reports`: 30-day report screen.
- `ui/profile`: profile and logout screen.
- `ui/components`: reusable cards, fields, progress, chart, and formatters.
- `ui/theme`: yellow/black Compose theme.

## How To Open In Android Studio

1. Open Android Studio.
2. Choose **File > Open**.
3. Select this folder:
   `C:\Users\ADMIN\OneDrive\Desktop\my code\suryashaktimain`
4. Wait for Gradle sync to finish.
5. If Android Studio asks for an SDK, install/select API 35 or API 34.

## How To Run

1. Start an Android emulator or connect a phone with USB debugging enabled.
2. Select the `app` run configuration.
3. Click **Run**.
4. The app starts on the login screen.

## How Frontend Connects With Room

1. `MainActivity` creates `AppDatabase`.
2. `MainActivity` passes `UserDao` and `EnergyLogDao` into `SolarRepository`.
3. `AuthViewModel` calls the repository for register, login, and forgot password.
4. `EnergyViewModel` observes Room log data as `Flow`.
5. Compose screens collect ViewModel `StateFlow` using `collectAsStateWithLifecycle`.
6. When you save a log, Room stores it and emits fresh data automatically.
7. Dashboard and Reports update because they are observing the same Room-backed state.

## Main Calculations

- `Units consumed = current meter reading - previous meter reading`, unless manual units are entered.
- `Solar usage = min(solar generation, consumption)`.
- `Grid usage = max(consumption - solar generation, 0)`.
- `Net usage = grid usage`.
- `Exported to Grid = max(solar generation - consumption, 0)`.
- `Net Savings = solar units used x per-unit electricity rate`.
- `Green Energy Independence Score = solar usage share + small battery bonus`, capped at 100%.

## Demo Test

1. Tap **Create a new account**.
2. Enter:
   - Name: `Aarav`
   - Email: `aarav@example.com`
   - Password: `1234`
   - Home location: `Pune`
   - Solar panel capacity: `3`
   - Electricity unit rate: `8`
3. The app logs you in and opens the dashboard.
4. Open **Add Log**.
5. Select `Sunny`, tap **Simulate from weather**, then enter:
   - Previous meter reading: `1000`
   - Current meter reading: `1010`
   - Units consumed: leave empty or enter `10`
6. Tap **Save log**.
7. Check Dashboard and Reports.
8. Add another log where solar generation is more than consumption to test **Exported to Grid**.
9. Open Profile and tap Logout.
10. Login again with `aarav@example.com` and `1234` to confirm Room DB authentication.

## Internship Explanation

This project is local-first, so it does not need a server. Room works like the backend database inside the phone. Compose is the frontend. ViewModels sit between them: the UI sends actions to ViewModels, ViewModels call the repository, and the repository talks to Room. The formulas are separated in `EnergyCalculator`, which makes the app easier to explain, test, and extend.
