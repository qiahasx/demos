package com.example.opengl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.opengl.ElementActivity.Companion.selectElement
import com.example.opengl.TransitionActivity.Companion.navTransition
import com.example.opengl.ui.ButtonItem
import com.example.opengl.ui.ButtonItemBean
import com.example.opengl.ui.LocalDialog
import com.example.opengl.ui.TextInfoDialog
import kotlinx.coroutines.flow.MutableStateFlow

class SelectElementActivity : ComponentActivity() {
    private val dialog = MutableStateFlow<Pair<String, String>?>(null)
    private val elements by lazy {
        listOf(
            ButtonItemBean(R.string.gl_points, R.string.gl_points_info) {
                selectElement(ElementActivity.GL_POINTS)
            },
            ButtonItemBean(R.string.gl_lines, R.string.gl_lines_info) {
                selectElement(ElementActivity.GL_LINES)
            },
            ButtonItemBean(R.string.gl_line_loop, R.string.gl_line_loop_info) {
                selectElement(ElementActivity.GL_LINE_LOOP)
            },
            ButtonItemBean(R.string.gl_line_strip, R.string.gl_line_strip_info) {
                selectElement(ElementActivity.GL_LINE_STRIP)
            },
            ButtonItemBean(R.string.gl_triangles, R.string.gl_triangles_info) {
                selectElement(ElementActivity.GL_TRIANGLES)
            },
            ButtonItemBean(R.string.gl_triangle_strip, R.string.gl_triangle_strip_info) {
                selectElement(ElementActivity.GL_TRIANGLE_STRIP)
            },
            ButtonItemBean(R.string.gl_triangle_fan, R.string.gl_triangle_fan_info) {
                selectElement(ElementActivity.GL_TRIANGLE_FAN)
            }
        )
    }
    private val transitions by lazy {
        listOf(
            ButtonItemBean(R.string.transition_render, R.string.transition_render_info) {
                navTransition(TransitionActivity.TransitionMode.SLIDE)
            },
            ButtonItemBean(R.string.transition_render, R.string.transition_render_info) {
                navTransition(TransitionActivity.TransitionMode.LINEAR_WIPE)
            },
            ButtonItemBean(R.string.transition_render, R.string.transition_render_info) {
                navTransition(TransitionActivity.TransitionMode.RADIAL_UNFOLD)
            },
            ButtonItemBean(R.string.transition_render, R.string.transition_render_info) {
                navTransition(TransitionActivity.TransitionMode.FADE)
            },
            ButtonItemBean(R.string.transition_render, R.string.transition_render_info) {
                navTransition(TransitionActivity.TransitionMode.WRAP)
            },
            ButtonItemBean(R.string.transition_render, R.string.transition_render_info) {
                navTransition(TransitionActivity.TransitionMode.ZOOM_BLUR)
            },
            ButtonItemBean(R.string.transition_render, R.string.transition_render_info) {
                navTransition(TransitionActivity.TransitionMode.BURN)
            },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mode = intent.getIntExtra(SELECT_MODE, ELEMENT)
        val buttons = if (mode == ELEMENT) elements else transitions
        setContent {
            CompositionLocalProvider(LocalDialog provides dialog) {
                Scaffold {
                    LazyColumn(
                        Modifier
                            .padding(it)
                            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(buttons.size) {
                            ButtonItem(buttons[it])
                        }
                    }
                }
                dialog.collectAsState().value?.let {
                    TextInfoDialog()
                }
            }
        }
    }

    companion object {
        private const val SELECT_MODE = "key_Select_Mode"
        const val ELEMENT = 0;
        const val TRANSITION = 1;
        fun Context.navSelect(mode: Int) {
            val intent = Intent(this, SelectElementActivity::class.java)
            intent.putExtra(SELECT_MODE, mode)
            startActivity(intent)
        }
    }
}