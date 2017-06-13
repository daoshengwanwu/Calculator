package com.daoshengwanwu.math_util.calculator.exception;


public class VariableSpanNotSuitableException extends RuntimeException {
    private static final long serialVersionUID = -2601380434694975420L;


    public VariableSpanNotSuitableException() {
        super("当前变量的下限确实小于上限，但是却无法找到一个可用的值（可能是Span过大）");
    }
}
