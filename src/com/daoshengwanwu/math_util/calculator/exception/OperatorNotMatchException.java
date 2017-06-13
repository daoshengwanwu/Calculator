package com.daoshengwanwu.math_util.calculator.exception;


public class OperatorNotMatchException extends RuntimeException {
    private static final long serialVersionUID = -771892600487000288L;


    public OperatorNotMatchException(String topOperatorStr, String curOperatorStr) {
        super("不匹配的运算符：" + topOperatorStr + "与 " + curOperatorStr);
    }
}
