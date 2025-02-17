package com.example.record.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import com.example.common.util.*
import com.example.record.R
import com.example.record.RecordViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class DBMeterLayout(context: Context) : ViewGroup(context) {
    var radius = 16.dpf
        set(value) {
            field = value
            val drawable = (background as? ShadowDrawable) ?: return
            drawable.setShadowCorner(field)
        }
    private val viewModel by (context as ComponentActivity).viewModels<RecordViewModel>()
    private val lifecycleOwner = context as ComponentActivity
    var clickSetting = { }

    val tvDbValue = textView {
        setMargins(l = 16.dpi, t = 36.dpi)
        setAttributes(56f, R.color.white, 700)
        transitionName = "tvDbValue"
        minWidth = paint.measureText("00").roundToInt()
        text = viewModel.volume.value?.value?.toString() ?: "00"
    }
    val tvDb = textView {
        setMargins(l = 4.dpi)
        setAttributes(32f, R.color.white, 600)
        transitionName = "tvDb"
        setText(R.string.db)
    }
    val tvSetting = textView {
        setMargins(t = 20.dpi)
        setPadding(16.dpi)
        gravity = Gravity.CENTER
        setAttributes(14f, R.color.gray, 600)
        text = SpannableString(getString(R.string.click_to_set_more))
        setOnClickListener { clickSetting.invoke() }
        transitionName = "tvSetting"
    }
    val soundWaveView = SoundWaveView(context).apply {
        layoutParams = marginLayoutParams(matchParent, wrapContent)
        transitionName = "soundWaveView"
        setMargins(l = 16.dpi, t = 24.dpi, r = 16.dpi)
        this@DBMeterLayout.addView(this)
    }
    val tvDescribe = TvDescribe(context).apply {
        layoutParams = marginLayoutParams(matchParent, 180.dpi)
        bgHeight = 180.dpi
        radius = this@DBMeterLayout.radius
        setPadding(16.dpi, 24.dpi, 16.dpi, 48.dpi)
        transitionName = "tvDescribe"
        val db = viewModel.volume.value?.value ?: 0
        text = when {
            db <= 30 -> getString(R.string.decibel_30)
            db in 31..40 -> getString(R.string.decibel_40)
            db in 41..50 -> getString(R.string.decibel_50)
            db in 51..60 -> getString(R.string.decibel_60)
            db in 61..70 -> getString(R.string.decibel_70)
            else -> getString(R.string.decibel_80_above)
        }
        setMargins(t = 16.dpi)
        setAttributes(24f, R.color.black, 600)
        this@DBMeterLayout.addView(this)
    }
    val frameLayout = FrameLayout(context).apply {
        layoutParams = marginLayoutParams(matchParent, matchParent)
        transitionName = "frameLayout"
        this@DBMeterLayout.addView(this)
    }

    init {
        layoutParams = marginLayoutParams(matchParent, wrapContent)
        transitionName = "dbMeter"
        val solidDrawable = ColorDrawable(Color.BLACK)
        background = ShadowDrawable(solidDrawable, 8.dpf, radius)
        viewModel.volume.observe(lifecycleOwner) { volumes ->
            soundWaveView.setVolume(volumes)
            volumes ?: return@observe
            lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                volumes.collect {
                    val db = it.coerceAtLeast(0)
                    withContext(Dispatchers.Main) {
                        tvDbValue.text = db.toString()
                        tvDescribe.text = when {
                            db <= 30 -> getString(R.string.decibel_30)
                            db in 31..40 -> getString(R.string.decibel_40)
                            db in 41..50 -> getString(R.string.decibel_50)
                            db in 51..60 -> getString(R.string.decibel_60)
                            db in 61..70 -> getString(R.string.decibel_70)
                            else -> getString(R.string.decibel_80_above)
                        }
                    }
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(tvDbValue, widthMeasureSpec, heightMeasureSpec)
        measureChild(tvDb, widthMeasureSpec, heightMeasureSpec)
        measureChild(tvSetting, widthMeasureSpec, heightMeasureSpec)
        measureChildWithMargins(soundWaveView, widthMeasureSpec, 0, heightMeasureSpec, 0)
        measureChild(tvDescribe, widthMeasureSpec, heightMeasureSpec)
        val used = tvDbValue.heightUsed + soundWaveView.heightUsed + tvDescribe.heightUsed
        val remain = if (frameLayout.childCount > 0) MeasureSpec.getSize(heightMeasureSpec) - used else 0
        measureChild(frameLayout, widthMeasureSpec, MeasureSpec.makeMeasureSpec(remain, MeasureSpec.EXACTLY))
        val h = used + frameLayout.heightUsed
        setMeasuredDimension(widthMeasureSpec, resolveSize(h, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        tvDbValue.run { layout(marginLeft, marginTop) }
        tvDb.run { layout(tvDbValue.right + marginLeft, tvDbValue.bottom - measuredHeight - marginBottom) }
        tvSetting.run {
            val x = r - l - measuredWidth - marginRight
            layout(x, marginTop)
        }
        soundWaveView.run { layout(marginLeft, tvDbValue.bottom + marginTop) }
        tvDescribe.run { layout(marginLeft, soundWaveView.bottom + marginTop) }
        frameLayout.run { layout(marginLeft, tvDescribe.bottom + marginTop) }
    }
}