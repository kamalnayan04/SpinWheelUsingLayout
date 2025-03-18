package com.kn.spinwheelpoc

import android.graphics.Typeface
import androidx.annotation.DrawableRes

data class WheelData(
    @DrawableRes val bgId: Int,
    val startColor: String,
    val endColor: String,
    @DrawableRes val icon: Int,
    val text: String="",
    val textColor: IntArray,
    val textFontTypeface: Typeface? = null,
)