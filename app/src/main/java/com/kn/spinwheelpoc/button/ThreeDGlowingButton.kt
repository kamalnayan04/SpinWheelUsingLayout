package com.kn.spinwheelpoc.button

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.kn.spinwheelpoc.databinding.GlowingButtonLayoutBinding

@SuppressLint("ClickableViewAccessibility")
class ThreeDGlowingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = GlowingButtonLayoutBinding.inflate(LayoutInflater.from(context), this)

    init {
//        binding.glowView.style = Paint.Style.FILL
        binding.mainImage.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(
                v: View?,
                event: MotionEvent?
            ): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v?.animate()?.translationY(8f)?.setDuration(50)?.start()  // Push down
                        binding.imageBg.animate().alpha(0f).setDuration(70)
                            .start()  // Darken shadow
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v?.animate()?.translationY(0f)?.setDuration(100)?.start()  // Lift up
                        binding.imageBg.animate().alpha(1f).setDuration(100)
                            .start()  // Restore shadow
                    }
                }
                return false
            }
        })

    }

}