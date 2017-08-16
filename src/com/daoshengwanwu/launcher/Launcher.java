package com.daoshengwanwu.launcher;


import com.daoshengwanwu.math_util.calculator.Calculator;
import com.daoshengwanwu.math_util.calculator.VarAriExp;


public class Launcher {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        String expStr = "log(log-5+10~25)~4";
        double result = calculator.calculate(expStr);

        System.out.println("result: " + result + ", \n" + (new VarAriExp(expStr, null)));
    }//main


}//class_Launcher
