package com.daoshengwanwu.math_util.calculator.exception;


public class VariableNotExistException extends RuntimeException {
    private static final long serialVersionUID = 3025921129815754917L;


    public VariableNotExistException(String flagStr) {
        super("VariableAssistant 中不存在这个变量定义：" + flagStr);
    }
}
