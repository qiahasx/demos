package com.example.view

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.doOnLayout
import com.example.common.util.*
import com.example.view.ui.MyCenterLayout

class CenterActivity : ComponentActivity() {
    private var cnt = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = MyCenterLayout(this)
        layout.setRowSpacing(12.dpi)
        repeat(20) {
            layout.addView(createTextView())
        }
        setContentView(layout)
    }


    private fun createTextView() = TextView(this).apply {
        layoutParams = marginLayoutParams(wrapContent, wrapContent)
        setMargins(8.dpi, 0, 8.dpi, 0)
        setPadding(12.dpi, 8.dpi, 12.dpi, 8.dpi)
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
        text = generateRandomString(4, 20)
        doOnLayout {
            background = GradientDrawable().apply {
                setColor(getBackgroundColor(cnt++))
                cornerRadius = height / 2.0f
            }
        }
        setOnClickListener {
            text = text.toString() + generateRandomString(4, 8)
            requestLayout()
        }
    }

    private fun getBackgroundColor(position: Int) = when (position % 3) {
        0 -> getColor(R.color.color_8f4c38)
        1 -> getColor(R.color.color_e8d6d2)
        else -> getColor(R.color.color_ffdbd1)
    }
}