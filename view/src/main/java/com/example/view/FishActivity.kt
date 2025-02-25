package com.example.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.view.ui.FishView

class FishActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FishView(this))
    }
}