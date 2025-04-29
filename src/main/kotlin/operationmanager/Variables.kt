package operationmanager

import java.util.Arrays

data class Variable(val name: String, var value: Boolean)
class Variables {
    private var Vars: MutableList<Variable> = mutableListOf()
    fun setNewVar(myVar: String){
        setNewVar(myVar, false)
    }
    private fun setNewVar(myVar: String, value: Boolean){
        setNewVar(Variable(myVar, value))
    }
    private fun setNewVar(myVar: Variable){
        var done = false
        if(myVar.name == "0" || myVar.name == "1") done = true
        for(i in 0 until  Vars.size){
            if(Vars[i].name == myVar.name){
                Vars[i].value = myVar.value
                done = true
                break
            }
        }
        if(!done) Vars.add(myVar)
    }
    fun setNewVars(vars: Variables){
        //val newVars: MutableList<Variable> = vars.Vars.toList().toMutableList()
        //newVars.addAll(vars.Vars)
        //setNewVars(vars.Vars.toList().toMutableList())
        setNewVars(vars.Vars)
    }
    private fun setNewVars(newVars2: MutableList<Variable>) {
        val newVars: MutableList<Variable> = mutableListOf()
        newVars +=  newVars2
        for(i in 0 until  Vars.size){
            var j = 0
            while(j < newVars.size){
                if(newVars[j].name == "0" || newVars[j].name == "1") newVars.removeAt(j)
                if(Vars[i].name == newVars[j].name){
                    Vars[i].value = newVars[j].value
                    newVars.removeAt(j)
                }
                else j++
            }
        }
        if(newVars.size > 0){
            Vars.addAll(newVars)
        }
    }
    fun setNewVarsSimple(newVars: BooleanArray){
        var i = 0
        while(i < Vars.size && i < newVars.size){
            Vars[i].value = newVars[i]
            i++
        }
    }
    fun getBooleans(): BooleanArray {
        val bools = BooleanArray(Vars.size)
        for(i in 0 until Vars.size){
            bools[i] = Vars[i].value
        }
        return bools
    }
    /*fun setNewVarsSimple(newVars: MutableList<Variable>){ //THIS FUNCTION ASSUMES THE VARIABLES ARE THE SAME
        var i = 0
        while(i < Vars.size && i < newVars.size){
            Vars[i].value = newVars[i].value
            i++
        }
    }*/
    fun getValueAt(name: String): Boolean {
        if(name == truth.name) return true
        else if(name == falsehood.name) return false
        for(i in 0 until Vars.size){
            if(Vars[i].name == name) return Vars[i].value
        }
        throw Exception("Variable not found!")
    }
    fun getVarsStr(): String{
        var tempStr = ""
        for(i in 0 until Vars.size){
            tempStr += Vars[i].name + ": " + Vars[i].value + "\n"
        }
        return tempStr
    }
    fun getSize(): Int{
        return Vars.size
    }

    fun getVarsCharList(): List<Char> {
        var vars: Array<Char> = emptyArray<Char>()
        Arrays.fill(vars, "")
        for(i in 0 until Vars.size){
            vars += Vars[i].name.toCharArray()[0]
        }
        return vars.toList()
    }
    companion object fundamentals{
        val truth = Variable("1", true)
        val falsehood = Variable("0", false)
    }
}