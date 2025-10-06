const imageBase64 =
  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAQAAAB4pVU0AAAAjElEQVR42u3XMQrCMBQF0Jj5/6y0k7i0qg0s4mB0yJ8zW8mQKxv4W2P7eQk2C0R8i3nB0p2wWcJ1i8j0JmD3iJ7G5x4b+o2mZbK3n4kqk9o0w0vE0w8wS1CwV5XwCz6cH8f7m3kq1g+7bJ1rCkq8V4Wm2l8y3QGv1C0c7pKqEw3S8M1rGgqzv2v9gC9HjV8Tt1cPzW5m0g0cY3f0Cj9C2a4S5b3CkAAAAASUVORK5CYII=";

function setText(id: string, text: string) {
  const el = document.getElementById(id);
  if (el) el.textContent = text;
}

function loadImage(): Promise<HTMLImageElement> {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.onload = () => resolve(img);
    img.onerror = reject;
    img.src = imageBase64;
  });
}

async function main() {
  const canvas = document.getElementById("canvas") as HTMLCanvasElement;
  const ctx = canvas.getContext("2d");
  if (!ctx) return;

  const start = performance.now();
  const img = await loadImage();
  const width = img.width;
  const height = img.height;
  canvas.width = width;
  canvas.height = height;
  ctx.drawImage(img, 0, 0);

  const end = performance.now();
  const elapsedMs = end - start;
  const fps = (1000 / elapsedMs).toFixed(1);
  setText("stats", `FPS: ~${fps} | Resolution: ${width}x${height}`);
}

window.addEventListener("DOMContentLoaded", () => {
  void main();
});
