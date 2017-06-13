package com.daoshengwanwu.math_util.calculator.exception;


public class OperandNumException extends RuntimeException {
    private static final long serialVersionUID = 195937494088326957L;


    public OperandNumException (String operatorStr, int requireNum, int actualNum) {
        super("非法操作数个数，所属运算符：" + operatorStr + ", 操作数要求个数：" + requireNum + ", 实际操作数个数：" + actualNum);
    }//con_OperandNumException
}//class_OperandNumException
