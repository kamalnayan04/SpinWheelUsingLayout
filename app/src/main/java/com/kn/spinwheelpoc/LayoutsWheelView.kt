package com.kn.spinwheelpoc

import android.animation.AnimatorSet
import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.view.animation.PathInterpolator
import androidx.annotation.IntDef
import androidx.collection.arraySetOf
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import com.kn.spinwheelpoc.databinding.LayoutWheelViewsBinding
import kotlin.math.abs
import kotlin.random.Random

const val MIN_ITEMS_C0UNT = 4
const val MAX_ITEMS_C0UNT = 6
const val ROTATION_SENSITIVITY = .5f
const val WHEEL_ROTATION_COUNT = 9
const val INVALID_TARGET = -1
class LayoutsWheelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    private val binding = LayoutWheelViewsBinding.inflate(LayoutInflater.from(context), this)

    private var lastAnimatedDot = Integer.MIN_VALUE
    var itemCount = 4
        set(value) {
            field = value
            setupItemViews()
            requestLayout()
        }


    var target = 0
        set(value) {
            field = value
            if (value == INVALID_TARGET) {
                handleInvalidTarget()
            }
        }

    private fun handleInvalidTarget() {
        binding.wheelOverlay.apply {
            overlayColor = WheelOverlayView.ERROR_OVERLAY_COLOR.toColorInt()
            overlayType = WheelOverlayView.OVERLAY_TYPE_FULL
            isVisible = true
            margin = 25f
            animate().alpha(1f).withStartAction { alpha = 0f }.setDuration(1000).start()
        }
    }

    var rotationDuration = 14000L
    var wheelListener: WheelListener? = null
    private var previousAngle = 0f
    private var previousRadius = 0f
    private var centerX = 0f
    private var centerY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
        if (binding.wheelOverlay.isVisible) return false


        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                centerX = width / 2f
                centerY = height / 2f
                previousAngle = getAngle(event.x, event.y)
                previousRadius = getRadius(event.x, event.y)
            }

            MotionEvent.ACTION_MOVE -> {
                val currentAngle = getAngle(event.x, event.y)
                val currentRadius = getRadius(event.x, event.y)

                // Ensure the movement is circular by checking angle change and radius consistency
                if (isCircularMotion(previousRadius, currentRadius)) {
                    val deltaAngle = (currentAngle - previousAngle) * ROTATION_SENSITIVITY
                    binding.wheelContainer.rotation += deltaAngle.coerceIn(
                        -10f,
                        10f
                    ) //capping speed of rotation
                    onRotationValueUpdated(binding.wheelContainer.rotation, rotatedManually = true)
                    previousAngle = currentAngle
                    previousRadius = currentRadius
                }
            }
        }
        return true
    }

    /**
     * Gets the angle of the touch point relative to the center of the wheel
     */
    private fun getAngle(x: Float, y: Float): Float {
        val dx = x - centerX
        val dy = y - centerY
        return Math.toDegrees(kotlin.math.atan2(dy, dx).toDouble()).toFloat()
    }

    /**
     * Calculates the distance (radius) from the center of the wheel to the touch point
     */
    private fun getRadius(x: Float, y: Float): Float {
        val dx = x - centerX
        val dy = y - centerY
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }


    /**
     * Detects if the movement is circular by checking:
     * 1. The radius (distance from center) remains relatively stable.
     * 2. The touch moves in an angular fashion rather than randomly.
     */
    private fun isCircularMotion(prevRadius: Float, currentRadius: Float): Boolean {
        val radiusChange = abs(currentRadius - prevRadius)
        return radiusChange < 30
    }

    private fun setItemsViewsRotationAndAngle() {
        (0 until itemViews.size).forEach { index ->
            val item = itemViews[index]
            if (index < itemCount) {
                item.apply {
                    isVisible = true
                    post { rotation = getInitialAngle() + index * 360 / itemCount }
                    item.setupBg {

                        this.sweepDegree = 360f / itemCount
                    }
//                    item.setTriangleImage(sweepDegree, bgList.random())
                }
            } else
                item.isVisible = false


        }
    }

    private val logoList =
        listOf(
            R.drawable.zomato,
            R.drawable.better_luck,
            R.drawable.district,
            R.drawable.pringles,
            R.drawable.coke,
            R.drawable.match_ticket
        )

    private val bgList =
        listOf(
            R.drawable.texture_gold,
            R.drawable.texture_light_blue,
            R.drawable.texture_yellow,
            R.drawable.texture_dark_blue,
            R.drawable.texture_back
        )

    private val dotAngleList = arraySetOf<Int>()

    fun setupItemViews() {
        binding.wheelOverlay.totalItems = itemCount
        binding.dotsView.apply {
            totalItems = itemCount
            animateDots()
        }
        binding.glowView.totalItems = itemCount
        setItemsViewsRotationAndAngle()
        setDotPointList()
        (0 until itemCount).forEach { index ->
            itemViews[index].apply {
                setUpTitle {
                    text = "${getIndexText(index)} Wheel"
                    setTextColor(
                        Color.rgb(
                            Random.nextInt(0, 255),
                            Random.nextInt(0, 255),
                            Random.nextInt(0, 255)
                        )
                    )
                }
                setupLogo {
                    setImageResource(logoList.random())
                }

                setupBg {
                    setImageResource(bgList[(0+index)%bgList.size])
                }
            }
        }
    }

    private fun setDotPointList() {
        val angleStep = (360 / itemCount)
        dotAngleList.clear()
        (0 until itemCount).forEach { index ->
            dotAngleList.add((angleStep * index))
        }
        Log.e("arrow_anim", "list = $dotAngleList")
    }

    private var isAnimating = false


    fun play() {
        if (isAnimating) return
        isAnimating = true
        binding.dotsView.apply {
            animateDots()
        }
        resetValues()
        val offerRevealAlphaAnim =
            ObjectAnimator.ofFloat(
                binding.wheelOverlay,
                ALPHA,
                1f
            ).apply {
                doOnStart {
                    binding.wheelOverlay.isVisible=true
                    binding.glowView.apply {
                        showTopGlow = true
                        hideWithAnimation()
                    }
                }
                doOnEnd {
                    isAnimating = false
                }
                duration = 800
                interpolator = DecelerateInterpolator()
            }


        val propertyValueAnimator =
            ValueAnimator.ofPropertyValuesHolder(getRotationAnimationKeyFrameSet()).apply {
                duration = rotationDuration
                interpolator = PathInterpolator(0.15f, 1f, 0.8f, 1f)
                doOnStart {
                    wheelListener?.onRotationStatusChanged(RotationStatus.ROTATION_STARTED)
                }
                doOnEnd {
                    wheelListener?.onRotationStatusChanged(RotationStatus.ROTATION_COMPLETED)
                }
                doOnCancel {
                    wheelListener?.onRotationStatusChanged(RotationStatus.ROTATION_CANCELLED)
                }

                addUpdateListener {
                    val updatedValue = (it.animatedValue as Float)
                    binding.wheelContainer.rotation = updatedValue
                    onRotationValueUpdated(updatedValue, false)
                }
            }


        val rotationAnimationSet = AnimatorSet()
        rotationAnimationSet.playSequentially(propertyValueAnimator, offerRevealAlphaAnim)
        rotationAnimationSet.start()
    }

    private fun getRotationAnimationKeyFrameSet(): PropertyValuesHolder {
        val totalRotation =
            (360 * WHEEL_ROTATION_COUNT) + getRotationValueOfTarget(target).toFloat() + abs(
                getInitialAngle()
            )
        val finalPosition =
            (360 * WHEEL_ROTATION_COUNT) + getRotationValueOfTarget(target).toFloat()   // Slight bounce-back position

        val startFrame = Keyframe.ofFloat(0f, 0f)                // Start at 0°
        val rotateLeftFrame =
            Keyframe.ofFloat(0.15f, getInitialAngle())                // go to left
        val speedUpRotationFrame =
            Keyframe.ofFloat(0.55f, (totalRotation * 0.85f)) // Speed up smoothly
        val slowDownRotationFrame =
            Keyframe.ofFloat(0.85f, (totalRotation * 0.96f)) // Slow down earlier
        val verySlowEndFrame =
            Keyframe.ofFloat(0.975f, (totalRotation * 1f)) // Almost final, very slow
        val rotateToFinalPositionFrame =
            Keyframe.ofFloat(1f, finalPosition)     // Bounce back to actual position

        val keyframeSet =
            PropertyValuesHolder.ofKeyframe(
                "rotation",
                startFrame,
                rotateLeftFrame,
                speedUpRotationFrame,
                slowDownRotationFrame, verySlowEndFrame,
                rotateToFinalPositionFrame
            )
        return keyframeSet
    }

    private fun onRotationValueUpdated(currentRotation: Float, rotatedManually: Boolean) {
        val threshold = 5 // Define how close the dot should be to start animation
        val adjustedRotation = (currentRotation % 360).toInt() // Normalize rotation to 0-360
        val rotationCount = (currentRotation / 360)// Normalize rotation to 0-360
        if (!rotatedManually) wheelListener?.onRotationUpdated(
            currentRotation,
            rotationCount,
            getRotationCompletionPercent(currentRotation)
        )

        dotAngleList.forEach { dotAngle ->

            if (adjustedRotation in (dotAngle - threshold)..(dotAngle + threshold) && lastAnimatedDot != dotAngle) {
                lastAnimatedDot = dotAngle
                animateArrow(speedMultiplier = rotationCount)
            }
        }
    }

    private fun getRotationCompletionPercent(currentRotation: Float): Float {
        val totalRotation = 360f * WHEEL_ROTATION_COUNT + getRotationValueOfTarget(target)
        return (currentRotation / totalRotation) * 100
    }

    private var arrowAnimDuration = 50L
    private fun animateArrow(speedMultiplier: Float) {
        val speedOnScaleOfOne = (speedMultiplier * .1)
        val finalDuration = (arrowAnimDuration * speedOnScaleOfOne + arrowAnimDuration)
        binding.pointerImage.apply {
            pivotX = width / 2f
            pivotY = height * 0.2f
            animate().setInterpolator(null)
                .rotation(-20f) // Rotate slightly to the left
                .setDuration(finalDuration.toLong())
                .withEndAction {
                    binding.pointerImage.animate().setInterpolator(null)
                        .rotation(0f) // Reset to original position
                        .setDuration(finalDuration.toLong())
                        .start()
                }
                .start()
        }

    }

    private fun resetValues() {
        binding.glowView.apply {
            showTopGlow = false
            topMargin = 0f
        }
        binding.wheelContainer.rotation = 0f
        target = target.coerceIn(0, itemCount)
        binding.wheelOverlay.apply {
            isVisible = false
            alpha = 0f
            overlayColor = WheelOverlayView.DEFAULT_OVERLAY_COLOR.toColorInt()
            overlayType = WheelOverlayView.OVERLAY_TYPE_PARTIAL
        }
    }

    private fun getRotationValueOfTarget(target: Int): Float {
        val sweepAngle: Float = (360 / itemCount).toFloat() // 60
        val targetItemAngle: Float = getInitialAngle() + sweepAngle * (itemCount - target + 1)
        return targetItemAngle
    }


    private fun getIndexText(index: Int) = when (index) {
        0 -> "First"
        1 -> "Second"
        2 -> "Third"
        3 -> "Fourth"
        4 -> "Fifth"
        5 -> "Sixth"
        else -> "GUlu GuLU"
    }

    private val itemViews by lazy {
        listOf(
            binding.wheelItem1,
            binding.wheelItem2,
            binding.wheelItem3,
            binding.wheelItem4,
            binding.wheelItem5,
            binding.wheelItem6,
        )
    }

    fun getInitialAngle() = -(360 / itemCount) / 2f
    fun rotateByAngle(angle: Int) {
        binding.wheelContainer.rotation = angle.toFloat()
    }

    interface WheelListener {
        fun onRotationUpdated(
            rotation: Float,
            rotationCount: Float,
            rotationCompletionPercent: Float
        )

        fun onRotationStatusChanged(@RotationStatus rotationStatus: Int)
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        RotationStatus.ROTATION_IDLE,
        RotationStatus.ROTATION_STARTED,
        RotationStatus.ROTATION_COMPLETED,
        RotationStatus.ROTATION_CANCELLED,
    )
    annotation class RotationStatus {

        companion object {
            const val ROTATION_IDLE = 0
            const val ROTATION_STARTED = 1
            const val ROTATION_COMPLETED = 2
            const val ROTATION_CANCELLED = -1
        }
    }

}

