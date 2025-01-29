package com.example.opengl

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.opengl.ui.LocalDialog
import com.example.opengl.ui.TextInfoDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val dialog = MutableStateFlow<Pair<String, String>?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold {
                LazyColumn(
                    Modifier
                        .padding(it)
                        .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
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
                }
            }
            CompositionLocalProvider(LocalDialog provides dialog) {
                dialog.collectAsState().value?.let {
                    TextInfoDialog()
                }
            }
        }
    }

    private fun showInfo(@StringRes title: Int, @StringRes message: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            dialog.emit(Pair(getString(title), getString(message)))
        }
    }

    @Composable
    private fun ButtonItem(
        @StringRes title: Int,
        @StringRes message: Int,
        modifier: Modifier = Modifier,
        onClick: () -> Unit,
    ) {
        ElevatedButton(
            onClick = {
                onClick.invoke()
            },
            modifier = modifier
                .padding(16.dp)
                .height(44.dp)
        ) {
            Text(getString(title))
            IconButton({ showInfo(title, message) }) { Icon(Icons.Default.Info, "") }
        }
    }

    private fun <T : Activity> startActivity(tClass: Class<T>) {
        val intent = android.content.Intent(this, tClass)
        startActivity(intent)
    }

    init {
        System.loadLibrary("opengl")
    }
}