package com.daoshengwanwu.math_util.calculator.exception;


public class NoNextValueException extends RuntimeException {
    private static final long serialVersionUID = 1957472119086244072L;


    public NoNextValueException(String varStr) {
        super(varStr + "变量已不存在可用的值");
    }
}
