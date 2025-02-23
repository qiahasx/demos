package com.example.view.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.RESTART
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.common.util.dpf
import com.example.common.util.dpi
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class FishView
@JvmOverloads
constructor(
    context: Context? = null,
    attrs: AttributeSet? = null,
    i: Int = 0,
) : View(context, attrs, i) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val lightColor = Color.parseColor("#FFA58E")
    private val headLength = 15.dpf
    private var orientation = -90f
        set(value) {
            field = value
            invalidate()
        }
    private val mPath = Path()
    private val swingingAnimation: ObjectAnimator =
        ObjectAnimator.ofFloat(this, "swingCurrentValue", 0f, 360f).apply {
            repeatMode = RESTART
            repeatCount = INFINITE
            interpolator = LinearInterpolator()
            duration = 1000
        }
    private var swingCurrentValue = 0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        swingingAnimation.start()
        paint.color = lightColor
        paint.alpha = (255 * 0.6).toInt()
    }

    private val hPoint by lazy { Point(width / 2, height / 2) }
    private lateinit var bodyPoint: Point
    private var hPointX: Float
        get() = hPoint.x
        set(value) {
            hPoint.x = value + oldPoint.x - bodyPoint.x
            invalidate()
        }

    private var hPointY: Float
        get() = hPoint.y
        set(value) {
            hPoint.y = value + oldPoint.y - bodyPoint.y
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val hPoint = hPoint
        canvas.drawCircle(hPoint.x, hPoint.y, headLength, paint)
        val point = drawBody(canvas, hPoint)
        val lFinStartPoint = hPoint.calculateNewPoint(-115 + orientation, headLength * 0.9)
        drawFin(canvas, lFinStartPoint, true)
        val rFinStartPoint = hPoint.calculateNewPoint(115 + orientation, headLength * 0.9)
        drawFin(canvas, rFinStartPoint, false)
        val drawLimb = drawLimb(canvas, point)
        draTail(canvas, drawLimb)
    }

    private val targetPoint by lazy { Point(0, 0) }
    private val oldPoint by lazy { Point(0, 0) }
    private val swimSpeed = 120.dpi
    private val swimAnimator: ValueAnimator =
        ValueAnimator.ofInt(0, 1000).apply {
            addUpdateListener {
                val rate = it.animatedValue as Int / 1000.0f
                hPoint.x = (targetPoint.x - oldPoint.x) * rate + oldPoint.x
                hPoint.y = (targetPoint.y - oldPoint.y) * rate + oldPoint.y
            }
        }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                targetPoint.x = event.x
                targetPoint.y = event.y
                oldPoint.x = hPointX
                oldPoint.y = hPointY
                bodyPoint =
                    hPoint.calculateNewPoint(180 + orientation, 2.2 * headLength)
                val angleBisectorPoint = bodyPoint.findAngleBisectorPoint(hPoint, targetPoint)
                angleBisectorPoint.x *= 1.6f * headLength
                angleBisectorPoint.y *= 1.6f * headLength
                angleBisectorPoint.x += bodyPoint.x
                angleBisectorPoint.y += bodyPoint.y
                val path = mPath
                path.reset()
                path.moveTo(bodyPoint)
                path.cubicTo(
                    hPoint.x,
                    hPoint.y,
                    angleBisectorPoint.x,
                    angleBisectorPoint.y,
                    targetPoint.x,
                    targetPoint.y,
                )
                val measure = PathMeasure(path, false)
                val animator = ObjectAnimator.ofFloat(this, "hPointX", "hPointY", path)
                animator.addUpdateListener {
                    val tan = FloatArray(2)
                    measure.getPosTan(measure.length * animator.animatedFraction, null, tan)
                    orientation = Math.toDegrees(atan2(tan[1].toDouble(), tan[0].toDouble())).toFloat()
                }
                animator.duration = (measure.length / swimSpeed).toLong() * 1000
                animator.start()
            }
        }
        return false
    }

    private fun draTail(
        canvas: Canvas,
        apex: Point,
    ) {
        val orientation = orientation + sin(Math.toRadians(swingCurrentValue.toDouble()) * 2) * 5
        val bottomCenter = apex.calculateNewPoint(180 + orientation, headLength * 0.6)
        val lBottom = bottomCenter.calculateNewPoint(-90 + orientation, headLength * 0.5)
        val rBottom = bottomCenter.calculateNewPoint(90 + orientation, headLength * 0.5)
        val path = mPath
        path.reset()
        path.moveTo(apex)
        path.lineTo(lBottom)
        path.lineTo(rBottom)
        path.close()
        canvas.drawPath(path, paint)
        val bottomCenter2 = apex.calculateNewPoint(180 + orientation, headLength * 0.6 * 0.8)
        val lBottom2 = bottomCenter2.calculateNewPoint(-90 + orientation, headLength * 0.5 * 0.6)
        val rBottom2 = bottomCenter2.calculateNewPoint(90 + orientation, headLength * 0.5 * 0.6)
        path.reset()
        path.moveTo(apex)
        path.lineTo(lBottom2)
        path.lineTo(rBottom2)
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawLimb(
        canvas: Canvas,
        bottomBodyCenter: Point,
    ): Point {
        val orientation = orientation + sin(Math.toRadians(swingCurrentValue.toDouble()) * 2) * 5
        val orientation2 = orientation + cos(Math.toRadians(swingCurrentValue.toDouble()) * 3) * 8

        canvas.drawCircle(bottomBodyCenter.x, bottomBodyCenter.y, headLength * 0.6f, paint)
        val limbCenter1 =
            bottomBodyCenter.calculateNewPoint(180 + orientation, headLength)
        canvas.drawCircle(limbCenter1.x, limbCenter1.y, headLength * 0.6f * 0.6f, paint)
        val limbCenter2 =
            limbCenter1.calculateNewPoint(180 + orientation2, headLength * 0.6f)
        canvas.drawCircle(limbCenter2.x, limbCenter2.y, headLength * 0.6f * 0.6f * 0.6f, paint)

        val r1 = bottomBodyCenter.calculateNewPoint(90 + orientation, headLength * 0.6)
        val l1 = bottomBodyCenter.calculateNewPoint(-90 + orientation, headLength * 0.6)
        val r2 = limbCenter1.calculateNewPoint(90 + orientation, headLength * 0.6 * 0.6)
        val l2 = limbCenter1.calculateNewPoint(-90 + orientation, headLength * 0.6 * 0.6)
        val r3 = limbCenter2.calculateNewPoint(90 + orientation2, headLength * 0.6 * 0.6 * 0.6)
        val l3 = limbCenter2.calculateNewPoint(-90 + orientation2, headLength * 0.6 * 0.6 * 0.6)
        val path = mPath
        path.reset()
        path.moveTo(l1)
        path.lineTo(l2)
        path.lineTo(r2)
        path.lineTo(r1)
        path.lineTo(l1)
        canvas.drawPath(path, paint)
        path.reset()
        path.moveTo(l2)
        path.lineTo(l3)
        path.lineTo(r3)
        path.lineTo(r2)
        path.lineTo(l2)
        canvas.drawPath(path, paint)
        return limbCenter1
    }

    private fun drawBody(
        canvas: Canvas,
        hPoint: Point,
    ): Point {
        val orientation = orientation + sin(Math.toRadians(swingCurrentValue.toDouble())) * 5

        val rBodyStart = hPoint.calculateNewPoint(90 + orientation, headLength)
        val lBodyStart = hPoint.calculateNewPoint(-90 + orientation, headLength)
        val bottomBodyCenter = hPoint.calculateNewPoint(180 + orientation, headLength * 3.5)
        val rBodyEnd = bottomBodyCenter.calculateNewPoint(90 + orientation, headLength * 0.6)
        val lBodyEnd = bottomBodyCenter.calculateNewPoint(-90 + orientation, headLength * 0.6)
        val lControl = hPoint.calculateNewPoint(-130 + orientation, headLength * 1.65)
        val rControl = hPoint.calculateNewPoint(130 + orientation, headLength * 1.65)
        val path = mPath
        path.reset()
        path.moveTo(lBodyStart)
        path.quadTo(lControl, lBodyEnd)
        path.lineTo(rBodyEnd)
        path.quadTo(rControl, rBodyStart)
        path.lineTo(lBodyStart)
        paint.alpha = (255 * 1).toInt()
        canvas.drawPath(path, paint)
        paint.alpha = (255 * 0.6).toInt()
        return bottomBodyCenter
    }

    private fun drawFin(
        canvas: Canvas,
        startPoint: Point,
        isLeft: Boolean,
    ) {
        val orientation = orientation + sin(Math.toRadians(swingCurrentValue.toDouble())) * 10
        val endPoint = startPoint.calculateNewPoint(180 + orientation, headLength * 1.5)
        val conPoint = startPoint.calculateNewPoint(if (isLeft) -125 + orientation else 125 + orientation, headLength * 2.3f)
        val path = mPath
        path.fillType = Path.FillType.WINDING
        path.reset()
        path.moveTo(startPoint.x, startPoint.y)
        path.quadTo(conPoint.x, conPoint.y, endPoint.x, endPoint.y)
        canvas.drawPath(path, paint)
    }
}

fun Path.moveTo(startPoint: Point) {
    this.moveTo(startPoint.x, startPoint.y)
}

fun Path.lineTo(point: Point) {
    this.lineTo(point.x, point.y)
}

fun Path.quadTo(
    control: Point,
    end: Point,
) {
    this.quadTo(control.x, control.y, end.x, end.y)
}