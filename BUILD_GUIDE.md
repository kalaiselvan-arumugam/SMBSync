# Building MyBackup - Step-by-Step Guide

## Prerequisites Check

Before building, ensure you have:
- ✅ Android Studio Hedgehog (2023.1.1) or newer
- ✅ JDK 17 (bundled with Android Studio)
- ✅ Android SDK 34

## Method 1: Build with Android Studio (Recommended)

### Step 1: Open the Project
1. Launch Android Studio
2. Click **File → Open**
3. Navigate to `e:\SMBSync`
4. Click **OK**

### Step 2: Sync Gradle
1. Android Studio will automatically detect the project
2. A banner will appear: "Gradle files have changed"
3. Click **Sync Now**
4. Wait for Gradle sync to complete (first time may take 5-10 minutes)
   - Downloads Gradle 8.2
   - Downloads all dependencies (JCIFS-NG, Room, Compose, etc.)
   - Builds project structure

### Step 3: Build the Project
Once Gradle sync completes:

**Option A: Build APK**
1. Click **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Wait for build to complete
3. APK location: `e:\SMBSync\app\build\outputs\apk\debug\app-debug.apk`

**Option B: Run on Device/Emulator**
1. Connect Android device (USB debugging enabled) OR start an emulator
2. Click the **Run** button (▶) or press `Shift+F10`
3. Select your device
4. App will build, install, and launch automatically

## Method 2: Command Line Build (If Gradle is Installed)

If you have Gradle installed globally:

```powershell
cd e:\SMBSync

# Build debug APK
gradle assembleDebug

# Or with wrapper (after first Android Studio sync)
.\gradlew.bat assembleDebug
```

## Method 3: Install Gradle Manually

If you want to use command line without Android Studio:

### Install Gradle
1. Download Gradle 8.2 from https://gradle.org/releases/
2. Extract to `C:\Gradle\gradle-8.2`
3. Add to PATH: `C:\Gradle\gradle-8.2\bin`
4. Verify: `gradle --version`

### Build
```powershell
cd e:\SMBSync
gradle wrapper
.\gradlew.bat assembleDebug
```

## Expected Build Output

### Successful Build
```
BUILD SUCCESSFUL in 2m 15s
45 actionable tasks: 45 executed
```

APK will be at:
```
e:\SMBSync\app\build\outputs\apk\debug\app-debug.apk
```

### Common Issues

#### Issue 1: "SDK location not found"
**Solution**: Create `local.properties` in project root:
```properties
sdk.dir=C\:\\Users\\<YourUsername>\\AppData\\Local\\Android\\Sdk
```

#### Issue 2: "Gradle sync failed"
**Solution**: 
- Check internet connection (needs to download dependencies)
- Clear Gradle cache: Delete `C:\Users\<YourUsername>\.gradle\caches`
- Restart Android Studio

#### Issue 3: "Compilation errors"
**Solution**:
- Ensure JDK 17 is selected: **File → Project Structure → SDK Location**
- Invalidate caches: **File → Invalidate Caches / Restart**

#### Issue 4: "AAPT2 error"
**Solution**:
- Update Android SDK Build Tools to latest version
- **Tools → SDK Manager → SDK Tools → Android SDK Build-Tools**

## Verification

After successful build:

1. **Check APK exists**:
   ```powershell
   Test-Path e:\SMBSync\app\build\outputs\apk\debug\app-debug.apk
   ```

2. **Check APK size** (should be ~15-25 MB):
   ```powershell
   (Get-Item e:\SMBSync\app\build\outputs\apk\debug\app-debug.apk).Length / 1MB
   ```

3. **Install on device**:
   ```powershell
   adb install e:\SMBSync\app\build\outputs\apk\debug\app-debug.apk
   ```

## Next Steps After Build

1. **Run the app** on a device or emulator
2. **Verify**:
   - App launches without crashes
   - Bottom navigation works
   - Theme switches (light/dark)
   - No permission errors

## Current Limitations

The app currently shows placeholder screens. Full functionality requires:
- Authentication UI implementation
- Server management screens
- Sync configuration screens
- Sync engine implementation

See [walkthrough.md](file:///C:/Users/admin/.gemini/antigravity/brain/3446258b-fbf0-4171-9692-99923cf26334/walkthrough.md) for details.

## Troubleshooting

### Gradle Daemon Issues
```powershell
# Stop all Gradle daemons
.\gradlew.bat --stop

# Clean build
.\gradlew.bat clean
.\gradlew.bat assembleDebug
```

### Android Studio Performance
- Increase heap size: **Help → Edit Custom VM Options**
- Add: `-Xmx4096m`

### Build Cache Issues
```powershell
# Clean project
.\gradlew.bat clean

# Delete build folders
Remove-Item -Recurse -Force .\build, .\app\build
```

## Build Variants

- **Debug**: `assembleDebug` - Includes debugging symbols, not optimized
- **Release**: `assembleRelease` - Optimized, requires signing key

For now, use **Debug** variant for testing.
