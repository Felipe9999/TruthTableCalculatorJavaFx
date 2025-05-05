package dev.felipe9999.truthtablecalculator.operationmanager

open class TinyOperation: Operation {
    constructor(operationAsCA: CharArray, newLength: Int) : super(operationAsCA, newLength) {
    }
    constructor(operation: Boolean, a: Any?): super(a, operation, null) {
    }
    override fun identifyInput(myInput: CharArray){
        var i = 0
        while (i < myInput.size) {
            if (myInput[i] == '-' || myInput[i] == '¬')
                operation = true
            else if(myInput[i] == '(') {
                val tmpList = myInput.sliceArray(i + 1 until myInput.size)
                val insideOperation: OperationWrapper = OperationWrapper(tmpList, length)
                i += insideOperation.requestLength() + 1
                a = insideOperation
                length += i
                break //yeah this only handles negation so no need to run anything else
            }
            else {
                for (j in i until  myInput.size) {
                    if (myInput[j] == '∧' || myInput[j] == 'v' || myInput[j] == ')') {
                        //i = j - 1
                        break //yeah this only handles negation so no need to run anything else
                    }
                    else{
                        if (a == null)a = ""
                        a = (a as String) + myInput[j]
                        length += i
                    }
                }
                break
            }
            i += 1
        }
        initializeVariables()
    }
    override fun initializeVariables(){
        if(a is String) variables.setNewVar(a as String)
        else {
            (a as Operation).initializeVariables()
            val internalVars = (a as Operation).requestVariables()
            variables.setNewVars(internalVars)
        }
    }
    override fun getTruthSection(): List<Any> {
        val truthTable: List<Any>
        if ((a is String)){ //the simplest of operations
            val aVal = variables.getValueAt(a as String)
            truthTable = if(operation) (listOf('¬', aVal, '=', (!aVal)))
            else (listOf(aVal))
            return truthTable
        }
        else if((a is Operation)){
            (a as Operation).addVariables(variables)
            val aTruth = (a as Operation).getTruthSection()
            truthTable = if(operation)
                (listOf('¬', '|') + aTruth.subList(0, aTruth.size-2) + listOf('|', '=') + !((aTruth[aTruth.size-1]) as Boolean))
            else
                aTruth
            return truthTable
        }
        else throw Exception("How did we get here?")
    }
    override fun getEquationAsStr(): String { //TODO: TESTING ONLY, REMOVE THIS FUNCTION
        var operationStr = ""
        operationStr += getOperationSymbol()
        if (a is String) operationStr += a
        else if(a is Operation){
            operationStr += '('
            operationStr += (a as Operation).getEquationAsStrSafely()
            operationStr += ')'
        }
        return operationStr
    }
    override fun getOperationSymbol(): Char {
        return if(operation) '¬'
        else ' '
    }
    override fun getTruthResult(): Boolean{ //Gets the result of the expression using the variable values we have
        /*val aVal: Boolean = if(a is String) variables.getValueAt(a as String)
        else (a as OperationManager.Operation).getTruthResult()
        return if(operation) (!aVal)
        else aVal*/
        if(a is String) return !variables.getValueAt(a as String)
        else {
            (a as Operation).addVariables(variables)
            return !(a as Operation).getTruthResult()
        }
    }
}