package com.daoshengwanwu.math_util.calculator;


import com.daoshengwanwu.math_util.calculator.exception.*;
import com.daoshengwanwu.math_util.calculator.util.DigitUtil;


class Variable extends ExpItem {
    private String mFlagStr;
    private double mUpperLimit;
    private double mLowerLimit;
    private double mSpan;
    private double mCurValue;


    Variable(String flagStr, Operand lowerLimit, boolean isLowerOpen
            , Operand upperLimit, boolean isUpperOpen, Operand span) {
        super(ItemType.VARIABLE);

        //变量的字符串标识
        mFlagStr = flagStr;

        //判断变量的上下限是否合法
        if (lowerLimit.getValue() > upperLimit.getValue()) {
            throw new VariableDomainErrorException();
        }//if

        if (span.getValue() < 0) {
            throw new VariableSpanMustBePositiveException();
        }
        Operand standardRef = Math.abs(lowerLimit.getValue()) >
                Math.abs(upperLimit.getValue()) ? lowerLimit : upperLimit;
        Operand minimuxSpan = Operand.getOperand(DigitUtil.getMinimumSpan(
                standardRef.getValue(), Operand.SIGNIFICANCE_DIGIT));
        if (span.getValue() < minimuxSpan.getValue()) {
            span = minimuxSpan;
        }
        //变量的跨度
        mSpan = span.getValue();

        if (isLowerOpen) {
            lowerLimit = Operand.getOperand(lowerLimit.getValue() + mSpan);
        } //if
        if (isUpperOpen) {
            upperLimit = Operand.getOperand(upperLimit.getValue() - mSpan);
        }//if

        if (lowerLimit.getValue() > upperLimit.getValue()) {
            throw new VariableSpanNotSuitableException();
        }//if

        mUpperLimit = upperLimit.getValue();
        mLowerLimit = lowerLimit.getValue();

        mCurValue = mLowerLimit;
    }//con_Variable

    public boolean hasNext() {
        return mCurValue < mUpperLimit;
    }//hasNext

    public double nextValue() {
        if (hasNext()) {
            if (mCurValue + mSpan <= mUpperLimit) {
                mCurValue += mSpan;
            } else {
                mCurValue = mUpperLimit;
            }//if-else

            return mCurValue;
        }//if

        throw new NoNextValueException(mFlagStr);
    }//nextValue

    public double curValue() {
        return mCurValue;
    }//curValue

    @Override
    public String toString() {
        return mFlagStr;
    }//toString
}//class_Variable
