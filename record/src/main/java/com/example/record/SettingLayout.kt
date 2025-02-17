package com.example.record

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.core.transition.addListener
import androidx.core.view.isVisible
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.common.util.*
import com.example.record.ui.DBMeterLayout
import com.example.record.ui.LocalRecordViewModel
import com.example.record.ui.SettingsScreen
import kotlin.math.min

class SettingLayout(context: Context) : ViewGroup(context), DefaultLifecycleObserver {
    private val viewModel by (context as ComponentActivity).viewModels<RecordViewModel>()
    override fun onCreate(owner: LifecycleOwner) {
        val transition = (context as ComponentActivity).window.sharedElementEnterTransition
        var isEnter = true
        transition.addListener(
            onStart = { _ ->
                val target = if (isEnter) 0.dpf else 16.dpf
                ObjectAnimator.ofFloat(dbMeter, "radius", target).apply {
                    duration = transition.duration
                    interpolator = transition.interpolator
                    start()
                }
                dbMeter.tvDescribe.pivotY = 0f
                val initHeight = dbMeter.height
                val targetHeight = dbMeter.measuredHeight
                ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = transition.duration
                    val t = dbMeter.tvDescribe.bgHeight
                    addUpdateListener { _ ->
                        val offset = (dbMeter.height - min(initHeight, targetHeight)).coerceAtMost(0)
                        dbMeter.tvDescribe.bgHeight = t + offset
                    }
                    start()
                }
                isEnter = !isEnter
            }
        )
    }

    private val encodeBtn = ComposeView(context).apply {
        layoutParams = marginLayoutParams(matchParent, matchParent)
        setContent {
            CompositionLocalProvider(LocalRecordViewModel provides viewModel) {
                SettingsScreen()
            }
        }
    }
    private val dbMeter = DBMeterLayout(context).apply {
        setMargins(8.dpi, 8.dpi, 8.dpi, 8.dpi)
        tvSetting.isVisible = false
        frameLayout.addView(encodeBtn)
        this@SettingLayout.addView(this)
    }

    init {
        clipChildren = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildWithMargins(dbMeter, widthMeasureSpec, 0, heightMeasureSpec, 0)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        dbMeter.run { layout(marginLeft, marginTop) }
    }
}