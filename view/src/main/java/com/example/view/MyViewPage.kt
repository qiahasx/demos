package com.example.view

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.OverScroller
import androidx.core.view.children
import java.lang.Integer.min
import kotlin.math.abs

class MyViewPage @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) :
    ViewGroup(context, attributeSet) {
    private var downY: Float = 0f
    private var downX: Float = 0f
    private var movedY: Float = 0f
    private var scrollY: Int = 0
    private var views: ArrayList<View> = ArrayList()
    private val overScroller by lazy { OverScroller(context) }
    private val velocityTracker by lazy { VelocityTracker.obtain() }
    private val viewConfiguration = ViewConfiguration.get(context)
    private val minV = viewConfiguration.scaledMinimumFlingVelocity
    private val maxV = viewConfiguration.scaledMaximumFlingVelocity
    private val minDistance = viewConfiguration.scaledPagingTouchSlop

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = t
        val height = b - t
        val left = 0
        val right = left + r - l
        for (child in children) {
            child.layout(left, top, right, top + height)
            top += height
            views.add(child)
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        var res = false
        if (event.action == MotionEvent.ACTION_DOWN) {
            velocityTracker.clear()
        }
        velocityTracker.addMovement(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                scrollY = 0
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = (event.y - downY).toInt()
                if (abs(dy) > minDistance) res = true
            }
        }
        return res
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            velocityTracker.clear()
        }
        velocityTracker.addMovement(event)
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dy = (event.y - downY - movedY).toInt()
                movedY += dy
                offsetChildrenVertical(dy)
            }
            MotionEvent.ACTION_UP -> {
                movedY = 0f
                toScroll()
            }
        }
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    private fun offsetChildrenVertical(dy: Int) {
        val moveY = if (dy > 0) {
            min(dy, abs(views[0].top))
        } else {
            if (views[views.size - 1].bottom + dy > height) dy else {
                -(views[views.size - 1].bottom - height)
            }
        }
        for (child in children) {
            child.offsetTopAndBottom(moveY)
        }
    }

    private fun toScroll() {
        // 算
        velocityTracker.computeCurrentVelocity(1000, maxV.toFloat())
        var distance = 0
        for ((i, child) in children.withIndex()) {
            if (child.top > -height) {
                val v = velocityTracker.yVelocity
                if (abs(v) < minV) {
                    if (abs(child.top) > height / 2) {
                        distance = child.bottom
                    } else {
                        distance = child.top
                    }
                } else {
                    if (v < 0 && i != childCount - 1) {
                        distance = child.bottom
                    } else if (v > 0) {
                        distance = child.top
                    }
                }
                break
            }
        }
        overScroller.startScroll(0, 0, 0, -distance)
        postInvalidateOnAnimation()
    }

    override fun computeScroll() {
        if (overScroller.computeScrollOffset()) {
            for (child in children) {
                child.offsetTopAndBottom(overScroller.currY - scrollY)
            }
            scrollY = overScroller.currY
            postInvalidateOnAnimation()
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }
}