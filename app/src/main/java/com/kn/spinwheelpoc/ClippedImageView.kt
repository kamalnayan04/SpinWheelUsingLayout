package com.kn.spinwheelpoc

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.withClip
import androidx.core.view.updateLayoutParams
import com.kn.spinwheelpoc.databinding.ClippedImageLayoutBinding
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin

class ClippedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding = ClippedImageLayoutBinding.inflate(LayoutInflater.from(context), this)

    var sweepDegree = 90f
        set(value) {
            field = value
            invalidate()
        }

    private val path = Path()
    private val rectF = RectF()
    private val pathBounds = RectF()
    var clippedWidth = 0
        private set

    override fun dispatchDraw(canvas: Canvas) {
        rectF.set(0f, 0f, width.toFloat(), height.toFloat())
        path.reset()
        setClippingPath(rectF)
        canvas.withClip(path) {
            super.dispatchDraw(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        rectF.set(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
        setClippingPath(rectF)
        path.computeBounds(pathBounds, true)
        binding.image.updateLayoutParams {
            height = pathBounds.bottom.toInt()
        }
        clippedWidth = (pathBounds.left - pathBounds.right).toInt()
    }

    fun setClippingPath(rectF: RectF) {
        val startAngle = 270f - sweepDegree / 2
        val sweepAngle = sweepDegree
        val startRadians = toRadians(startAngle.toDouble())
        val endRadians = toRadians((startAngle + sweepAngle).toDouble())

        val startX = rectF.centerX() + (rectF.width() / 2) * cos(startRadians)
        val startY = rectF.centerY() + (rectF.height() / 2) * sin(startRadians)

        val endX = rectF.centerX() + (rectF.width() / 2) * cos(endRadians)
        val endY = rectF.centerY() + (rectF.height() / 2) * sin(endRadians)

        path.apply {
            addArc(rectF, startAngle, sweepDegree)
            moveTo(startX.toFloat(), startY.toFloat())
            lineTo(endX.toFloat(), endY.toFloat())
            lineTo(rectF.centerX(), rectF.centerY())
            close()
        }
    }

    fun setImageResource(id: Int) {
        binding.image.setImageResource(id)
    }

}