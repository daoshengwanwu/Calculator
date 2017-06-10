package com.daoshengwanwu.math_util.calculator;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.daoshengwanwu.math_util.calculator.ExpItem.Operand;
import com.daoshengwanwu.math_util.calculator.ExpItem.Operator;
import com.daoshengwanwu.math_util.calculator.ExpItem.Variable.VariableAssistant;
import com.daoshengwanwu.math_util.calculator.exception.OperatorNotExistException;
import com.daoshengwanwu.math_util.calculator.exception.VariableNotExistException;


/**
 * 带有变量的数学表达式类
 * 标识符由字母数字下划线组成，不可以数字打头
 * 数字由数字字符和小数点组成
 * 运算符只由特殊字符组成
 * @author 白浩然
 */
public class VarAriExp {
	private List<ExpItem> mExpItems = new ArrayList<>();
	
	
	public static void main(String[] args) {	
		long startTime = System.currentTimeMillis();
		VarAriExp exp = new VarAriExp("sin(1+2)", null);
		long endTime = System.currentTimeMillis();
		System.out.println(exp + "\n耗费时间：" + (endTime - startTime) + "毫秒");
	}
	
	public VarAriExp(String expStr, VariableAssistant varAssist) {
		char curChar;
		int curIndex;
		int itemStartIndex = -1;	
		String itemStr = null;
		boolean isOperatorOpen = false;
		boolean isOperandOpen = false;
		boolean isIdentifierOpen = false;
		
		Set<String> ocSet = new HashSet<>();
		ocSet.add("(");
		ocSet.add(")");
		ocSet.add("|");
		
		for (curIndex = 0; curIndex < expStr.length(); curIndex++) {
			curChar = expStr.charAt(curIndex);
			
			if (curChar == ' ') {				
				if (isOperandOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					mExpItems.add(Operand.getOperand(itemStr));
					
					isOperandOpen = false;
					
				} else if (isOperatorOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					
					if (!Operator.isIdentifierAlreadyExist(itemStr)) {
						throw new OperatorNotExistException(itemStr);
					}//if
					
					mExpItems.add(Operator.getOperator(itemStr));
					isOperatorOpen = false;
					
				} else if (isIdentifierOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					
					if (Operator.isIdentifierAlreadyExist(itemStr)) {
						mExpItems.add(Operator.getOperator(itemStr));
					} else if (null != varAssist && varAssist.hasVariable(itemStr)) {
						mExpItems.add(varAssist.getVariable(itemStr));
					} else {
						throw new VariableNotExistException(itemStr);
					}//if-else
					
					isIdentifierOpen = false;
				}//if-else
				
			} else if (curChar >= '0' && curChar <= '9') {
				//curChar是数字字符
				if (isOperatorOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					
					if (!Operator.isIdentifierAlreadyExist(itemStr)) {
						throw new OperatorNotExistException(itemStr);
					}//if
					
					mExpItems.add(Operator.getOperator(itemStr));
					isOperatorOpen = false;
				}//if
				
				if (!isOperandOpen && !isIdentifierOpen) {
					isOperandOpen = true;
					itemStartIndex = curIndex;
				}//if
				
			} else if ((curChar < 'a' || curChar > 'z')
					&& (curChar <'A' || curChar > 'Z') && curChar != '_') {
				//curChar是特殊字符
				if (isOperandOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					mExpItems.add(Operand.getOperand(itemStr));
					
					isOperandOpen = false;
					
				} else if (isIdentifierOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					
					if (Operator.isIdentifierAlreadyExist(itemStr)) {
						mExpItems.add(Operator.getOperator(itemStr));
					} else if (null != varAssist && varAssist.hasVariable(itemStr)) {
						mExpItems.add(varAssist.getVariable(itemStr));
					} else {
						throw new VariableNotExistException(itemStr);
					}//if-else
					
					isIdentifierOpen = false;
				}//if-else
				
				String curCharStr = String.valueOf(curChar);
				if (ocSet.contains(curCharStr)) {
					mExpItems.add(Operator.getOperator(curCharStr));
					
					if (isOperatorOpen) {
						itemStr = expStr.substring(itemStartIndex, curIndex);
						
						if (!Operator.isIdentifierAlreadyExist(itemStr)) {
							throw new OperatorNotExistException(itemStr);
						}//if
						
						mExpItems.add(Operator.getOperator(itemStr));
						isOperatorOpen = false;
					}//if
				} else if (!isOperatorOpen) {
					isOperatorOpen = true;
					itemStartIndex = curIndex;
				}//if-else
				
			} else {
				//curChar是字母字符
				if (isOperandOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					mExpItems.add(Operand.getOperand(itemStr));
					
					isOperandOpen = false;
					
				} else if (isOperatorOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					
					if (!Operator.isIdentifierAlreadyExist(itemStr)) {
						throw new OperatorNotExistException(itemStr);
					}//if
					
					mExpItems.add(Operator.getOperator(itemStr));
					isOperatorOpen = false;
					
				}//if-else
				
				if (!isIdentifierOpen) {
					isIdentifierOpen = true;
					itemStartIndex = curIndex;
				}//if
				
			}//if-else
		}//for
		
		if (isOperandOpen) {
			itemStr = expStr.substring(itemStartIndex, curIndex);
			mExpItems.add(Operand.getOperand(itemStr));
			
			isOperandOpen = false;
			
		} else if (isOperatorOpen) {
			itemStr = expStr.substring(itemStartIndex, curIndex);
			
			if (!Operator.isIdentifierAlreadyExist(itemStr)) {
				throw new OperatorNotExistException(itemStr);
			}//if
			
			mExpItems.add(Operator.getOperator(itemStr));
			isOperatorOpen = false;
			
		} else if (isIdentifierOpen) {
			itemStr = expStr.substring(itemStartIndex, curIndex);
			
			if (Operator.isIdentifierAlreadyExist(itemStr)) {
				mExpItems.add(Operator.getOperator(itemStr));
			} else if (null != varAssist && varAssist.hasVariable(itemStr)) {
				mExpItems.add(varAssist.getVariable(itemStr));
			} else {
				throw new VariableNotExistException(itemStr);
			}//if-else
			
			isIdentifierOpen = false;
		}//if-else
	}

	@Override
	public String toString() {
		return mExpItems.toString();
	}//toString
}//class_VarAriExp
