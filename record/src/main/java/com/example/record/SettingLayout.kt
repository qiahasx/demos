package com.example.record

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.transition.addListener
import androidx.core.view.isVisible
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.common.util.dpf
import com.example.common.util.dpi
import com.example.common.util.getColor
import com.example.common.util.getString
import com.example.common.util.layout
import com.example.common.util.marginLayoutParams
import com.example.common.util.matchParent
import com.example.common.util.setMargins
import com.example.common.util.toast
import com.example.common.util.wrapContent
import com.example.record.ui.DBMeterLayout
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

    @OptIn(ExperimentalMaterial3Api::class)
    private val encodeBtn = ComposeView(context).apply {
        layoutParams = marginLayoutParams(matchParent, wrapContent)
        setContent {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(getString(R.string.encode_type), Modifier.weight(1f), color = Color.White, fontWeight = FontWeight(600), fontSize = 18.sp)
                SingleChoiceSegmentedButtonRow {
                    val encoder by viewModel.encoder.collectAsState()
                    Encoder.entries.forEachIndexed { index, entry ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = Encoder.entries.size
                            ),
                            colors = SegmentedButtonDefaults.colors().copy(
                                activeContainerColor = Color(getColor(R.color.green_deep))
                            ),
                            onClick = {
                                if (viewModel.state != AudioRecorder.RecordState.INIT) return@SegmentedButton toast(getString(R.string.please_before_start))
                                viewModel.selectEncoder(entry)
                            },
                            selected = index == encoder.ordinal,
                            label = { Text(entry.name) }
                        )
                    }
                }
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

    enum class Encoder {
        MP3, AAC
    }
}