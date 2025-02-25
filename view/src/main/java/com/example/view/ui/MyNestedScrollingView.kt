package com.example.view.ui

import android.content.Context
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.OverScroller
import androidx.annotation.Px
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min

abstract class MyNestedScrollingView(
    context: Context,
) : ViewGroup(context), NestedScrollingParent3 {
    private var mLastY: Float = 0f
    private var mActivePointerId: Int = 0
    private val nestedScrollingView: RecyclerView by lazy {
        getNestedScrollingChildView()
    }

    /**
     * 计算计算惯性滑动速度的工具
     */
    private val velocityTracker by lazy { VelocityTracker.obtain() }

    /**
     * 计算惯性滑动距离的工具
     */
    private val overScroller by lazy { OverScroller(context) }
    private var mScrollY = 0
    private val viewConfiguration = ViewConfiguration.get(context)
    private val maxVelocity = viewConfiguration.scaledMaximumFlingVelocity.toFloat()

    /**
     * 最小的滑动距离，手指移动的距离超过这个就认为是手指在滑动屏幕
     */
    private val minTouchSlopDistance = viewConfiguration.scaledTouchSlop
    private var downY = 0f
    private var downX = 0f
    private var totalConsumedY = 0;

    abstract fun getNestedScrollingChildView(): RecyclerView
    open fun onConsumeScroll(y: Int) {}

    /**
     * @param dy > 0 表示手指向上划动屏幕，将子view上移
     * @param dy < 0 表示手指向下划动屏幕
     * @return 返回值表示消耗后剩下的距离
     */
    private fun consumeScrollVertical(@Px dy: Int): Int {
        var consumedY = prioritizeNestedScrollingY(dy)
        consumedY = preventMovingBelowTop(consumedY)
        consumedY = preventMovingAboveBottom(consumedY)
        scrollBy(0, consumedY)
        totalConsumedY += consumedY
        onConsumeScroll(totalConsumedY)
        return dy - consumedY
    }

    /**
     * 嵌套滑动的子view优先消费手指向下滑动的事件
     */
    private fun prioritizeNestedScrollingY(unConsumedY: Int): Int {
        if (unConsumedY < 0 && nestedScrollingView.canScrollVertically(unConsumedY)) {
            val rvScrollY = nestedScrollingView.computeVerticalScrollOffset()
            val consumedY = min(abs(unConsumedY), rvScrollY)
            nestedScrollingView.scrollBy(0, -consumedY)
            return consumedY
        }
        return unConsumedY
    }

    /**
     * 手指下滑的时候
     * 防止子view不会全部跑到顶部的下面
     */
    private fun preventMovingBelowTop(@Px unConsumedY: Int): Int {
        if (unConsumedY < 0) {
            return if (scrollY + unConsumedY >= 0) unConsumedY
            else -scrollY
        }
        return unConsumedY
    }

    /**
     * 防止手指向上滑动的时候
     * 防止子view不会全部跑到底部的上面
     */
    private fun preventMovingAboveBottom(unConsumedY: Int): Int {
        val bottom = children.maxOf { it.bottom }
        return min(unConsumedY, bottom - height - scrollY)
    }

    private fun fling() {
        velocityTracker.computeCurrentVelocity(1000, maxVelocity)
        val yVelocity = -velocityTracker.yVelocity.toInt()
        mScrollY = 0
        overScroller.fling(
            0, 0,
            0, yVelocity,
            Int.MIN_VALUE, Int.MAX_VALUE,
            Int.MIN_VALUE, Int.MAX_VALUE
        )
        postInvalidateOnAnimation()
    }

    private fun stopFling() {
        if (!overScroller.isFinished) overScroller.forceFinished(true)
    }

    override fun computeScroll() {
        if (overScroller.computeScrollOffset()) {
            val unConsumedY = consumeScrollVertical(overScroller.currY - mScrollY)
            if (unConsumedY > 0) {
                stopFling()
                nestedScrollingView.fling(0, overScroller.currVelocity.toInt())
            }
            mScrollY = overScroller.currY
            postInvalidateOnAnimation()
        }
    }

    /**
     * 判断是否在嵌套滑动子View区域内，如在子View，则交给子view处理
     */
    private fun isTouchInNestedScrollingView(e: MotionEvent): Boolean {
        return e.y + scrollY > nestedScrollingView.top &&
                e.y + scrollY < nestedScrollingView.bottom &&
                e.x > nestedScrollingView.left &&
                e.x < nestedScrollingView.right
    }

    /**
     * 拦截纵向的滑动
     */
    private fun handleInterceptTouchEventLogic(e: MotionEvent): Boolean {
        var intercept = false
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mLastY = e.y
                downY = e.y
                downX = e.x
                velocityTracker.addMovement(e)
                stopFling()
            }

            MotionEvent.ACTION_MOVE -> {
                // 优先让子view去处理横向的滑动
                if (abs(e.x - downX) < minTouchSlopDistance && abs(e.y - downY) > minTouchSlopDistance) {
                    intercept = true
                } else {
                    velocityTracker.addMovement(e)
                }
            }
        }
        return intercept
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (e.actionMasked == MotionEvent.ACTION_DOWN) {
            velocityTracker.clear()
        }
        return if (isTouchInNestedScrollingView(e)) false
        else handleInterceptTouchEventLogic(e)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        velocityTracker.addMovement(e)
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val index = e.actionIndex
                mLastY = e.getY(index)
                mActivePointerId = e.getPointerId(index)
                stopFling()
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = e.actionIndex
                val pointerId = e.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mLastY = e.getY(newPointerIndex)
                    mActivePointerId = e.getPointerId(newPointerIndex)
                }
            }

            MotionEvent.ACTION_UP -> {
                fling()
            }

            MotionEvent.ACTION_MOVE -> {
                val activePointerIndex = e.findPointerIndex(mActivePointerId)
                if (activePointerIndex != -1) {
                    val y = e.getY(activePointerIndex)
                    consumeScrollVertical((mLastY - y).toInt())
                    mLastY = y
                }
            }
        }
        return true
    }

    /**
     * 接受纵向的嵌套滑动
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        val isStart = (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
        if (isStart && type == ViewCompat.TYPE_TOUCH) stopFling()
        return isStart
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy > 0) {
            val unConsumedY = dy - consumed[1]
            consumed[1] += unConsumedY - consumeScrollVertical(unConsumedY)
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray,
    ) {
        if (dyUnconsumed < 0) {
            consumed[1] += dyUnconsumed - consumeScrollVertical(dyUnconsumed)
        }
    }

    /**
     * NestedScrollingParent2的方法
     * 兼容API 21 以下版本需要实现
     */
    override fun onNestedScroll(
        target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int,
    ) {
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
//        如需兼容21以下版本
//        mParentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
//        如需兼容21以下版本
//        mParentHelper.onStopNestedScroll(target, type)
    }
}