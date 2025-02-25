package com.example.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.view.ui.NestScrollViewImpl

class NestScrollActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(NestScrollViewImpl(this))
    }
}

