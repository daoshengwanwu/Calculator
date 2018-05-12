package com.daoshengwanwu.launcher;


import com.daoshengwanwu.math_util.calculator.AriExp;
import com.daoshengwanwu.math_util.calculator.VarAriExp;
import com.daoshengwanwu.math_util.calculator.Calculator;
import com.daoshengwanwu.math_util.calculator.Variable;


public class Launcher {
    public static void main(String[] args) { //Calculator类使用示例
        Calculator calculator = new Calculator();

        //待计算的表达式字符串（不含有变量的纯数学表达式）
        String expStr = "(sin(pi / 2) - |5 - 10|)^2";

        //直接通过表达式字符串进行计算
        System.out.println(expStr + " = " + calculator.calculate(expStr));

        //先生成AriExp对象，之后计算, 该calculate方法的执行速度较快
        AriExp ariExp = new AriExp(expStr); //将表达式字符串封装为AriExp对象
        System.out.println(ariExp + " = " + calculator.calculate(ariExp));

        //接下来演示解析含有变量的表达式的方式
        //首先还是提供一个表达式字符串，其中x,y为变量名，当然变量名称可以使任意的，比如a,b,c...
        // 或者由多个字母组合而成的ab,ac,age,year等等，变量名由数字，字母，下划线组成，且不以数字打头
        // 例如，a1, a2, _1, _2 等等都是可以的
        //变量个数也没有限制，可以是任意个
        expStr = "x + y + 1";
        double rst = calculator.calculateCurrentValue(
                new VarAriExp(expStr)
                .setVariableValue("x", 10)
                .setVariableValue("y", 20));
        System.out.println(expStr + " = " + rst + " (x = " + 10 + ", y = " + 20 + ")");

        //如果变量需要频繁的赋值，则采用下面的方式
        expStr = "x^2 + y^2 + 2";
        VarAriExp varAriExp = new VarAriExp(expStr);
        Variable x = varAriExp.getVariable("x");
        Variable y = varAriExp.getVariable("y");
        x.setCurValue(10);
        y.setCurValue(20);
        rst = calculator.calculateCurrentValue(varAriExp);
        System.out.println(expStr + " = " + rst + " (x = " + 10 + ", y = " + 20 + ")");
    }
}//class_Launcher
