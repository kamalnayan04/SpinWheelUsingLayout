package com.kn.spinwheelpoc


import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class TriangleLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    var sweepDegree = 60f
        set(value) {
            field = value
            requestLayout()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        // Measure children
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount == 0) return

        val child = getChildAt(0) // Assuming only one child for now

        val width = width.toFloat()
        val height = height.toFloat()

        val rect = RectF(0f, 0f, width, height)

        val startAngle = 270f - sweepDegree / 2
        val sweepAngle = sweepDegree
        val startRadians = Math.toRadians(startAngle.toDouble())
        val endRadians = Math.toRadians((startAngle + sweepAngle).toDouble())

        val startX = rect.centerX() + (rect.width() / 2) * cos(startRadians)
        val startY = rect.centerY() + (rect.height() / 2) * sin(startRadians)

        val endX = rect.centerX() + (rect.width() / 2) * cos(endRadians)
        val endY = rect.centerY() + (rect.height() / 2) * sin(endRadians)

        val childWidth = child.measuredWidth
        val childHeight = child.measuredHeight

        // Calculate the center of the triangle
        val centerX = width / 2f
        val centerY = height / 2f

        // Place the child at the center of the triangle
        val childLeft = (centerX - childWidth / 2).toInt()
        val childTop = (centerY - childHeight / 2).toInt()

        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val path = Path()
        val width = width.toFloat()
        val height = height.toFloat()

        val rect = RectF(
            0f, 0f, width, height
        )

        val startAngle = 270f - sweepDegree / 2
        val sweepAngle = sweepDegree
        val startRadians = Math.toRadians(startAngle.toDouble())
        val endRadians = Math.toRadians((startAngle + sweepAngle).toDouble())

        val startX = rect.centerX() + (rect.width() / 2) * cos(startRadians)
        val startY = rect.centerY() + (rect.height() / 2) * sin(startRadians)

        val endX = rect.centerX() + (rect.width() / 2) * cos(endRadians)
        val endY = rect.centerY() + (rect.height() / 2) * sin(endRadians)

        path.apply {
            addArc(rect, startAngle, sweepDegree)
            moveTo(startX.toFloat(), startY.toFloat())
            lineTo(endX.toFloat(), endY.toFloat())
            lineTo(rect.centerX(), rect.centerY())
            close()
        }

        canvas.save()
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
        canvas.restore()
    }
}