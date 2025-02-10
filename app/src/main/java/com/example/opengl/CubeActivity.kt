package com.example.opengl

import android.graphics.Color
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import com.example.opengl.render.ShaderRender

class CubeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        val glsView = GLSurfaceView(this)
        glsView.setEGLContextClientVersion(3)
        glsView.setRenderer(ShaderRender())
        setContentView(glsView)
    }
}