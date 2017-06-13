package com.daoshengwanwu.math_util.calculator.exception;


public class ShouldNotOperateException extends RuntimeException {
    private static final long serialVersionUID = 2799611142068416188L;


    public ShouldNotOperateException(String operatorStr) {
        super(operatorStr + "运算符不应该调用operate方法");
    }
}
