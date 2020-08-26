package com.jakode.calculator.calculatorOutput

import com.jakode.calculator.utils.Type
import org.mariuszgromada.math.mxparser.Expression

object CalculatorOutputPresenter {
    // Current attach view
    private var mmView: CalculatorOutputInterfaceView? = null

    private var mmCurrentEquation: String = ""  // Current equation
    private var mmCurrentOutcome: String = ""   // Current outcome

    private var hasDot = false
    private var braces = 0 // To count braces

    fun attach(view: CalculatorOutputInterfaceView) {
        mmView = view
        updateEquation()
        updateOutcome()
    }

    fun detach() {
        mmView = null
    }

    fun add(item: String, type: Type) {
        if (type == Type.Number) { // When input is a number
            handelNum(item)
        } else { // When input is a operator
            when (item) {
                "(", "(-" -> handelBraces(item) // Handel braces
                "." -> handelDot(item) // Handel dot
                else -> handelOperator(item) // Handel basic operator and percentage
            }
        }

        updateEquation()
        calculateOutcome()
        updateOutcome()
    }

    private fun handelNum(item: String) {
        if (mmCurrentEquation.isNotEmpty()) { // Second input and ...
            if (isLastChar(')')) { // Control (5)5 Error
                mmCurrentEquation = mmCurrentEquation.plus("*$item")
            } else if (!isLastChar('%') && !isFirstCharZero() || (hasDot && isFirstCharZero())
            ) { // Control 2%2 , 000000 Error
                mmCurrentEquation = mmCurrentEquation.plus(item)
            }
        } else { // First input
            mmCurrentEquation = mmCurrentEquation.plus(item)
        }
    }

    private fun handelBraces(item: String) {
        mmCurrentEquation =
            if (mmCurrentEquation.isNotEmpty()) { // Second input and ...
                if (!isLastChar('.')) { // control '.(5)' Error
                    // When last input is a operator or '(' add braces
                    if (isLastChar('+', '-', '*', '/', '(')) {
                        braces++
                        mmCurrentEquation.plus(item)
                    } else if (isLastChar(
                            ')', '%', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
                        ) && braces == 0
                    ) { // After closed all braces, number and % add multiple open brace
                        braces++
                        mmCurrentEquation.plus("*$item")
                    } else { // and when last input is a number we start closed brace
                        braces--
                        mmCurrentEquation.plus(")")
                    }
                } else {
                    mmCurrentEquation
                }
            } else { // First input
                braces++
                mmCurrentEquation.plus(item)
            }
    }

    private fun handelDot(item: String) {
        if (!hasDot && !isDecimal()) { // Control '2.....' Error
            mmCurrentEquation =
                if (mmCurrentEquation.isEmpty() || isLastChar('+', '-', '*', '/', '(')
                ) { // In first input or after the last operator
                    mmCurrentEquation.plus("0.")
                } else if (isLastChar(')', '%')) { // Control '(5)*0.' and '2%.' Error
                    mmCurrentEquation.plus("*0.")
                } else { // Dot called between numbers
                    mmCurrentEquation.plus(item)
                }
            hasDot = true // Use this value for handel duplicated dots '2.01.1' Error}
        }
    }

    private fun handelOperator(item: String) {
        if (mmCurrentEquation.isNotEmpty() && !(isLastChar('%', '(') && item == "%")
        ) { // Control '%%' and '(%' Error
            mmCurrentEquation =
                if (isLastChar('+', '-', '*', '/') && item != "%"
                ) { // When second input is operator we can change them
                    mmCurrentEquation.substring(0, mmCurrentEquation.length - 1).plus(item)
                } else if (isLastChar('+', '-', '*', '/') && item == "%"
                ) { // Control '2+%' and '2%+%' Error
                    mmCurrentEquation
                } else { // First input operator
                    if (isLastChar('(') && (item == "/" || item == "*")) { // Control '(*2' and '(/2' Error
                        mmCurrentEquation
                    } else {
                        mmCurrentEquation.plus(item)
                    }
                }
            hasDot = false
        }
    }

    fun remove() {
        mmCurrentEquation = if (mmCurrentEquation.isNotEmpty()) {
            when {
                isLastChar(')') -> braces++
                isLastChar('(') -> braces--
                isLastChar('.') -> hasDot = false
            }
            mmCurrentEquation.substring(0, mmCurrentEquation.length - 1)
        } else {
            ""
        }

        updateEquation()
        calculateOutcome()
        updateOutcome()
    }

    fun solve() {
        braces = 0
        if (mmCurrentEquation.isNotEmpty() && mmCurrentOutcome.isNotEmpty()) {
            mmCurrentEquation = mmCurrentOutcome
            mmCurrentOutcome = ""
        }

        updateEquation()
        updateOutcome()
    }

    fun clear() {
        hasDot = false
        braces = 0
        mmCurrentEquation = ""
        mmCurrentOutcome = ""

        updateEquation()
        updateOutcome()
    }

    private fun calculateOutcome() {
        mmCurrentOutcome =
            if (mmCurrentEquation.isNotEmpty()) { // CurrentEquation should not empty
                if (!isLastChar(
                        '+',
                        '-',
                        '*',
                        '/',
                        '.'
                    ) // Last character input should not operator
                    && braces == 0 // All braces should be closed
                ) {
                    val e = Expression(mmCurrentEquation)
                    val result = e.calculate()
                    // Result cast to int when decimal not exist
                    if (result.toInt().toDouble() == result) result.toInt().toString()
                    else result.toString()
                } else { // When nothing to show current outcome is shown
                    mmCurrentOutcome
                }
            } else {
                ""
            }
    }

    /**
     * Check the last character input
     * @return a boolean
     */
    private fun isLastChar(vararg chars: Char): Boolean {
        val len = mmCurrentEquation.length

        for (char in chars) if (mmCurrentEquation[len - 1] == char) return true
        return false
    }

    /**
     * Convert sign
     * @return current equation
     */
    private fun singConverter() = mmCurrentEquation
        .replace("/", "÷")
        .replace("*", "×")
        .replace("-", "–")

    /**
     * Check first character input is zero
     * @return a Boolean
     */
    private fun isFirstCharZero(): Boolean {
        val args = mmCurrentEquation.split("+", "-", "*", "/", ".")
        val lastPart = args[args.size - 1]
        return if (lastPart.isNotEmpty()) lastPart[0] == '0' else false
    }

    /**
     * Check last number after operator is a decimal
     * @return a Boolean
     */
    private fun isDecimal(): Boolean {
        val args = mmCurrentEquation.split("+", "-", "*", "/")
        return args[args.size - 1].contains(".")
    }

    private fun updateEquation() {
        mmView?.setEquation(singConverter())
    }

    private fun updateOutcome() {
        mmView?.setOutcome(mmCurrentOutcome)
    }
}