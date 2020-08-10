package com.jakode.calculator

import com.jakode.calculator.calculatorOutput.CalculatorOutputInterfaceView
import com.jakode.calculator.calculatorOutput.CalculatorOutputPresenter
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.then
import org.mockito.Mockito

class CalculateOutputTest {
    private var mmPresenter = CalculatorOutputPresenter
    private val mmMockView = Mockito.mock(CalculatorOutputInterfaceView::class.java)

    @Before
    fun setup() {
        // Clear presenter
        mmPresenter.clear()
        // Given that the view attached
        mmPresenter.attach(mmMockView)
    }

    @Test
    fun `1 plus 1 is 2`() {
        // When a number is added
        mmPresenter.add("1")
        // Then the correct equation should be set
        then(mmMockView).should().setEquation("1")

        // When a operators is added
        mmPresenter.add("+")
        // Then the correct equation should be set
        then(mmMockView).should().setEquation("1+")

        // When a number is added
        mmPresenter.add("1")
        // Then the correct equation should be set
        then(mmMockView).should().setEquation("1+1")

        // the the correct outcome should be test
        then(mmMockView).should().setOutcome("2")
    }

    @Test
    fun `2 plus 2 minus 1 is 3`() {
        // When a number is added
        mmPresenter.add("2")
        // Then the correct equation should be set
        then(mmMockView).should().setEquation("2")

        // When a operators is added
        mmPresenter.add("+")
        // Then the correct equation should be set
        then(mmMockView).should().setEquation("2+")

        // When a number is added
        mmPresenter.add("2")
        // Then the correct equation should be set
        then(mmMockView).should().setEquation("2+2")

        // When a operators is added
        mmPresenter.add("-")
        // Then the correct equation should be set
        then(mmMockView).should().setEquation("2+2-")

        // When a number is added
        mmPresenter.add("1")
        // Then the correct equation should be set
        then(mmMockView).should().setEquation("2+2-1")

        // the the correct outcome should be test
        then(mmMockView).should().setOutcome("3")
    }
}