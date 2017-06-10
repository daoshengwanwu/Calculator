package com.daoshengwanwu.math_util.calculator;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.daoshengwanwu.math_util.calculator.exception.IllegalIdentifierException;
import com.daoshengwanwu.math_util.calculator.exception.NoNextValueException;
import com.daoshengwanwu.math_util.calculator.exception.OperandNumException;
import com.daoshengwanwu.math_util.calculator.exception.OperandOutOfBoundsException;
import com.daoshengwanwu.math_util.calculator.exception.ShouldNotOperateException;
import com.daoshengwanwu.math_util.calculator.exception.SpecDirPriorNotExistException;
import com.daoshengwanwu.math_util.calculator.exception.VarIdentifierAlreadyExistException;
import com.daoshengwanwu.math_util.calculator.exception.VariableDomainErrorException;
import com.daoshengwanwu.math_util.calculator.exception.VariableNotExistException;
import com.daoshengwanwu.math_util.calculator.exception.VariableSpanNotSuitableException;
import com.daoshengwanwu.math_util.calculator.util.DigitUtil;


/*
 * 该类的对象为构成表达式的项，是构成AriExp以及VarAriExp的基本单位
 * 这里没有使用public修饰该类，因为其他包的类不需要引用
 * 到该类的对象
 */
abstract class ExpItem {
	//该项的类型，取值分别有：OPERATOR(运算符)、OPERAND(操作数)、VARIABLE(变量)
	private final ItemType mItemType;
	
	
	public ExpItem(ItemType itemType) {
		mItemType = itemType;
	}//con_ExpItem
	
	public ItemType getItemType() {
		return mItemType;
	}//getItemType
	
	@Override
	public String toString() {
		switch (mItemType) {
		case OPERATOR: return ((Operator)this).toString();
		case OPERAND: return ((Operand)this).toString();
		case VARIABLE: return ((Variable)this).toString();
		default: return "";
		}//switch-case
	}//toString
	
	
	/*
	 * 项的类型的枚举定义
	 */
	public static enum ItemType {
		OPERATOR, OPERAND, VARIABLE;
	}//enum_ItemType	
	
	
	/*
	 * 运算符类
	 */
	public static abstract class Operator extends ExpItem {		
		private static final ItemType OPERATOR_ITEM_TYPE = ItemType.OPERATOR;
		
		private final String mOperatorStr; //运算符的字符串描述
		
		
		//通过该静态方法，来获取operatorStr确定的Operator对象
		public static Operator getOperator(String operatorStr) throws IllegalIdentifierException {
			return OperatorAssistant.getOperator(operatorStr);
		}//getOperator
		
		public static boolean isIdentifierAlreadyExist(String identifierStr) {
			return OperatorAssistant.isIdentifierAlreadyExist(identifierStr);
		}//isIdentifierAlreadyExist
		
		public Operator(String operatorStr) {
			super(OPERATOR_ITEM_TYPE);
			
			mOperatorStr = operatorStr;
		}//con_Operator		
		
		//通过该方法来判断该运算符是否为类型确定的运算符
		public abstract boolean isCertain();
		
		//获取运算符的字符串描述
		public String getOperatorStr() {
			return mOperatorStr;
		}//getOperatorStr
		
		@Override
		public String toString() {
			return mOperatorStr;
		}//toString
		
		public static abstract class CertainOperator extends Operator {
			private static final boolean IS_CERTAIN = true;
			
			
			public CertainOperator(String operatorStr) {
				super(operatorStr);
			}

			//通过调用该方法来执行对应运算符的计算，将计算结果封装为一个Operand对象并返回
			public abstract Operand operate(Operand[] operands) throws OperandNumException;
			
			//获取运算符运算时需要的操作数个数
			public abstract int getDimension();
			
			//获取运算符的类型
			public abstract OperatorType getOperatorType();
			
			//通过该方法来判断运算符是否有左侧优先级
			public abstract boolean isLeftDirPriorExist();
			
			//通过该方法来判断运算符是否有右侧优先级
			public abstract boolean isRightDirPriorExist();
			
			//通过该方法判断对于本运算符是否需要进行运算
			public abstract boolean isNeedOperate();
			
			//通过该方法判断对于本运算符是否需要入栈
			public abstract boolean isNeedPush();		
			
			//通过该方法获得本运算符的id
			public abstract int getId();
			
			@Override
			public boolean isCertain() {
				return IS_CERTAIN;
			}//isCertain
			
			//获取运算符的左侧优先级，若不存在该侧优先级则抛出异常
			public int getLeftDirPriority() throws SpecDirPriorNotExistException {
				throw new SpecDirPriorNotExistException(getOperatorStr(), "左");
			}//getLeftDirPriority
			
			//获取运算符的右侧优先级，若不存在该侧优先级则抛出异常
			public int getRightDirPriority() throws SpecDirPriorNotExistException {
				throw new SpecDirPriorNotExistException(getOperatorStr(), "右");
			}//getRightDirPriority
			
			//检查实际传入的操作数个数和运算符要求的运算符个数是否相等
			protected void checkOperandNumCorrect(int actualOperandNum) throws OperandNumException {
				if (actualOperandNum != getDimension()) {
					throw new OperandNumException(getOperatorStr(), getDimension(), actualOperandNum);
				}//if
			}//checkOperandNumCorrect
			
			
			/*
			 * 该枚举定义了Operator的几种类型
			 */
			public static enum OperatorType {
				NORMAL, OPEN, CLOSE;
			}//enum_OperatorType
			
			
			/*
			 * 普通运算符（所有类型确定并且不具有Open和Close特性的运算符均属于普通运算符）
			 */
			private static abstract class NormalOperator extends CertainOperator {
				private static final OperatorType OPERATOR_TYPE = OperatorType.NORMAL;
				private static final boolean IS_NEED_PUSH = true;
				private static final boolean IS_NEED_OPERATE = true;
				private static final int NORMAL_OPERATOR_ID = -1;
				
				
				public NormalOperator(String operatorStr) {
					super(operatorStr);
				}//con_NormalOperator
				
				@Override
				public OperatorType getOperatorType() {
					return OPERATOR_TYPE;
				}//getOperatorType
				
				@Override
				public boolean isNeedPush() {
					return IS_NEED_PUSH;
				}//isNeedPush
				
				@Override
				public boolean isNeedOperate() {
					return IS_NEED_OPERATE;
				}//isNeedOperate
				
				@Override
				public int getId() {
					return NORMAL_OPERATOR_ID;
				}//普通运算符不需要判定id，所以普通运算符id置为-1
			}//class_NormalOperator
			
			/*
			 * OPEN类型运算符的基类
			 */
			private static abstract class OpenOperator extends CertainOperator {
				private static final OperatorType OPERATOR_TYPE = OperatorType.OPEN;
				private static final boolean IS_NEED_PUSH = true;
				
				private int mId;
				
				
				public OpenOperator(String operatorStr, int id) {
					super(operatorStr);
					
					mId = id;
				}//con_OpenOperator
				
				@Override
				public OperatorType getOperatorType() {
					return OPERATOR_TYPE;
				}//getOperatorType
				
				@Override
				public int getId() {
					return mId;
				}//getId
				
				@Override
				public boolean isNeedPush() {
					return IS_NEED_PUSH;
				}//isNeedPush
			}//class_OpenOperator
			
			/*
			 * CLOSE类型的运算符的基类
			 */
			private static abstract class CloseOperator extends CertainOperator {
				private static final OperatorType OPERATOR_TYPE = OperatorType.CLOSE;
				
				private int mId;
				
				
				public CloseOperator(String operatorStr, int id) {
					super(operatorStr);
					
					mId = id;
				}//con_CloseOperator
				
				@Override
				public OperatorType getOperatorType() {
					return OPERATOR_TYPE;
				}//getOperatorType
				
				@Override
				public int getId() {
					return mId;
				}//getId
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
				
				
				public DoubleDirOperator(int leftPriority, int rightPriority, String operatorStr) {
					super(operatorStr);
					
					mLeftDirPriority = leftPriority;
					mRightDirPriority = rightPriority;
				}//con_DoubleDirOperator
				
				@Override
				public boolean isLeftDirPriorExist() {
					return IS_LEFT_DIR_PRIOR_EXIST;
				}//isLeftPriorExist
				
				@Override
				public boolean isRightDirPriorExist() {
					return IS_RIGHT_DIR_PRIOR_EXIST;
				}//isRightPriorExist
				
				@Override
				public int getLeftDirPriority() {
					return mLeftDirPriority;
				}//getLeftDirPriority
				
				@Override
				public int getRightDirPriority() {
					return mRightDirPriority;
				}//getRightDirPriority
				
				@Override
				public int getDimension() {
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
				
				
				public LeftSingleDirOperator(int leftDirPriority, String operatorStr) {
					super(operatorStr);
					
					mLeftDirPriority = leftDirPriority;
				}//con_LeftSingleDirOperator
				
				@Override
				public boolean isLeftDirPriorExist() {
					return IS_LEFT_DIR_PRIOR_EXIST;
				}//isLeftPriorExist
				
				@Override
				public boolean isRightDirPriorExist() {
					return IS_RIGHT_DIR_PRIOR_EXIST;
				}//isRightPriorExist
				
				@Override
				public int getLeftDirPriority() {
					return mLeftDirPriority;
				}//getLeftDirPriority
				
				@Override
				public int getDimension() {
					return DIMENSION;
				}//getDimension
			}//class_LeftSingleDirOperator
			
			/*
			 * 具有右单侧优先级的运算符的基类
			 */
			private static abstract class RightSingleDirOperator extends NormalOperator {
				private static final boolean IS_LEFT_DIR_PRIOR_EXIST = false;
				private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = true;
				private static final int DIMENSION = 1;
				
				private final int mRightDirPriority;
				
				
				public RightSingleDirOperator(int rightDirPriority, String operatorStr) {
					super(operatorStr);
					
					mRightDirPriority = rightDirPriority;
				}//con_LeftSingleDirOperator
				
				@Override
				public boolean isLeftDirPriorExist() {
					return IS_LEFT_DIR_PRIOR_EXIST;
				}//isLeftPriorExist
				
				@Override
				public boolean isRightDirPriorExist() {
					return IS_RIGHT_DIR_PRIOR_EXIST;
				}//isRightPriorExist
				
				@Override
				public int getRightDirPriority() {
					return mRightDirPriority;
				}//getLeftDirPriority
				
				@Override
				public int getDimension() {
					return DIMENSION;
				}//getDimension
			}//class_LeftSingleDirOperator
			
			/*
			 * 加法运算符对应的类
			 */
			private static class Add extends DoubleDirOperator {
				public Add(int leftPriority, int rightPriority, String operatorStr) {
					super(leftPriority, rightPriority, operatorStr);
				}//con_Add

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double leftOperand = operands[0].getValue();
					double rightOperand = operands[1].getValue();
					
					return new Operand(leftOperand + rightOperand);
				}//operate
			}//class_Add
			
			/*
			 * 减法运算符对应的类
			 */
			private static class Sub extends DoubleDirOperator {
				public Sub(int leftPriority, int rightPriority, String operatorStr) {
					super(leftPriority, rightPriority, operatorStr);
				}//con_Sub        

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double leftOperand = operands[0].getValue();
					double rightOperand = operands[1].getValue();
					
					return new Operand(leftOperand - rightOperand);
				}//operate
			}//class_Sub
			
			/*
			 * 乘法运算符对应的类
			 */
			private static class Mul extends DoubleDirOperator {
				public Mul(int leftPriority, int rightPriority, String operatorStr) {
					super(leftPriority, rightPriority, operatorStr);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double leftOperand = operands[0].getValue();
					double rightOperand = operands[1].getValue();
					
					return new Operand(leftOperand * rightOperand);
				}
			}//class_Mul
			
			/*
			 * 除法运算符对应的类
			 */
			private static class Div extends DoubleDirOperator {
				public Div(int leftPriority, int rightPriority, String operatorStr) {
					super(leftPriority, rightPriority, operatorStr);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double leftOperand = operands[0].getValue();
					double rightOperand = operands[1].getValue();
					
					return new Operand(leftOperand / rightOperand);
				}
			}//class_Div
			
			/*
			 * 取余运算符对应的类
			 */
			private static class Mod extends DoubleDirOperator {
				public Mod(int leftPriority, int rightPriority, String operatorStr) {
					super(leftPriority, rightPriority, operatorStr);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double leftOperand = operands[0].getValue();
					double rightOperand = operands[1].getValue();
					
					return new Operand(leftOperand % rightOperand);
				}
			}//class_Mod
			
			/*
			 * 次幂运算符对应的类
			 */
			private static class Pow extends DoubleDirOperator {
				public Pow(int leftPriority, int rightPriority, String operatorStr) {
					super(leftPriority, rightPriority, operatorStr);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double leftOperand = operands[0].getValue();
					double rightOperand = operands[1].getValue();
					
					return new Operand(Math.pow(leftOperand, rightOperand));
				}
			}//class_Pow
			
			/*
			 * 取负运算符
			 */
			private static class Negate extends RightSingleDirOperator {
				public Negate(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					
					return new Operand(0 - operand);
				}
			}//class_Negate
			
			/*
			 * sin运算符
			 */
			private static class Sin extends RightSingleDirOperator {
				public Sin(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					
					return new Operand(Math.sin(operand));
				}
			}//class_Sin
			
			/*
			 * cos运算符
			 */
			private static class Cos extends RightSingleDirOperator {
				public Cos(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					
					return new Operand(Math.cos(operand));
				}
			}//class_Cos
			
			/*
			 * tan运算符
			 */
			private static class Tan extends RightSingleDirOperator {
				public Tan(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					
					return new Operand(Math.tan(operand));
				}
			}//class_Tan
			
			/*
			 * asin运算符
			 */
			private static class ASin extends RightSingleDirOperator {
				public ASin(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					if (operand < -1 || operand > 1) {
						throw new OperandOutOfBoundsException(this.getOperatorStr(), "[-1, 1]", operand);
					}//if
					
					return new Operand(Math.asin(operand));
				}
			}//class_ASin
			
			/*
			 * acos运算符
			 */
			private static class ACos extends RightSingleDirOperator {
				public ACos(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					if (operand < -1 || operand > 1) {
						throw new OperandOutOfBoundsException(this.getOperatorStr(), "[-1, 1]", operand);
					}//if
					
					return new Operand(Math.acos(operand));
				}
			}//class_ACos
			
			/*
			 * atan运算符
			 */
			private static class ATan extends RightSingleDirOperator {
				public ATan(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					if (operand < -1 || operand > 1) {
						throw new OperandOutOfBoundsException(this.getOperatorStr(), "[-1, 1]", operand);
					}//if
					
					return new Operand(Math.atan(operand));
				}	
			}//class_ATan
			
			/*
			 * ln运算符
			 */
			private static class Ln extends RightSingleDirOperator {
				public Ln(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					if (operand <= 0) {
						throw new OperandOutOfBoundsException(getOperatorStr(), "(0, +∞)", operand);
					}//if
					
					return new Operand(Math.log1p(operand - 1)); //Math.log1p(x)返回值为：ln(1 + x);
				}
			}//class_Ln
			
			/*
			 * lg运算符
			 */
			private static class Lg extends RightSingleDirOperator {
				public Lg(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					if (operand <= 0) {
						throw new OperandOutOfBoundsException(getOperatorStr(), "(0, +∞)", operand);
					}//if
					
					return new Operand(Math.log10(operand));
				}
			}//class_Lg
			
			/*
			 * sqrt运算符
			 */
			private static class Sqrt extends RightSingleDirOperator {
				public Sqrt(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					if (operand < 0) {
						throw new OperandOutOfBoundsException(getOperatorStr(), "[0, +∞)", operand);
					}//if
					
					return new Operand(Math.sqrt(operand));
				}
			}//class_Sqrt
			
			/*
			 * fact运算符（阶乘）
			 */
			private static class Fact extends LeftSingleDirOperator {
				public Fact(int leftDirPriority, String operatorStr) {
					super(leftDirPriority, operatorStr);
					// TODO Auto-generated constructor stub
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					
					return new Operand(fact(operand));
				}//operate
				
				private long fact(double operand) {
					if (operand < 0) {
						throw new OperandOutOfBoundsException(getOperatorStr(), "非负整数", operand);
					}//if
					
					if (!new Operand(operand - (int)operand).equals(new Operand(0))) {
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
			private static class LeftAbs extends OpenOperator {
				private static final int LOG_START_DIMENSION = 1;
				private static final boolean IS_NEED_PUSH = false;
				private static final boolean IS_NEED_OPERATE = true;
				private static final boolean IS_LEFT_DIR_PRIOR_EXIST = false;
				private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = false;
				
				
				public LeftAbs(String operatorStr, int id) {
					super(operatorStr, id);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					
					return new Operand(Math.abs(operand));
				}

				@Override
				public int getDimension() {
					return LOG_START_DIMENSION;
				}

				@Override
				public boolean isLeftDirPriorExist() {
					return IS_LEFT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isRightDirPriorExist() {
					return IS_RIGHT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isNeedOperate() {
					return IS_NEED_OPERATE;
				}

				@Override
				public boolean isNeedPush() {
					return IS_NEED_PUSH;
				}		
			}//LeftAbs
			
			/*
			 * 左括号
			 */
			private static class LeftBrackets extends OpenOperator {
				private static final int LEFT_BRACKETS_DIMENSION = -1;
				private static final boolean IS_NEED_PUSH = false;
				private static final boolean IS_NEED_OPERATE = false;
				private static final boolean IS_LEFT_DIR_PRIOR_EXIST = false;
				private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = false;
				
				
				public LeftBrackets(String operatorStr, int id) {
					super(operatorStr, id);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					throw new ShouldNotOperateException(getOperatorStr());
				}

				@Override
				public int getDimension() {
					return LEFT_BRACKETS_DIMENSION;
				}

				@Override
				public boolean isLeftDirPriorExist() {
					return IS_LEFT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isRightDirPriorExist() {
					return IS_RIGHT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isNeedOperate() {
					return IS_NEED_OPERATE;
				}

				@Override
				public boolean isNeedPush() {
					return IS_NEED_PUSH;
				}		
			}//LeftBrackets
			
			/*
			 * log开始运算符（log）
			 */
			private static class LogStart extends OpenOperator {
				private static final int LOG_START_DIMENSION = -1;
				private static final boolean IS_NEED_PUSH = false;
				private static final boolean IS_NEED_OPERATE = false;
				private static final boolean IS_LEFT_DIR_PRIOR_EXIST = false;
				private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = false;
				
				
				public LogStart(String operatorStr, int id) {
					super(operatorStr, id);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					throw new ShouldNotOperateException(getOperatorStr());
				}

				@Override
				public int getDimension() {
					return LOG_START_DIMENSION;
				}

				@Override
				public boolean isLeftDirPriorExist() {
					return IS_LEFT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isRightDirPriorExist() {
					return IS_RIGHT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isNeedOperate() {
					return IS_NEED_OPERATE;
				}

				@Override
				public boolean isNeedPush() {
					return IS_NEED_PUSH;
				}				
			}//LogStart
			
			/*
			 * 表达式开始标记
			 */
			private static class StartFlag extends OpenOperator {
				private static final int FLAG_END_DIMENSION = -1;
				private static final boolean IS_NEED_PUSH = false;
				private static final boolean IS_NEED_OPERATE = false;
				private static final boolean IS_LEFT_DIR_PRIOR_EXIST = false;
				private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = false;
				
				
				public StartFlag(String operatorStr, int id) {
					super(operatorStr, id);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					throw new ShouldNotOperateException(getOperatorStr());
				}

				@Override
				public int getDimension() {
					return FLAG_END_DIMENSION;
				}

				@Override
				public boolean isLeftDirPriorExist() {
					return IS_LEFT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isRightDirPriorExist() {
					return IS_RIGHT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isNeedOperate() {
					return IS_NEED_OPERATE;
				}

				@Override
				public boolean isNeedPush() {
					return IS_NEED_PUSH;
				}
			}
			
			/*
			 * 右绝对值
			 */
			private static class RightAbs extends CloseOperator {
				private static final int RIGHT_ABS_DIMENSION = -1;
				private static final boolean IS_NEED_PUSH = false;
				private static final boolean IS_NEED_OPERATE = false;
				private static final boolean IS_LEFT_DIR_PRIOR_EXIST = false;
				private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = false;
				
				
				public RightAbs(String operatorStr, int id) {
					super(operatorStr, id);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					throw new ShouldNotOperateException(getOperatorStr());
				}

				@Override
				public int getDimension() {
					return RIGHT_ABS_DIMENSION;
				}

				@Override
				public boolean isLeftDirPriorExist() {
					return IS_LEFT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isRightDirPriorExist() {
					return IS_RIGHT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isNeedOperate() {
					return IS_NEED_OPERATE;
				}

				@Override
				public boolean isNeedPush() {
					return IS_NEED_PUSH;
				}
			}//RightAbs
			
			/*
			 * 右括号
			 */
			private static class RightBrackets extends CloseOperator {
				private static final int RIGHT_BRACKETS_DIMENSION = -1;
				private static final boolean IS_NEED_PUSH = false;
				private static final boolean IS_NEED_OPERATE = false;
				private static final boolean IS_LEFT_DIR_PRIOR_EXIST = false;
				private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = false;
				
				
				public RightBrackets(String operatorStr, int id) {
					super(operatorStr, id);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					throw new ShouldNotOperateException(getOperatorStr());
				}

				@Override
				public int getDimension() {
					return RIGHT_BRACKETS_DIMENSION;
				}

				@Override
				public boolean isLeftDirPriorExist() {
					return IS_LEFT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isRightDirPriorExist() {
					return IS_RIGHT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isNeedOperate() {
					return IS_NEED_OPERATE;
				}

				@Override
				public boolean isNeedPush() {
					return IS_NEED_PUSH;
				}
			}//RightBrackets
			
			/*
			 * log运算符结束运算符（~）
			 */
			private static class LogEnd extends CloseOperator {
				private static final int LOG_END_DIMENSION = 2;
				private static final boolean IS_NEED_PUSH = true;
				private static final boolean IS_NEED_OPERATE = true;
				private static final boolean IS_LEFT_DIR_PRIOR_EXIST = false;
				private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = true;
				
				private final int mRightPrior;
				
				
				public LogEnd(int rightPrior, String operatorStr, int id) {
					super(operatorStr, id);
					
					mRightPrior = rightPrior;
				}//con_LogEnd

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					checkOperandNumCorrect(operands.length);
					
					double leftOperand = operands[0].getValue();
					double rightOperand =  operands[1].getValue();
					
					if (leftOperand <= 0 || leftOperand == 1) {
						throw new OperandOutOfBoundsException(getOperatorStr(), "底数应该大于0并且不等于1", leftOperand);
					}//if
					
					if (rightOperand <= 0) {
						throw new OperandOutOfBoundsException(getOperatorStr(), "logx~y: 中的y的取值应该大于0", rightOperand);
					}//if
					
					return new Operand(Math.log(rightOperand) / Math.log(leftOperand));
				}

				@Override
				public int getDimension() {
					return LOG_END_DIMENSION;
				}

				@Override
				public boolean isLeftDirPriorExist() {
					return IS_LEFT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isRightDirPriorExist() {
					return IS_RIGHT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isNeedOperate() {
					return IS_NEED_OPERATE;
				}

				@Override
				public boolean isNeedPush() {
					return IS_NEED_PUSH;
				}
				
				@Override
				public int getRightDirPriority() {
					return mRightPrior;
				}
			}//LogEnd
		
			/*
			 * 表达式结束标记
			 */
			private static class EndFlag extends CloseOperator {
				private static final int FLAG_END_DIMENSION = -1;
				private static final boolean IS_NEED_PUSH = false;
				private static final boolean IS_NEED_OPERATE = false;
				private static final boolean IS_LEFT_DIR_PRIOR_EXIST = false;
				private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = false;
				
				
				public EndFlag(String operatorStr, int id) {
					super(operatorStr, id);
				}

				@Override
				public Operand operate(Operand[] operands) throws OperandNumException {
					throw new ShouldNotOperateException(getOperatorStr());
				}

				@Override
				public int getDimension() {
					return FLAG_END_DIMENSION;
				}

				@Override
				public boolean isLeftDirPriorExist() {
					return IS_LEFT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isRightDirPriorExist() {
					return IS_RIGHT_DIR_PRIOR_EXIST;
				}

				@Override
				public boolean isNeedOperate() {
					return IS_NEED_OPERATE;
				}

				@Override
				public boolean isNeedPush() {
					return IS_NEED_PUSH;
				}
			}
		}//class_CertainOperator
		
		public static abstract class UncertainOperator extends Operator {
			private static final boolean IS_CERTAIN = false;
			
			
			public UncertainOperator(String operatorStr) {
				super(operatorStr);
			}//con_UncertainOperator
			
			//通过该方法来确认出该未确定类型运算符的具体类型
			public abstract Operator getCertainOperator(ExpItem preItem) throws IllegalIdentifierException;

			@Override
			public boolean isCertain() {
				return IS_CERTAIN;
			}//isCertain
			
			
			private static class Hyphen extends UncertainOperator {
				public Hyphen(String operatorStr) {
					super(operatorStr);
				}//con_Hyphen

				@Override
				public Operator getCertainOperator(ExpItem preItem) throws IllegalIdentifierException {
					ItemType itemType = preItem.getItemType();
					if (itemType == ItemType.OPERAND || itemType == ItemType.VARIABLE) {
						//如果'-'前边的项是一个操作数或者变量，那么这个'-'的含义必然为减号
						return OperatorAssistant.getOperator(OperatorAssistant.SUB);
					}//if
					
					CertainOperator operator = (CertainOperator)preItem;
					if (!operator.isRightDirPriorExist()) {
						//如果之前的项是不拥有右方向优先级的运算符，那么这个'-'的含义必然为减号
						return OperatorAssistant.getOperator(OperatorAssistant.SUB);
					}//if
					
					return OperatorAssistant.getOperator(OperatorAssistant.NEGATE);
				}//getCertainOperator
			}//class_Hyphen
			
			private static class VerticalLine extends UncertainOperator {
				public VerticalLine(String operatorStr) {
					super(operatorStr);
					
				}//con_VerticalLine

				@Override
				public Operator getCertainOperator(ExpItem preItem) throws IllegalIdentifierException {
					ItemType itemType = preItem.getItemType();
					if (itemType == ItemType.OPERAND || itemType == ItemType.VARIABLE) {
						return OperatorAssistant.getOperator(OperatorAssistant.RIGHT_ABS);
					}//if
					
					CertainOperator operator = (CertainOperator)preItem;
					if (!operator.isRightDirPriorExist()) {
						return OperatorAssistant.getOperator(OperatorAssistant.RIGHT_ABS);
					}//if
					
					return OperatorAssistant.getOperator(OperatorAssistant.LEFT_ABS);
				}//getCertainOperator
			}//class_VerticalLine
		}//class_UncertainOperator
		
		
		/*
		 * 运算符助手静态内部类
		 * 可以根据运算符的字符串描述返回对应的Operator对象, 并且这个Operator在应用的整个运行过程中，只会存在一个实例
		 */
		public static class OperatorAssistant {
			//定义成对存在的运算符的id
			private static final int ABS_ID = 0;
			private static final int FLAG_ID = 1;
			private static final int BRACKETS_ID = 2;
			private static final int LOG_ID = 3;
			
			//定义运算符的标识描述			
			//==================类型确定运算符=========================
			//==================Normal运算符=========================
			//====================双目运算符==========================
			private static final int ADD = 0;
			private static final int SUB = 1;
			private static final int MUL = 2;
			private static final int DIV = 3;
			private static final int MOD = 4;
			private static final int POW = 5;
			
			//===================单目运算符===========================
			//==================右单目运算符===========================
			private static final int NEGATE = 128;
			private static final int SIN = 129;
			private static final int COS = 130;
			private static final int TAN = 131;
			private static final int ASIN = 132;
			private static final int ACOS = 133;
			private static final int ATAN = 134;
			private static final int LN = 135;
			private static final int LG = 136;
			private static final int SQRT = 137;
			
			//====================左单目运算符=========================
			private static final int FACT = 256;
			
			//====================Open运算符=========================
			private static final int LEFT_ABS = 384;
			private static final int LEFT_BRACKETS = 385;
			private static final int START_FLAG = 386;
			private static final int LOG_START = 387;
			
			//===================Close运算符=========================
			private static final int RIGHT_ABS = 512;
			private static final int RIGHT_BRACKETS = 513;
			private static final int END_FLAG = 514;
			private static final int LOG_END = 515;
			
			//==================类型未确定运算符========================
			private static final int HYPHEN_UNCERTAIN = 640;
			private static final int VERTICAL_LINE_UNCERTAIN = 641;
			
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
			
			
			private static Operator newOperator(int operatorFlag) throws IllegalIdentifierException {
				Operator operator = null;
				
				//--------------在这个switch里添加运算符的字符串描述与实际运算符类的对应--------------------
				switch (operatorFlag) {
				case START_FLAG: operator = new CertainOperator.StartFlag("start_flag", FLAG_ID);
				case END_FLAG: operator = new CertainOperator.EndFlag("end_flag", FLAG_ID);
				case LEFT_BRACKETS: operator = new CertainOperator.LeftBrackets("(", BRACKETS_ID);
				case RIGHT_BRACKETS: operator = new CertainOperator.RightBrackets(")", BRACKETS_ID);
				case LEFT_ABS: operator = new CertainOperator.LeftAbs("|", ABS_ID);
				case RIGHT_ABS: operator = new CertainOperator.RightAbs("|", ABS_ID);
				case LOG_START: operator = new CertainOperator.LogStart("log", LOG_ID);
				case LOG_END: operator = new CertainOperator.LogEnd(00000000, "~", LOG_ID);
				case ADD: operator = new CertainOperator.Add(1, 1, "+"); break;
				case SUB: operator = new CertainOperator.Sub(1, 1, "-"); break;
				case MUL: operator = new CertainOperator.Mul(2, 2, "*"); break;
				case DIV: operator = new CertainOperator.Div(2, 2, "/"); break;
				case MOD: operator = new CertainOperator.Mod(2, 2, "%"); break;
				case POW: operator = new CertainOperator.Pow(8, 8, "^"); break;
				case HYPHEN_UNCERTAIN: operator = new UncertainOperator.Hyphen("-");
				case VERTICAL_LINE_UNCERTAIN: operator = new UncertainOperator.VerticalLine("|");
				default: break;
				}//switch
				
				return operator;
			}//newOperator
			
			public static Operator getOperator(String operatorStr) throws IllegalIdentifierException {
				int operatorFlag = sStrFlagMap.get(operatorStr);
				
				return getOperator(operatorFlag);
			}//getOperator
			
			public static Operator getOperator(int operatorFlag) throws IllegalIdentifierException {
				Operator operator = sFlagOperatorMap.get(operatorFlag);
				
				if (null == operator) {
					operator = newOperator(operatorFlag);
					sFlagOperatorMap.put(operatorFlag, operator);
				}//if

				return operator;
			}//getOperator
			
			public static Set<String> getOperatorIdentifiers() {
				return sStrFlagMap.keySet();
			}//getOperatorIdentifiers
			
			public static boolean isIdentifierAlreadyExist(String identifierStr) {
				return sStrFlagMap.containsKey(identifierStr);
			}//isIdentifierAlreadyExist
		}//class_OperatorAssistant
	}//class_Operator
	
	/*
	 * 操作数类
	 */
	public static class Operand extends ExpItem {	
		//操作数的有效位数
		private static final int SIGNIFICANCE_DIGIT = 15;
		
		//操作数的值
		private double mValue;
		
		
		public static Operand getOperand(String operandStr) {
			return new Operand(Double.parseDouble(operandStr));
		}//getOperand
		
		public static Operand getOperand(double operandValue) {
			return new Operand(operandValue);
		}//getOperand
		
		public Operand(double value) {
			super(ItemType.OPERAND);
			
			mValue = DigitUtil.reserveSignificantDigits(value, SIGNIFICANCE_DIGIT);
		}//con_Operand
		
		public void setValue(double value) {
			mValue = DigitUtil.reserveSignificantDigits(value, SIGNIFICANCE_DIGIT);
		}//setValue
		
		public double getValue() {
			return mValue;
		}//getValue
		
		@Override
		public String toString() {
			return String.valueOf(mValue);
		}//toString
	}//class_Operand
	
	/*
	 * 变量类
	 */
	public static class Variable extends ExpItem {
		private String mFlagStr; //变量的字符串标识，只能是
		private double mUpperLimit;
		private double mLowerLimit;
		private double mSpan;
		private double mCurValue;
		
		
		public static boolean isIdentifierAlreadyExistInOperator(String identifierStr) {			
			return Operator.OperatorAssistant.getOperatorIdentifiers().contains(identifierStr);
		}
		
		//私有构造函数防止直接实例化
		private Variable(String flagStr, Operand lowerLimit, boolean isLowerOpen
				, Operand upperLimit, boolean isUpperOpen, Operand span) {
			super(ItemType.VARIABLE);
			
			//变量的字符串标识
			mFlagStr = flagStr;
			
			//判断变量的上下限是否合法
			if (lowerLimit.getValue() > upperLimit.getValue()) {
				throw new VariableDomainErrorException();
			}
			
			//变量的最小跨度，以上限为标准
			Operand minimuxSpan = new Operand(DigitUtil.getMinimumSpan(upperLimit.getValue(), Operand.SIGNIFICANCE_DIGIT));
			if (span.getValue() < minimuxSpan.getValue()) {
				span = minimuxSpan;
			}
			//变量的跨度
			mSpan = span.getValue();

			if (isLowerOpen) {
				lowerLimit = new Operand(lowerLimit.getValue() + mSpan);
			} 
			if (isUpperOpen) {
				upperLimit = new Operand(upperLimit.getValue() - mSpan);
			}
			
			if (lowerLimit.getValue() > upperLimit.getValue()) {
				throw new VariableSpanNotSuitableException();
			}
			
			mUpperLimit = upperLimit.getValue();
			mLowerLimit = lowerLimit.getValue();
			
			mCurValue = mLowerLimit;
		}//con_Variable
		
		public boolean hasNext() {
			if (mCurValue <= mUpperLimit) {
				return true;
			}
			
			return false;
		}//hasNext
		
		public double nextValue() {
			if (mCurValue <= mUpperLimit) {
				mCurValue += mSpan;
				return mCurValue;
			}
			
			throw new NoNextValueException(mFlagStr);
		}//nextValue
		
		public double curValue() {
			return mCurValue;
		}//curValue
		
		@Override
		public String toString() {
			return mFlagStr;
		}//toString
		
		
		public class VariableAssistant {
			private Map<String, Variable> mVariablesMap = new HashMap<>();
			
			
			public VariableAssistant addVariable(String flagStr, Operand lowerLimit, boolean isLowerOpen
				, Operand upperLimit, boolean isUpperOpen, Operand span, VariableAssistant variableAssistant) {
				if (mVariablesMap.containsKey(flagStr)
						|| Operator.OperatorAssistant.isIdentifierAlreadyExist(flagStr)) {
					throw new VarIdentifierAlreadyExistException();
				}//if
				
				mVariablesMap.put(flagStr, new Variable(
						flagStr,
						lowerLimit, isLowerOpen,
						upperLimit, isUpperOpen,
						span));
				
				return this;
			}//addVariable
			
			public Variable getVariable(String flagStr) {
				if (hasVariable(flagStr)) {
					return mVariablesMap.get(flagStr);
				}//if
				
				throw new VariableNotExistException(flagStr);
			}//getVariable
			
			public VariableAssistant removeVariable(String flagStr) {
				mVariablesMap.remove(flagStr);
				return this;
			}//removeVariable
			
			public boolean hasVariable(String flagStr) {
				return mVariablesMap.containsKey(flagStr);
			}//hasVariable
		}//class_VariableAssistant
	}//class_Variable
}//class_ExpItem
