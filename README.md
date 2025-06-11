# Master

TODO:

## release alpha
* create SceneGraphs
* manage resources
* install adMob

## release beta
* create MatchingSystem

## Compatibility Notice

This project was created in 2013 and uses older versions of various tools:
- **SBT**: 0.13.18 (originally 0.12.4)
- **Scala**: 2.10.3
- **LibGDX**: Game development framework
- **Android SDK Plugin**: For Android builds

### Known Issues

The project is **incompatible with modern Java versions** (Java 17+) due to the old SBT version's reliance on the deprecated Security Manager. You will encounter this error with modern Java:
```
java.lang.UnsupportedOperationException: The Security Manager is deprecated and will be removed in a future release
```

## How to Build

### Option 1: Using Java 8 (Recommended)

1. Install Java 8 (required for compatibility with SBT 0.13.18)
2. Clone this repo
3. Install Scala 2.10.3 and SBT
4. Navigate to the cloned directory in terminal
5. Run `sbt`
6. Once SBT loads, type `desktop/run` to launch the desktop version
7. To see the smoke demo, select "TransformFeedback" and press "Launch" button

### Option 2: Modern Setup (Requires Project Migration)

To run on modern systems, the project would need to be migrated to:
- SBT 1.x
- Scala 2.13.x or 3.x
- Updated LibGDX version
- Modern Android build tools

## Desktop Application Parameters

The desktop launcher supports various command-line options:
- `-f, --file_check`: Enable file checking
- `-r, --res_dir <dir>`: Specify resources directory
- `-s, --screen <file>`: Specify JSON screen config file
- `-t, --test <name>`: Specify test screen name
- `-g, --game_class <class>`: Specify game class name
- `-h, --height <pixels>`: Set window height
- `-w, --width <pixels>`: Set window width
