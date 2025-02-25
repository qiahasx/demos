package com.example.opengl

import android.annotation.SuppressLint
import android.graphics.Color
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import com.example.opengl.render.ShaderRender
import kotlin.math.abs

class CubeActivity : ComponentActivity() {
    private val render = ShaderRender()
    private val dp by lazy { resources.displayMetrics.density * 2.5f }
    private var lastX = 0f
    private var lastY = 0f

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { v, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastY = event.y
                lastX = event.x
            }

            MotionEvent.ACTION_MOVE -> {
                val dy = lastY - event.y
                val dx = lastX - event.x
                if (abs(dy) > dp || abs(dx) > dp) {
                    render.rotate(dx / dp, dy / dp)
                    lastY = event.y
                    lastX = event.x
                }
            }
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        val glsView = GLSurfaceView(this)
        glsView.setEGLContextClientVersion(3)
        glsView.setRenderer(render)
        glsView.setOnTouchListener(onTouchListener)
        setContentView(glsView)
    }
}