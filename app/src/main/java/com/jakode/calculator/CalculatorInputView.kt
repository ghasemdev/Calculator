package com.jakode.calculator

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.view_calculator_input.view.*

class CalculatorInputView(context: Context, attributeSet: AttributeSet?) :
    RelativeLayout(context, attributeSet) {
    init {
        // Inflate layout
        LayoutInflater.from(context).inflate(R.layout.view_calculator_input, this, true)
        // Read attribute set
        attributeSet?.run {
            val typedArray =
                context.obtainStyledAttributes(attributeSet, R.styleable.CalculatorInputView)

            val textResource = typedArray.getString(R.styleable.CalculatorInputView_text)
            val iconResource =
                typedArray.getResourceId(R.styleable.CalculatorInputView_icon, -1)

            when {
                iconResource != -1 -> {
                    input_element_text.visibility = View.GONE
                    input_element_image.apply {
                        visibility = View.VISIBLE
                        setImageResource(iconResource)
                    }
                    input_element_click.apply {
                        val outValue = TypedValue()
                        context.theme.resolveAttribute(
                            android.R.attr.selectableItemBackgroundBorderless,
                            outValue,
                            true
                        )
                        setBackgroundResource(outValue.resourceId)
                    }
                }
                !textResource.isNullOrEmpty() -> {
                    input_element_image.visibility = View.GONE
                    input_element_text.apply {
                        visibility = View.VISIBLE
                        TypedValue()
                        text = textResource
                        val color =
                            typedArray.getColor(R.styleable.CalculatorInputView_color_text, -1)
                        if (color != -1) setTextColor(color)
                    }
                    input_element_click.apply {
                        val outValue = TypedValue()
                        context.theme.resolveAttribute(
                            android.R.attr.selectableItemBackground,
                            outValue,
                            true
                        )
                        setBackgroundResource(outValue.resourceId)
                    }
                }
                else -> {
                    input_element_text.visibility = View.GONE
                    input_element_image.visibility = View.GONE
                }
            }
            typedArray.recycle()
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        input_element_click.setOnClickListener(l)
    }
}