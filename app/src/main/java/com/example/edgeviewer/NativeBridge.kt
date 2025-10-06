package com.example.edgeviewer

object NativeBridge {
	init {
		System.loadLibrary("edgeproc")
	}

	@JvmStatic external fun processRgba(
		rgba: ByteArray,
		width: Int,
		height: Int,
		stride: Int,
		mode: Int
	): ByteArray?

	const val MODE_GRAY = 0
	const val MODE_CANNY = 1
}
