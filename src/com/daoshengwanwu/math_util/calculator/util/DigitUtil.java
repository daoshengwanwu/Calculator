package com.daoshengwanwu.math_util.calculator.util;


public class DigitUtil {
    public static final int DOUBLE_SIGNIFICANT_DIGIT = 15;
    
    
    /**
     * @author 白浩然
     * @param value 要被规范化的数
     * @param significantDigits 有效位数
     * @return 被规范化后的值, 被规范化后的double值可以直接通过 "=="运算符来判等
     */
    public static double reserveSignificantDigits(double value, int significantDigits) {
        // 计算出整数的位数integerFigures
        int integerFigures = getIntegerFigures(value);
        if (integerFigures > significantDigits) {
            value /= (long)Math.pow(10, integerFigures - significantDigits);
            integerFigures = significantDigits;
        }

        // 计算出应有的小数位数：decimalFigures = significantDigit - integerFigures
        int decimalFigures = significantDigits - integerFigures;

        // 根据decimalFigures对value进行四舍五入并返回
        long auxiliary = (long)Math.pow(10, decimalFigures);

        return (double)Math.round(value * auxiliary) / auxiliary;
    }//reserveSignificantDigits

    /**
     * @author 白浩然
     * @param value 要计算最小跨度的值
     * @param significantDigits 有效位数
     * @return 在指定有效位数下的能对value产生影响的最小跨度值
     */
    public static double getMinimumSpan(double value, int significantDigits) {
        //得到value的整数位数
        int integerFigures = getIntegerFigures(value);

        //得到对应有效数字位数下的最多小数位数
        int maxDecimalFigures = significantDigits - integerFigures;

        return Math.pow(10, 0 - maxDecimalFigures);
    }//getMinimumSpan

    /**
     * @author 白浩然
     * @param value 要计算整数位数的数
     * @return value 的整数位数
     */
    private static int getIntegerFigures(double value) {
        int integerFigures = 0;
        long lValue = Math.abs((long)value);
        
        while (lValue >= 1000) {
            integerFigures += 4;
            lValue /= 10000;
        }
        
        while (lValue > 0) {
            integerFigures++;
            lValue /= 10;
        }
        
        return integerFigures;
    }//getIntegerFigures
}//class_DigitUtil
