package com.example.launch

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.example.common.MainViewModel
import com.example.common.util.LocalMainViewModel
import com.example.common.util.toast
import com.example.launch.ui.LaunchLayout
import com.example.launch.ui.LocalActivity

open class LaunchActivity : ComponentActivity() {
    private val mainViewModel = MainViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LocalActivity provides this,
                LocalMainViewModel provides mainViewModel
            ) {
                LaunchLayout()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        toast(this::class.java.simpleName + "onNewIntent")
    }
}