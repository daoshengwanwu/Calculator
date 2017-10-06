package com.daoshengwanwu.math_util.calculator.exception;


public class ResultErrorException extends RuntimeException {
    private static final long serialVersionUID = 4451724884019012413L;


    public ResultErrorException() {
        super("计算过程中出现问题，请检查表达式是否输入有误。");
    }
}
