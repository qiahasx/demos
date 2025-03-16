package com.example.demolist

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.common.R
import com.example.common.util.edgeToEdgeCompat
import com.example.common.util.setAttributes

class Demo1Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        edgeToEdgeCompat()
        val textView = TextView(this).apply {
            gravity = Gravity.CENTER
            text = "Demo1"
            setAttributes(24f, R.color.black, 600)
        }
        setContentView(textView)
    }
}