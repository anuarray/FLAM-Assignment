package com.example.edgeviewer

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraProcessor(
	private val context: Context,
	private val lifecycleOwner: LifecycleOwner,
	private val listener: (grayBytes: ByteArray, width: Int, height: Int) -> Unit,
	private val mode: () -> Int
) {
	private lateinit var cameraExecutor: ExecutorService

	@SuppressLint("UnsafeOptInUsageError")
	fun start() {
		cameraExecutor = Executors.newSingleThreadExecutor()
		val provider = ProcessCameraProvider.getInstance(context).get()

		val analysis = ImageAnalysis.Builder()
			.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
			.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
			.build()

		analysis.setAnalyzer(cameraExecutor) { imageProxy ->
			val img = imageProxy.image
			if (img != null) {
				processImage(img) { gray, w, h ->
					listener(gray, w, h)
				}
			}
			imageProxy.close()
		}

		val selector = CameraSelector.DEFAULT_BACK_CAMERA
		provider.unbindAll()
		provider.bindToLifecycle(lifecycleOwner, selector, analysis)
	}

	private fun yuvToRgba(image: Image): Triple<ByteArray, Int, Int> {
		val width = image.width
		val height = image.height
		val yPlane = image.planes[0]
		val yBuf = yPlane.buffer
		val rgba = ByteArray(width * height * 4)
		var di = 0
		if (yPlane.rowStride == width) {
			val yBytes = ByteArray(width * height)
			yBuf.get(yBytes)
			for (i in 0 until yBytes.size) {
				val v = yBytes[i]
				rgba[di] = v; rgba[di+1] = v; rgba[di+2] = v; rgba[di+3] = -1; di += 4
			}
		} else {
			for (row in 0 until height) {
				val rowBuf = ByteArray(width)
				yBuf.position(row * yPlane.rowStride)
				yBuf.get(rowBuf, 0, width)
				for (x in 0 until width) {
					val v = rowBuf[x]
					rgba[di] = v; rgba[di+1] = v; rgba[di+2] = v; rgba[di+3] = -1; di += 4
				}
			}
		}
		return Triple(rgba, width, height)
	}

	private fun processImage(image: Image, out: (ByteArray, Int, Int) -> Unit) {
		val (rgba, w, h) = yuvToRgba(image)
		val stride = w * 4
		val processed = NativeBridge.processRgba(rgba, w, h, stride, mode())
		if (processed != null) out(processed, w, h)
	}
}
