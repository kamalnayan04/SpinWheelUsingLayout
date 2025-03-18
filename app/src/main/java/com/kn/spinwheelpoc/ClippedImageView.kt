package com.kn.spinwheelpoc

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.cos
import kotlin.math.sin

class ClippedImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val path = Path()
    var sweepDegree = 60f
        set(value) {
            field = value
            postInvalidate()
        }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()

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

//        canvas.drawPath(path, paint)

//        val centerX = width / 2f
//        val bottomY = height
//
//        val radius = width / 2f // Adjust for proper size
//        val rect = RectF(centerX - radius, bottomY - 2 * radius, centerX + radius, bottomY)
//
//        path.reset()
//        path.moveTo(centerX, bottomY) // Start at bottom center
//        path.arcTo(rect, 180f, 180f, false) // Arc from left to right
//        path.close() // Connect back to start

        // Clip the path before drawing the image
        canvas.save()
        canvas.clipPath(path)
        super.onDraw(canvas)
        canvas.restore()
    }

    /**
     *   path.reset()
     *         val rect = RectF(
     *             0f, 0f, width.toFloat(), height.toFloat()
     *         )
     *
     *         val startAngle = 225f
     *         val sweepAngle = angleDegrees
     *         val startRadians = Math.toRadians(startAngle.toDouble())
     *         val endRadians = Math.toRadians((startAngle + sweepAngle).toDouble())
     *
     *         val startX = rect.centerX() + (rect.width() / 2) * cos(startRadians)
     *         val startY = rect.centerY() + (rect.height() / 2) * sin(startRadians)
     *
     *         val endX = rect.centerX() + (rect.width() / 2) * cos(endRadians)
     *         val endY = rect.centerY() + (rect.height() / 2) * sin(endRadians)
     *
     *         path.apply {
     *             addArc(rect, startAngle, angleDegrees)
     *             moveTo(startX.toFloat(), startY.toFloat())
     *             lineTo(endX.toFloat(), endY.toFloat())
     *             lineTo(rect.centerX(), rect.centerY())
     *             close()
     *         }
     *         canvas.drawPath(path, paint)
     */
}
