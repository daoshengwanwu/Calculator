package com.daoshengwanwu.math_util.calculator.exception;


public class VariableDomainErrorException extends RuntimeException {
    private static final long serialVersionUID = 8823048796140775960L;


    public VariableDomainErrorException() {
        super("区间的下界应当小于或等于上届");
    }
}
