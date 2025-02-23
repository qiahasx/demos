package com.example.view.ui

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Point(var x: Float, var y: Float) {
    constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat())

    fun calculateNewPoint(
        angle: Double,
        length: Float,
    ): Point {
        val angleInRadians = Math.toRadians(angle).toFloat()
        val xOffset = length * cos(angleInRadians)
        val yOffset = length * sin(angleInRadians)
        val newX = this.x + xOffset
        val newY = this.y + yOffset
        return Point(newX, newY)
    }

    fun calculateNewPoint(
        angle: Double,
        length: Double,
    ): Point {
        return calculateNewPoint(angle, length.toFloat())
    }

    fun calculateNewPoint(
        angle: Float,
        length: Double,
    ): Point {
        return calculateNewPoint(angle.toDouble(), length.toFloat())
    }

    fun distanceBetweenPoints(point1: Point): Float {
        val deltaX = this.x - point1.x
        val deltaY = this.y - point1.y
        return sqrt(deltaX * deltaX + deltaY * deltaY)
    }

    fun findAngleBisectorPoint(
        a: Point,
        b: Point,
    ): Point {
        val oal = this.distanceBetweenPoints(a)
        val obl = this.distanceBetweenPoints(b)
        val oa = Point((a.x - this.x) / oal, (a.y - this.y) / oal)
        val ob = Point((b.x - this.x) / obl, (b.y - this.y) / obl)
        return Point((oa.x + ob.x) / 2, (oa.y + ob.y) / 2)
    }
}