# MyBackup - SMB File Sync Android App

A secure, feature-rich Android application for syncing files between local storage and SMB network shares.

## Features

### ✅ Implemented
- **Project Structure**: Complete Gradle setup with all dependencies
- **Database Layer**: Room database with entities for servers, sync configurations, and logs
- **Security**: 
  - AES-GCM encryption using Android Keystore
  - Biometric authentication with PIN fallback
  - Secure credential storage
- **Battery Optimization**: Manufacturer-specific battery management support
- **Material 3 UI**: Modern theme with dynamic colors and dark/light mode
- **Navigation**: Bottom navigation with 4 main sections

### 🚧 In Progress
- SMB Client implementation (JCIFS-NG)
- Sync Engine with all modes (Mirror, Backup, Sync, Download, Move, Archive)
- UI Screens (Servers, Tasks, History, Settings)
- Background sync with WorkManager
- Foreground service with notifications

### 📋 Planned
- Biometric/PIN lock screen
- Server discovery and management
- Sync configuration screens
- Dry-run preview
- History and logging UI
- Advanced filters and options

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Hilt dependency injection
- **Database**: Room
- **Background Work**: WorkManager
- **SMB Library**: JCIFS-NG 2.1.9
- **Security**: Android Keystore, Biometric API
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 34

## Project Structure

```
app/src/main/java/com/mybackup/smbsync/
├── data/
│   ├── local/          # Room database, DAOs, type converters
│   ├── model/          # Data models (SmbServer, SyncConfiguration, SyncLog)
│   └── remote/         # SMB client (to be implemented)
├── di/                 # Hilt dependency injection modules
├── domain/
│   ├── service/        # Sync engine, background workers (to be implemented)
│   └── usecase/        # Business logic (to be implemented)
├── ui/
│   └── theme/          # Material 3 theme
├── util/               # Utilities (encryption, biometric, battery)
└── MainActivity.kt
```

## Building the Project

1. **Prerequisites**:
   - Android Studio Hedgehog or newer
   - JDK 17
   - Android SDK 34

2. **Clone and Open**:
   ```bash
   cd e:\SMBSync
   # Open in Android Studio
   ```

3. **Sync Gradle**:
   - Android Studio will automatically sync Gradle dependencies
   - Wait for the sync to complete

4. **Run**:
   - Connect an Android device (API 29+) or start an emulator
   - Click Run or press Shift+F10

## Permissions

The app requests the following permissions:
- `INTERNET` - SMB connections
- `ACCESS_NETWORK_STATE` - Network status checking
- `MANAGE_EXTERNAL_STORAGE` - Full file system access
- `USE_BIOMETRIC` - Fingerprint authentication
- `FOREGROUND_SERVICE` - Background sync notifications
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` - Reliable background syncs
- `POST_NOTIFICATIONS` - Android 13+ notifications

## Database Schema

### SmbServer
- Server configurations with encrypted credentials
- SMB protocol version selection (SMB1/2/3)

### SyncConfiguration
- Comprehensive sync settings
- 6 sync modes: Mirror, Backup, Sync, Download, Move, Archive
- 7 conflict resolution strategies
- Advanced comparison options (time tolerance, DST, hash)
- File filters (size, date, hidden files, extensions)
- Scheduling (manual, interval, daily)
- Network and battery preferences

### SyncLog
- Transaction history
- Statistics (files copied, deleted, skipped, failed)
- Error messages and detailed logs

## Security Features

- **Credential Encryption**: AES-256-GCM with Android Keystore
- **Biometric Auth**: Fingerprint with PIN fallback
- **PIN Storage**: SHA-256 hashed in DataStore
- **Battery Optimization**: Manufacturer-specific whitelist support

## Next Steps

1. Implement SMB client wrapper
2. Build sync engine with all modes
3. Create UI screens for each section
4. Implement background sync workers
5. Add foreground service with notifications
6. Build authentication screens
7. Comprehensive testing

## License

This project is created for personal use.

## Acknowledgments

- Inspired by SMBSync2
- Uses JCIFS-NG for SMB protocol support
- Material 3 design guidelines
