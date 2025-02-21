package com.example.view.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.core.view.children
import androidx.core.view.marginStart
import com.example.common.util.heightUsed
import com.example.common.util.layout
import com.example.common.util.widthUsed
import kotlin.math.max

class MyCenterLayout(context: Context) : ViewGroup(context) {
    private var rowSpacing: Int = 0
    private val coordinates = ArrayMap<View, ViewCoordinate>()
    private val lineWidthUsed = ArrayMap<Int, Int>()

    fun setRowSpacing(spacing: Int) {
        rowSpacing = spacing
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var currentWidth = 0
        var currentHeight = 0
        val width = MeasureSpec.getSize(widthMeasureSpec)
        var heightUsed = 0
        for (child in children) {
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            heightUsed = max(heightUsed, currentHeight + child.heightUsed)
            if (currentWidth + child.widthUsed > width) {
                heightUsed += rowSpacing
                currentHeight = heightUsed
                currentWidth = 0
            }
            val coordinate = coordinates.getOrPut(child) { ViewCoordinate() }
            coordinate.set(currentWidth, currentHeight)
            currentWidth += child.widthUsed
            lineWidthUsed[currentHeight] = currentWidth
        }
        setMeasuredDimension(width, resolveSize(heightUsed, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val w = r - l
        children.forEach { view ->
            val coordinate = coordinates[view] ?: return
            val usedWidth = lineWidthUsed.getOrDefault(coordinate.y, null) ?: return
            val offset = (w - usedWidth) / 2 + view.marginStart
            view.layout(coordinate.x + offset, coordinate.y)
        }
    }

    private class ViewCoordinate(var x: Int = 0, var y: Int = 0) {
        fun set(newX: Int, newY: Int) {
            x = newX
            y = newY
        }
    }
}