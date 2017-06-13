package com.daoshengwanwu.math_util.calculator.exception;


public class OperatorNotExistException extends RuntimeException {
    private static final long serialVersionUID = -8017831371124050966L;


    public OperatorNotExistException(String flagStr) {
        super("未定义的运算符：" + flagStr);
    }
}
