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
import com.example.opengl.SelectElementActivity.Companion.navSelect
import com.example.opengl.ui.ButtonItem
import com.example.opengl.ui.LocalDialog
import com.example.opengl.ui.TextInfoDialog
import com.example.opengl.ui.startActivity
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    private val dialog = MutableStateFlow<Pair<String, String>?>(null)

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
                        item {
                            ButtonItem(R.string.image_gl_render, R.string.image_gl_render_info) {
                                startActivity(ImageActivity::class.java)
                            }
                        }
                        item {
                            ButtonItem(R.string.cube_gl_render, R.string.cube_gl_render_info) {
                                startActivity(CubeActivity::class.java)
                            }
                        }
                        item {
                            ButtonItem(R.string.element_gl_render, R.string.element_gl_render_info) {
                                navSelect(SelectElementActivity.ELEMENT)
                            }
                        }
                        item {
                            ButtonItem(R.string.element_gl_render, R.string.element_gl_render_info) {
                                navSelect(SelectElementActivity.TRANSITION)
                            }
                        }
                    }
                }
                dialog.collectAsState().value?.let {
                    TextInfoDialog()
                }
            }
        }
    }

    init {
        System.loadLibrary("opengl")
    }
}