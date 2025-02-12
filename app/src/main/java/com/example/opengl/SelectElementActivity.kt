package com.example.opengl

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
import com.example.opengl.ui.ButtonItem
import com.example.opengl.ui.ButtonItemBean
import com.example.opengl.ui.LocalDialog
import com.example.opengl.ui.TextInfoDialog
import kotlinx.coroutines.flow.MutableStateFlow

class SelectElementActivity : ComponentActivity() {
    private val dialog = MutableStateFlow<Pair<String, String>?>(null)
    private val buttonItemBeans = listOf(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalDialog provides dialog) {
                Scaffold {
                    LazyColumn(
                        Modifier
                            .padding(it)
                            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(buttonItemBeans.size) {
                            ButtonItem(buttonItemBeans[it])
                        }
                    }
                }
                dialog.collectAsState().value?.let {
                    TextInfoDialog()
                }
            }
        }
    }
}