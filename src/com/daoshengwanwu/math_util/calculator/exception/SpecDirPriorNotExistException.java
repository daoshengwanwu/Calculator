package com.daoshengwanwu.math_util.calculator.exception;

public class SpecDirPriorNotExistException extends Exception {
	public SpecDirPriorNotExistException(String operatorStr, String directionStr) {
		super(operatorStr + "运算符不存在" + directionStr + "方向上的优先级");
	}//con_SpecDirPriorNotExistException
}//class_SpecDirPriorNotExistException
