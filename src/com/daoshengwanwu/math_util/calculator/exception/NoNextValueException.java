package com.daoshengwanwu.math_util.calculator.exception;

public class NoNextValueException extends RuntimeException {
	public NoNextValueException(String varStr) {
		super(varStr + "变量已不存在可用的值");
	}
}
