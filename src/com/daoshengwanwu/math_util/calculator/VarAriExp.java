package com.daoshengwanwu.math_util.calculator;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.daoshengwanwu.math_util.calculator.exception.VariableNotExistException;


/**
 * 带有变量的数学表达式类
 * 标识符由字母数字下划线组成，不可以数字打头
 * 数字由数字字符和小数点组成
 * 运算符只由特殊字符组成
 */
public class VarAriExp {
    private static Set<Character> sOcSet = new HashSet<>();

    private VariableAssistant mVarAssist;
    private List<ExpItem> mExpItems = new ArrayList<>();
    
    
    static {
        //初始化sOcSet
        sOcSet.add(')');
        sOcSet.add('(');
        sOcSet.add('|');
        
    }//static


    public VarAriExp(String expStr, VariableAssistant varAssist) {
        expStr = expStr + " ";
        mVarAssist = varAssist;

        int curIndex;
        char curChar;
        String itemStr;
        int itemStartIndex = -1;
        boolean isNumberIdentifierOpen = false;
        boolean isNormalIdentifierOpen = false;
        boolean isSpecialIdentifierOpen = false;

        mExpItems.add(OperatorAssistant.getStartFlag());
        for (curIndex = 0; curIndex < expStr.length(); curIndex++) {
            curChar = expStr.charAt(curIndex);

            if (isNumberIdentifierOpen) {
                if (isNumberPartCharacter(expStr.charAt(curIndex - 1), curChar)) {
                    continue;
                }

                isNumberIdentifierOpen = false;
                itemStr = expStr.substring(itemStartIndex, curIndex);
                mExpItems.add(analysisOperand(itemStr));
            } else if (isSpecialIdentifierOpen) {
                if (isSpecialIdentifierPartCharacter(curChar) && !sOcSet.contains(curChar)) {
                    continue;
                }

                isSpecialIdentifierOpen = false;
                itemStr = expStr.substring(itemStartIndex, curIndex);
                mExpItems.add(analysisOperator(itemStr));
            } else if (isNormalIdentifierOpen) {
                if (isNormalIdentifierPartCharacter(curChar)) {
                    continue;
                }

                isNormalIdentifierOpen = false;
                itemStr = expStr.substring(itemStartIndex, curIndex);
                mExpItems.add(analysisNormalIdentifier(itemStr));
            }

            if (!isBlankCharacter(curChar)) {
                itemStartIndex = curIndex;

                if (isNumberStartCharacter(curChar)) {
                    isNumberIdentifierOpen = true;
                } else if (isSpecialIdentifierStartCharacter(curChar)) {
                    if (!sOcSet.contains(curChar)) {
                        isSpecialIdentifierOpen = true;
                    } else {
                        mExpItems.add(analysisOperator(String.valueOf(curChar)));
                    }
                } else if (isNormalIdentifierStartCharacter(curChar)) {
                    isNormalIdentifierOpen = true;
                }
            }
        }
        mExpItems.add(OperatorAssistant.getEndFlag());
    }//con_VarAriExp

    public VariableAssistant getVariableAssistant() {
        return mVarAssist;
    }//getVariableAssistant

    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 1; i < mExpItems.size() - 1; i++) {
            resultBuilder.append(mExpItems.get(i));
        }

        return resultBuilder.toString();
    }//toString
    
    List<ExpItem> getExpItemList() {
        return mExpItems;
    }//getExpItemList
    
    private ExpItem analysisNormalIdentifier(String itemStr) {
        if (OperatorAssistant.isIdentifierAlreadyExist(itemStr)) {
            return analysisOperator(itemStr);
        } else if (Operand.hasConstant(itemStr)) { 
            return Operand.getConstant(itemStr);
        } else if (null != mVarAssist) {
            return mVarAssist.getVariable(itemStr);
        } else {
            throw new VariableNotExistException(itemStr);
        }//if-else
    }//analysisIdentifier
    
    private ExpItem analysisOperator(String itemStr) {
        Operator operator = OperatorAssistant.getOperator(itemStr);
        if (!operator.isCertain()) {
            operator = ((UncertainOperator)operator).
                    getCertainOperator(mExpItems.get(mExpItems.size() - 1));
        }

        return operator;
    }//analysisOperator
    
    private ExpItem analysisOperand(String itemStr) {
        return Operand.getOperand(itemStr);
    }//analysisOperand

    private boolean isBlankCharacter(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }

    private boolean isNumberPartCharacter(char preC, char curC) {
        return curC >= '0' && curC <= '9' || curC == '.' ||
                curC == 'e' || curC == '-' && preC == 'e';
    }

    private boolean isNormalIdentifierPartCharacter(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' ||
                c >= '0' && c <= '9' || c == '_';
    }

    private boolean isSpecialIdentifierPartCharacter(char c) {
        return (c < '0' || c > '9') && (c < 'a' || c > 'z') &&
                (c < 'A' || c > 'Z') && !isBlankCharacter(c);
    }

    private boolean isNumberStartCharacter(char c) {
        return c >= '0' && c <= '9' || c == '.';
    }

    private boolean isNormalIdentifierStartCharacter(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_';
    }

    private boolean isSpecialIdentifierStartCharacter(char c) {
        return !isNumberStartCharacter(c) &&
                !isNormalIdentifierStartCharacter(c) && !isBlankCharacter(c);
    }
}//class_VarAriExp
