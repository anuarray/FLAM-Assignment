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

## Features Implemented (Android + Web)

- [x] Camera feed via CameraX analyzer (Y plane → RGBA)
- [x] JNI bridge with native C++ processing
- [x] OpenCV optional (auto-detected); fallback CPU grayscale/edges if OpenCV is not available
- [x] OpenGL ES 2.0 textured quad rendering
- [x] Toggle between Grayscale and Canny
- [x] FPS text overlay (approximate)
- [x] Web viewer shows a sample processed frame and FPS estimate

## Screenshots / GIFs

Add your own captures here (place files under a `docs/` folder and update the paths):

- Android app (Grayscale):
  ![unnamed](https://github.com/user-attachments/assets/5fd53a53-9223-418f-a837-075ede00b4a1)


- Android app (Canny):
  ![unnamed](https://github.com/user-attachments/assets/59d195f5-6ea9-4285-8d29-83a3db6d3e1b)


- Web viewer:
![unnamed](https://github.com/user-attachments/assets/5e51fcef-de2e-4939-95ac-9465b69ee808)

 

## Setup Instructions

### Android Prerequisites

- Android Studio (Giraffe+)
- Android SDK platform(s)
- NDK + CMake (SDK Manager → SDK Tools → check both and apply)

### OpenCV (Optional but recommended)

If you want native OpenCV instead of fallback:

1) Download and unzip OpenCV for Android.
2) In Android Studio, set `OpenCV_DIR` for CMake:
   - File → Settings → Build, Execution, Deployment → CMake → select your profile → CMake options:
   - `-DOpenCV_DIR="C:/path/to/OpenCV-android-sdk/sdk/native/jni"`
3) Sync/Rebuild. You should see: `OpenCV found: <version>` in the CMake output.

If not configured, the app still works using the built-in fallback grayscale/edge processor.

### Run (Android)

- Connect a device (or start an emulator with camera)
- Click Run ▶ in Android Studio
- Grant the Camera permission
- Use the bottom toggle to switch Gray/Canny; FPS shows top-right

### Web Viewer

```
cd web
npm install
npm run build
```
Open `web/public/index.html` directly in a browser or serve `web/public/` with any static server.

## Architecture (JNI, Frame Flow, TypeScript)

- Kotlin/CameraX: `CameraProcessor` gets YUV frames, uses Y plane as luma to form an RGBA buffer (simple/fast path for demo) and calls `NativeBridge.processRgba(rgba, w, h, stride, mode)`.
- JNI/C++: `processFrameRGBA` converts RGBA → Gray and runs either Grayscale or Canny.
  - If OpenCV is available (via `find_package(OpenCV)`), uses `cv::cvtColor` + `cv::Canny`.
  - Otherwise, uses a simple CPU grayscale and Sobel-like fallback.
- Renderer: `SimpleRenderer` expands the returned 8-bit grayscale into RGBA and uploads as a GL texture; draws full-screen quad.
- UI: `MainActivity` wires permission, the CameraX analyzer, JNI calls, GL upload via `GLView.queueEvent`, and updates FPS.
- Web: a minimal TS app that draws a base64 image to a canvas and shows a simple FPS estimate.

## Notes

- Minimum target: Android 7.0 (API 24)
- You can swap CameraX to Camera2 or add shader-based effects as bonus
- Commit history is incremental (scaffolding → web → Android/NDK → JNI/GL/Camera wiring → docs)

## License

MIT


"# FLAM-Assignment" 
