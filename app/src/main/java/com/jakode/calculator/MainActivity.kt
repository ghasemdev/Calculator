package com.jakode.calculator

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.jakode.calculator.databinding.ActivityMainBinding
import kotlin.math.abs

private const val SWIPE_THRESHOLD = 100
private const val SWIPE_VELOCITY_THRESHOLD = 100

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mDetector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDetector = GestureDetectorCompat(this, MyGestureListener(binding))

        binding.key0.setOnClickListener { binding.calculatorOutput.addItem("0") }
        binding.key1.setOnClickListener { binding.calculatorOutput.addItem("1") }
        binding.key2.setOnClickListener { binding.calculatorOutput.addItem("2") }
        binding.key3.setOnClickListener { binding.calculatorOutput.addItem("3") }
        binding.key4.setOnClickListener { binding.calculatorOutput.addItem("4") }
        binding.key5.setOnClickListener { binding.calculatorOutput.addItem("5") }
        binding.key6.setOnClickListener { binding.calculatorOutput.addItem("6") }
        binding.key7.setOnClickListener { binding.calculatorOutput.addItem("7") }
        binding.key8.setOnClickListener { binding.calculatorOutput.addItem("8") }
        binding.key9.setOnClickListener { binding.calculatorOutput.addItem("9") }

        binding.keyClear.setOnClickListener { binding.calculatorOutput.clear() }
        binding.keyEqual.setOnClickListener { binding.calculatorOutput.solve() }

        binding.keySum.setOnClickListener { binding.calculatorOutput.addItem("+") }
        binding.keySub.setOnClickListener { binding.calculatorOutput.addItem("–") }
        binding.keyMul.setOnClickListener { binding.calculatorOutput.addItem("×") }
        binding.keyDiv.setOnClickListener { binding.calculatorOutput.addItem("÷") }
        binding.keyPercentage.setOnClickListener { binding.calculatorOutput.addItem("%") }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    // gesture
    private class MyGestureListener(private val binding: ActivityMainBinding) :
        GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            event1: MotionEvent?,
            event2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false
            val diffY = event2!!.y - event1!!.y
            val diffX = event2.x - event1.x
            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX <= 0) {
                        onSwipeLeft()
                    }
                    result = true
                }
            }
            return result
        }

        // long press clear output
        override fun onLongPress(event: MotionEvent?) {
            binding.calculatorOutput.clear()
        }

        // swipe left remove item output
        private fun onSwipeLeft() = binding.calculatorOutput.removeItem()
    }
}