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

- `app`: Java/Kotlin Android application code (camera, UI, glue)
- `jni`: C++ code using OpenCV exposed via JNI (Canny/Grayscale)
- `gl`: OpenGL ES 2.0 renderer and shaders for textured quad rendering
- `web`: TypeScript viewer (buildable via `tsc`)

## Status

- [ ] Android: Camera → JNI → OpenGL pipeline
- [ ] JNI C++ OpenCV processing
- [ ] OpenGL ES renderer
- [x] Web: TypeScript viewer scaffolding

## Getting Started

### Prerequisites

- Android Studio (Giraffe+), Android SDK and NDK
- OpenCV for Android SDK (for native + Java bindings as needed)
- Node.js 18+ and npm for the web viewer

### Web Viewer

```
cd web
npm install
npm run build
```

Open `web/public/index.html` directly in a browser, or serve `web/public/` with any static server.

### Android (coming as implementation proceeds)

High-level plan:

1. Use `TextureView` and Camera2 to acquire frames
2. Pass frames to JNI (NV21/RGBA) → process in C++ with OpenCV (Canny/Gray)
3. Upload processed buffer as GL texture and render with OpenGL ES 2.0
4. Optional toggle raw/processed and FPS counter

Setup notes to be expanded with exact Gradle, CMake, and OpenCV SDK integration commands.

## License

MIT


