package com.daoshengwanwu.launcher;


import com.daoshengwanwu.math_util.calculator.AriExp;
import com.daoshengwanwu.math_util.calculator.Calculator;
import com.daoshengwanwu.math_util.calculator.Calculator.ResultGenerator;
import com.daoshengwanwu.math_util.calculator.ExpItem.Operand;
import com.daoshengwanwu.math_util.calculator.ExpItem.Variable.VariableAssistant;


public class Launcher {
	public static void main(String[] args) {
		long startTime = -1;
		long endTime = -1;
		AriExp ariExp = null;
		double result = -1.0;
		
		String expStr = "";
		
		startTime = System.currentTimeMillis();
		ariExp = new AriExp(expStr);
		endTime = System.currentTimeMillis();
		
		System.out.println("解析表达式耗时：" + (endTime - startTime) + " 毫秒");
	
		
		startTime = System.currentTimeMillis();
		result = Calculator.calculate(ariExp);
		endTime = System.currentTimeMillis();
		
		System.out.println("计算表达式耗时：" + (endTime - startTime) + " 毫秒");
		
		
		System.out.println(expStr + " = " + result);
	}//main
}//class_Launcher
