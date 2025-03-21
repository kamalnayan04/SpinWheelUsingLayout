package com.kn.spinwheelpoc

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd
import kotlin.math.min

class GlowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val glowWidth = 30f
    private val padding = 60
    var topMargin = 0f
        set(value) {
            field = value
            invalidate()
        }

    var totalItems: Int = 4
        set(value) {
            field = value
            invalidate()
        }
    var showTopGlow = false
        set(value) {
            field = value
            postInvalidate()
        }

    private val glowingPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        isDither = true
        strokeWidth = glowWidth
        color = Color.rgb(183, 213, 251)
        maskFilter = BlurMaskFilter(
            glowWidth,
            BlurMaskFilter.Blur.NORMAL
        )
    }

    private val glowRadius: Float
        get() = (wheelRadius - (glowWidth + padding * 0.45).toInt()) - topMargin

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (showTopGlow) drawTopGlowArc(canvas)
        else drawGlow(canvas)
    }

    private fun drawTopGlowArc(canvas: Canvas) {
        val sweepAngle = 360f / totalItems
        val radius = wheelRadius - (glowWidth + padding * 0.45).toInt()
        val itemArc = RectF(
            centerOfWheel - radius,
            centerOfWheel - radius,
            centerOfWheel + radius,
            centerOfWheel + radius
        )

        canvas.drawArc(itemArc, 270 - (sweepAngle / 2), sweepAngle, true, glowingPaint)
    }

    private var wheelRadius: Float = 0F
    private var centerOfWheel: Float = 0F

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minDimension = min(measuredWidth, measuredHeight)

        wheelRadius = minDimension / 2F
        centerOfWheel = minDimension / 2F

        setMeasuredDimension(minDimension, minDimension)
    }

    private fun drawGlow(canvas: Canvas) {
        canvas.drawCircle(
            centerOfWheel,
            centerOfWheel,
            glowRadius,
            glowingPaint
        )
    }

    fun hideWithAnimation(onAnimationEnd: () -> Unit = {}) {
        val animator =
            ValueAnimator.ofFloat(0f, 50f)
        animator.duration = 800
        animator.interpolator = AccelerateInterpolator()

        animator.addUpdateListener { animation ->
            topMargin = animation.animatedValue as Float
        }

        animator.doOnEnd {
            onAnimationEnd() // Call the callback when the animation finishes
        }

        animator.start()
    }


}