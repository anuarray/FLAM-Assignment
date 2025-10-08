# Android + OpenCV-C++ + OpenGL Assessment + Web

This repository contains a minimal multi-part project for the assessment:

- Android app with Camera feed → JNI (C++) → OpenCV processing → OpenGL ES 2.0 rendering
- TypeScript web viewer that displays a sample processed frame and simple stats

## Structure

```
./app   
./jni   
./gl    
./web   
```

- `app`: Kotlin Android application code (camera, UI, glue)
- `jni`: C++ code using OpenCV exposed via JNI (Canny/Grayscale)
- `gl`: OpenGL ES 2.0 renderer and shaders for textured quad rendering
- `web`: TypeScript viewer (buildable via `tsc`)

## Features Implemented

- Camera feed via CameraX analyzer (Y plane → RGBA)
- JNI bridge with native C++ processing
- OpenCV optional (auto-detected); fallback CPU grayscale/edges if OpenCV is not available
- OpenGL ES 2.0 textured quad rendering
- Toggle between Grayscale and Canny
- FPS text overlay (approximate)
- Web viewer shows a sample processed frame and simple FPS estimate

## Android Setup

Prerequisites:

- Android Studio (Giraffe+)
- Android SDK and NDK installed (SDK Manager → SDK Tools → NDK, CMake)

Open the project in Android Studio and let Gradle sync. If prompted, install missing components.

### OpenCV (Optional)

The native layer attempts to find OpenCV with `find_package(OpenCV QUIET)`. If not found, it builds a fallback implementation for grayscale and edges. To link OpenCV Android SDK:

- Download OpenCV for Android and unzip
- Set `OpenCV_DIR` to the CMake directory for your SDK when configuring/syncing the project (e.g. `.../OpenCV-android-sdk/sdk/native/jni`)
- Re-sync Gradle/CMake; you should see a log line in the CMake output: `OpenCV found: <version>`

### Run

- Connect an Android device (or start an emulator with camera)
- Click Run ▶ in Android Studio
- Grant the camera permission when prompted
- Use the "Toggle Gray/Canny" button to switch modes; watch FPS in the corner

Notes:

- Image flow: CameraX YUV → Kotlin RGBA buffer → JNI → C++ process (OpenCV or fallback) → grayscale bytes → uploaded as RGBA to GL → displayed
- Minimum target: Android 7.0 (API 24)

## Web Viewer

```
cd web
npm install
npm run build
```

Open `web/public/index.html` in a browser (or serve `web/public/` with any static server). It shows a static sample processed frame and FPS estimate.

## Screenshots / GIFs

- Android app running (insert screenshot)
- Web viewer (insert screenshot)

## Architecture Overview

- `CameraProcessor`: CameraX analyzer; converts Y plane → RGBA and calls `NativeBridge.processRgba`
- `NativeBridge`: Loads `edgeproc` and exposes JNI method
- C++: `processFrameRGBA` runs Grayscale or Canny (OpenCV if available; otherwise naive fallback). Returns an 8-bit gray buffer
- `SimpleRenderer`: GL ES renderer; expands grayscale → RGBA on CPU and uploads as a texture; draws full-screen quad
- `GLView`: Wraps `GLSurfaceView` and exposes `uploadGray`
- `MainActivity`: Permissions, mode toggle, FPS text, uploads frames to `GLView`

## Commit History

Meaningful, incremental commits: scaffolding, web viewer, Android Gradle setup, JNI/GL/camera wiring.

## License

MIT


