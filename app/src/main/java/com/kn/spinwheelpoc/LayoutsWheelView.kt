package com.kn.spinwheelpoc

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import com.kn.spinwheelpoc.databinding.LayoutWheelViewsBinding
import kotlin.random.Random

const val MIN_ITEMS_C0UNT = 4
const val MAX_ITEMS_C0UNT = 6

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

    init {

        this.setOnLongClickListener {
            this.rotation = 0f
//            rotate()
            false
        }
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

