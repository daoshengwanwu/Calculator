package com.daoshengwanwu.math_util.calculator;


import java.util.ArrayList;
import java.util.List;

import com.daoshengwanwu.math_util.calculator.CertainOperator.CertainOperatorType;
import com.daoshengwanwu.math_util.calculator.exception.OperatorNotMatchException;
import com.daoshengwanwu.math_util.calculator.exception.ResultErrorException;
import com.daoshengwanwu.math_util.calculator.util.Stack;


/**
 * 计算器类，外部通过该类来解析计算算术表达式
 */
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

    /**
     * 计算出表达式的当前值，当前值就是指，当我在解析表达式的时候如果遇到变量类型的item
     * 则直接将该变量item.curValue()压入栈中
     * @param varAriExp 要计算的表达式
     * @return 计算结果
     */
    private double calculateCurrentValue(VarAriExp varAriExp) {
        //每次计算前清空运算符栈和操作数栈
        mOperandStack.clear();
        mOperatorStack.clear();
        
        double curValue;
        int curIndex = 0; //当前在处理的item的索引，从0开始
        int curLeftPrior;
        int topRightPrior;
        ExpItem curItem;
        CertainOperator curOperator;
        CertainOperator topOperator;

        List<ExpItem> expItems = varAriExp.getExpItemList();
        int expItemsSize = expItems.size();
        while (curIndex < expItemsSize) {
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
                        case CLOSE: { //如果发现CLOSE性质
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
                            if (((CertainOperator.CloseOperator)curOperator).isNeedPush()) {
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
        
        if (mOperandStack.size() != 1 || !mOperatorStack.isEmpty()) {
            throw new ResultErrorException();
        }//if
        
        return mOperandStack.pop().getValue();
    }//calculateCurrentValue

    /**
     * 从运算符栈中弹出一个运算符，并进行相应的运算
     */
    private void makeOperate() {
        //获取栈顶运算符
        CertainOperator operator = mOperatorStack.pop();
        
        if (!operator.isNeedOperate()) {
            //如果该运算符是无需计算的，则直接返回
            return;
        }//if
        
        int operandNum = operator.getDimension(); //获取运算符需要的操作数个数
        Operand[] operands = new Operand[operandNum]; //承载操作数的容器

        //根据需要的操作数个数，依次从操作数栈中取出操作数放入operands数组中
        for (int i = 1; i <= operandNum; i++) {
            operands[operands.length - i] = mOperandStack.pop();
        }//for

        //最后再将运算结果放入操作数栈中
        mOperandStack.push(operator.operate(operands));
    }//makeOperate


    /**
     * 结果生成器类，该类可依次返回变量表达式的所有可能计算结果
     * 例如：变量表达式为 x * y, 其中x区间为[0, 1], y区间为[1, 2], 且x, y跨度均为0.1
     * 那么该ResultGenerator会依次返回当 x = 0, y = 0; x = 0.1, y = 0; ... x = 1, y = 0; x = 1, y = 0.1; ... x = 1, y = 1;
     * 时对应的结果
     * 第一次调用curValue()方法会返回变量表达式的第一个值，
     * 之后每次调用nextValue()方法，会依次返回变量表达式的下一个值
     */
    public static class ResultGenerator {
        private VarAriExp mVarAriExp;
        private Calculator mCalculator;
        private VariableAssistant mVarAssist;
        
        
        private ResultGenerator(VarAriExp varAriExp) {
            mVarAriExp = varAriExp;
            mCalculator = new Calculator();
            mVarAssist = mVarAriExp.getVariableAssistant();
        }//con_ResultGenerator

        /**
         * 获得当前结果
         * @return 当前结果
         */
        public double curValue() {
            return mCalculator.calculateCurrentValue(mVarAriExp);
        }//curValue

        /**
         * 判断是否还有下一个结果
         * @return true: 如果有下一个计算结果; false: 反之
         */
        public boolean hasNext() {
            return null != mVarAssist && mVarAssist.hasNext();
        }//hasNext

        /**
         * 计算出下一个结果并返回
         * @return 下一个结果
         */
        public double nextValue() {
            mVarAssist.nextValue();
            return curValue();
        }//nextValue

        /**
         * 计算出所有结果，将之保存到List中，并返回
         * @return 包含所有结果的List
         */
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
