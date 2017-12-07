package com.daoshengwanwu.math_util.calculator;


abstract class UncertainOperator extends Operator {
    private static final boolean IS_CERTAIN = false;


    private UncertainOperator(String operatorStr) {
        super(operatorStr);
    }//con_UncertainOperator

    @Override
    boolean isCertain() {
        return IS_CERTAIN;
    }//isCertain

    //通过该方法来确认出该未确定类型运算符的具体类型
    abstract Operator getCertainOperator(ExpItem preItem);


    static class Hyphen extends UncertainOperator {
        Hyphen(String operatorStr) {
            super(operatorStr);
        }//con_Hyphen

        @Override
        Operator getCertainOperator(ExpItem preItem) {
            ItemType itemType = preItem.getItemType();
            if (itemType == ItemType.OPERAND || itemType == ItemType.VARIABLE) {
                //如果'-'前边的项是一个操作数或者变量，那么这个'-'的含义必然为减号
                return OperatorAssistant.getOperator(OperatorAssistant.SUB);
            }//if

            CertainOperator operator = (CertainOperator)preItem;
            if (operator.getCertainOperatorType() !=
                    CertainOperator.CertainOperatorType.OPEN && !operator.isRightDirPriorExist()) {

                //如果之前的项是不拥有右方向优先级的运算符，那么这个'-'的含义必然为减号
                return OperatorAssistant.getOperator(OperatorAssistant.SUB);
            }//if

            return OperatorAssistant.getOperator(OperatorAssistant.NEGATE);
        }//getCertainOperator
    }//class_Hyphen

    static class VerticalLine extends UncertainOperator {
        VerticalLine(String operatorStr) {
            super(operatorStr);
        }//con_VerticalLine

        @Override
        Operator getCertainOperator(ExpItem preItem) {
            ItemType itemType = preItem.getItemType();
            if (itemType == ItemType.OPERAND || itemType == ItemType.VARIABLE) {
                return OperatorAssistant.getOperator(OperatorAssistant.RIGHT_ABS);
            }//if

            CertainOperator operator = (CertainOperator)preItem;
            if (operator.getCertainOperatorType() !=
                    CertainOperator.CertainOperatorType.OPEN && !operator.isRightDirPriorExist()) {

                return OperatorAssistant.getOperator(OperatorAssistant.RIGHT_ABS);
            }//if

            return OperatorAssistant.getOperator(OperatorAssistant.LEFT_ABS);
        }//getCertainOperator
    }//class_VerticalLine
}//class_UncertainOperator
