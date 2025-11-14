#!/bin/bash
# Build script that automatically uses Java 17

# Set JAVA_HOME to Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

if [ -z "$JAVA_HOME" ]; then
    echo "❌ Error: Java 17 not found!"
    echo "Please install Java 17 or use: export JAVA_HOME=\$(/usr/libexec/java_home -v 17)"
    exit 1
fi

echo "✅ Using Java: $JAVA_HOME"
echo "   Version: $(java -version 2>&1 | head -1)"
echo ""

# Change to project directory
cd "$(dirname "$0")"

# Make gradlew executable
chmod +x gradlew

# Stop any existing Gradle daemons to ensure they use Java 17
./gradlew --stop 2>/dev/null || true
pkill -f "GradleDaemon" 2>/dev/null || true
pkill -f "KotlinCompileDaemon" 2>/dev/null || true

# Run the gradle command with all arguments passed to this script
./gradlew "$@"

# If build succeeded and user asked for assembleDebug, show APK location
if [ $? -eq 0 ] && [[ "$*" == *"assembleDebug"* ]]; then
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
    if [ -f "$APK_PATH" ]; then
        echo ""
        echo "✅ BUILD SUCCESSFUL!"
        echo "📱 APK location: $APK_PATH"
        echo ""
        echo "To install on your phone:"
        echo "  adb install $APK_PATH"
    fi
fi

