package com.daoshengwanwu.math_util.calculator;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.daoshengwanwu.math_util.calculator.ExpItem.Operand;
import com.daoshengwanwu.math_util.calculator.ExpItem.Operator;
import com.daoshengwanwu.math_util.calculator.ExpItem.Operator.UncertainOperator;
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
	private boolean mIsCertain = false;
	private List<ExpItem> mExpItems = new ArrayList<>();
	
	
	public static void main(String[] args) {
		String expStr = "-1234567 \n	890123454e-10-10-pi*e";
		long startTime = System.currentTimeMillis();
		VarAriExp exp = new VarAriExp(expStr, null);
		long endTime = System.currentTimeMillis();
		
		System.out.println(exp + "\n耗费时间：" + (endTime - startTime) + "毫秒");
		

		System.out.println("exp is certain: " + exp.isCertain());
		exp.ensureAriExp();
		System.out.println("exp is certain: " + exp.isCertain());
		System.out.println(exp);
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
		
		expStr = expStr.toLowerCase();
		mExpItems.add(Operator.getStartFlag());
		for (curIndex = 0; curIndex < expStr.length(); curIndex++) {
			curChar = expStr.charAt(curIndex);
			
			if (curChar == ' ' || curChar == '	' || curChar == '\n') {				
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
					} else if (Operand.hasConstant(itemStr)) { 
						mExpItems.add(Operand.getConstant(itemStr));
					} else if (null != varAssist && varAssist.hasVariable(itemStr)) {
						mExpItems.add(varAssist.getVariable(itemStr));
					} else {
						throw new VariableNotExistException(itemStr);
					}//if-else
					
					isIdentifierOpen = false;
				}//if-else
				
			} else if (curChar >= '0' && curChar <= '9' || curChar == '.') {
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
					&& (curChar <'A' || curChar > 'Z') 
					&& curChar != '_'
					&& (curChar != '-' || !isOperandOpen || expStr.charAt(curIndex - 1) != 'e')) {				
				//curChar是特殊字符
				if (isOperandOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					mExpItems.add(Operand.getOperand(itemStr));
					
					isOperandOpen = false;
					
				} else if (isIdentifierOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					
					if (Operator.isIdentifierAlreadyExist(itemStr)) {
						mExpItems.add(Operator.getOperator(itemStr));
					} else if (Operand.hasConstant(itemStr)) { 
						mExpItems.add(Operand.getConstant(itemStr));
					} else if (null != varAssist && varAssist.hasVariable(itemStr)) {
						mExpItems.add(varAssist.getVariable(itemStr));
					} else {
						throw new VariableNotExistException(itemStr);
					}//if-else
					
					isIdentifierOpen = false;
				}//if-else
				
				String curCharStr = String.valueOf(curChar);
				if (ocSet.contains(curCharStr)) {
					if (isOperatorOpen) {
						itemStr = expStr.substring(itemStartIndex, curIndex);
						
						if (!Operator.isIdentifierAlreadyExist(itemStr)) {
							throw new OperatorNotExistException(itemStr);
						}//if
						
						mExpItems.add(Operator.getOperator(itemStr));
						isOperatorOpen = false;
					}//if
					
					mExpItems.add(Operator.getOperator(curCharStr));				
				} else if (!isOperatorOpen) {
					isOperatorOpen = true;
					itemStartIndex = curIndex;
				}//if-else
				
			} else if ((curChar >= 'a' && curChar <= 'z' 
					|| curChar >= 'A' && curChar <= 'Z') 
					&& (curChar != 'e' || !isOperandOpen)
					|| curChar == '_') {			
				//curChar是标识符组成字符
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
			} else if (Operand.hasConstant(itemStr)) { 
				mExpItems.add(Operand.getConstant(itemStr));
			} else if (null != varAssist && varAssist.hasVariable(itemStr)) {
				mExpItems.add(varAssist.getVariable(itemStr));
			} else {
				throw new VariableNotExistException(itemStr);
			}//if-else
			
			isIdentifierOpen = false;
		}//if-else
		
		mExpItems.add(Operator.getEndFlag());
	}//con_VarAriExp

	public void ensureAriExp() {
		if (mIsCertain) {
			return;
		}//if
		
		ExpItem curItem;
		Operator curOperator;
		UncertainOperator curUncertainOperator;
		for (int i = 0; i < mExpItems.size(); i++) {
			curItem = mExpItems.get(i);
			if (curItem.getItemType() == ExpItem.ItemType.OPERATOR) {
				curOperator = (Operator)curItem;
				if (!curOperator.isCertain()) {
					curUncertainOperator = (UncertainOperator)curOperator;
					mExpItems.set(i, curUncertainOperator
							.getCertainOperator(mExpItems.get(i - 1)));
				}//if
			}//if
		}//for
		
		mIsCertain = true;
	}//ensureAriExp
	
	public boolean isCertain() {
		return mIsCertain;
	}//isCertain
	
	@Override
	public String toString() {
		return mExpItems.toString();
	}//toString
}//class_VarAriExp
