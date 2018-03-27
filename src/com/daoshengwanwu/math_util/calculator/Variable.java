package com.daoshengwanwu.math_util.calculator;


import com.daoshengwanwu.math_util.calculator.exception.*;
import com.daoshengwanwu.math_util.calculator.util.DigitUtil;


public class Variable extends ExpItem {
    private String mFlagStr;
    private double mUpperLimit;
    private double mLowerLimit;
    private double mSpan;
    private double mCurValue;


    Variable(String flagStr) {
        super(ItemType.VARIABLE);

        //变量的字符串标识
        mFlagStr = flagStr;

        mUpperLimit = -1;
        mLowerLimit = 1;
        mSpan = 0;
        mCurValue = 0;
    }

    Variable(String flagStr, double lowerLimit, boolean isLowerOpen
            , double upperLimit, boolean isUpperOpen, double span) {
        this(flagStr);
        set(lowerLimit, isLowerOpen, upperLimit, isUpperOpen, span);
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
        if (!isSet()) {
            throw new VariableNotSetException();
        }//if

        return mCurValue;
    }//curValue

    public int size() {
        if (!isSet()) {
            return 0;
        }

        int size = (int)((mUpperLimit - mLowerLimit) / mSpan) + 1;

        if ((mUpperLimit - mLowerLimit) % mSpan != 0) {
            size++;
        }

        return size;
    }

    public void reset() {
        mCurValue = mLowerLimit;
    }

    public void set(double dLowerLimit, boolean isLowerOpen
            , double dUpperLimit, boolean isUpperOpen, double dSpan) {

        Operand lowerLimit = Operand.getOperand(dLowerLimit);
        Operand upperLimit = Operand.getOperand(dUpperLimit);
        Operand span = Operand.getOperand(dSpan);

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
    }

    public void setCurValue(double curValue) {
        mCurValue = curValue;
    }

    @Override
    public String toString() {
        return mFlagStr;
    }//toString

    public boolean isSet() {
        return mLowerLimit <= mUpperLimit;
    }//isSet
}//class_Variable
