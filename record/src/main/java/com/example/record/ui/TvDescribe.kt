package com.example.record.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import androidx.appcompat.widget.AppCompatTextView
import com.example.common.util.dpf
import com.example.common.util.getColor
import com.example.record.R

class TvDescribe(context: Context) : AppCompatTextView(context) {
    var bgHeight = 0
        set(value) {
            field = value
            requestLayout()
        }
    var radius = 0.dpf
    private val bgPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = getColor(R.color.green_deep)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(0f, 0f, width.toFloat(), bgHeight.toFloat(), radius, radius, bgPaint)
        super.draw(canvas)
    }
}