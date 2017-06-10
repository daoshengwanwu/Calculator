package com.daoshengwanwu.math_util.calculator.exception;


public class IllegalIdentifierException extends RuntimeException {
	public IllegalIdentifierException(String operatorStr) {
		super("非法标识符：" + operatorStr);
	}//con_IllegalIdentifierException
}//class_IllegalIdentifierException
