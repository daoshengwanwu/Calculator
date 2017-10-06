package com.daoshengwanwu.math_util.calculator.exception;


public class VariableSpanMustBePositiveException extends RuntimeException {
    public VariableSpanMustBePositiveException() {
        super("VariableSpan 必须是正数");
    }
}
