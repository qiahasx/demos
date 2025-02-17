package com.example.opengl

import android.graphics.Color
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.opengl.render.ImageGlRender
import java.io.File

class ImageActivity : ComponentActivity() {
    private lateinit var glsView: GLSurfaceView
    private val imageGlRender = ImageGlRender()
    private val filePick = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri ?: return@registerForActivityResult
        val file = File(getExternalFilesDir("img"), "${System.currentTimeMillis()}.jpg")
        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        glsView.queueEvent {
            imageGlRender.setImage(file.absolutePath)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        glsView = GLSurfaceView(this)
        glsView.setRenderer(imageGlRender)
        glsView.setOnClickListener {
            filePick.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        setContentView(glsView)
    }
}

fun debug(any: Any) {
    Log.d("Player_ANDROID", "debug: $any")
}