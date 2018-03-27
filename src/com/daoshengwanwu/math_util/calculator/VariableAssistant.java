package com.daoshengwanwu.math_util.calculator;


import com.daoshengwanwu.math_util.calculator.exception.VarAssistHasNoNextValueException;
import com.daoshengwanwu.math_util.calculator.exception.VarIdentifierAlreadyExistException;
import com.daoshengwanwu.math_util.calculator.exception.VariableNotExistException;

import java.util.LinkedHashMap;
import java.util.Map;


public class VariableAssistant {
    private Map<String, Variable> mVariablesMap = new LinkedHashMap<>();


    public VariableAssistant addVariable(String flagStr, double lowerLimit, boolean isLowerOpen,
                                         double upperLimit, boolean isUpperOpen, double span) {

        if (OperatorAssistant.isIdentifierAlreadyExist(flagStr) ||
                Operand.hasConstant(flagStr) || hasVariable(flagStr)) {

            throw new VarIdentifierAlreadyExistException();
        }//if

        mVariablesMap.put(flagStr, new Variable(
                flagStr,
                lowerLimit, isLowerOpen,
                upperLimit, isUpperOpen,
                span));

        return this;
    }//addVariable

    public VariableAssistant addVariable(String flagStr) {
        if (OperatorAssistant.isIdentifierAlreadyExist(flagStr) ||
                Operand.hasConstant(flagStr) || hasVariable(flagStr)) {

            throw new VarIdentifierAlreadyExistException();
        }//if

        mVariablesMap.put(flagStr, new Variable(flagStr));

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
        for (Variable variable : mVariablesMap.values()) {
            if (variable.hasNext()) {
                return true;
            }//if
        }//for

        return false;
    }//hasNext

    public void nextValue() {
        for (Variable variable : mVariablesMap.values()) {
            if (variable.hasNext()) {
                variable.nextValue();
                return;
            }//if
        }//for

        throw new VarAssistHasNoNextValueException();
    }//nextValue

    public void clear() {
        mVariablesMap.clear();
    }//clear
}//class_VariableAssistant
