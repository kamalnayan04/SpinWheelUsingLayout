package com.kn.spinwheelpoc

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import kotlin.math.cos
import kotlin.math.sin

class AngleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        isAntiAlias = true
        isDither = true
    }

    var color = Color.RED
        set(value) {
            field = value
            paint.color = field
            postInvalidate()
        }

    val path = Path()

    var sweepDegree: Float = 90f
        set(value) {
            field = value
            postInvalidate()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = MeasureSpec.getSize(widthMeasureSpec) / 2
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        path.reset()
        val rect = RectF(
            0f, 0f, width.toFloat(), height.toFloat()
        )

        val startAngle = 270f - sweepDegree / 2//240f////180+ ((270 +( sweepDegree / 2)) -270)
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

        canvas.drawPath(path, paint)
    }

    class AngleViewOutlineProvider(val path: Path) : ViewOutlineProvider() {
        override fun getOutline(p0: View?, p1: Outline?) {
            if (path.isConvex) {
                p1?.setConvexPath(path)
//                p0?.clipToOutline = true
            }
        }

    }

}