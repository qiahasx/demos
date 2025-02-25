package com.example.opengl

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import com.example.opengl.render.ElementRender

class ElementActivity : ComponentActivity() {
    private lateinit var render: ElementRender
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        val mode = intent.getIntExtra(ELEMENT_MODE, GL_POINTS)
        render = ElementRender(mode)
        val glsView = GLSurfaceView(this)
        glsView.setEGLContextClientVersion(3)
        glsView.setRenderer(render)
        setContentView(glsView)
    }

    companion object {
        const val GL_POINTS = 0x0000
        const val GL_LINES = 0x0001
        const val GL_LINE_LOOP = 0x0002
        const val GL_LINE_STRIP = 0x0003
        const val GL_TRIANGLES = 0x0004
        const val GL_TRIANGLE_STRIP = 0x0005
        const val GL_TRIANGLE_FAN = 0x0006
        private const val ELEMENT_MODE = "key_Element_Mode"

        fun Context.selectElement(mode: Int) {
            val intent = Intent(this, ElementActivity::class.java)
            intent.putExtra(ELEMENT_MODE, mode)
            startActivity(intent)
        }
    }
}