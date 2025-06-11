# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Match3 puzzle game built with Scala and LibGDX framework. The project supports both desktop (via LWJGL) and Android platforms using a custom SBT-based build system.

## Technology Stack

- **Language**: Scala 2.10.3
- **Game Framework**: LibGDX
- **Build System**: SBT 0.12.4
- **Android Plugin**: Custom android-sdk-plugin 1.2.9 (included in project)
- **Functional Libraries**: Scalaz 7.0.4, Shapeless 1.2.4
- **Desktop Backend**: LWJGL
- **Testing**: ScalaTest 1.9.2

## Build Commands

### Desktop Version
```bash
# Run desktop version
sbt desktop/run

# Run with command-line options
sbt "desktop/run --width 800 --height 600"

# Package desktop JAR
sbt desktop/package-dist
```

### Android Version
```bash
# Package Android APK
sbt android/package

# Install to device
sbt android/install

# Run on device
sbt android/run

# Clean Android build
sbt android/clean
```

### Common Tasks
```bash
# Compile all projects
sbt compile

# Run tests
sbt test

# Clean all projects
sbt clean

# Enter SBT interactive mode
sbt
```

## Command-Line Options

The desktop version supports these options:
- `--width`: Window width (default: 640)
- `--height`: Window height (default: 480)
- `--fullscreen`: Enable fullscreen mode
- `--fps`: Frames per second limit
- `--script`: JavaScript file to execute
- `--no-sound`: Disable sound

## Project Structure

```
Match3/
├── core/           # Game logic and shared code
│   └── src/
│       └── main/
│           └── scala/
│               └── org/jliszka/match3/
│                   ├── Board.scala      # Game board logic
│                   ├── Game.scala       # Main game mechanics
│                   ├── Match3.scala     # Core game class
│                   └── screens/         # Game screens
├── desktop/        # Desktop-specific launcher
│   └── src/
│       └── main/
│           └── scala/
│               └── Main.scala          # Desktop entry point
├── android/        # Android-specific code
│   ├── AndroidManifest.xml
│   ├── assets/     # Game assets (shared with desktop)
│   └── src/
│       └── com/jliszka/match3/
│           └── MainActivity.scala      # Android entry point
├── tools/          # Development tools
├── macro/          # Scala macros
└── android-sdk-plugin/  # Custom Android SBT plugin
```

## Key Classes

- **Match3**: Main game class extending ApplicationListener
- **Board**: Game board representation and logic
- **Game**: Core game mechanics and rules
- **Main (desktop)**: Desktop launcher with command-line parsing
- **MainActivity (android)**: Android activity launcher

## Development Notes

1. The project uses a custom Android SDK plugin maintained within the repository
2. Assets are stored in `android/assets/` and shared between platforms
3. The game uses functional programming patterns extensively (Scalaz, Shapeless)
4. Command-line options are parsed using scopt library in the desktop version

## Common Issues and Solutions

1. **Android SDK Path**: Ensure ANDROID_HOME is set or create local.properties with sdk.dir
2. **SBT Version**: This project requires SBT 0.12.4 (older version)
3. **Asset Loading**: Assets must be placed in android/assets/ directory

## Testing

Run tests with:
```bash
sbt test
```

For specific test suites:
```bash
sbt "test-only *BoardTest"
```