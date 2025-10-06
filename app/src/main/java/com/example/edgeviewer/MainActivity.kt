package com.example.edgeviewer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
	private lateinit var glView: GLView
	private lateinit var fpsText: TextView
	private lateinit var toggleBtn: Button
	private var mode = NativeBridge.MODE_GRAY

	private var lastFrameTs = 0L

	private lateinit var camera: CameraProcessor

	private val requestPermission = registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { granted -> if (granted) startCamera() }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		glView = findViewById(R.id.glView)
		fpsText = findViewById(R.id.fpsText)
		toggleBtn = findViewById(R.id.toggleBtn)

		toggleBtn.setOnClickListener {
			mode = if (mode == NativeBridge.MODE_GRAY) NativeBridge.MODE_CANNY else NativeBridge.MODE_GRAY
		}

		camera = CameraProcessor(
			this,
			this,
			listener = { gray, w, h ->
				val now = System.nanoTime()
				if (lastFrameTs != 0L) {
					val fps = 1e9 / (now - lastFrameTs).toDouble()
					runOnUiThread { fpsText.text = "FPS: ${String.format("%.1f", fps)}" }
				}
				lastFrameTs = now
				glView.uploadGray(gray, w, h)
			},
			mode = { mode }
		)

		ensurePermissionAndStart()
	}

	private fun ensurePermissionAndStart() {
		when (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
			PackageManager.PERMISSION_GRANTED -> startCamera()
			else -> requestPermission.launch(Manifest.permission.CAMERA)
		}
	}

	private fun startCamera() {
		camera.start()
	}
}
