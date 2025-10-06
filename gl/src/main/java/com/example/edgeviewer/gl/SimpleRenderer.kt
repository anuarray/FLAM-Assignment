package com.example.edgeviewer.gl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class SimpleRenderer : GLSurfaceView.Renderer {
	private var program = 0
	private var posAttr = 0
	private var uvAttr = 0
	private var texUniform = 0
	private var textureId = 0

	private var frameWidth = 0
	private var frameHeight = 0

	private var vertices: FloatBuffer
	private var uvs: FloatBuffer

	private val vertexData = floatArrayOf(
		-1f, -1f,
		 1f, -1f,
		-1f,  1f,
		 1f,  1f
	)
	private val uvData = floatArrayOf(
		0f, 1f,
		1f, 1f,
		0f, 0f,
		1f, 0f
	)

	private val vs = """
		attribute vec2 aPos;
		attribute vec2 aUV;
		varying vec2 vUV;
		void main(){ vUV = aUV; gl_Position = vec4(aPos, 0.0, 1.0); }
	"""

	private val fs = """
		precision mediump float;
		varying vec2 vUV;
		uniform sampler2D uTex;
		void main(){
			vec4 c = texture2D(uTex, vUV);
			float g = c.r; // grayscale in R channel
			gl_FragColor = vec4(g, g, g, 1.0);
		}
	"""

	init {
		vertices = ByteBuffer.allocateDirect(vertexData.size * 4)
			.order(ByteOrder.nativeOrder())
			.asFloatBuffer().put(vertexData)
		vertices.position(0)
		uvs = ByteBuffer.allocateDirect(uvData.size * 4)
			.order(ByteOrder.nativeOrder())
			.asFloatBuffer().put(uvData)
		uvs.position(0)
	}

	override fun onSurfaceCreated(gl: javax.microedition.khronos.opengles.GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
		val vsId = compileShader(GLES20.GL_VERTEX_SHADER, vs)
		val fsId = compileShader(GLES20.GL_FRAGMENT_SHADER, fs)
		program = GLES20.glCreateProgram()
		GLES20.glAttachShader(program, vsId)
		GLES20.glAttachShader(program, fsId)
		GLES20.glLinkProgram(program)
		posAttr = GLES20.glGetAttribLocation(program, "aPos")
		uvAttr = GLES20.glGetAttribLocation(program, "aUV")
		texUniform = GLES20.glGetUniformLocation(program, "uTex")
		textureId = createTexture()
		GLES20.glClearColor(0f, 0f, 0f, 1f)
	}

	override fun onSurfaceChanged(gl: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
		GLES20.glViewport(0, 0, width, height)
	}

	override fun onDrawFrame(gl: javax.microedition.khronos.opengles.GL10?) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
		GLES20.glUseProgram(program)
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
		GLES20.glUniform1i(texUniform, 0)

		GLES20.glEnableVertexAttribArray(posAttr)
		GLES20.glVertexAttribPointer(posAttr, 2, GLES20.GL_FLOAT, false, 0, vertices)
		GLES20.glEnableVertexAttribArray(uvAttr)
		GLES20.glVertexAttribPointer(uvAttr, 2, GLES20.GL_FLOAT, false, 0, uvs)

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
	}

	fun uploadGrayscale(bytes: ByteArray, width: Int, height: Int) {
		frameWidth = width
		frameHeight = height
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
		val buf = ByteBuffer.allocateDirect(width * height)
		buf.put(bytes)
		buf.position(0)
		// Upload gray as red channel of RGBA using GL_LUMINANCE emulation by specifying GL_ALPHA/RED on ES 2.0 devices is inconsistent.
		// Use GL_LUMINANCE via extension or pack gray into RGBA manually. Simpler: expand on CPU to RGBA.
		val rgba = ByteArray(width * height * 4)
		var si = 0
		var di = 0
		while (si < bytes.size && di + 3 < rgba.size) {
			val v = bytes[si]
			rgba[di] = v; rgba[di+1] = v; rgba[di+2] = v; rgba[di+3] = -1
			si += 1; di += 4
		}
		val rgbaBuf = ByteBuffer.allocateDirect(rgba.size).order(ByteOrder.nativeOrder())
		rgbaBuf.put(rgba).position(0)
		GLES20.glTexImage2D(
			GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
			width, height, 0,
			GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, rgbaBuf
		)
	}

	private fun createTexture(): Int {
		val ids = IntArray(1)
		GLES20.glGenTextures(1, ids, 0)
		val id = ids[0]
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
		return id
	}

	private fun compileShader(type: Int, src: String): Int {
		val id = GLES20.glCreateShader(type)
		GLES20.glShaderSource(id, src)
		GLES20.glCompileShader(id)
		return id
	}
}
