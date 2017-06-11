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
	private static Set<String> sOcSet = new HashSet<>();
	
	private boolean mIsCertain = false;
	private VariableAssistant mVarAssist;
	private List<ExpItem> mExpItems = new ArrayList<>();
	
	
	static {
		//初始化sOcSet
		sOcSet.add("(");
		sOcSet.add(")");
		sOcSet.add("|");
		
	}//static
	
	
	public VarAriExp(String expStr, VariableAssistant varAssist) {
		mVarAssist = varAssist;
		
		char curChar;
		int curIndex;
		int itemStartIndex = -1;
		String itemStr = null;
		boolean isOperatorOpen = false;
		boolean isOperandOpen = false;
		boolean isIdentifierOpen = false;
		
		expStr = expStr.toLowerCase();
		mExpItems.add(Operator.getStartFlag());
		for (curIndex = 0; curIndex < expStr.length(); curIndex++) {
			curChar = expStr.charAt(curIndex);
			
			if (curChar == ' ' || curChar == '	' || curChar == '\n') {				
				if (isOperandOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					mExpItems.add(analysisOperand(itemStr));
					isOperandOpen = false;
					
				} else if (isOperatorOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					mExpItems.add(analysisOperator(itemStr));
					isOperatorOpen = false;
					
				} else if (isIdentifierOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);			
					mExpItems.add(analysisIdentifier(itemStr));			
					isIdentifierOpen = false;
					
				}//if-else
				
			} else if (curChar >= '0' && curChar <= '9' || curChar == '.') {
				//curChar是数字字符
				if (isOperatorOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					mExpItems.add(analysisOperator(itemStr));
					isOperatorOpen = false;
					
				}//if
				
				if (!isOperandOpen && !isIdentifierOpen) {
					isOperandOpen = true;
					itemStartIndex = curIndex;
				}//if
				
			} else if ((curChar < 'a' || curChar > 'z')  
					&& (curChar <'A' || curChar > 'Z') 
					&& curChar != '_'
					&& (!isOperandOpen || curChar != '-'
					|| expStr.charAt(curIndex - 1) != 'e')) {				
				//curChar是特殊字符
				if (isOperandOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					mExpItems.add(analysisOperand(itemStr));
					isOperandOpen = false;
					
				} else if (isIdentifierOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);			
					mExpItems.add(analysisIdentifier(itemStr));			
					isIdentifierOpen = false;
					
				}//if-else
				
				String curCharStr = String.valueOf(curChar);
				if (sOcSet.contains(curCharStr)) {
					if (isOperatorOpen) {
						itemStr = expStr.substring(itemStartIndex, curIndex);
						mExpItems.add(analysisOperator(itemStr));
						isOperatorOpen = false;
						
					}//if
					
					mExpItems.add(analysisOperator(curCharStr));				
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
					mExpItems.add(analysisOperand(itemStr));
					isOperandOpen = false;
					
				} else if (isOperatorOpen) {
					itemStr = expStr.substring(itemStartIndex, curIndex);
					mExpItems.add(analysisOperator(itemStr));
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
			mExpItems.add(analysisOperand(itemStr));
			isOperandOpen = false;
			
		} else if (isOperatorOpen) {
			itemStr = expStr.substring(itemStartIndex, curIndex);
			mExpItems.add(analysisOperator(itemStr));
			isOperatorOpen = false;
			
		} else if (isIdentifierOpen) {
			itemStr = expStr.substring(itemStartIndex, curIndex);			
			mExpItems.add(analysisIdentifier(itemStr));			
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
	
	public List<ExpItem> getExpItemList() {
		return mExpItems;
	}//getExpItemList
	
	public VariableAssistant getVariableAssistant() {
		return mVarAssist;
	}//getVariableAssistant
	
	@Override
	public String toString() {
		return mExpItems.toString();
	}//toString
	
	private ExpItem analysisIdentifier(String itemStr) {
		if (Operator.isIdentifierAlreadyExist(itemStr)) {
			return Operator.getOperator(itemStr);
		} else if (Operand.hasConstant(itemStr)) { 
			return Operand.getConstant(itemStr);
		} else if (null != mVarAssist && mVarAssist.hasVariable(itemStr)) {
			return mVarAssist.getVariable(itemStr);
		} else {
			throw new VariableNotExistException(itemStr);
		}//if-else
	}//analysisIdentifier
	
	private ExpItem analysisOperator(String itemStr) {
		if (!Operator.isIdentifierAlreadyExist(itemStr)) {
			throw new OperatorNotExistException(itemStr);
		}//if
		
		return Operator.getOperator(itemStr);
	}//analysisOperator
	
	private ExpItem analysisOperand(String itemStr) {
		return Operand.getOperand(itemStr);
	}//analysisOperand
}//class_VarAriExp
