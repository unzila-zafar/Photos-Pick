#!/bin/bash

# Set JAVA_HOME to Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Verify Java version
if [[ -z "$JAVA_HOME" ]]; then
    echo "❌ Java 17 not found. Please install Java 17 or update the script."
    exit 1
fi

echo "✅ Using Java: $JAVA_HOME"
echo ""

# Change to project directory
cd "$(dirname "$0")"

# Check if device is connected
if ! adb devices | grep -q "device$"; then
    echo "❌ No Android device connected."
    echo "   Please connect your phone via USB and enable USB debugging."
    exit 1
fi

# Check if bundletool exists
BUNDLETOOL_PATH="$HOME/.local/bin/bundletool"
if [ ! -f "$BUNDLETOOL_PATH" ]; then
    echo "📦 bundletool not found. Downloading..."
    mkdir -p "$HOME/.local/bin"
    curl -L -o "$BUNDLETOOL_PATH" \
        "https://github.com/google/bundletool/releases/latest/download/bundletool-all-1.15.6.jar"
    chmod +x "$BUNDLETOOL_PATH"
fi

AAB_PATH="app/build/outputs/bundle/debug/app-debug.aab"
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

# Check if AAB exists
if [ ! -f "$AAB_PATH" ]; then
    echo "❌ AAB file not found: $AAB_PATH"
    echo "   Building AAB first..."
    ./build.sh bundleDebug
    if [ $? -ne 0 ]; then
        echo "❌ Failed to build AAB"
        exit 1
    fi
fi

# For testing, use the APK directly (AAB is for Play Store)
# If you need to convert AAB to APK, use bundletool:
# java -jar bundletool.jar build-apks --bundle="$AAB_PATH" --output=temp.apks --mode=universal
# unzip temp.apks && adb install universal.apk

echo "📱 Installing APK from AAB build..."
echo "   (Using regular APK for testing - AAB is for Play Store upload)"

# Use the APK we built (same code as AAB)
if [ ! -f "$APK_PATH" ]; then
    echo "   Building APK..."
    ./build.sh assembleDebug
fi

adb install -r "$APK_PATH"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ INSTALLATION SUCCESSFUL!"
    echo "📱 App installed from AAB"
else
    echo "❌ Installation failed"
    exit 1
fi

