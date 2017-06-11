package com.daoshengwanwu.launcher;


import com.daoshengwanwu.math_util.calculator.Calculator;


public class Launcher {
	public static void main(String[] args) {
		String ariExp = "log(2)~ln(e *sin|- - -|-log(e)~e^2-10| -1 - -13 - pi / 2|)^2";
		double result = Calculator.calculate(ariExp);
		System.out.println(result);
	}//main
}//class_Launcher
