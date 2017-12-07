package com.daoshengwanwu.math_util.calculator;


abstract class Operator extends ExpItem {
    private static final ItemType OPERATOR_ITEM_TYPE = ItemType.OPERATOR;

    private final String mOperatorStr; //运算符的字符串描述


    Operator(String operatorStr) {
        super(OPERATOR_ITEM_TYPE);

        mOperatorStr = operatorStr;
    }//con_Operator

    @Override
    public String toString() {
        return mOperatorStr;
    }//toString

    //获取运算符的字符串描述
    String getOperatorStr() {
        return mOperatorStr;
    }//getOperatorStr

    //通过该方法来判断该运算符是否为类型确定的运算符
    abstract boolean isCertain();
}//class_Operator
