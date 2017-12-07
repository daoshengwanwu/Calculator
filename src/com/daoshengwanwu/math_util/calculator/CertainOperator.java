package com.daoshengwanwu.math_util.calculator;


import com.daoshengwanwu.math_util.calculator.exception.OperandNumException;
import com.daoshengwanwu.math_util.calculator.exception.OperandOutOfBoundsException;
import com.daoshengwanwu.math_util.calculator.exception.ShouldNotOperateException;
import com.daoshengwanwu.math_util.calculator.exception.SpecDirPriorNotExistException;


abstract class CertainOperator extends Operator {
    private static final boolean IS_CERTAIN = true;


    private CertainOperator(String operatorStr) {
        super(operatorStr);
    }//con_CertainOperator

    @Override
    boolean isCertain() {
        return IS_CERTAIN;
    }//isCertain

    //检查实际传入的操作数个数和运算符要求的运算符个数是否相等
    void checkOperandNumCorrect(int actualOperandNum) {
        if (actualOperandNum != getDimension()) {
            throw new OperandNumException(getOperatorStr(), getDimension(), actualOperandNum);
        }//if
    }//checkOperandNumCorrect

    //获取运算符的左侧优先级
    abstract int getLeftDirPriority();

    //获取运算符的右侧优先级
    abstract int getRightDirPriority();

    //通过调用该方法来执行对应运算符的计算，将计算结果封装为一个Operand对象并返回
    abstract Operand operate(Operand[] operands);

    //获取运算符运算时需要的操作数个数
    abstract int getDimension();

    //获取运算符的类型
    abstract CertainOperatorType getCertainOperatorType();

    //通过该方法来判断运算符是否有左侧优先级
    abstract boolean isLeftDirPriorExist();

    //通过该方法来判断运算符是否有右侧优先级
    abstract boolean isRightDirPriorExist();

    //通过该方法判断对于本运算符是否需要进行运算
    abstract boolean isNeedOperate();

    //通过该方法获得本运算符的id
    abstract int getId();


    /*
     * 该枚举定义了Operator的几种类型
     */
    enum CertainOperatorType {
        NORMAL, OPEN, CLOSE
    }//enum_OperatorType


    /*
     * 普通运算符（所有类型确定并且不具有Open和Close特性的运算符均属于普通运算符）
     */
    static abstract class NormalOperator extends CertainOperator {
        private static final CertainOperatorType OPERATOR_TYPE = CertainOperatorType.NORMAL;
        private static final boolean IS_NEED_OPERATE = true;
        private static final int NORMAL_OPERATOR_ID = -1;


        private NormalOperator(String operatorStr) {
            super(operatorStr);
        }//con_NormalOperator

        @Override
        CertainOperatorType getCertainOperatorType() {
            return OPERATOR_TYPE;
        }//getOperatorType

        @Override
        boolean isNeedOperate() {
            return IS_NEED_OPERATE;
        }//isNeedOperate

        @Override
        int getId() {
            return NORMAL_OPERATOR_ID;
        }//普通运算符不需要判定id，所以普通运算符id置为-1
    }//class_NormalOperator

    /*
     * OPEN类型运算符的基类
     */
    static abstract class OpenOperator extends CertainOperator {
        private static final CertainOperatorType OPERATOR_TYPE = CertainOperatorType.OPEN;
        private static final boolean IS_NEED_OPERATE = false;
        private static final boolean IS_LEFT_DIR_PRIORITY_EXIST = false;
        private static final boolean IS_RIGHT_DIR_PRIORITY_EXIST = false;
        private static final int DIMENSION = 0;

        private int mId;


        private OpenOperator(String operatorStr, int id) {
            super(operatorStr);

            mId = id;
        }//con_OpenOperator

        @Override
        CertainOperatorType getCertainOperatorType() {
            return OPERATOR_TYPE;
        }//getOperatorType

        @Override
        int getId() {
            return mId;
        }//getId

        @Override
        boolean isNeedOperate() {
            return IS_NEED_OPERATE;
        }//isNeedOperate

        @Override
        boolean isLeftDirPriorExist() {
            return IS_LEFT_DIR_PRIORITY_EXIST;
        }//isLeftDirPriorExist

        @Override
        boolean isRightDirPriorExist() {
            return IS_RIGHT_DIR_PRIORITY_EXIST;
        }//isRightDirPriorExist

        @Override
        int getLeftDirPriority() {
            throw new SpecDirPriorNotExistException(getOperatorStr(), "左");
        }//getLeftDirPriority

        @Override
        int getRightDirPriority() {
            throw new SpecDirPriorNotExistException(getOperatorStr(), "右");
        }//getRightDirPriority

        @Override
        int getDimension() {
            return DIMENSION;
        }//getDimension

        @Override
        Operand operate(Operand[] operands) {
            throw new ShouldNotOperateException(getOperatorStr());
        }//operate
    }//class_OpenOperator

    /*
     * CLOSE类型的运算符的基类
     */
    static abstract class CloseOperator extends CertainOperator {
        private static final CertainOperatorType OPERATOR_TYPE = CertainOperatorType.CLOSE;
        private static final boolean IS_LEFT_DIR_PRIORITY_EXIST = false;
        private static final boolean IS_RIGHT_DIR_PRIORITY_EXIST = false;
        private static final boolean IS_NEED_PUSH = false;
        private static final boolean IS_NEED_OPERATE = false;
        private static final int DIMENSION = 0;

        private int mId;


        private CloseOperator(String operatorStr, int id) {
            super(operatorStr);

            mId = id;
        }//con_CloseOperator

        boolean isNeedPush() {
            return IS_NEED_PUSH;
        }//isNeedPush

        @Override
        CertainOperatorType getCertainOperatorType() {
            return OPERATOR_TYPE;
        }//getOperatorType

        @Override
        int getId() {
            return mId;
        }//getId

        @Override
        boolean isLeftDirPriorExist() {
            return IS_LEFT_DIR_PRIORITY_EXIST;
        }//isLeftDirPriorExist

        @Override
        int getLeftDirPriority() {
            throw new SpecDirPriorNotExistException(getOperatorStr(), "左");
        }//getLeftDirPriority

        @Override
        boolean isRightDirPriorExist() {
            return IS_RIGHT_DIR_PRIORITY_EXIST;
        }//isRightDirPriorExist

        @Override
        int getRightDirPriority() {
            throw new SpecDirPriorNotExistException(getOperatorStr(), "左");
        }//getRightDirPriority

        @Override
        boolean isNeedOperate() {
            return IS_NEED_OPERATE;
        }//isNeedOperate

        @Override
        int getDimension() {
            return DIMENSION;
        }//getDimension

        @Override
        Operand operate(Operand[] operands) {
            throw new ShouldNotOperateException(getOperatorStr());
        }//operate
    }//class_CloseOperator

    /*
     * 具有双侧优先级的运算符的基类
     */
    private static abstract class DoubleDirOperator extends NormalOperator {
        private static final boolean IS_LEFT_DIR_PRIOR_EXIST = true;
        private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = true;
        private static final int DIMENSION = 2;

        private final int mLeftDirPriority;
        private final int mRightDirPriority;


        private DoubleDirOperator(int leftPriority, int rightPriority, String operatorStr) {
            super(operatorStr);

            mLeftDirPriority = leftPriority;
            mRightDirPriority = rightPriority;
        }//con_DoubleDirOperator

        @Override
        boolean isLeftDirPriorExist() {
            return IS_LEFT_DIR_PRIOR_EXIST;
        }//isLeftPriorExist

        @Override
        boolean isRightDirPriorExist() {
            return IS_RIGHT_DIR_PRIOR_EXIST;
        }//isRightPriorExist

        @Override
        int getLeftDirPriority() {
            return mLeftDirPriority;
        }//getLeftDirPriority

        @Override
        int getRightDirPriority() {
            return mRightDirPriority;
        }//getRightDirPriority

        @Override
        int getDimension() {
            return DIMENSION;
        }//getDimension
    }//class_DoubleDirOperator

    /*
     * 具有左单侧优先级的运算符的基类
     */
    private static abstract class LeftSingleDirOperator extends NormalOperator {
        private static final boolean IS_LEFT_DIR_PRIOR_EXIST = true;
        private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = false;
        private static final int DIMENSION = 1;

        private final int mLeftDirPriority;


        private LeftSingleDirOperator(int leftDirPriority, String operatorStr) {
            super(operatorStr);

            mLeftDirPriority = leftDirPriority;
        }//con_LeftSingleDirOperator

        @Override
        boolean isLeftDirPriorExist() {
            return IS_LEFT_DIR_PRIOR_EXIST;
        }//isLeftPriorExist

        @Override
        boolean isRightDirPriorExist() {
            return IS_RIGHT_DIR_PRIOR_EXIST;
        }//isRightPriorExist

        @Override
        int getLeftDirPriority() {
            return mLeftDirPriority;
        }//getLeftDirPriority

        @Override
        int getDimension() {
            return DIMENSION;
        }//getDimension

        @Override
        int getRightDirPriority() {
            throw new SpecDirPriorNotExistException(getOperatorStr(), "右");
        }//getRightDirPriority
    }//class_LeftSingleDirOperator

    /*
     * 具有右单侧优先级的运算符的基类
     */
    private static abstract class RightSingleDirOperator extends NormalOperator {
        private static final boolean IS_LEFT_DIR_PRIOR_EXIST = false;
        private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = true;
        private static final int DIMENSION = 1;

        private final int mRightDirPriority;


        private RightSingleDirOperator(int rightDirPriority, String operatorStr) {
            super(operatorStr);

            mRightDirPriority = rightDirPriority;
        }//con_LeftSingleDirOperator

        @Override
        boolean isLeftDirPriorExist() {
            return IS_LEFT_DIR_PRIOR_EXIST;
        }//isLeftPriorExist

        @Override
        boolean isRightDirPriorExist() {
            return IS_RIGHT_DIR_PRIOR_EXIST;
        }//isRightPriorExist

        @Override
        int getRightDirPriority() {
            return mRightDirPriority;
        }//getLeftDirPriority

        @Override
        int getDimension() {
            return DIMENSION;
        }//getDimension

        @Override
        int getLeftDirPriority() {
            throw new SpecDirPriorNotExistException(getOperatorStr(), "左");
        }//getLeftDirPriority
    }//class_LeftSingleDirOperator

    /*
     * 加法运算符对应的类
     */
    static class Add extends DoubleDirOperator {
        Add(int leftPriority, int rightPriority, String operatorStr) {
            super(leftPriority, rightPriority, operatorStr);
        }//con_Add

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double leftOperand = operands[0].getValue();
            double rightOperand = operands[1].getValue();

            return Operand.getOperand(leftOperand + rightOperand);
        }//operate
    }//class_Add

    /*
     * 减法运算符对应的类
     */
    static class Sub extends DoubleDirOperator {
        Sub(int leftPriority, int rightPriority, String operatorStr) {
            super(leftPriority, rightPriority, operatorStr);
        }//con_Sub

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double leftOperand = operands[0].getValue();
            double rightOperand = operands[1].getValue();

            return Operand.getOperand(leftOperand - rightOperand);
        }//operate
    }//class_Sub

    /*
     * 乘法运算符对应的类
     */
    static class Mul extends DoubleDirOperator {
        Mul(int leftPriority, int rightPriority, String operatorStr) {
            super(leftPriority, rightPriority, operatorStr);
        }//con_Mul

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double leftOperand = operands[0].getValue();
            double rightOperand = operands[1].getValue();

            return Operand.getOperand(leftOperand * rightOperand);
        }//operate
    }//class_Mul

    /*
     * 除法运算符对应的类
     */
    static class Div extends DoubleDirOperator {
        Div(int leftPriority, int rightPriority, String operatorStr) {
            super(leftPriority, rightPriority, operatorStr);
        }//con_Div

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double leftOperand = operands[0].getValue();
            double rightOperand = operands[1].getValue();

            return Operand.getOperand(leftOperand / rightOperand);
        }//operate
    }//class_Div

    /*
     * 取余运算符对应的类
     */
    static class Mod extends DoubleDirOperator {
        Mod(int leftPriority, int rightPriority, String operatorStr) {
            super(leftPriority, rightPriority, operatorStr);
        }//con_Mod

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double leftOperand = operands[0].getValue();
            double rightOperand = operands[1].getValue();

            return Operand.getOperand(leftOperand % rightOperand);
        }//operate
    }//class_Mod

    /*
     * 次幂运算符对应的类
     */
    static class Pow extends DoubleDirOperator {
        Pow(int leftPriority, int rightPriority, String operatorStr) {
            super(leftPriority, rightPriority, operatorStr);
        }//con_Pow

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double leftOperand = operands[0].getValue();
            double rightOperand = operands[1].getValue();

            return Operand.getOperand(Math.pow(leftOperand, rightOperand));
        }//operate
    }//class_Pow

    /*
     * 取负运算符
     */
    static class Negate extends RightSingleDirOperator {
        Negate(int rightDirPriority, String operatorStr) {
            super(rightDirPriority, operatorStr);
        }//con_Negate

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double operand = operands[0].getValue();

            return Operand.getOperand(0 - operand);
        }//operate
    }//class_Negate

    /*
     * sin运算符
     */
    static class Sin extends RightSingleDirOperator {
        Sin(int rightDirPriority, String operatorStr) {
            super(rightDirPriority, operatorStr);
        }

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double operand = operands[0].getValue();

            return Operand.getOperand(Math.sin(operand));
        }//operate
    }//class_Sin

    /*
     * cos运算符
     */
    static class Cos extends RightSingleDirOperator {
        Cos(int rightDirPriority, String operatorStr) {
            super(rightDirPriority, operatorStr);
        }

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double operand = operands[0].getValue();

            return Operand.getOperand(Math.cos(operand));
        }//operate
    }//class_Cos

    /*
     * tan运算符
     */
    static class Tan extends RightSingleDirOperator {
        Tan(int rightDirPriority, String operatorStr) {
            super(rightDirPriority, operatorStr);
        }//con_Tan

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double operand = operands[0].getValue();

            return Operand.getOperand(Math.tan(operand));
        }//operate
    }//class_Tan

    /*
     * asin运算符
     */
    static class ASin extends RightSingleDirOperator {
        ASin(int rightDirPriority, String operatorStr) {
            super(rightDirPriority, operatorStr);
        }//con_ASin

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double operand = operands[0].getValue();
            if (operand < -1 || operand > 1) {
                throw new OperandOutOfBoundsException(this.getOperatorStr(), "[-1, 1]", operand);
            }//if

            return Operand.getOperand(Math.asin(operand));
        }//operate
    }//class_ASin

    /*
     * acos运算符
     */
    static class ACos extends RightSingleDirOperator {
        ACos(int rightDirPriority, String operatorStr) {
            super(rightDirPriority, operatorStr);
        }//con_ACos

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double operand = operands[0].getValue();
            if (operand < -1 || operand > 1) {
                throw new OperandOutOfBoundsException(this.getOperatorStr(), "[-1, 1]", operand);
            }//if

            return Operand.getOperand(Math.acos(operand));
        }//operate
    }//class_ACos

    /*
     * atan运算符
     */
    static class ATan extends RightSingleDirOperator {
        ATan(int rightDirPriority, String operatorStr) {
            super(rightDirPriority, operatorStr);
        }//con_ATan

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double operand = operands[0].getValue();
            if (operand < -1 || operand > 1) {
                throw new OperandOutOfBoundsException(this.getOperatorStr(), "[-1, 1]", operand);
            }//if

            return Operand.getOperand(Math.atan(operand));
        }//operate
    }//class_ATan

    /*
     * ln运算符
     */
    static class Ln extends RightSingleDirOperator {
        Ln(int rightDirPriority, String operatorStr) {
            super(rightDirPriority, operatorStr);
        }//con_Ln

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double operand = operands[0].getValue();
            if (operand <= 0) {
                throw new OperandOutOfBoundsException(getOperatorStr(), "(0, +∞)", operand);
            }//if

            return Operand.getOperand(Math.log1p(operand - 1)); //Math.log1p(x)返回值为：ln(1 + x);
        }//operate
    }//class_Ln

    /*
     * lg运算符
     */
    static class Lg extends RightSingleDirOperator {
        Lg(int rightDirPriority, String operatorStr) {
            super(rightDirPriority, operatorStr);
        }

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double operand = operands[0].getValue();
            if (operand <= 0) {
                throw new OperandOutOfBoundsException(getOperatorStr(), "(0, +∞)", operand);
            }//if

            return Operand.getOperand(Math.log10(operand));
        }//operate
    }//class_Lg

    /*
     * sqrt运算符
     */
    static class Sqrt extends RightSingleDirOperator {
        Sqrt(int rightDirPriority, String operatorStr) {
            super(rightDirPriority, operatorStr);
        }

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double operand = operands[0].getValue();
            if (operand < 0) {
                throw new OperandOutOfBoundsException(getOperatorStr(), "[0, +∞)", operand);
            }//if

            return Operand.getOperand(Math.sqrt(operand));
        }//operate
    }//class_Sqrt

    /*
     * fact运算符（阶乘）
     */
    static class Fact extends LeftSingleDirOperator {
        Fact(int leftDirPriority, String operatorStr) {
            super(leftDirPriority, operatorStr);
        }//con_Fact

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double operand = operands[0].getValue();

            return Operand.getOperand(fact(operand));
        }//operate

        private long fact(double operand) {
            if (operand < 0) {
                throw new OperandOutOfBoundsException(getOperatorStr(), "非负整数", operand);
            }//if

            if (!Operand.getOperand(operand - (int)operand).equals(Operand.getOperand(0))) {
                throw new OperandOutOfBoundsException(getOperatorStr(), "非负整数", operand);
            }//if

            long result = 1;
            int nOperand = (int)operand;
            for (int i = 2; i <= nOperand; i++) {
                result *= i;
            }//for

            return result;
        }//fact
    }//class_Fact

    /*
     * 左绝对值
     */
    static class LeftAbs extends OpenOperator {
        private static final int LOG_START_DIMENSION = 1;
        private static final boolean IS_NEED_OPERATE = true;


        LeftAbs(String operatorStr, int id) {
            super(operatorStr, id);
        }//con_LeftAbs

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double operand = operands[0].getValue();

            return Operand.getOperand(Math.abs(operand));
        }//operate

        @Override
        int getDimension() {
            return LOG_START_DIMENSION;
        }//getDimension

        @Override
        boolean isNeedOperate() {
            return IS_NEED_OPERATE;
        }//isNeedOperate
    }//LeftAbs

    /*
     * 左括号
     */
    static class LeftBrackets extends OpenOperator {
        LeftBrackets(String operatorStr, int id) {
            super(operatorStr, id);
        }//con_LeftBrackets
    }//LeftBrackets

    /*
     * log开始运算符（log）
     */
    static class LogStart extends OpenOperator {
        LogStart(String operatorStr, int id) {
            super(operatorStr, id);
        }//con_LogStart
    }//class_LogStart

    /*
     * 表达式开始标记
     */
    static class StartFlag extends OpenOperator {
        StartFlag(String operatorStr, int id) {
            super(operatorStr, id);
        }//con_StartFlag
    }//class_StartFlag

    /*
     * 右绝对值
     */
    static class RightAbs extends CloseOperator {
        RightAbs(String operatorStr, int id) {
            super(operatorStr, id);
        }//con_RightAbs
    }//class_RightAbs

    /*
     * 右括号
     */
    static class RightBrackets extends CloseOperator {
        RightBrackets(String operatorStr, int id) {
            super(operatorStr, id);
        }//con_RightBrackets
    }//RightBrackets

    /*
     * 表达式结束标记
     */
    static class EndFlag extends CloseOperator {
        EndFlag(String operatorStr, int id) {
            super(operatorStr, id);
        }//con_EndFlag
    }//class_EndFlag

    /*
     * log运算符结束运算符（~）
     */
    static class LogEnd extends CloseOperator {
        private static final int LOG_END_DIMENSION = 2;
        private static final boolean IS_NEED_PUSH = true;
        private static final boolean IS_NEED_OPERATE = true;
        private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = true;

        private final int mRightPrior;


        LogEnd(int rightPrior, String operatorStr, int id) {
            super(operatorStr, id);

            mRightPrior = rightPrior;
        }//con_LogEnd

        @Override
        Operand operate(Operand[] operands) {
            checkOperandNumCorrect(operands.length);

            double leftOperand = operands[0].getValue();
            double rightOperand =  operands[1].getValue();

            if (leftOperand <= 0 || leftOperand == 1) {
                throw new OperandOutOfBoundsException(getOperatorStr(), "底数应该大于0并且不等于1", leftOperand);
            }//if

            if (rightOperand <= 0) {
                throw new OperandOutOfBoundsException(getOperatorStr(), "logx~y: 中的y的取值应该大于0", rightOperand);
            }//if

            return Operand.getOperand(Math.log(rightOperand) / Math.log(leftOperand));
        }//operate

        @Override
        int getDimension() {
            return LOG_END_DIMENSION;
        }//getDimension

        @Override
        boolean isRightDirPriorExist() {
            return IS_RIGHT_DIR_PRIOR_EXIST;
        }//isRightDirPriorExist

        @Override
        boolean isNeedOperate() {
            return IS_NEED_OPERATE;
        }//isNeedOperate

        @Override
        boolean isNeedPush() {
            return IS_NEED_PUSH;
        }//isNeedPush

        @Override
        int getRightDirPriority() {
            return mRightPrior;
        }//getRightDirPriority
    }//class_LogEnd
}//class_CertainOperator
