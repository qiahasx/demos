package com.example.opengl

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.opengl.render.TransitionRender

class TransitionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        val mode = intent.getIntExtra(TRANSITION_MODE, TransitionMode.SLIDE.ordinal)
        setContent {
            Scaffold { padding ->
                AndroidView(
                    {
                        val render = TransitionRender(mode);
                        val glsView = GLSurfaceView(this)
                        glsView.setEGLContextClientVersion(3)
                        glsView.setRenderer(render)
                        glsView
                    },
                    Modifier.padding(padding)
                )
            }
        }
    }

    enum class TransitionMode {
        SLIDE, LINEAR_WIPE, RADIAL_UNFOLD, FADE, WRAP, ZOOM_BLUR, BURN
    }

    companion object {
        private const val TRANSITION_MODE = "KEY_TRANSITION_MODE"


        fun Context.navTransition(mode: TransitionMode) {
            val intent = Intent(this, TransitionActivity::class.java)
            intent.putExtra(TRANSITION_MODE, mode.ordinal)
            startActivity(intent)
        }
    }
}