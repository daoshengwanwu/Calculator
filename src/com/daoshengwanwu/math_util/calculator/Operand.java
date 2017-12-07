package com.daoshengwanwu.math_util.calculator;


import com.daoshengwanwu.math_util.calculator.exception.ConstantNotExistException;
import com.daoshengwanwu.math_util.calculator.util.DigitUtil;

import java.util.HashMap;
import java.util.Map;


class Operand extends ExpItem {
    //操作数的有效数字个数
    static final int SIGNIFICANCE_DIGIT = DigitUtil.DOUBLE_SIGNIFICANT_DIGIT;

    private static final Map<String, Operand> sConstantsMap = new HashMap<>();


    static {
        //初始化常量表
        sConstantsMap.put("pi", new Constant_PI());
        sConstantsMap.put("e", new Constant_E());
    }//static


    private double mValue;


    public static Operand getOperand(String operandStr) {
        return getOperand(Double.parseDouble(operandStr));
    }//getOperand

    public static Operand getOperand(double operandValue) {
        return new Operand(operandValue);
    }//getOperand

    public static boolean hasConstant(String constantStr) {
        return sConstantsMap.containsKey(constantStr);
    }//hasContant

    public static Operand getConstant(String constantStr) {
        if (hasConstant(constantStr)) {
            return sConstantsMap.get(constantStr);
        }//if

        throw new ConstantNotExistException(constantStr);
    }//getConstant

    private Operand(double value) {
        super(ItemType.OPERAND);

        mValue = DigitUtil.reserveSignificantDigits(value, SIGNIFICANCE_DIGIT);
    }//con_Operand

    public double getValue() {
        return mValue;
    }//getValue

    @Override
    public String toString() {
        return String.valueOf(mValue);
    }//toString

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Operand && getValue() == ((Operand) obj).getValue();
    }//equals


    private static class Constant_PI extends Operand {
        private Constant_PI() {
            super(0); //对于常量来说这个mValue无用，默认置为0
        }

        @Override
        public double getValue() {
            return Math.PI;
        }
    }

    private static class Constant_E extends Operand {
        private Constant_E() {
            super(0); //对于常量来说这个mValue无用，默认置为0
        }

        @Override
        public double getValue() {
            return Math.E;
        }
    }
}//class_Operand
