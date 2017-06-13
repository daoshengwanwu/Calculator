package com.daoshengwanwu.math_util.calculator.exception;


public class VarAssistHasNoNextValueException extends RuntimeException {
    private static final long serialVersionUID = 7830192870850056786L;


    public VarAssistHasNoNextValueException() {
        super("VariableAssistant中不存在下一个可用的值");
    }
}
