package com.daoshengwanwu.math_util.calculator;


import com.daoshengwanwu.math_util.calculator.exception.OperatorNotExistException;

import java.util.HashMap;
import java.util.Map;


/*
 * 运算符助手
 * 可以根据运算符的字符串描述返回对应的Operator对象, 并且这个Operator在应用的整个运行过程中，只会存在一个实例
 */
class OperatorAssistant {
    //定义成对存在的运算符的id
    static final int ABS_ID = 0;
    static final int FLAG_ID = 1;
    static final int BRACKETS_ID = 2;
    static final int LOG_ID = 3;

    //定义运算符的标识描述
    //==================类型确定运算符=========================
    //==================Normal运算符=========================
    //====================双目运算符==========================
    static final int ADD = 0;
    static final int SUB = 1;
    static final int MUL = 2;
    static final int DIV = 3;
    static final int MOD = 4;
    static final int POW = 5;

    //===================单目运算符===========================
    //==================右单目运算符===========================
    static final int NEGATE = 128;
    static final int SIN = 129;
    static final int COS = 130;
    static final int TAN = 131;
    static final int ASIN = 132;
    static final int ACOS = 133;
    static final int ATAN = 134;
    static final int LN = 135;
    static final int LG = 136;
    static final int SQRT = 137;

    //====================左单目运算符=========================
    static final int FACT = 256;

    //====================Open运算符=========================
    static final int LEFT_ABS = 384;
    static final int LEFT_BRACKETS = 385;
    static final int START_FLAG = 386;
    static final int LOG_START = 387;

    //===================Close运算符=========================
    static final int RIGHT_ABS = 512;
    static final int RIGHT_BRACKETS = 513;
    static final int END_FLAG = 514;
    static final int LOG_END = 515;

    //==================类型未确定运算符========================
    static final int HYPHEN_UNCERTAIN = 640;
    static final int VERTICAL_LINE_UNCERTAIN = 641;

    private static Map<String, Integer> sStrFlagMap = new HashMap<>();
    private static Map<Integer, Operator> sFlagOperatorMap = new HashMap<>();


    static {
        //在此初始化运算符的字符串描述与标识描述的映射
        sStrFlagMap.put("+", ADD);
        sStrFlagMap.put("-", HYPHEN_UNCERTAIN); //'-'可能表示减号意义也可能表示负号意义
        sStrFlagMap.put("*", MUL);
        sStrFlagMap.put("/", DIV);
        sStrFlagMap.put("%", MOD);
        sStrFlagMap.put("^", POW);
        sStrFlagMap.put("|", VERTICAL_LINE_UNCERTAIN); //'|'可能表示做绝对值负号，也可能表示又绝对值负号
        sStrFlagMap.put("(", LEFT_BRACKETS);
        sStrFlagMap.put(")", RIGHT_BRACKETS);
        sStrFlagMap.put("sin", SIN);
        sStrFlagMap.put("cos", COS);
        sStrFlagMap.put("tan", TAN);
        sStrFlagMap.put("asin", ASIN);
        sStrFlagMap.put("acos", ACOS);
        sStrFlagMap.put("atan", ATAN);
        sStrFlagMap.put("log", LOG_START);
        sStrFlagMap.put("~", LOG_END);
        sStrFlagMap.put("lg", LG);
        sStrFlagMap.put("ln", LN);
        sStrFlagMap.put("sqrt", SQRT);
        sStrFlagMap.put("!", FACT);
    }//static


    static Operator getStartFlag() {
        return getOperator(START_FLAG);
    }//getStartFlag

    static Operator getEndFlag() {
        return getOperator(END_FLAG);
    }//getEndFlag

    static Operator getOperator(String operatorStr) {
        if (!sStrFlagMap.containsKey(operatorStr)) {
            throw new OperatorNotExistException(operatorStr);
        }//if

        int operatorFlag = sStrFlagMap.get(operatorStr);

        return getOperator(operatorFlag);
    }//getOperator

    static Operator getOperator(int operatorFlag) {
        Operator operator = sFlagOperatorMap.get(operatorFlag);
        if (null == operator) {
            operator = newOperator(operatorFlag);
            sFlagOperatorMap.put(operatorFlag, operator);
        }
        return operator;
    }//getOperator

    static boolean isIdentifierAlreadyExist(String identifierStr) {
        return sStrFlagMap.containsKey(identifierStr);
    }//isIdentifierAlreadyExist

    private static Operator newOperator(int operatorFlag) {
        Operator operator = null;

        //--------------在这个switch里添加运算符的字符串描述与实际运算符类的对应--------------------
        switch (operatorFlag) {
            case START_FLAG: operator = new CertainOperator.StartFlag("start_flag", FLAG_ID); break;
            case END_FLAG: operator = new CertainOperator.EndFlag("end_flag", FLAG_ID); break;
            case LEFT_BRACKETS: operator = new CertainOperator.LeftBrackets("(", BRACKETS_ID); break;
            case RIGHT_BRACKETS: operator = new CertainOperator.RightBrackets(")", BRACKETS_ID); break;
            case LEFT_ABS: operator = new CertainOperator.LeftAbs("|", ABS_ID); break;
            case RIGHT_ABS: operator = new CertainOperator.RightAbs("|", ABS_ID); break;
            case LOG_START: operator = new CertainOperator.LogStart("log", LOG_ID); break;
            case LOG_END: operator = new CertainOperator.LogEnd(200, "~", LOG_ID); break;
            case ADD: operator = new CertainOperator.Add(0, 0, "+"); break;
            case SUB: operator = new CertainOperator.Sub(0, 0, "-"); break;
            case MUL: operator = new CertainOperator.Mul(100, 100, "*"); break;
            case DIV: operator = new CertainOperator.Div(100, 100, "/"); break;
            case MOD: operator = new CertainOperator.Mod(100, 100, "%"); break;
            case POW: operator = new CertainOperator.Pow(300, 300, "^"); break;
            case NEGATE: operator = new CertainOperator.Negate(200, "-"); break;
            case SIN: operator = new CertainOperator.Sin(200, "sin"); break;
            case COS: operator = new CertainOperator.Cos(200, "cos"); break;
            case TAN: operator = new CertainOperator.Tan(200, "tan"); break;
            case ASIN: operator = new CertainOperator.ASin(200, "asin"); break;
            case ACOS: operator = new CertainOperator.ACos(200, "acos"); break;
            case ATAN: operator = new CertainOperator.ATan(200, "atan"); break;
            case LN: operator = new CertainOperator.Ln(200, "ln"); break;
            case LG: operator = new CertainOperator.Lg(200, "lg"); break;
            case SQRT: operator = new CertainOperator.Sqrt(200, "sqrt"); break;
            case FACT: operator = new CertainOperator.Fact(400, "!"); break;
            case HYPHEN_UNCERTAIN: operator = new UncertainOperator.Hyphen("-"); break;
            case VERTICAL_LINE_UNCERTAIN: operator = new UncertainOperator.VerticalLine("|"); break;
            default: break;
        }//switch

        return operator;
    }//newOperator
}//class_OperatorAssistant
