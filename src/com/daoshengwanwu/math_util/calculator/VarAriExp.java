package com.daoshengwanwu.math_util.calculator;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class VarAriExp {
	public static void main(String[] args) {		
		String expStr = "sin(pi/2+0*log(e)~2)";
		List<String> keywords = pickKeywords(expStr);
		System.out.println(keywords);
	}

	/**
	 * 关键字提取方法
	 * 标识符由字母数字下划线组成，不可以数字打头
	 * 数字由数字字符和小数点组成
	 * 运算符只由特殊字符组成
	 * @author 白浩然
	 * @param expStr 要提取关键字的字符串表达式
	 * @return 关键字集合
	 */
	public static List<String> pickKeywords(String expStr) {
		List<String> keywords = new ArrayList<>();

		char curChar;
		int curIndex;
		int itemStartIndex = -1;			
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
				if (isOperatorOpen || isOperandOpen || isIdentifierOpen) {
					keywords.add(expStr.substring(itemStartIndex, curIndex));
					isOperatorOpen = false;
					isOperandOpen = false;
					isIdentifierOpen = false;
				}//if
				
			} else if (curChar >= '0' && curChar <= '9') {
				//curChar是数字字符
				if (isOperatorOpen) {
					keywords.add(expStr.substring(itemStartIndex, curIndex));
					isOperatorOpen = false;
				}//if
				
				if (!isOperandOpen && !isIdentifierOpen) {
					isOperandOpen = true;
					itemStartIndex = curIndex;
				}//if
				
			} else if ((curChar < 'a' || curChar > 'z')
					&& (curChar <'A' || curChar > 'Z') && curChar != '_') {
				//curChar是特殊字符
				if (isOperandOpen || isIdentifierOpen) {
					keywords.add(expStr.substring(itemStartIndex, curIndex));
					
					isOperandOpen = false;
					isIdentifierOpen = false;
				}//if
				
				String curCharStr = String.valueOf(curChar);
				if (ocSet.contains(curCharStr)) {
					keywords.add(curCharStr);
					
					if (isOperatorOpen) {
						keywords.add(expStr.substring(itemStartIndex, curIndex));
						isOperatorOpen = false;
					}//if
				} else if (!isOperatorOpen) {
					isOperatorOpen = true;
					itemStartIndex = curIndex;
				}//if-else
				
			} else {
				//curChar是字母字符
				if (isOperandOpen || isOperatorOpen) {
					keywords.add(expStr.substring(itemStartIndex, curIndex));
					
					isOperandOpen = false;
					isOperatorOpen = false;
				}//if
				
				if (!isIdentifierOpen) {
					isIdentifierOpen = true;
					itemStartIndex = curIndex;
				}//if
				
			}//if-else
		}//for
		
		if (isOperatorOpen || isOperandOpen || isIdentifierOpen) {
			keywords.add(expStr.substring(itemStartIndex, curIndex));
		}//if
			
		return keywords;
	}//pickKeywords
}//class_VarAriExp
