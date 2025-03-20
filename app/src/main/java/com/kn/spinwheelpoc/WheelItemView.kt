package com.kn.spinwheelpoc

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.kn.spinwheelpoc.databinding.WheelItemBinding

class WheelItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: WheelItemBinding =
        WheelItemBinding.inflate(LayoutInflater.from(context), this)

    fun setUpTitle(action: TextView.() -> Unit) {
        binding.title.apply(action)
    }

    fun setupLogo(action: ImageView.() -> Unit) {
        binding.logoImage.apply { action() }
    }


    fun setupBg(action: ClippedImageView.() -> Unit) {
        binding.bgImage.apply {
            action()
        }

    }

}