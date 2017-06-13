package com.daoshengwanwu.math_util.calculator.exception;


public class OperandOutOfBoundsException extends RuntimeException {
    private static final long serialVersionUID = 830822729160070834L;


    public OperandOutOfBoundsException(String operatorStr, String bounds, double operand) {
        super("操作数范围异常：" + operatorStr + "运算符要求操作数的范围为：" + bounds + ", 实际传入的操作数为：" + operand);
    }
}
