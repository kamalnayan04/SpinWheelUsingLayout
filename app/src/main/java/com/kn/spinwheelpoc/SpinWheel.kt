package com.kn.spinwheelpoc

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.DrawableRes
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withClip
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random


class SpinWheel @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private val textColorList = arrayListOf(
        Color.parseColor("#000000"),
        Color.parseColor("#FFFFFF"),
        Color.parseColor("#FF0000"),
        Color.parseColor("#00FF00"),
        Color.parseColor("#0000FF"),
        Color.parseColor("#00FFFF"),
        Color.parseColor("#FF00FF"),
        Color.parseColor("#FFFF00"),
    )


    private val wheelData = listOf(
        WheelData(
            R.drawable.texture_dark_blue,
            "#E0BD53",
            "#E0BD53",
            R.drawable.better_luck,
            "Better Luck",
            intArrayOf(textColorList[Random.nextInt(8)])
        ),
        WheelData(
            R.drawable.texture_light_blue,
            "#E0BD53",
            "#E0BD53",
            R.drawable.district,
            "District Off",
            intArrayOf(textColorList[Random.nextInt(8)])
        ),
        WheelData(
            R.drawable.texture_gold,
            "#071C2A",
            "#000000",
            R.drawable.zomato,
            "200 OFF",
            intArrayOf(textColorList[Random.nextInt(8)])
        ),
//        WheelData(R.drawable.radial),
//
        WheelData(
            R.drawable.texture_yellow,
            "#4FA3E0",
            "#3F3CCE",
            R.drawable.match_ticket,
            "Free Match",
            intArrayOf(textColorList[Random.nextInt(8)])
        ),
        WheelData(
            R.drawable.texture_light_blue,
            "#4FA3E0",
            "#3F3CCE",
            R.drawable.pringles,
            "Free Pringles",
            intArrayOf(textColorList[Random.nextInt(8)])
        ),
//        WheelData(R.drawable.blue),


//        WheelData(R.drawable.radial)
    )
    private val onClick: ((View) -> Unit) = {
        this.animate().rotation(3600f).setDuration(3000).setInterpolator(DecelerateInterpolator())
            .start()

        this.invalidate()
    }

    init {
        setOnClickListener(onClick)
    }

    private var rimWidth = 15f
    private var glowWidth = 45f
    private var wheelRadius: Float = 0F
    private var centerOfWheel: Float = 0F
    private var wheelItemBackgroundPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
    }
    private var textPadding: Float = resources.getDimensionPixelSize(R.dimen.dp4).toFloat()
    private var itemTextLetterSpacing: Float = 0.1F
    private var itemTextSize: Float = resources.getDimensionPixelSize(R.dimen.sp16).toFloat()
    private var itemTextFont: Typeface = Typeface.SANS_SERIF
    private val wheelItemTextPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        letterSpacing = itemTextLetterSpacing
        textSize = itemTextSize
        typeface = itemTextFont
        textAlign = Paint.Align.CENTER
    }

    private val wheelBackgroundPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = Color.WHITE
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        drawGlow(canvas)
        drawWheelBackground(canvas)
//        drawItemsWithColor(canvas)
        drawImages(canvas)
        drawCenterDot(canvas)
        drawLogo(canvas)
        drawText(canvas)
        drawRimImage(canvas)
        drawRim(canvas)
        drawPointsOverRim(canvas)
    }

    private fun drawText(canvas: Canvas) {
        var sweepAngle = 360f / wheelData.size
        var startAngle = 0f
        wheelData.forEachIndexed { index, item ->
            val itemTextTypeface = if (item.textFontTypeface == null) {
                itemTextFont
            } else {
                item.textFontTypeface
            }

            val adjustedRadius =
                (wheelRadius - wheelItemTextPaint.textSize - textPadding )
            val textRectF = RectF(
                centerOfWheel - adjustedRadius,
                centerOfWheel - adjustedRadius,
                centerOfWheel + adjustedRadius,
                centerOfWheel + adjustedRadius,
            )


            val path = Path().apply {
//                addArc(textRectF, startAngle, sweepAngle)
            }
            val startRadians = Math.toRadians(startAngle.toDouble())
            val endRadians = Math.toRadians((startAngle + sweepAngle).toDouble())

            val startX = centerOfWheel + adjustedRadius * cos(startRadians)
            val startY = centerOfWheel + adjustedRadius * sin(startRadians)
            val endX = centerOfWheel + adjustedRadius * cos(endRadians)
            val endY = centerOfWheel + adjustedRadius * sin(endRadians)

            path.moveTo(startX.toFloat(), startY.toFloat())
            path.lineTo(endX.toFloat(), endY.toFloat())

            val bounds = RectF()
            path.computeBounds(bounds, true)

            val textPaintShader = if (item.textColor.size == 1) {
                LinearGradient(
                    bounds.left,
                    bounds.left,
                    bounds.right,
                    bounds.right,
                    intArrayOf(item.textColor[0], item.textColor[0]),
                    null,
                    Shader.TileMode.CLAMP
                )
            } else if (item.textColor.isEmpty()) {
                throw IllegalArgumentException("At least one color value is required: textColor list is empty.")
            } else {
                LinearGradient(
                    bounds.left,
                    bounds.left,
                    bounds.right,
                    bounds.right,
                    item.textColor,
                    null,
                    Shader.TileMode.CLAMP
                )
            }

            wheelItemTextPaint.apply {
                typeface = itemTextTypeface
                shader = textPaintShader
            }

            val separatedText = item.text.split("\n")

            val horizontalOffset = (((wheelRadius * Math.PI) / wheelRadius)).toFloat()
            val verticalOffset = ((wheelRadius / 2 / 3) - 75)

            if (separatedText.size > 1) {
                separatedText.forEachIndexed { lineIndex, lineText ->
                    canvas.drawTextOnPath(
                        lineText,
                        path,
                        horizontalOffset,
                        verticalOffset + ((wheelItemTextPaint.textSize + wheelItemTextPaint.letterSpacing) * lineIndex),
                        wheelItemTextPaint
                    )
                }
            } else {

                canvas.drawTextOnPath(
                    item.text,
                    path,
                    horizontalOffset,
                    verticalOffset,
                    wheelItemTextPaint
                )
            }
            startAngle += sweepAngle

        }
    }

    private fun drawLogo(canvas: Canvas) {
        val iconSizeMultiplier = 2f
        val iconPositionFraction = 0.35f
        var sweepAngle = 360f / wheelData.size // Calculate the sweep angle for each slice
        var startAngle = 0f
        for (index in wheelData.indices) {
            val imgWidth: Int =
                (resources.getDimensionPixelSize(R.dimen.dp36) * iconSizeMultiplier).toInt()
            val icon = getOptimizedBitmap(wheelData[index].icon, imgWidth) ?: return
            val angle = sweepAngle * index + sweepAngle / 2
            val radians = Math.toRadians(angle.toDouble())

            val sliceCenterX = (centerOfWheel + (wheelRadius * iconPositionFraction) * cos(
                radians
            )).toFloat()
            val sliceCenterY = (centerOfWheel + (wheelRadius * iconPositionFraction) * sin(
                radians
            )).toFloat()

            val rect = Rect(
                (sliceCenterX - imgWidth / 2).toInt(),
                (sliceCenterY - imgWidth / 2).toInt(),
                (sliceCenterX + imgWidth / 2).toInt(),
                (sliceCenterY + imgWidth / 2).toInt()
            )

            canvas.drawBitmap(icon, null, rect, null)


            startAngle += sweepAngle
        }
    }

    private fun drawRim(canvas: Canvas) {

    }

    private fun drawPointsOverRim(canvas: Canvas) {
        val pointSize = resources.getDimensionPixelSize(R.dimen.dp36)
        val bitmap = getOptimizedBitmap(R.drawable.glare, pointSize) ?: return

        val totalPointsCount = wheelData.size
        val angleStep = (2 * Math.PI / totalPointsCount).toFloat()

        for (i in 0 until totalPointsCount) {
            val angle = i * angleStep
            val pointX = centerOfWheel + (wheelRadius - glowWidth * 0.85) * cos(angle)
            val pointY = centerOfWheel + (wheelRadius - glowWidth * 0.85) * sin(angle)

            val pointRect = Rect(
                (pointX - pointSize / 2).toInt(),
                (pointY - pointSize / 2).toInt(),
                (pointX + pointSize / 2).toInt(),
                (pointY + pointSize / 2).toInt(),
            )
            canvas.drawBitmap(bitmap, null, pointRect, null)
        }
    }

    private fun drawItemsWithColor(canvas: Canvas) {
        var sweepAngle = 360f / wheelData.size // Calculate the sweep angle for each slice
        var startAngle = 0f
        for (index in wheelData.indices) {

            wheelItemBackgroundPaint.shader = RadialGradient(
                centerOfWheel,
                centerOfWheel,
                wheelRadius - (rimWidth / 2 + glowWidth),
                intArrayOf(
                    wheelData[index].startColor.toColorInt(),
                    wheelData[index].endColor.toColorInt()
                ),
                null,
                Shader.TileMode.CLAMP
            )
            val itemArc = RectF(
                centerOfWheel - (wheelRadius - (rimWidth / 2 + glowWidth)),
                centerOfWheel - (wheelRadius - (rimWidth / 2 + glowWidth)),
                centerOfWheel + (wheelRadius - (rimWidth / 2 + glowWidth)),
                centerOfWheel + (wheelRadius - (rimWidth / 2 + glowWidth)),
            )
            canvas.drawArc(itemArc, startAngle, sweepAngle, true, wheelItemBackgroundPaint)

            startAngle += sweepAngle
        }
    }

    private var itemIndex = 0

    /**
     * private fun drawImages(canvas: Canvas) {
     *         val sliceCount = wheelData.size
     *         val sweepAngle = 360f / sliceCount
     *         wheelData.forEach { item ->
     *
     *
     *             val arcRadius = wheelRadius - (rimWidth / 2 + glowWidth)
     *
     *             val itemArc = RectF(
     *                 centerOfWheel - arcRadius,
     *                 centerOfWheel - arcRadius,
     *                 centerOfWheel + arcRadius,
     *                 centerOfWheel + arcRadius,
     *             )
     *             Log.d(
     *                 "bitmap_issue",
     *                 "itemArc width = ${itemArc.width()} and height = ${itemArc.height()} and wheel radius = $wheelRadius"
     *             )
     *             val originalBitmap = getOptimizedBitmap(
     *                 item.bgId,
     *                 wheelRadius.toInt()
     *             ) ?: return
     *
     *             val shader = BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
     *
     *             val arcPath = Path().apply {
     *                 moveTo(centerOfWheel, centerOfWheel)
     *                 arcTo(itemArc, sweepAngle * wheelData.indexOf(item), sweepAngle, false)
     *                 close()
     *             }
     *             val paint = Paint().apply {
     *                 isAntiAlias = true
     *                 this.shader = shader
     *             }
     *
     *
     * //            // Calculate the maximum size that fits within the arc segment
     * //            val segmentWidth = min((itemArc.width() * (sweepAngle / 360f)), (itemArc.width() / 1.5).toFloat())
     * //            val segmentHeight = itemArc.height() /2 // Adjust this value to control height
     * //
     * //            // Calculate the scale factor to maintain aspect ratio
     * //            val bitmapAspect = originalBitmap.width.toFloat() / originalBitmap.height
     * //            val targetWidth: Float
     * //            val targetHeight: Float
     * //
     * //            if (originalBitmap.width > originalBitmap.height) {
     * //                targetWidth = segmentWidth
     * //                targetHeight = segmentWidth / bitmapAspect
     * //            } else {
     * //                targetHeight = segmentHeight
     * //                targetWidth = segmentHeight * bitmapAspect
     * //            }
     * //
     * //            // Calculate the position to center the bitmap in the arc segment
     * //            val left = sliceCenterX - targetWidth / 2
     * //            val top = sliceCenterY - targetHeight / 2
     * //
     * //            // Create a destination rectangle that maintains aspect ratio
     * //            val destRect = RectF(
     * //                left,
     * //                top,
     * //                left + targetWidth,
     * //                top + targetHeight
     * //            )
     *
     *             canvas.withClip(arcPath) {
     *                 drawBitmap(originalBitmap, null, itemArc, paint)
     *             }
     *
     *         }
     *     }
     */
    private fun drawImages(canvas: Canvas) {
        val sliceCount = wheelData.size
        val sweepAngle = 360f / sliceCount
        wheelData.forEach { item ->

            val angle = sweepAngle * wheelData.indexOf(item) + sweepAngle / 2
            val radians = Math.toRadians(angle.toDouble())

            val sliceCenterX =
                (centerOfWheel + (wheelRadius) * cos(radians)).toFloat()
            val sliceCenterY =
                (centerOfWheel + (wheelRadius) * sin(radians)).toFloat()

            val arcRadius = wheelRadius - (rimWidth / 2 + glowWidth)

            val itemArc = RectF(
                centerOfWheel - arcRadius,
                centerOfWheel - arcRadius,
                centerOfWheel + arcRadius,
                centerOfWheel + arcRadius,
            )
            Log.d(
                "bitmap_issue",
                "itemArc width = ${itemArc.width()} and height = ${itemArc.height()} and wheel radius = $wheelRadius and minimum width = $measuredWidth and height = $measuredHeight"
            )
            val originalBitmap = getOptimizedBitmap(
                item.bgId,
                wheelRadius.toInt()
            ) ?: return

            val shader = BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

            val arcPath = Path().apply {
                moveTo(centerOfWheel, centerOfWheel)
                arcTo(itemArc, sweepAngle * wheelData.indexOf(item), sweepAngle, false)
                close()
            }
            val paint = Paint().apply {
                isAntiAlias = true
                this.shader = shader
            }


            //            // Calculate the maximum size that fits within the arc segment
            //            val segmentWidth = min((itemArc.width() * (sweepAngle / 360f)), (itemArc.width() / 1.5).toFloat())
            //            val segmentHeight = itemArc.height() /2 // Adjust this value to control height
            //
            //            // Calculate the scale factor to maintain aspect ratio
            //            val bitmapAspect = originalBitmap.width.toFloat() / originalBitmap.height
            //            val targetWidth: Float
            //            val targetHeight: Float
            //
            //            if (originalBitmap.width > originalBitmap.height) {
            //                targetWidth = segmentWidth
            //                targetHeight = segmentWidth / bitmapAspect
            //            } else {
            //                targetHeight = segmentHeight
            //                targetWidth = segmentHeight * bitmapAspect
            //            }
            //
            //            // Calculate the position to center the bitmap in the arc segment
            //            val left = sliceCenterX - targetWidth / 2
            //            val top = sliceCenterY - targetHeight / 2
            //
            //            // Create a destination rectangle that maintains aspect ratio
            //            val destRect = RectF(
            //                left,
            //                top,
            //                left + targetWidth,
            //                top + targetHeight
            //            )

            canvas.withClip(arcPath) {
                drawBitmap(originalBitmap, null, itemArc, paint)
            }

        }
    }


    /**
     * Draws full image ;; has no clipping
     */
//    private fun drawImages(canvas: Canvas) {
//        val sliceCount = wheelData.size
//        val sweepAngle = 360f / sliceCount
//
//        // Create a square bitmap for the entire wheel
//        val wheelBitmap = Bitmap.createBitmap(
//            (wheelRadius * 2).toInt(),
//            (wheelRadius * 2).toInt(),
//            Bitmap.Config.ARGB_8888
//        )
//        val wheelCanvas = Canvas(wheelBitmap)
//
//        // Draw each segment's background onto the wheel bitmap
//        wheelData.forEachIndexed { index, item ->
//            val startAngle = sweepAngle * index
//
//            // Load the background image
//            val backgroundImage = getOptimizedBitmap(
//                item.bgId,
//                wheelRadius.toInt(), // Half size - for the quadrant
//                wheelRadius.toInt()
//            ) ?: return
//
//            // Determine which quadrant this segment belongs to
//            val quadrantRect = when (index) {
//                0 -> RectF(wheelRadius, 0f, wheelRadius * 2, wheelRadius)              // Top right
//                1 -> RectF(wheelRadius, wheelRadius, wheelRadius * 2, wheelRadius * 2) // Bottom right
//                2 -> RectF(0f, wheelRadius, wheelRadius, wheelRadius * 2)              // Bottom left
//                3 -> RectF(0f, 0f, wheelRadius, wheelRadius)                           // Top left
//                else -> RectF(0f, 0f, wheelRadius, wheelRadius) // Fallback
//            }
//
//            // Draw this background image in its quadrant
//            wheelCanvas.drawBitmap(backgroundImage, null, quadrantRect, null)
//        }
//
//        // Now draw the complete wheel bitmap onto the main canvas
//        val arcRadius = wheelRadius - (rimWidth / 2 + glowWidth)
//        val wheelRect = RectF(
//            centerOfWheel - arcRadius,
//            centerOfWheel - arcRadius,
//            centerOfWheel + arcRadius,
//            centerOfWheel + arcRadius
//        )
//
//        canvas.drawBitmap(wheelBitmap, null, wheelRect, null)
//
//        // Clean up
//        wheelBitmap.recycle()
//    }
//



    private fun drawGlow(canvas: Canvas) {
        canvas.drawCircle(
            centerOfWheel,
            centerOfWheel,
            wheelRadius - (glowWidth + rimWidth * 0.35).toInt(),
            glowingPaint
        )
    }

    private fun drawRimImage(canvas: Canvas) {
        val rimSize = wheelRadius.times(2).toInt()
        val rimBitmap = getOptimizedBitmap(R.drawable.rim, rimSize, rimSize) ?: return
        val rectForCenterDrawable = Rect(
            (centerOfWheel - (rimSize - glowWidth) / 2).toInt(),
            (centerOfWheel - (rimSize - glowWidth) / 2).toInt(),
            (centerOfWheel + (rimSize - glowWidth) / 2).toInt(),
            (centerOfWheel + (rimSize - glowWidth) / 2).toInt()
        )
        canvas.drawBitmap(rimBitmap, null, rectForCenterDrawable, null)
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

    private fun drawWheelBackground(canvas: Canvas) {
        canvas.drawCircle(
            centerOfWheel,
            centerOfWheel,
            wheelRadius - (rimWidth / 2 + glowWidth),
            wheelBackgroundPaint
        )
    }


    private fun drawCenterDot(canvas: Canvas) {
        val dotSize = resources.getDimension(R.dimen.dp36).toInt()
        val centerDrawable = getOptimizedBitmap(R.drawable.center_dot, dotSize) ?: return

        val center = centerOfWheel.toInt()
        val rectForCenterDrawable = Rect(
            center - dotSize / 2,
            center - dotSize / 2,
            center + dotSize / 2,
            center + dotSize / 2
        )
        canvas.drawBitmap(centerDrawable, null, rectForCenterDrawable, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minDimension = min(measuredWidth, measuredHeight)

        wheelRadius = minDimension / 2F
        centerOfWheel = minDimension / 2F

        setMeasuredDimension(minDimension, minDimension)
    }


}