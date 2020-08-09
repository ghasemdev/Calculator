package com.jakode.calculator

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout

class CalculatorOutputView(context: Context, attributeSet: AttributeSet?) :
    LinearLayout(context, attributeSet) {
    init {
        // Set orientation
        orientation = VERTICAL
        // Set gravity
        gravity = Gravity.CENTER_VERTICAL
        // Inflate layout
        LayoutInflater.from(context).inflate(R.layout.view_calculator_output, this, true)
    }
}