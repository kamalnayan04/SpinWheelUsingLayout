package com.kn.spinwheelpoc

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.kn.spinwheelpoc.AngleView.AngleViewOutlineProvider
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
    var itemCount = 6
        set(value) {
            field = value
            setupItemViews()
            requestLayout()
        }

    init {

        this.setOnLongClickListener {
            this.animate().rotation(5000f).setDuration(4000)
                .setInterpolator(DecelerateInterpolator()).start()
            false
        }
    }

    private fun setItemsViewsRotationAndAngle() {
        (0 until itemViews.size).forEach { index ->
            val item = itemViews[index]
            if (index < itemCount) {
                item.apply {
                    isVisible = true
                    rotation = getInitialAngle() + index * 360 / itemCount
                    item.setupBg {
                        sweepDegree= 360f / itemCount
                    }
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
            R.drawable.texture_back,
        )

    fun setupItemViews() {
        setItemsViewsRotationAndAngle()
        (0 until itemCount).forEach { index ->
            itemViews[index].apply {
                setUpTitle {
                    text = "${getIndexText(index)} Wheel"
                }
                setupLogo {
                    setImageResource(logoList.random())
                }
                setupArcView {
                    color = Color.rgb(
                        Random.nextInt(0, 255),
                        Random.nextInt(0, 255),
                        Random.nextInt(0, 255)
                    )
                    outlineProvider = AngleViewOutlineProvider(path)
                }
                setupBg {
                    setImageResource(bgList.random())

                }
            }
        }
    }

    fun rotate() {
        this.animate()
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