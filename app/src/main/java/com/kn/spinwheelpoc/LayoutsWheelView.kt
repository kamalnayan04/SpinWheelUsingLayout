package com.kn.spinwheelpoc

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import com.kn.spinwheelpoc.databinding.LayoutWheelViewsBinding
import kotlin.math.abs
import kotlin.random.Random

const val MIN_ITEMS_C0UNT = 4
const val MAX_ITEMS_C0UNT = 6
const val ROTATION_SENSITIVITY = .5f

class LayoutsWheelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    private val binding = LayoutWheelViewsBinding.inflate(LayoutInflater.from(context), this)


    var itemCount = 4
        set(value) {
            field = value
            setupItemViews()
            requestLayout()
        }


    private var previousX = 0f
    private var previousY = 0f


    private var previousAngle = 0f
    private var previousRadius = 0f
    private var centerX = 0f
    private var centerY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (binding.wheelOverlay.isVisible)
            return false

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
                    binding.wheelContainer.rotation += deltaAngle
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
            R.drawable.texture_yellow,
            R.drawable.texture_light_blue,
            R.drawable.texture_dark_blue,
        )

    fun setupItemViews() {
        binding.wheelOverlay.totalItems = itemCount
        binding.dotsView.apply {
            totalItems = itemCount
        }
        binding.glowView.totalItems = itemCount
        setItemsViewsRotationAndAngle()
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
                    setImageResource(bgList.random())
                }
            }
        }
    }

    var target = 0

    fun play() {
      resetValues()
        val rotateAnim =
            ObjectAnimator.ofFloat(
                binding.wheelContainer,
                "rotation",
                getRotationValueOfTarget(target)
            ).apply {
                duration = 5000
                interpolator = DecelerateInterpolator()
            }

        val alphaAnim =
            ObjectAnimator.ofFloat(
                binding.wheelOverlay,
                "alpha",
                1f
            ).apply {
                doOnStart {
                    binding.wheelOverlay.apply {
                        isVisible = true
                    }
                    binding.glowView.apply {
                        showTopGlow = true
                        hideWithAnimation()
                    }
                }
                duration = 800
                interpolator = DecelerateInterpolator()
            }

        val animationSet = AnimatorSet()
        animationSet.playSequentially(rotateAnim, alphaAnim)
        animationSet.start()

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
        }
    }

    private fun getRotationValueOfTarget(target: Int): Float {
        val sweepAngle: Float = (360 / itemCount).toFloat() // 60
        val targetItemAngle: Float = getInitialAngle() + sweepAngle * (itemCount - target + 1)
        return 360 * 9f + targetItemAngle
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

}

