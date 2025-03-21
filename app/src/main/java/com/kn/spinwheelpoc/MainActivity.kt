package com.kn.spinwheelpoc

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.kn.spinwheelpoc.LayoutsWheelView.RotationStatus
import com.kn.spinwheelpoc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        this.window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        binding.btnSpin.setText("Spin to ${binding.wheelView.target+1}")
        binding.btnSpin.setOnClickListener {
            if (binding.wheelView.isVisible)
            binding.wheelView.apply {
                play()
                target++
                if (target==binding.wheelView.itemCount)
                    target=INVALID_TARGET
                binding.btnSpin.setText("Spin to ${binding.wheelView.target+1}")
            }else
                binding.wheelLoading.apply {
                    isVisible = true
                    startAnimating()
                }
        }

        binding.btnRotate.setOnClickListener {
            binding.wheelView.itemCount = (binding.wheelView.itemCount + 1)
            val angle =
                binding.rotationEdt.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            binding.wheelView.rotateByAngle(angle)
        }

        binding.wheelView.apply {
            itemCount = 4
            wheelListener = object : LayoutsWheelView.WheelListener {
                override fun onRotationUpdated(
                    rotation: Float,
                    rotationCount: Float,
                    rotationCompletionPercent: Float
                ) {
                    when {

                        rotationCompletionPercent.toInt() >= 90 -> {
                            binding.statusText.text = "CLOSING IN..."
                            return
                        }
                    }
                }

                override fun onRotationStatusChanged(rotationStatus: Int) {
                    when (rotationStatus) {
                        RotationStatus.ROTATION_STARTED -> {
                            binding.btnSpin.isVisible = false
                            binding.statusText.text = "LET'S GO!"
                        }

                        RotationStatus.ROTATION_COMPLETED -> {
                            binding.btnSpin.isVisible = true
                            binding.statusText.text = "YOU WON!"
                        }
                    }
                }
            }
        }

//        binding.wheelView.setOnClickListener {
//            binding.wheelView.itemCount =
//                max(MIN_ITEMS_C0UNT, (binding.wheelView.itemCount + 1) % (MAX_ITEMS_C0UNT + 1)) // will be 0 when exceeds MAX_ITEMS_C0UNT then MIN will be set
//        }
    }

    private fun setupViews(spin: AngleView, index: Int) {
        spin.apply {
//            isGone=true
            postDelayed({
//                pivotX =  spin1.width / 2f
//                pivotY =  spin1.height.toFloat()
                rotation = getAngle() + index * 90
            }, 2000)
        }
    }

    private val item = 4
    fun getAngle() = -(360 / item) / 2f
}