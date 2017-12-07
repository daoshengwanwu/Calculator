package com.daoshengwanwu.math_util.calculator;


import com.daoshengwanwu.math_util.calculator.exception.VarAssistHasNoNextValueException;
import com.daoshengwanwu.math_util.calculator.exception.VarIdentifierAlreadyExistException;
import com.daoshengwanwu.math_util.calculator.exception.VariableNotExistException;

import java.util.LinkedHashMap;
import java.util.Map;


public class VariableAssistant {
    private Map<String, Variable> mVariablesMap = new LinkedHashMap<>();


    public VariableAssistant addVariable(String flagStr, double lowerLimit, boolean isLowerOpen
            , double upperLimit, boolean isUpperOpen, double span) {
        if (hasVariable(flagStr) || Operand.hasConstant(flagStr)
                || OperatorAssistant.isIdentifierAlreadyExist(flagStr)) {
            throw new VarIdentifierAlreadyExistException();
        }//if

        mVariablesMap.put(flagStr, new Variable(
                flagStr,
                Operand.getOperand(lowerLimit), isLowerOpen,
                Operand.getOperand(upperLimit), isUpperOpen,
                Operand.getOperand(span)));

        return this;
    }//addVariable

    public Variable getVariable(String flagStr) {
        if (hasVariable(flagStr)) {
            return mVariablesMap.get(flagStr);
        }//if

        throw new VariableNotExistException(flagStr);
    }//getVariable

    public VariableAssistant removeVariable(String flagStr) {
        mVariablesMap.remove(flagStr);
        return this;
    }//removeVariable

    public boolean hasVariable(String flagStr) {
        return mVariablesMap.containsKey(flagStr);
    }//hasVariable

    public boolean hasNext() {
        for (String varStr : mVariablesMap.keySet()) {
            Variable var = mVariablesMap.get(varStr);
            if (var.hasNext()) {
                return true;
            }//if
        }//for

        return false;
    }//hasNext

    public void nextValue() {
        for (String varStr : mVariablesMap.keySet()) {
            Variable var = mVariablesMap.get(varStr);
            if (var.hasNext()) {
                var.nextValue();
                return;
            }//if
        }//for

        throw new VarAssistHasNoNextValueException();
    }//nextValue
}//class_VariableAssistant
