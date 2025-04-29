package dev.felipe9999.truthtablecalculator

import operationmanager.OperationWrapper
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.Button
import javafx.event.ActionEvent
import java.util.logging.Logger

class TruthTableController {
    @FXML
    private lateinit var boolenXpTextField: TextField

    @FXML
    private lateinit var resultArea: TextArea

    @FXML
    private lateinit var btnFalse: Button

    @FXML
    private lateinit var btnTrue: Button

    @FXML
    private lateinit var notBtn: Button

    @FXML
    private lateinit var btnExec: Button

    @FXML
    private lateinit var btnA: Button

    @FXML
    private lateinit var btnB: Button

    @FXML
    private lateinit var andBtn: Button

    @FXML
    private lateinit var orBtn: Button

    @FXML
    private lateinit var btnC: Button

    @FXML
    private lateinit var btnD: Button

    @FXML
    private lateinit var btnImplyInvers: Button

    @FXML
    private lateinit var btnImply: Button

    @FXML
    private lateinit var btnE: Button

    @FXML
    private lateinit var btnF: Button

    @FXML
    private lateinit var btnBiconditional: Button

    @FXML
    private lateinit var btnXor: Button

    @FXML
    private lateinit var btnG: Button

    @FXML
    private lateinit var btnH: Button

    @FXML
    private lateinit var btnNand: Button

    @FXML
    private lateinit var btnNor: Button

    @FXML
    private lateinit var btnOpenParentheses: Button

    @FXML
    private lateinit var btnCloseParentheses: Button

    @FXML
    private lateinit var btnNimply: Button

    @FXML
    private lateinit var btnConverseNimply: Button

    val logger = Logger.getLogger(TruthTableController::class.java.name)
    @FXML
    fun onButtonClick(event: ActionEvent) {
        val button = event.source as Button
        if(button.text == "∨") appendToExpression("v")
        else appendToExpression(button.text)
    }

    @FXML
    fun calculate() {
        try {
            val expression: kotlin.String = boolenXpTextField.text.trim()
            if (expression.isEmpty()) {
                resultArea.text = "Please enter a boolean expression"
                return
            }

            val operationWrapper = OperationWrapper(expression)
            val equation = operationWrapper.getEquationAsStrSafely()
            val truthTable = operationWrapper.getTruthTableAsString()
            
            resultArea.text = "Equation: $equation\n\nTruth Table:\n$truthTable"
        } catch (e: Exception) {
            resultArea.text = "Error: ${e.message}"
            logger.warning(e.stackTraceToString())
        }
    }

    private fun appendToExpression(text: String) {
        boolenXpTextField.text = boolenXpTextField.text + text
    }
} 