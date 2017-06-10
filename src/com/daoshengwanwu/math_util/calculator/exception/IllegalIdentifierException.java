package com.daoshengwanwu.math_util.calculator.exception;


public class IllegalIdentifierException extends RuntimeException {
	private static final long serialVersionUID = -1670577013454755305L;

	public IllegalIdentifierException(String operatorStr) {
		super("非法标识符：" + operatorStr);
	}//con_IllegalIdentifierException
}//class_IllegalIdentifierException
