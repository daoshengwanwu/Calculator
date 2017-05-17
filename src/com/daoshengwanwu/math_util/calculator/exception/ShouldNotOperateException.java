package com.daoshengwanwu.math_util.calculator.exception;


public class ShouldNotOperateException extends RuntimeException {
	public ShouldNotOperateException(String operatorStr) {
		super(operatorStr + "运算符不应该调用operate方法");
	}
}
