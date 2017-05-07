package com.daoshengwanwu.math_util.calculator;


public class IllegalIdentifierException extends Exception {
	public IllegalIdentifierException(String operatorStr) {
		super("非法标识符：" + operatorStr);
	}//con_IllegalIdentifierException
}//class_IllegalIdentifierException
