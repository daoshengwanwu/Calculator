package com.daoshengwanwu.math_util.calculator.exception;


public class OperatorNotExistException extends RuntimeException {
	public OperatorNotExistException(String flagStr) {
		super("未定义的运算符：" + flagStr);
	}
}
