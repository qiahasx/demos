package com.example.view.ui

import android.content.Context
import android.view.*
import android.widget.OverScroller
import androidx.core.view.children
import com.example.common.util.debug
import java.lang.Integer.min
import kotlin.math.abs

class MyViewPage(context: Context) : ViewGroup(context) {
    private var downY: Float = 0f
    private var downX: Float = 0f
    private var movedY: Float = 0f
    private var scrollY: Int = 0
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

    private fun offsetChildrenVertical(dy: Int) {
        val moveY = if (dy > 0) {
            min(dy, abs(getChildAt(0).top))
        } else {
            val bottom = getChildAt(childCount - 1).bottom
            if (bottom + dy > height) dy else {
                -(bottom - height)
            }
        }
        for (child in children) {
            child.offsetTopAndBottom(moveY)
        }
    }

    private fun toScroll() {
        velocityTracker.computeCurrentVelocity(1000, maxV.toFloat())
        val targetChild = findTargetChild() ?: return
        val distance = calculateScrollDistance(targetChild)
        overScroller.startScroll(0, 0, 0, -distance)
        postInvalidateOnAnimation()
    }

    private fun findTargetChild(): View? = children.firstOrNull {
        it.top > -height
    }

    private fun calculateScrollDistance(child: View): Int {
        val velocity = velocityTracker.yVelocity
        debug("$velocity")
        return when {
            isLowSpeedScroll(velocity) -> handleLowSpeedScroll(child)
            isUpSwipe(velocity) -> handleUpSwipe(child)
            isDownSwipe(velocity) -> handleDownSwipe(child)
            else -> 0
        }
    }

    private fun isLowSpeedScroll(v: Float) = abs(v) < minV
    private fun isUpSwipe(v: Float) = v < 0
    private fun isDownSwipe(v: Float) = v > 0

    private fun handleLowSpeedScroll(child: View) =
        if (abs(child.top) > height / 2) child.bottom else child.top

    private fun handleUpSwipe(child: View) =
        if (children.indexOf(child) != childCount - 1) child.bottom else 0

    private fun handleDownSwipe(child: View) = child.top

    override fun computeScroll() {
        if (overScroller.computeScrollOffset()) {
            offsetChildrenVertical(overScroller.currY - scrollY)
            scrollY = overScroller.currY
            postInvalidateOnAnimation()
        }
    }

    override fun addView(child: View?) {
        require(child?.layoutParams?.width == LayoutParams.MATCH_PARENT) { "child layoutParams.width must be MATCH_PARENT" }
        require(child?.layoutParams?.height == LayoutParams.MATCH_PARENT) { "child layoutParams.height must be MATCH_PARENT" }
        super.addView(child)
    }
}