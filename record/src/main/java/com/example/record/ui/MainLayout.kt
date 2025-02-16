package com.example.record.ui

import android.app.Activity
import android.content.Context
import android.graphics.drawable.StateListDrawable
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import com.example.common.util.dpi
import com.example.common.util.getDrawable
import com.example.common.util.heightUsed
import com.example.common.util.layout
import com.example.common.util.marginLayoutParams
import com.example.common.util.setMargins
import com.example.record.R
import com.example.record.RecordActivity

class MainLayout(context: Context) : ViewGroup(context) {
    private val dbMeter =
        DBMeterLayout(context).apply {
            setMargins(l = 16.dpi, r =16.dpi)
            clickSetting = {
                val options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context as Activity,
                        Pair(tvDbValue, "tvDbValue"),
                        Pair(tvDb, "tvDb"),
                        Pair(tvSetting, "tvSetting"),
                        Pair(soundWaveView, "soundWaveView"),
                        Pair(tvDescribe, "tvDescribe"),
                        Pair(frameLayout, "frameLayout"),
                        Pair(this, "dbMeter"),
                    )
                (context as? RecordActivity)?.navSetting(options)
            }
            this@MainLayout.addView(this)
        }
    val btnAction = ImageView(context).apply {
        layoutParams = marginLayoutParams(88.dpi, 88.dpi)
        setMargins(b = 62.dpi)
        val drawable = StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_selected), getDrawable(R.drawable.pause_record))
            addState(intArrayOf(), getDrawable(R.drawable.record))
        }
        setImageDrawable(drawable)
        addView(this)
    }

    init {
        clipChildren = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int, ) {
        measureChildWithMargins(dbMeter, widthMeasureSpec, 0, heightMeasureSpec, 0)
        measureChild(btnAction, widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int, ) {
        btnAction.run {
            val x = (r - l - measuredWidth) / 2
            val y = b - t - measuredHeight - marginBottom
            layout(x, y)
        }
        val remaining = b - t - listOf(btnAction, dbMeter).sumOf { it.heightUsed }
        dbMeter.run { layout(marginLeft, (remaining * 0.3).toInt()) }
    }
}