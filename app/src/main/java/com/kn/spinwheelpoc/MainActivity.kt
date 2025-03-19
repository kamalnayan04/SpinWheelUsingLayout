package com.kn.spinwheelpoc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kn.spinwheelpoc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var spin1: AngleView
    private lateinit var spin2: AngleView
    private lateinit var spin3: AngleView
    private lateinit var spin4: AngleView
    private lateinit var binding: ActivityMainBinding
//    private lateinit var spin1: AngleView

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
        binding.btnSpin.text="Spin to ${binding.wheelView.target+1}"
        binding.btnSpin.setOnClickListener {

            binding.wheelView.apply {
                play()
                target++
                binding.btnSpin.text="Spin to ${binding.wheelView.target+1}"
            }
        }
        binding.wheelView.setupItemViews()
        binding.wheelView.itemCount=4
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