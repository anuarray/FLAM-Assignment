package com.example.edgeviewer

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.example.edgeviewer.gl.SimpleRenderer

class GLView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {
	private val renderer = SimpleRenderer()
	init {
		setEGLContextClientVersion(2)
		setRenderer(renderer)
		renderMode = RENDERMODE_WHEN_DIRTY
	}
	fun uploadGray(bytes: ByteArray, w: Int, h: Int) {
		queueEvent {
			renderer.uploadGrayscale(bytes, w, h)
		}
		requestRender()
	}
}
