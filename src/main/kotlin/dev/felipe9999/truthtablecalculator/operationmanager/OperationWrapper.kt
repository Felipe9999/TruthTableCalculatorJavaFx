package dev.felipe9999.truthtablecalculator.operationmanager

class OperationWrapper: Operation {
    private var weirdOperation: String? = null
    constructor(operationAsStr: String) : super(operationAsStr) {
    }
    constructor(operationAsCA: CharArray, newLength: Int) : super(operationAsCA, newLength) {
    }
    constructor(newA: Any?, operation: Char, newB: Any?) : super(newA, false, newB) {
        setOperationFromChar(operation)
        handleWeirdOperations()
    }
    private var fakeA: Any? = null
    private var fakeB: Any? = null
    override fun identifyInput(myInput: CharArray) {
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
                a = OperationWrapper(a, getOperationSymbol(), b)
                b = null
            }
            if (myInput[i] == '∧')
                operation = true
            else if(myInput[i] == 'v')
                operation = false
            else if(myInput[i] == '(') {
                val tmpList = myInput.sliceArray(i + 1 until myInput.size)
                val insideOperation: OperationWrapper = OperationWrapper(tmpList, length)
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
            else{
                val weirdOperationOffset = checkForWeirdOperations(myInput, i)
                if(weirdOperationOffset != -1){
                    i += weirdOperationOffset
                }
                else if(myInput[i] == '-'  || myInput[i] == '¬'){
                    if(i+1 >= myInput.size || myInput[i+1] != '>'){
                        val tmpList = myInput.sliceArray(i until myInput.size)
                        val insideOperation: TinyOperation = TinyOperation(tmpList, length)
                        i += insideOperation.requestLength()
                        if (isA) {
                            a = insideOperation
                            isA = false
                        } else {
                            b = insideOperation
                            isB = false
                        }
                    }
                }
                else {
                    for (j in i until  myInput.size) {
                        if (myInput[j] == '∧' || myInput[j] == 'v' || myInput[j] == ')' || checkForWeirdOperations(myInput, j) != -1) {
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
                            i++
                        }
                    }
                }
            }
            i += 1
        }
        initializeVariables()
        handleWeirdOperations()
    }

    private fun handleWeirdOperations() {
        if(weirdOperation != null){
            fakeA = a
            fakeB = b
            when(weirdOperation){
                "->" ->{
                    a = TinyOperation(true, a)
                    operation = false
                    //b is unchanged
                }
                "<-" ->{
                    val tempB = a
                    a = TinyOperation(true, b)
                    operation = false
                    b = tempB
                }
                "<->" ->{
                    val tempA = a
                    val tempB = b
                    a = Operation(tempA, true, tempB)
                    operation = false
                    b = Operation(TinyOperation(true, tempA), true, TinyOperation(true, tempB))
                }
                "XOR" ->{
                    val tempA = a
                    val tempB = b
                    a = Operation(tempA, false, tempB)
                    operation = true
                    b = TinyOperation(true, Operation(tempA, true, tempB))
                }
                "NAND" ->{
                    val tempA = a
                    val tempB = b
                    a = TinyOperation(true, tempA)
                    operation = false
                    b = TinyOperation(true, tempB)
                }
                "NOR" ->{
                    val tempA = a
                    val tempB = b
                    a = TinyOperation(true, tempA)
                    operation = true
                    b = TinyOperation(true, tempB)
                }
                "NIMPLY" ->{
                    //a is unchanged
                    b = TinyOperation(true, b)
                    operation = true
                }
                "ConverseNIMPLY" ->{
                    a = TinyOperation(true, a)
                    operation = true
                    //b is unchanged
                }
            }
        }
    }

    private fun checkForWeirdOperations(myInput: CharArray, i: Int): Int {
        when(myInput[i]){
            '-' -> {
                if (i + 1 < myInput.size && myInput[i + 1] == '>') {
                    weirdOperation = "->"
                    return 1
                } else return -1
            }
            '→' -> {
                weirdOperation = "->"
                return 0
            }
            '←' -> {
                weirdOperation = "<-"
                return 0
            }
            '⊕' -> {
                weirdOperation = "XOR"
                return 0
            }
            '=' -> {
                weirdOperation = "<->"
                return 0
            }
            '↔' -> {
                weirdOperation = "<->"
                return 0
            }
            '<' -> {
                if (i + 1 < myInput.size && myInput[i + 1] == '-') {
                    if(i + 2 < myInput.size && myInput[i + 2] == '>'){
                        weirdOperation = "<->"
                        return 2
                    }
                    else{
                        weirdOperation = "<-"
                        return 1
                    }
                } else return -1
            }
            '↑' ->{
                weirdOperation = "NAND"
                return 0
            }
            '↓'->{
                weirdOperation = "NOR"
                return 0
            }
            '↛'->{
                weirdOperation = "NIMPLY"
                return 0
            }
            '↚' ->{
                weirdOperation = "ConverseNIMPLY"
                return 0
            }
            else -> return -1
        }
    }
    override fun getTruthSection(): List<Any> {
        if(weirdOperation != null){
            /*val truthA: Any
            val truthB: Any
            if(a is TinyOperationV2){
                truthA = (a as TinyOperationV2).getPartialTruthSection()
            }
            else truthA
            if(b is TinyOperationV2){
                truthB = (a as TinyOperationV2).getPartialTruthSection()
            }
            result = (emptyList<Any>() + truthA)*/
            val truthTable: List<Any>
            if(a is Operation)(a as Operation).addVariables(variables)
            if(b is Operation)(b as Operation).addVariables(variables)
            if ((fakeA is String) && (fakeB is String)){ //the simplest of fake operations
                val fakeaVal =  variables.getValueAt(fakeA as String)
                val fakebVal =  variables.getValueAt(fakeB as String)
                truthTable = listOf(fakeaVal, getWeirdOperationAsChar(), fakebVal, '=', getTruthResult())
                return truthTable
            }
            else if((fakeA is Operation) && (fakeB is Operation)){
                (fakeA as Operation).addVariables(variables)
                (fakeB as Operation).addVariables(variables)
                val aTruth = (fakeA as Operation).getTruthSection()
                val bTruth = (fakeB as Operation).getTruthSection()
                truthTable = listOf('|') + aTruth + '|' + getWeirdOperationAsChar() + '|' + bTruth + listOf('|', '=', getTruthResult())
                return truthTable
            }
            else if(fakeA is Operation){
                (fakeA as Operation).addVariables(variables)
                val bVal = variables.getValueAt(fakeB as String)
                val aTruth = (fakeA as Operation).getTruthSection()
                truthTable = listOf('|') + aTruth + '|' + getWeirdOperationAsChar() + bVal + listOf('=', getTruthResult())
                return truthTable
            }
            else if(fakeB is Operation){
                val aVal = variables.getValueAt(fakeA as String)
                (fakeB as Operation).addVariables(variables)
                val bTruth = (fakeB as Operation).getTruthSection()
                truthTable = listOf(aVal) + getWeirdOperationAsChar() + '|' + bTruth + listOf('|', '=', getTruthResult())
                return truthTable
            }
            else throw Exception("How did we get here?")
        }
        else return super.getTruthSection()
    }

    override fun getOperationSymbol(): Char {
        return if(weirdOperation != null) getWeirdOperationAsChar()
        else super.getOperationSymbol()
    }
    private fun getWeirdOperationAsChar(): Char {
        return when(weirdOperation){
            "->" ->{
                '→'
            }

            "<-" ->{
                '←'
            }

            "<->" ->{
                '↔'
            }

            "XOR" ->{
                '⊕'
            }
            "NAND" ->{
                '↑'
            }
            "NOR" ->{
                '↓'
            }
            "NIMPLY"->{
                '↛'
            }
            "ConverseNIMPLY"->{
                '↚'
            }

            else -> throw Exception("OperationManager.Operation not found!")
        }
    }
    override fun getEquationAsStrSafely(): String {
        if(weirdOperation != null){
            var operationStr = ""
            if (fakeA is String) operationStr += fakeA
            else if(fakeA is Operation){
                operationStr += '('
                operationStr += (fakeA as Operation).getEquationAsStrSafely()
                operationStr += ')'
            }
            operationStr += getWeirdOperationAsChar()
            if (fakeB is String) operationStr += fakeB
            else if(fakeB is Operation){
                operationStr += '('
                operationStr += (fakeB as Operation).getEquationAsStrSafely()
                operationStr += ')'
            }
            return operationStr
        }
        else return super.getEquationAsStrSafely()
    }

    override fun setOperationFromChar(operation: Char) {
        if(checkForWeirdOperations(operation.toString().toCharArray(), 0) == -1){
            super.setOperationFromChar(operation)
        }
    }
}