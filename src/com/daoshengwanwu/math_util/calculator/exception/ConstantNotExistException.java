package com.daoshengwanwu.math_util.calculator.exception;


public class ConstantNotExistException extends RuntimeException {
    private static final long serialVersionUID = -490890695594111885L;


    public ConstantNotExistException(String constantStr) {
        super("未定义的常量：" + constantStr);
    }
}
