package com.kn.spinwheelpoc

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.graphics.toColorInt
import kotlin.math.min

class WheelLoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        private const val GREY_COLOR = "#adb5bd"
        private const val GREY_COLOR_LIGHT = "#dee2e6"
        private const val GREY_COLOR_MID = "#ced4da"
        private const val GREY_COLOR_DARK_6 = "#6c757d"
        private const val GREY_COLOR_DARK_7 = "#495057"
    }

    var rimStrokeWidth = 20f
        set(value) {
            field = value
            invalidate()
        }
    var itemsCount = 5
        set(value) {
            field = value
            invalidate()
        }
    val rimSegments
        get() = itemsCount * 10

    private var radius = 0f
    private var centerOfWheel = 0f
    private val colorList = listOf(GREY_COLOR_LIGHT.toColorInt(), GREY_COLOR_MID.toColorInt(),GREY_COLOR.toColorInt())
    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
    }
    private var selectedIndex = 0

    private var centerDotPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
    }

    private val rimSegmentPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = rimStrokeWidth // Adjust for desired thickness
        strokeCap = Paint.Cap.BUTT
    }

    private val centerShader by lazy {
        RadialGradient(
            centerOfWheel,
            centerOfWheel,
            20f,
            colorList.toIntArray(),
            floatArrayOf(0.2f, 0.8f, 1f),
            Shader.TileMode.CLAMP
        )
    }
    private val rectF = RectF()
    private var colorIndex=0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val sweepAngle = 360f / itemsCount
        var startAngle = 0f
        val circleRadius = (radius - rimStrokeWidth / 2)
        rectF.set(
            centerOfWheel - (circleRadius),
            centerOfWheel - (circleRadius),
            centerOfWheel + (circleRadius),
            centerOfWheel + (circleRadius),
        )
        (0 until itemsCount).forEach {
            if (selectedIndex == it) paint.color = colorList[1]
            else paint.color = colorList[2]
            startAngle += sweepAngle
            canvas.drawArc(rectF, startAngle, sweepAngle.toFloat(), true, paint)
        }
        centerDotPaint.shader = centerShader
        canvas.drawCircle(centerOfWheel, centerOfWheel, 20f, centerDotPaint)
        colorIndex = (colorIndex + 1) % colorList.size
        drawRim(canvas)
    }

    private fun drawRim(canvas: Canvas) {

        val segmentAngle = 360f / rimSegments
        var startAngle = 0f
        val rimRadius = (radius - rimStrokeWidth / 2)
        rectF.set(
            centerOfWheel - rimRadius,
            centerOfWheel - rimRadius,
            centerOfWheel + rimRadius,
            centerOfWheel + rimRadius,
        )
        for (i in 0 until rimSegments) {
            rimSegmentPaint.color =
                if (i % 2 == 0) GREY_COLOR_DARK_6.toColorInt() else GREY_COLOR_DARK_7.toColorInt()
            canvas.drawArc(
                rectF, startAngle, segmentAngle, false, rimSegmentPaint
            )
            startAngle += segmentAngle
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minDimen = min(measuredWidth, measuredHeight)
        radius = minDimen / 2f
        centerOfWheel = minDimen / 2f
        setMeasuredDimension(minDimen, minDimen)
    }

    fun startAnimating() {
        val valueAnimator = ValueAnimator.ofInt(itemsCount).apply {
            interpolator = LinearInterpolator()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            duration = 900
            addUpdateListener {
                selectedIndex = it.animatedValue as Int
                invalidate()
            }
        }
        valueAnimator.start()
    }
}