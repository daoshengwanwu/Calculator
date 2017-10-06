package com.daoshengwanwu.launcher;


import com.daoshengwanwu.math_util.calculator.AriExp;
import com.daoshengwanwu.math_util.calculator.Calculator;
import com.daoshengwanwu.math_util.calculator.ExpItem.Operand;
import com.daoshengwanwu.math_util.calculator.ExpItem.Variable.VariableAssistant;
import com.daoshengwanwu.math_util.calculator.VarAriExp;


public class Launcher {
    public static void main(String[] args) { //Calculator类使用示例
        Calculator calculator = new Calculator();

        //待计算的表达式字符串（不含有变量的纯数学表达式）
        String expStr = "11^2 + 5.9^2";
        AriExp ariExp = new AriExp(expStr); //将表达式字符串封装为AriExp对象

        //直接通过表达式字符串进行计算
        System.out.println(expStr + " = " + calculator.calculate(expStr));

        //先生成AriExp对象，之后计算, 该calculate方法的执行速度较快
        System.out.println(ariExp + " = " + calculator.calculate(ariExp));


        //接下来演示解析含有变量的表达式的方式
        //首先还是提供一个表达式字符串，其中x,y为变量名，当然变量名称可以使任意的，比如a,b,c...
        // 或者由多个字母组合而成的ab,ac,age,year等等，变量名由数字，字母，下划线组成，且不以数字打头
        // 例如，a1, a2, _1, _2 等等都是可以的
        //变量个数也没有限制，可以是任意个
        expStr = "x ^ 2 + 2 * x + y ^ 2 + 2 * y + 2";

        //之后，你需要一个VariableAssistant对象
        VariableAssistant varAssist = new VariableAssistant();

        //通过addVariable方法，来添加你对变量的描述，如变量的标识符，取值下限和上限，以及是否是开区间，以及变量变化的跨度
        //比如这里我添加了一个对 'x'变量的描述，其取值下限是-10，其下限不是开区间（也就是闭区间)
        //其取值上限是10，且上限也是闭区间，该变量的变化跨度为0.01
        varAssist.addVariable("x", Operand.getOperand(-10), false,
                Operand.getOperand(10), false, Operand.getOperand(0.01))
                //同样的含义添加变量y的描述
                .addVariable("y", Operand.getOperand(-5), true,
                        Operand.getOperand(5), true, Operand.getOperand(0.1));

        //注意添加描述时，变量的标识符要与表达式字符串中的变量标识符保持一致，这样才可以解析出来

        VarAriExp varAriExp = new VarAriExp(expStr, varAssist);

        //calculator.calculate(expStr, varAssist); 这种方式也是可以的
        Calculator.ResultGenerator result = calculator.calculate(varAriExp);

        //我们来把所有结果输出出来
        System.out.println(result.curValue());
        while (result.hasNext()) {
            System.out.println(result.nextValue());
        }
    }
}//class_Launcher
