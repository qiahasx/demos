package com.example.opengl

import android.graphics.Color
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    private lateinit var glsView: GLSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        glsView = GLSurfaceView(this)
        glsView.setRenderer(GLRender())
        setContentView(glsView)
    }

    companion object {
        init {
            System.loadLibrary("opengl")
        }
    }
}