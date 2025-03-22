package com.kn.spinwheelpoc

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.annotation.DrawableRes
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

class DotsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    var totalItems = 6
        set(value) {
            field = value
            invalidate()
        }


    private var wheelRadius: Float = 0F
    private var centerOfWheel: Float = 0F
    val pointSize = resources.getDimensionPixelSize(R.dimen.dp36)
    var finalPointSize = 0
    var margin = 25
        set(value) {
            field = value
            invalidate()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minDimen = min(measuredWidth, measuredHeight)
        wheelRadius = minDimen / 2f
        centerOfWheel = minDimen / 2f
        setMeasuredDimension(minDimen, minDimen)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawPointsOverRim(canvas)
    }

    fun getInitialAngle() = 270 - (360 / totalItems)
    private fun drawPointsOverRim(canvas: Canvas) {

        val bitmap = getOptimizedBitmap(R.drawable.glare, finalPointSize) ?: return

        val totalPointsCount = totalItems
        val angleStep = 360f / totalPointsCount

        for (i in 0 until totalPointsCount) {
            val angle = Math.toRadians((getInitialAngle() + i * angleStep).toDouble())
            val pointX = centerOfWheel + (wheelRadius - (margin + pointSize/2)) * cos(angle)
            val pointY = centerOfWheel + (wheelRadius - (margin + pointSize/2)) * sin(angle)

            val pointRect = Rect(
                (pointX - finalPointSize / 2).toInt(),
                (pointY - finalPointSize / 2).toInt(),
                (pointX + finalPointSize / 2).toInt(),
                (pointY + finalPointSize / 2).toInt(),
            )
            canvas.drawBitmap(bitmap, null, pointRect, null)
        }
    }


    private fun getOptimizedBitmap(
        @DrawableRes res: Int,
        desiredWidth: Int,
        desiredHeight: Int = desiredWidth
    ): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true // Only get the size, not the bitmap itself
        }
        BitmapFactory.decodeResource(context.resources, res, options)

        val actualWidth = options.outWidth
        val actualHeight = options.outHeight

        var inSampleSize = 1

        if (actualWidth > desiredWidth || actualHeight > desiredHeight) {
            val widthRatio = (actualWidth.toFloat() / desiredWidth.toFloat()).roundToInt()
            val heightRatio = (actualHeight.toFloat() / desiredHeight.toFloat()).roundToInt()

            inSampleSize = widthRatio.coerceAtLeast(heightRatio)
        }

        options.inJustDecodeBounds = false
        options.inSampleSize = inSampleSize
        return BitmapFactory.decodeResource(context.resources, res, options)
    }

    fun animateDots() {
        val animator =
            ValueAnimator.ofInt(pointSize / 2, pointSize)
        animator.duration = 800
        animator.interpolator = AccelerateInterpolator()
        animator.addUpdateListener {
            finalPointSize = it.animatedValue as Int
            invalidate()
        }
        animator.start()
    }
}