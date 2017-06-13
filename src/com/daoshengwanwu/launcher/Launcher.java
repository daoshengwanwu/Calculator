package com.daoshengwanwu.launcher;


import com.daoshengwanwu.math_util.calculator.AriExp;
import com.daoshengwanwu.math_util.calculator.Calculator;
import com.daoshengwanwu.math_util.calculator.Calculator.ResultGenerator;
import com.daoshengwanwu.math_util.calculator.ExpItem.Operand;
import com.daoshengwanwu.math_util.calculator.ExpItem.Variable.VariableAssistant;
import com.daoshengwanwu.math_util.calculator.VarAriExp;


public class Launcher {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        VariableAssistant varAssist = new VariableAssistant();
        varAssist.addVariable("x",
                Operand.getOperand(-10), false,
                Operand.getOperand(10), false,
                Operand.getOperand(0.1));

        String expStr = "lg(x^2 + 1)";
        VarAriExp varAriExp = new VarAriExp(expStr, varAssist);
        ResultGenerator resultGenerator = calculator.calculate(varAriExp);
        
        long startTime = System.currentTimeMillis();
        System.out.println(resultGenerator.getResultList());
        long endTime = System.currentTimeMillis();
        
        System.out.println("耗费时间：" + (endTime - startTime) + "毫秒");

        expStr = "1 + 2 * 3 / 6";
        AriExp ariExp = new AriExp(expStr);
        System.out.println(calculator.calculate(ariExp));


    }//main
}//class_Launcher
