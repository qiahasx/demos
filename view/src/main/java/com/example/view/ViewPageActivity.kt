package com.example.view

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.common.util.generateRandomString
import com.example.common.util.marginLayoutParams
import com.example.common.util.matchParent
import com.example.common.util.setFontWeight
import com.example.common.util.toast
import com.example.view.ui.MyViewPage

class ViewPageActivity : ComponentActivity() {
    private var cnt = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = MyViewPage(this)
        repeat(20) {
            layout.addView(createTextView())
        }
        setContentView(layout)
    }

    private fun createTextView() = TextView(this).apply {
        layoutParams = marginLayoutParams(matchParent, matchParent)
        gravity = Gravity.CENTER
        text = generateRandomString(20, 100)
        textSize = 24f
        setFontWeight(700)
        setBackgroundColor(getBackgroundColor(cnt++))
        setOnClickListener {
            toast(text.toString())
        }
    }

    private fun getBackgroundColor(position: Int) = when (position % 3) {
        0 -> getColor(R.color.color_8f4c38)
        1 -> getColor(R.color.color_e8d6d2)
        else -> getColor(R.color.color_ffdbd1)
    }
}