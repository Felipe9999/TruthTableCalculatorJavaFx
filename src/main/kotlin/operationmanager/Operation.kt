package operationmanager

import operationmanager.Variables
import java.util.*
import kotlin.math.pow

open class Operation{ //TODO: This thing explodes if b is very long and parentheses are written poorly by user
    protected var a: Any? = null
    protected var b: Any? = null
    protected var operation: Boolean = false //AND is true, OR is false
    protected var length: Int = 0
    protected var variables: Variables = Variables()
    constructor(operationAsStr: String) {
        identifyInput(operationAsStr.toCharArray())
    }
    constructor(operationAsCA: CharArray, newLength: Int) {
        length = newLength
        identifyInput(operationAsCA)
    }

    constructor(newA: Any?, operation: Boolean, newB: Any?){
        a = newA
        this.operation = operation
        b = newB
    }
    open fun identifyInput(myInput: CharArray){
        var isA: Boolean = true
        var isB: Boolean = true
        //var operations: MutableList<Any> = emptyList<Any>().toMutableList()
        var i = 0
        while (i < myInput.size) {
            if(myInput[i] == ')'){
                length += i
                break //We may be running nested-ly, so kill the thing here if that's the case
            }
            else if(!isB){
                isA = false
                isB = true
                //val tempA = a
                //val tempB = b
                a = Operation(a, operation, b)
                b = null
            }
            if (myInput[i] == '∧')
                operation = true
            else if(myInput[i] == 'v')
                operation = false
            else if(myInput[i] == '(') {
                val tmpList = myInput.sliceArray(i + 1 until myInput.size)
                val insideOperation: Operation = Operation(tmpList, length)
                i += insideOperation.requestLength() + 1
                if (isA) {
                    a = insideOperation
                    isA = false
                }
                else {
                    b = insideOperation
                    isB = false
                }
                //operations.add(insideOperation)
            }
            else if(myInput[i] == '-' || myInput[i] == '¬'){
                val tmpList = myInput.sliceArray(i until myInput.size)
                val insideOperation: TinyOperation = TinyOperation(tmpList, length)
                i += insideOperation.requestLength()
                if (isA) {
                    a = insideOperation
                    isA = false
                }
                else {
                    b = insideOperation
                    isB = false
                }
            }
            else {
                for (j in i until  myInput.size) {
                    if (myInput[j] == '∧' || myInput[j] == 'v' || myInput[j] == ')') {
                        i = j - 1
                        if(isA)isA = false
                        else isB = false
                        break
                    }
                    else {
                        if (isA) {
                            if (a == null)a = ""
                            a = (a as String) + myInput[j]
                        }
                        else{
                            if (b == null) b = ""
                            b = (b as String) + myInput[j]
                        }
                    }
                }
            }
            i += 1
        }
        initializeVariables()
    }
    open fun getOperationSymbol(): Char{
        return if(operation) '∧'
        else 'v'
    }
    open fun getEquationAsStr(): String { //Will implode on itself if we don't have both a and b
        var operationStr = ""
        if (a is String) operationStr += a
        else if(a is Operation){
            operationStr += '('
            operationStr += (a as Operation).getEquationAsStrSafely()
            operationStr += ')'
        }
        operationStr += getOperationSymbol()
        if (b is String) operationStr += b
        else if(b is Operation){
            operationStr += '('
            operationStr += (b as Operation).getEquationAsStrSafely()
            operationStr += ')'
        }
        return operationStr
    }

    fun getTruthTableAsString(): String{
        //val table = getTruthTable()
        //val table = getTruthTableV2()
        val table = getTruthTableV3()
        var tableStr = ""
        for (i in (0 until  table.size)) {
            for (j in (0 until table[i].size)){
                if (table[i][j] is Boolean) {
                    if (table[i][j] as Boolean) tableStr += "1 "
                    else tableStr += "0 "
                }
                else tableStr += (table[i][j] as Char + " ")
            }
            tableStr += "\n"
        }
        return tableStr
    }
    open fun initializeVariables(){
        try {
            initializeVariablesUnsafe()
        }
        catch (e: java.lang.NullPointerException){
            //This exception is only reached when a single-var operation is entered, so just kinda ignore it.
        }
    }
    private fun initializeVariablesUnsafe(){
        if(a is String) variables.setNewVar(a as String)
        else {
            (a as Operation).initializeVariables()
            val internalVars = (a as Operation).variables
            variables.setNewVars(internalVars)
        }
        if(b is String) variables.setNewVar(b as String)
        else {
            (b as Operation).initializeVariables()
            val internalVars = (b as Operation).variables
            variables.setNewVars(internalVars)
        }
    }
    fun addVariables(newVars: Variables){
        variables.setNewVars(newVars)
    }
    fun getVariables(): String{
        return variables.getVarsStr()
    }
    open fun getTruthSection(): List<Any>{ //Gets a section of the truth table using the variable values we have
        val truthTable: List<Any>
        if ((a is String) && (b is String)){ //the simplest of operations
            val aVal = variables.getValueAt(a as String)
            val bVal = variables.getValueAt(b as String)
            truthTable = if(operation) (listOf(aVal, '∧', bVal, '=', (aVal && bVal)))
            else (listOf(aVal, 'v', bVal, '=', (aVal || bVal)))
            return truthTable
        }
        else if((a is Operation) && (b is Operation)){
            (a as Operation).addVariables(variables)
            (b as Operation).addVariables(variables)
            val aTruth = (a as Operation).getTruthSection()
            val bTruth = (b as Operation).getTruthSection()
            truthTable = if(operation)
                listOf('|') + aTruth + listOf('|', '∧', '|') + bTruth + listOf('|', '=', (aTruth[aTruth.size-1] as Boolean && bTruth[bTruth.size-1] as Boolean))
            else
                listOf('|') + aTruth + listOf('|', 'v', '|') + bTruth + listOf('|', '=', (aTruth[aTruth.size-1] as Boolean || bTruth[bTruth.size-1] as Boolean))
            return truthTable
        }
        else if(a is Operation){
            (a as Operation).addVariables(variables)
            val bVal = variables.getValueAt(b as String)
            val aTruth = (a as Operation).getTruthSection()
            truthTable = if(operation)
                listOf('|') + aTruth + listOf('|', '∧') + bVal + listOf('=', (aTruth[aTruth.size-1] as Boolean && bVal))
            else
                listOf('|') + aTruth + listOf('|', 'v') + bVal + listOf('=', (aTruth[aTruth.size-1] as Boolean || bVal))
            return truthTable
        }
        else if(b is Operation){
            val aVal = variables.getValueAt(a as String)
            (b as Operation).addVariables(variables)
            val bTruth = (b as Operation).getTruthSection()
            truthTable = if(operation)
                listOf(aVal) + listOf('∧', '|') + bTruth + listOf('|', '=', (aVal && bTruth[bTruth.size-1] as Boolean))
            else
                listOf(aVal) + listOf('v', '|') + bTruth + listOf('|', '=', (aVal || bTruth[bTruth.size-1] as Boolean))
            return truthTable
        }
        else throw Exception("How did we get here?")
    }

    private fun updateInsideVars(){ //ONLY the main operation should run this method
        (a as? Operation)?.setNewInsideVars(variables)
        (b as? Operation)?.setNewInsideVars(variables)
    }
    private fun setNewInsideVars(newVars: Variables){
        (a as? Operation)?.setNewInsideVars(newVars)
        (b as? Operation)?.setNewInsideVars(newVars)
    }

    open fun getTruthTableV3(): MutableList<List<*>> {
        return if(b == null){
            (a as Operation).getTruthTableV3Internal()
        } else getTruthTableV3Internal()
    }
    private fun getTruthTableV3Internal(): MutableList<List<*>> {
        val truthTable: MutableList<List<*>> = mutableListOf()
        truthTable.add(variables.getVarsCharList())
        updateInsideVars()
        var testTruths = BooleanArray(variables.getSize())
        Arrays.fill(testTruths, false)
        var i = 0
        while (i < 2.0.pow(variables.getSize())){
            variables.setNewVarsSimple(testTruths)
            truthTable.add(variables.getBooleans().toList() + ':'+ getTruthSection())
            i++
            testTruths = intToBooleanArray(i, testTruths.size)
        }
        return truthTable
    }
    private fun intToBooleanArray(int: Int, expectedSize: Int): BooleanArray {
        var int2 = int
        val bools = BooleanArray(expectedSize)
        Arrays.fill(bools, false)
        for(i in 0 until expectedSize){
            bools[i] = int2 % 2 != 0
            int2 /= 2
        }
        return bools
    }
    fun requestLength(): Int {
        return length
    }
    fun requestVariables(): Variables {
        return variables
    }
    open fun getTruthResult(): Boolean{ //Gets the result of the expression using the variable values we have
        val aVal: Boolean = if(a is String) variables.getValueAt(a as String)
        else {
            (a as Operation).addVariables(variables)
            (a as Operation).getTruthResult()
        }
        val bVal: Boolean = if(b is String) variables.getValueAt(b as String)
        else {
            (b as Operation).addVariables(variables)
            (b as Operation).getTruthResult()
        }
        if(operation) return (aVal && bVal)
        else return (aVal || bVal)
    }
    open fun getEquationAsStrSafely(): String {  //TODO: TESTING ONLY, REMOVE THIS FUNCTION
        return if(b == null){
            if(a is Operation)(a as Operation).getEquationAsStr()
            else (a as String)
        } else getEquationAsStr()
    }
    open fun setOperationFromChar(operation: Char){
        this.operation = operation == '∧'
    }
}