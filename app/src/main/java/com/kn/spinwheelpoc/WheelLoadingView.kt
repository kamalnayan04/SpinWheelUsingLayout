package com.kn.spinwheelpoc

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
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
    }

    private var radius = 0f
    private var centerOfWheel = 0f
    private val colorList = listOf(GREY_COLOR_LIGHT.toColorInt(), GREY_COLOR_MID.toColorInt(),GREY_COLOR.toColorInt())
    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
    }
    private var selectedIndex = 0
    var itemsCount = 5
        set(value) {
            field = value
            postInvalidate()
        }

    private val rectF = RectF()
    private var colorIndex=0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val sweepAngle = 360f / itemsCount
        var startAngle = 0f
        rectF.set(
            centerOfWheel - (radius),
            centerOfWheel - (radius),
            centerOfWheel + (radius),
            centerOfWheel + (radius),
        )
        (0 until itemsCount).forEach {
            if (selectedIndex == it) paint.color = colorList[1]
            else paint.color = colorList[2]
            startAngle += sweepAngle
            canvas.drawArc(rectF, startAngle, sweepAngle.toFloat(), true, paint)
        }
        colorIndex = (colorIndex + 1) % colorList.size
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