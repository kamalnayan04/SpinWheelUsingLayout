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
    companion object {
        const val OVERLAY_TYPE_FULL = 1
        const val OVERLAY_TYPE_PARTIAL = 0
        private const val DEFAULT_OVERLAY_COLOR = "#B31B1B1B"
        const val ERROR_OVERLAY_COLOR = "#b3e9e2c9"

    }

    var totalItems = 6
        set(value) {
            field = value
            postInvalidate()
        }

    var margin = 0f
        set(value) {
            field = value
            postInvalidate()
        }

    var overlayColor = DEFAULT_OVERLAY_COLOR.toColorInt()
        set(value) {
            field = value
            invalidate()
        }

    var overlayType = OVERLAY_TYPE_PARTIAL
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        isDither = true
        color = overlayColor
        maskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.NORMAL)
    }

    private var radius = 0f
    private var center = 0f
    private val rect = RectF()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minDimen = min(measuredWidth, measuredHeight)
        radius = minDimen / 2f
        center = minDimen / 2f
        setMeasuredDimension(minDimen, minDimen)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val finalRadius = radius - margin
        rect.set(
            center - finalRadius,
            center - finalRadius,
            center + finalRadius,
            center + finalRadius
        )
        paint.color = overlayColor

        if (overlayType == OVERLAY_TYPE_FULL) {
            canvas.drawArc(rect, 0f, 360f, true, paint)
        } else {
            val sweepAngle = 360f / totalItems
            canvas.drawArc(rect, 270 + (sweepAngle / 2), 360 - sweepAngle, true, paint)
        }
    }
}