package com.daoshengwanwu.launcher;


import com.daoshengwanwu.math_util.calculator.Calculator;
import com.daoshengwanwu.math_util.calculator.Calculator.ResultGenerator;
import com.daoshengwanwu.math_util.calculator.ExpItem.Operand;
import com.daoshengwanwu.math_util.calculator.ExpItem.Variable.VariableAssistant;


public class Launcher {
	public static void main(String[] args) {
		String ariExp = "sin(x)";
		VariableAssistant varAssist = new VariableAssistant();
		varAssist.addVariable("x", Operand.getOperand(-Math.PI), false,
				Operand.getOperand(Math.PI), false,
				Operand.getOperand(0.01));
		ResultGenerator result = Calculator.calculate(ariExp, varAssist);
		
		System.out.println(result.curValue());
		while (result.hasNext()) {
			System.out.println(result.nextValue());
		}
	}//main
}//class_Launcher
