package com.kn.spinwheelpoc

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt
import kotlin.math.min

class WheelOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var totalItems = 6
        set(value) {
            field = value
            postInvalidate()
        }

    private var radius = 0f
    private var center = 0f
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        isDither = true
        color = "#B31B1B1B".toColorInt() //"#B3000000" 99000000
        maskFilter = BlurMaskFilter(30f, BlurMaskFilter.Blur.NORMAL)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minDimen = min(measuredWidth, measuredHeight)
        radius = minDimen / 2f
        center = minDimen / 2f
        setMeasuredDimension(minDimen, minDimen)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val sweepAngle = 360f / totalItems
        val itemArc = RectF(
            center - radius,
            center - radius,
            center + radius,
            center + radius
        )


        canvas.drawArc(itemArc, 270 + (sweepAngle / 2), 360-sweepAngle, true, paint)
    }


}