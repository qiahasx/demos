package com.example.record.ui

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.graphics.withSave
import com.example.common.util.dpf

class ShadowDrawable(
    private val drawable: Drawable,
    private val shadowRadius: Float = 0f,
    private var corner: Float = 0f,
) : Drawable() {
    private val paint = Paint()
    private val rectF = RectF()
    private val path = Path()

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(rectF, corner, corner, paint)
        canvas.withSave {
            canvas.clipPath(path)
            drawable.draw(canvas)
        }
    }

    fun setShadowCorner(corner: Float) {
        this.corner = corner
        invalidateSelf()
    }

    override fun onBoundsChange(bounds: Rect) {
        drawable.bounds = bounds
        rectF.set(bounds)
        path.reset()
        path.addRoundRect(rectF, corner, corner, Path.Direction.CW)
        paint.setShadowLayer(shadowRadius, 0.dpf, 0.dpf, paint.color)
    }

    override fun setAlpha(alpha: Int) {
        drawable.alpha = alpha
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        drawable.colorFilter = colorFilter
        paint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java", ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat"))
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}