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
            if (mmCurrentEquation.isNotEmpty()) { // Second input and ...
                // Control %2 , 000000 and 0.0000 Error
                if (!isLastChar('%') && !isFirstCharZero() || (hasDot && isFirstCharZero()))
                    mmCurrentEquation = mmCurrentEquation.plus(item)
            } else { // First input
                mmCurrentEquation = mmCurrentEquation.plus(item)
            }
        } else { // When input is a operator
            when {
                item == "(" || item == "(-" -> { // Handel braces
                    mmCurrentEquation =
                        if (mmCurrentEquation.isNotEmpty()) { // Second input and ...
                            // When last input is a operator or '(' add braces
                            if (isLastCharOperator() || isLastChar('(')) {
                                braces++
                                mmCurrentEquation.plus(item)
                            } else if (isLastChar(')') && braces == 0) { // After closed all braces add multiple open brace
                                braces++
                                mmCurrentEquation.plus("*$item")
                            } else { // and when last input is a number we start closed brace
                                braces--
                                mmCurrentEquation.plus(")")
                            }
                        } else { // First input
                            braces++
                            mmCurrentEquation.plus(item)
                        }
                }
                item == "." && !hasDot && !isDecimal() -> { // Handel dot
                    mmCurrentEquation =
                        if (mmCurrentEquation.isEmpty() || isLastCharOperator()) { // In first input or after the last operator
                            mmCurrentEquation.plus("0.")
                        } else { // Dot called between numbers
                            mmCurrentEquation.plus(item)
                        }
                    hasDot = true // Use this value for handel duplicated dots '2.01.1' Error
                }
                mmCurrentEquation.isNotEmpty() && !(isLastChar('%') && item == "%") && item != "." -> { // Handel basic operator and percentage (%% Error)
                    mmCurrentEquation =
                        if (isLastCharOperator()) { // When second input is operator we can change them
                            mmCurrentEquation.substring(0, mmCurrentEquation.length - 1).plus(item)
                        } else { // First input operator
                            mmCurrentEquation.plus(item)
                        }
                    hasDot = false
                }
            }
        }

        updateEquation()
        calculateOutcome()
        updateOutcome()
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
        if (mmCurrentEquation.isNotEmpty() && !isDecimal()) {
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
                if (!isLastCharOperator() // Last character input should not operator
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
     * Check the last character input is a operator
     * @return a boolean
     */
    private fun isLastCharOperator(): Boolean {
        val len = mmCurrentEquation.length

        if (mmCurrentEquation[len - 1] == '+' || mmCurrentEquation[len - 1] == '-' || mmCurrentEquation[len - 1] == '*' || mmCurrentEquation[len - 1] == '/' || mmCurrentEquation[len - 1] == '.') return true
        return false
    }

    /**
     * Check the last character input
     * @property char
     * @return a boolean
     */
    private fun isLastChar(char: Char): Boolean {
        return mmCurrentEquation[mmCurrentEquation.length - 1] == char
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