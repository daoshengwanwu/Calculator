package com.daoshengwanwu.math_util.calculator.exception;


public class SpecDirPriorNotExistException extends RuntimeException {
    private static final long serialVersionUID = 1380518926247314494L;


    public SpecDirPriorNotExistException(String operatorStr, String directionStr) {
        super(operatorStr + "运算符不存在" + directionStr + "方向上的优先级");
    }//con_SpecDirPriorNotExistException
}//class_SpecDirPriorNotExistException
