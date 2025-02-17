package com.example.record.ui
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.IntRange
import androidx.core.animation.doOnEnd
import com.example.common.util.dpi
import com.example.common.util.getColor
import com.example.record.R
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.ceil

class SoundWaveView(context: Context) : View(context) {
    private var volume : StateFlow<Int>? = null
    private val sampleRate = 12
    private val barPaint = Paint()
    private val textPaint = Paint().apply {
        color = Color.GRAY
    }
    private var barColor = BarColor.GREEN
    private val minDb = 0
    private val maxDb = 120
    private val warningDb = 80
    private val barValue = 5.0
    private val barNum = ceil((maxDb - minDb) / barValue).toInt()
    private val warningBarIndex = ceil((warningDb - minDb) / barValue).toInt()

    private var currentDb = minDb
        set(value) {
            field = value
            invalidate()
        }

    /**
     * 在onSizeChanged 中计算具体的值
     */
    private var barWidth = 0f
    private val barAspectRatio = 2f
    private val barWeight = 0.7f
    private var margin = 0f
    private val bigScale = 2
    private val barRadius
        get() = barWidth / 3

    private var textSize = 16f
    private val labelHeight: Int = run {
        val s = "$minDb $warningDb $maxDb"
        val rect = Rect()
        textPaint.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            textSize,
            context.resources.displayMetrics
        )
        textPaint.getTextBounds(s, 0, s.length, rect)
        rect.height()
    }
    private val labelPadding = 12.dpi
    private val paddingRect = Rect()

    init {
        startAnimator()
    }

    fun setVolume(volume: StateFlow<Int>?) {
        this.volume = volume
    }

    private fun startAnimator() {
        val targetDb = (volume?.value ?: 0).clamp()
        barColor = if (targetDb > warningDb) {
            BarColor.YELLOW
        } else {
            BarColor.GREEN
        }
        ObjectAnimator.ofInt(this, "currentDb", currentDb, targetDb).apply {
            duration = 1000L / sampleRate
            doOnEnd {
                startAnimator()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.clipRect(paddingRect)
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())
        drawBar(canvas, BarColor.GRAY, maxDb)
        drawBar(canvas, barColor, currentDb)
        drawLabel(canvas)
        canvas.restore()
    }

    private fun drawLabel(canvas: Canvas) {
        canvas.drawText(minDb.toString(), margin, paddingRect.height().toFloat(), textPaint)

        val warningDbWith = textPaint.measureText(warningDb.toString())
        val warningDbStart = (warningBarIndex + 1) * (margin + barWidth) - warningDbWith / 2 - barWidth / 2
        canvas.drawText(warningDb.toString(), warningDbStart, paddingRect.height().toFloat(), textPaint)

        val maxDbWith = textPaint.measureText(maxDb.toString())
        val maxDbStart = paddingRect.width() - margin - maxDbWith
        canvas.drawText(maxDb.toString(), maxDbStart, paddingRect.height().toFloat(), textPaint)
    }

    private fun drawBar(canvas: Canvas, color: BarColor, @IntRange(0, 120) db: Int) {
        val height = paddingRect.height()
        barPaint.color = getColor(color.deepColor)
        canvas.save()
        val maxWidth = ceil((db - minDb) / barValue) * margin + (db - minDb) / barValue * barWidth
        canvas.clipRect(0, 0, maxWidth.toInt(), height)
        var xOffset = margin
        for (i in 0 until barNum) {
            if (xOffset + (barWidth + margin) * 2 > maxWidth) {
                barPaint.color = getColor(color.lightColor)
            }
            val barHeight =
                if (i != warningBarIndex) barWidth * barAspectRatio
                else (barWidth * barAspectRatio * bigScale)
            val barRight = xOffset + barWidth
            val barTop = (height - labelPadding - labelHeight - barHeight) / 2f
            val barBottom = barTop + barHeight
            canvas.drawRoundRect(xOffset, barTop, barRight, barBottom, barRadius, barRadius, barPaint)
            xOffset += barWidth + margin
        }
        canvas.restore()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val width = w - (paddingLeft + paddingRight)
        margin = width * (1 - barWeight) / (barNum + 1)
        barWidth = width * barWeight / barNum
        paddingRect.set(paddingLeft, paddingTop, w - paddingRight, h - paddingBottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val targetWidth = resources.displayMetrics.widthPixels
        val width = resolveSize(targetWidth, widthMeasureSpec)
        val maxBarWidth = (width - (paddingLeft + paddingRight)) * barWeight / barNum * bigScale
        val maxBarHeight = maxBarWidth * barAspectRatio
        val targetHeight = maxBarHeight + labelHeight + labelPadding + (paddingTop + paddingBottom)
        setMeasuredDimension(
            width,
            resolveSize(targetHeight.toInt(), heightMeasureSpec)
        )
    }

    private fun Int.clamp() = this.coerceAtLeast(minDb).coerceAtMost(maxDb)

    enum class BarColor(@ColorRes val deepColor: Int, @ColorRes val lightColor: Int) {
        GREEN(R.color.green_deep, R.color.green_light),
        YELLOW(R.color.yellow_deep, R.color.yellow_light),
        GRAY(R.color.gray, R.color.gray);
    }
}
