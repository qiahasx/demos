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
import com.example.opengl.ui.ButtonItem
import com.example.opengl.ui.ButtonItemBean
import com.example.opengl.ui.LocalDialog
import com.example.opengl.ui.TextInfoDialog
import kotlinx.coroutines.flow.MutableStateFlow

class ElementActivity : ComponentActivity() {
    private val dialog = MutableStateFlow<Pair<String, String>?>(null)
    private val buttonItemBeans = listOf<ButtonItemBean>(
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