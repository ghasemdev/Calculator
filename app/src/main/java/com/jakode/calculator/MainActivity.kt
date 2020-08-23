package com.jakode.calculator

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.doOnPreDraw
import com.jakode.calculator.databinding.ActivityMainBinding
import com.jakode.calculator.utils.Guidance
import com.jakode.calculator.utils.Type
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mDetector: GestureDetectorCompat
    private var guidance: Guidance? = null
    private var swipeLeft = false // guidance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDetector = GestureDetectorCompat(this, MyGestureListener(binding))

        // onClickListener
        numberKey()
        operatorKey()
        bracesKey()
        otherKey()

        // guidance for clear calculate
        guide()
    }

    private fun numberKey() {
        binding.key0.setOnClickListener { binding.calculatorOutput.addItem("0", Type.Number) }
        binding.key1.setOnClickListener { binding.calculatorOutput.addItem("1", Type.Number) }
        binding.key2.setOnClickListener { binding.calculatorOutput.addItem("2", Type.Number) }
        binding.key3.setOnClickListener { binding.calculatorOutput.addItem("3", Type.Number) }
        binding.key4.setOnClickListener { binding.calculatorOutput.addItem("4", Type.Number) }
        binding.key5.setOnClickListener { binding.calculatorOutput.addItem("5", Type.Number) }
        binding.key6.setOnClickListener { binding.calculatorOutput.addItem("6", Type.Number) }
        binding.key7.setOnClickListener { binding.calculatorOutput.addItem("7", Type.Number) }
        binding.key8.setOnClickListener { binding.calculatorOutput.addItem("8", Type.Number) }
        binding.key9.setOnClickListener { binding.calculatorOutput.addItem("9", Type.Number) }
    }

    private fun operatorKey() {
        binding.keySum.setOnClickListener { binding.calculatorOutput.addItem("+", Type.Operator) }
        binding.keySub.setOnClickListener { binding.calculatorOutput.addItem("-", Type.Operator) }
        binding.keyMul.setOnClickListener { binding.calculatorOutput.addItem("*", Type.Operator) }
        binding.keyDiv.setOnClickListener { binding.calculatorOutput.addItem("/", Type.Operator) }
        binding.keyPercentage.setOnClickListener { binding.calculatorOutput.addItem("%", Type.Operator) }
    }

    private fun bracesKey() {
        binding.keyBraces.setOnClickListener { binding.calculatorOutput.addItem("(", Type.Operator) }
        binding.keyNegative.setOnClickListener { binding.calculatorOutput.addItem("(-", Type.Operator) }
    }

    private fun otherKey() {
        binding.keyClear.setOnClickListener { binding.calculatorOutput.clear() }
        binding.keyEqual.setOnClickListener { binding.calculatorOutput.solve() }
        binding.keyDecimal.setOnClickListener { binding.calculatorOutput.addItem(".", Type.Operator) }
    }

    private fun guide() {
        guidance = Guidance(this)
        binding.root.doOnPreDraw {
            if (guidance!!.first) {
                guidance?.apply {
                    add(R.layout.layout_target_swipe_left, calculator_output, 0F, false)
                    add(R.layout.layout_target_long_touch, calculator_output, 0F, false)
                    start()
                }
                guidance!!.targets.forEach {
                    it.overlay?.apply {
                        findViewById<View>(R.id.skip)?.setOnClickListener {
                            guidance!!.finish()
                            guidance!!.first = false
                        }
                    }
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    // gesture
    private inner class MyGestureListener(private val binding: ActivityMainBinding) :
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
            // guidance show first time
            if (guidance?.first!! && swipeLeft) {
                guidance?.next()
                guidance!!.first = false
            }
        }

        // swipe left remove item output
        private fun onSwipeLeft() {
            binding.calculatorOutput.removeItem()
            // guidance show first time
            if (guidance?.first!! && !swipeLeft) {
                guidance?.next()
                swipeLeft = true
            }
        }
    }
}