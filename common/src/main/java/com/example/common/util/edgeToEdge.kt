package com.example.common.util

import android.graphics.Color
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun ComponentActivity.edgeToEdgeCompat() {
    enableEdgeToEdge(
        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
    )
    val contentView = findViewById<View>(android.R.id.content)
    ViewCompat.setOnApplyWindowInsetsListener(contentView) { _, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        contentView.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        insets
    }
}