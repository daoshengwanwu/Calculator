package com.daoshengwanwu.math_util.calculator;


import java.util.ArrayList;
import java.util.List;

import com.daoshengwanwu.math_util.calculator.ExpItem.Operand;
import com.daoshengwanwu.math_util.calculator.ExpItem.Operator.CertainOperator;
import com.daoshengwanwu.math_util.calculator.ExpItem.Operator.CertainOperator.CertainOperatorType;
import com.daoshengwanwu.math_util.calculator.ExpItem.Variable;
import com.daoshengwanwu.math_util.calculator.ExpItem.Variable.VariableAssistant;
import com.daoshengwanwu.math_util.calculator.exception.OperatorNotMatchException;
import com.daoshengwanwu.math_util.calculator.exception.ResultErrorException;
import com.daoshengwanwu.math_util.calculator.util.Stack;


public class Calculator {
    private Stack<Operand> mOperandStack = new Stack<>();
    private Stack<CertainOperator> mOperatorStack = new Stack<>();
    
    
    public double calculate(String expStr) {
        AriExp ariExp = new AriExp(expStr);
        
        return calculate(ariExp);
    }//calculate
    
    public ResultGenerator calculate(String expStr, VariableAssistant varAssist) {
        VarAriExp varAriExp = new VarAriExp(expStr, varAssist);
        
        return calculate(varAriExp);
    }//calculate
    
    public double calculate(AriExp ariExp) {
        return calculateCurrentValue(ariExp);
    }//calculate
    
    public ResultGenerator calculate(VarAriExp varAriExp) {
        return new ResultGenerator(varAriExp);
    }//calculate
    
    private double calculateCurrentValue(VarAriExp varAriExp) {
        //每次计算前清空运算符栈和操作数栈
        mOperandStack.clear();
        mOperatorStack.clear();
        
        double curValue;
        int curIndex = 0;
        int curLeftPrior;
        int topRightPrior;
        ExpItem curItem;
        CertainOperator curOperator;
        CertainOperator topOperator;
        List<ExpItem> expItems = varAriExp.getExpItemList();
        while (curIndex < expItems.size()) {
            curItem = expItems.get(curIndex);
            
            switch (curItem.getItemType()) {
            case OPERAND: {
                mOperandStack.push((Operand)curItem);
                curIndex++;
            } break;
            case VARIABLE: {
                curValue = ((Variable)curItem).curValue();
                mOperandStack.push(Operand.getOperand(curValue));
                curIndex++;
            } break;
            case OPERATOR: {
                curOperator = (CertainOperator)curItem;
                switch (curOperator.getCertainOperatorType()) {
                case NORMAL: {
                    if (curOperator.isLeftDirPriorExist()) {
                        topOperator = mOperatorStack.getTop();
                        switch (topOperator.getCertainOperatorType()) {
                        case NORMAL: 
                        case CLOSE: {
                            if (topOperator.isRightDirPriorExist()) {
                                curLeftPrior = curOperator.getLeftDirPriority();
                                topRightPrior = topOperator.getRightDirPriority();
                                
                                if (curLeftPrior <= topRightPrior) {
                                    makeOperate();
                                } else {
                                    mOperatorStack.push(curOperator);
                                    curIndex++;
                                }//if-else
                            } else {
                                makeOperate();
                            }//if-else
                        } break;
                        case OPEN: {
                            mOperatorStack.push(curOperator);
                            curIndex++;
                        } break;
                        }//switch-case
                        
                    } else {
                        mOperatorStack.push(curOperator);
                        curIndex++;
                    }//if-else
                } break;
                case OPEN: {
                    //如果当前运算符是OPEN性质的，直接入栈
                    mOperatorStack.push(curOperator);
                    curIndex++;
                } break;
                case CLOSE: {
                    topOperator = mOperatorStack.getTop();
                    makeOperate();
                    
                    if (topOperator.getCertainOperatorType() == CertainOperatorType.OPEN) {
                        if (topOperator.getId() == curOperator.getId()) {
                            if (curOperator.isNeedPush()) {
                                mOperatorStack.push(curOperator);
                            }//if
                            curIndex++;
                        } else {
                            throw new OperatorNotMatchException(
                                    topOperator.getOperatorStr(), curOperator.getOperatorStr());
                        }//if-else
                    }//if
                } break;
                }//switch
            } break;
            }//switch-case
        } //while
        
        if (mOperandStack.isEmpty()) {
            throw new ResultErrorException();
        }//if
        
        return mOperandStack.pop().getValue();
    }//calculateCurrentValue
    
    private void makeOperate() {        
        CertainOperator operator = mOperatorStack.pop();
        
        if (!operator.isNeedOperate()) {
            return;
        }//if
        
        int operandNum = operator.getDimension();
        Operand[] operands = new Operand[operandNum];
        
        for (int i = 1; i <= operandNum; i++) {
            operands[operands.length - i] = mOperandStack.pop();
        }//for
        
        mOperandStack.push(operator.operate(operands));
    }//makeOperate
    
    
    public static class ResultGenerator {
        private VarAriExp mVarAriExp;
        private Calculator mCalculator;
        private VariableAssistant mVarAssist;
        
        
        private ResultGenerator(VarAriExp varAriExp) {
            mVarAriExp = varAriExp;
            mCalculator = new Calculator();
            mVarAssist = mVarAriExp.getVariableAssistant();
        }//con_ResultGenerator
        
        public double curValue() {
            return mCalculator.calculateCurrentValue(mVarAriExp);
        }//curValue
        
        public boolean hasNext() {
            return null != mVarAssist && mVarAssist.hasNext();
        }//hasNext
        
        public double nextValue() {
            mVarAssist.nextValue();
            return mCalculator.calculateCurrentValue(mVarAriExp);
        }//nextValue
        
        public List<Double> getResultList() {
            List<Double> resultList = new ArrayList<>();
            
            resultList.add(curValue());
            while (hasNext()) {
                resultList.add(nextValue());
            }//while
            
            return resultList;
        }//getResultList
    }//class_ResultGenerator
}//class_Calculator
