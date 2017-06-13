package com.daoshengwanwu.math_util.calculator;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.daoshengwanwu.math_util.calculator.ExpItem.Operator.CertainOperator.CertainOperatorType;
import com.daoshengwanwu.math_util.calculator.exception.ConstantNotExistException;
import com.daoshengwanwu.math_util.calculator.exception.NoNextValueException;
import com.daoshengwanwu.math_util.calculator.exception.OperandNumException;
import com.daoshengwanwu.math_util.calculator.exception.OperandOutOfBoundsException;
import com.daoshengwanwu.math_util.calculator.exception.OperatorNotExistException;
import com.daoshengwanwu.math_util.calculator.exception.ShouldNotOperateException;
import com.daoshengwanwu.math_util.calculator.exception.SpecDirPriorNotExistException;
import com.daoshengwanwu.math_util.calculator.exception.VarAssistHasNoNextValueException;
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
public abstract class ExpItem {
	//该项的类型，取值分别有：OPERATOR(运算符)、OPERAND(操作数)、VARIABLE(变量)
	private final ItemType mItemType;
	
	
	private ExpItem(ItemType itemType) {
		mItemType = itemType;
	}//con_ExpItem
	
	ItemType getItemType() {
		return mItemType;
	}//getItemType
	
	
	/*
	 * 项的类型的枚举定义
	 */
	public enum ItemType {
		OPERATOR, OPERAND, VARIABLE
	}//enum_ItemType	
	
	
	/*
	 * 运算符类
	 */
	static abstract class Operator extends ExpItem {
		private static final ItemType OPERATOR_ITEM_TYPE = ItemType.OPERATOR;
		
		private final String mOperatorStr; //运算符的字符串描述
		
		
		//通过该静态方法，来获取operatorStr确定的Operator对象
		static Operator getOperator(String operatorStr) {
			return OperatorAssistant.getOperator(operatorStr);
		}//getOperator
		
		//判断 identifierStr 是否已经被定义为运算符
		static boolean isIdentifierAlreadyExist(String identifierStr) {
			return OperatorAssistant.isIdentifierAlreadyExist(identifierStr);
		}//isIdentifierAlreadyExist
		
		//获取表达式的开始标识运算符
		static Operator getStartFlag() {
			return OperatorAssistant.getOperator(OperatorAssistant.START_FLAG);
		}//getStartFlag
		
		//获取表达式结束标识运算符
		static Operator getEndFlag() {
			return OperatorAssistant.getOperator(OperatorAssistant.END_FLAG);
		}//getEndFlag
		
		private Operator(String operatorStr) {
			super(OPERATOR_ITEM_TYPE);
			
			mOperatorStr = operatorStr;
		}//con_Operator		
		
		//获取运算符的字符串描述
		String getOperatorStr() {
			return mOperatorStr;
		}//getOperatorStr
		
		@Override
		public String toString() {
			return mOperatorStr;
		}//toString
		
		//通过该方法来判断该运算符是否为类型确定的运算符
		abstract boolean isCertain();
		
		
		static abstract class CertainOperator extends Operator {
			private static final boolean IS_CERTAIN = true;


			private CertainOperator(String operatorStr) {
				super(operatorStr);
			}//con_CertainOperator
			
			@Override
			boolean isCertain() {
				return IS_CERTAIN;
			}//isCertain
			
			//获取运算符的左侧优先级，若不存在该侧优先级则抛出异常
			int getLeftDirPriority() {
				throw new SpecDirPriorNotExistException(getOperatorStr(), "左");
			}//getLeftDirPriority
			
			//获取运算符的右侧优先级，若不存在该侧优先级则抛出异常
			int getRightDirPriority() {
				throw new SpecDirPriorNotExistException(getOperatorStr(), "右");
			}//getRightDirPriority
			
			//检查实际传入的操作数个数和运算符要求的运算符个数是否相等
			void checkOperandNumCorrect(int actualOperandNum) {
				if (actualOperandNum != getDimension()) {
					throw new OperandNumException(getOperatorStr(), getDimension(), actualOperandNum);
				}//if
			}//checkOperandNumCorrect

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
			
			//通过该方法判断对于本运算符是否需要入栈
			abstract boolean isNeedPush();
			
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
				private static final boolean IS_NEED_PUSH = true;
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
				boolean isNeedPush() {
					return IS_NEED_PUSH;
				}//isNeedPush
				
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
				private static final boolean IS_NEED_PUSH = true;
				private static final boolean IS_LEFT_DIR_PRIORITY_EXIST = false;
				private static final boolean IS_RIGHT_DIR_PRIORITY_EXIST = false;
				
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
				boolean isNeedPush() {
					return IS_NEED_PUSH;
				}//isNeedPush
				
				@Override
				boolean isLeftDirPriorExist() {
					return IS_LEFT_DIR_PRIORITY_EXIST;
				}//isLeftDirPriorExist
				
				@Override
				boolean isRightDirPriorExist() {
					return IS_RIGHT_DIR_PRIORITY_EXIST;
				}//isRightDirPriorExist
			}//class_OpenOperator
			
			/*
			 * CLOSE类型的运算符的基类
			 */
			static abstract class CloseOperator extends CertainOperator {
				private static final CertainOperatorType OPERATOR_TYPE = CertainOperatorType.CLOSE;
				private static final boolean IS_LEFT_DIR_PRIORITY_EXIST = false;
				
				private int mId;
				
				
				private CloseOperator(String operatorStr, int id) {
					super(operatorStr);
					
					mId = id;
				}//con_CloseOperator
				
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
			}//class_LeftSingleDirOperator
			
			/*
			 * 加法运算符对应的类
			 */
			private static class Add extends DoubleDirOperator {
				private Add(int leftPriority, int rightPriority, String operatorStr) {
					super(leftPriority, rightPriority, operatorStr);
				}//con_Add

				@Override
				Operand operate(Operand[] operands) {
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
				private Sub(int leftPriority, int rightPriority, String operatorStr) {
					super(leftPriority, rightPriority, operatorStr);
				}//con_Sub        

				@Override
				Operand operate(Operand[] operands) {
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
				private Mul(int leftPriority, int rightPriority, String operatorStr) {
					super(leftPriority, rightPriority, operatorStr);
				}//con_Mul

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double leftOperand = operands[0].getValue();
					double rightOperand = operands[1].getValue();
					
					return new Operand(leftOperand * rightOperand);
				}//operate
			}//class_Mul
			
			/*
			 * 除法运算符对应的类
			 */
			private static class Div extends DoubleDirOperator {
				private Div(int leftPriority, int rightPriority, String operatorStr) {
					super(leftPriority, rightPriority, operatorStr);
				}//con_Div

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double leftOperand = operands[0].getValue();
					double rightOperand = operands[1].getValue();
					
					return new Operand(leftOperand / rightOperand);
				}//operate
			}//class_Div
			
			/*
			 * 取余运算符对应的类
			 */
			private static class Mod extends DoubleDirOperator {
				private Mod(int leftPriority, int rightPriority, String operatorStr) {
					super(leftPriority, rightPriority, operatorStr);
				}//con_Mod

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double leftOperand = operands[0].getValue();
					double rightOperand = operands[1].getValue();
					
					return new Operand(leftOperand % rightOperand);
				}//operate
			}//class_Mod
			
			/*
			 * 次幂运算符对应的类
			 */
			private static class Pow extends DoubleDirOperator {
				private Pow(int leftPriority, int rightPriority, String operatorStr) {
					super(leftPriority, rightPriority, operatorStr);
				}//con_Pow

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double leftOperand = operands[0].getValue();
					double rightOperand = operands[1].getValue();
					
					return new Operand(Math.pow(leftOperand, rightOperand));
				}//operate
			}//class_Pow
			
			/*
			 * 取负运算符
			 */
			private static class Negate extends RightSingleDirOperator {
				private Negate(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}//con_Negate

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					
					return new Operand(0 - operand);
				}//operate
			}//class_Negate
			
			/*
			 * sin运算符
			 */
			private static class Sin extends RightSingleDirOperator {
				private Sin(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					
					return new Operand(Math.sin(operand));
				}//operate
			}//class_Sin
			
			/*
			 * cos运算符
			 */
			private static class Cos extends RightSingleDirOperator {
				private Cos(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					
					return new Operand(Math.cos(operand));
				}//operate
			}//class_Cos
			
			/*
			 * tan运算符
			 */
			private static class Tan extends RightSingleDirOperator {
				private Tan(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}//con_Tan

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					
					return new Operand(Math.tan(operand));
				}//operate
			}//class_Tan
			
			/*
			 * asin运算符
			 */
			private static class ASin extends RightSingleDirOperator {
				private ASin(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}//con_ASin

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					if (operand < -1 || operand > 1) {
						throw new OperandOutOfBoundsException(this.getOperatorStr(), "[-1, 1]", operand);
					}//if
					
					return new Operand(Math.asin(operand));
				}//operate
			}//class_ASin
			
			/*
			 * acos运算符
			 */
			private static class ACos extends RightSingleDirOperator {
				private ACos(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}//con_ACos

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					if (operand < -1 || operand > 1) {
						throw new OperandOutOfBoundsException(this.getOperatorStr(), "[-1, 1]", operand);
					}//if
					
					return new Operand(Math.acos(operand));
				}//operate
			}//class_ACos
			
			/*
			 * atan运算符
			 */
			private static class ATan extends RightSingleDirOperator {
				private ATan(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}//con_ATan

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					if (operand < -1 || operand > 1) {
						throw new OperandOutOfBoundsException(this.getOperatorStr(), "[-1, 1]", operand);
					}//if
					
					return new Operand(Math.atan(operand));
				}//operate
			}//class_ATan
			
			/*
			 * ln运算符
			 */
			private static class Ln extends RightSingleDirOperator {
				private Ln(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}//con_Ln

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					if (operand <= 0) {
						throw new OperandOutOfBoundsException(getOperatorStr(), "(0, +∞)", operand);
					}//if
					
					return new Operand(Math.log1p(operand - 1)); //Math.log1p(x)返回值为：ln(1 + x);
				}//operate
			}//class_Ln
			
			/*
			 * lg运算符
			 */
			private static class Lg extends RightSingleDirOperator {
				private Lg(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					if (operand <= 0) {
						throw new OperandOutOfBoundsException(getOperatorStr(), "(0, +∞)", operand);
					}//if
					
					return new Operand(Math.log10(operand));
				}//operate
			}//class_Lg
			
			/*
			 * sqrt运算符
			 */
			private static class Sqrt extends RightSingleDirOperator {
				private Sqrt(int rightDirPriority, String operatorStr) {
					super(rightDirPriority, operatorStr);
				}

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					if (operand < 0) {
						throw new OperandOutOfBoundsException(getOperatorStr(), "[0, +∞)", operand);
					}//if
					
					return new Operand(Math.sqrt(operand));
				}//operate
			}//class_Sqrt
			
			/*
			 * fact运算符（阶乘）
			 */
			private static class Fact extends LeftSingleDirOperator {
				private Fact(int leftDirPriority, String operatorStr) {
					super(leftDirPriority, operatorStr);
				}//con_Fact

				@Override
				Operand operate(Operand[] operands) {
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


				private LeftAbs(String operatorStr, int id) {
					super(operatorStr, id);
				}//con_LeftAbs

				@Override
				Operand operate(Operand[] operands) {
					checkOperandNumCorrect(operands.length);
					
					double operand = operands[0].getValue();
					
					return new Operand(Math.abs(operand));
				}//operate

				@Override
				int getDimension() {
					return LOG_START_DIMENSION;
				}//getDimension

				@Override
				boolean isNeedOperate() {
					return IS_NEED_OPERATE;
				}//isNeedOperate

				@Override
				boolean isNeedPush() {
					return IS_NEED_PUSH;
				}//isNeedPush	
			}//LeftAbs
			
			/*
			 * 左括号
			 */
			private static class LeftBrackets extends OpenOperator {
				private static final int LEFT_BRACKETS_DIMENSION = 0;
				private static final boolean IS_NEED_PUSH = false;
				private static final boolean IS_NEED_OPERATE = false;


				private LeftBrackets(String operatorStr, int id) {
					super(operatorStr, id);
				}//con_LeftBrackets

				@Override
				Operand operate(Operand[] operands) {
					throw new ShouldNotOperateException(getOperatorStr());
				}//operate

				@Override
				int getDimension() {
					return LEFT_BRACKETS_DIMENSION;
				}//getDimension

				@Override
				boolean isNeedOperate() {
					return IS_NEED_OPERATE;
				}//isNeedOperate

				@Override
				boolean isNeedPush() {
					return IS_NEED_PUSH;
				}//isNeedPush
			}//LeftBrackets
			
			/*
			 * log开始运算符（log）
			 */
			private static class LogStart extends OpenOperator {
				private static final int LOG_START_DIMENSION = 0;
				private static final boolean IS_NEED_PUSH = false;
				private static final boolean IS_NEED_OPERATE = false;


				private LogStart(String operatorStr, int id) {
					super(operatorStr, id);
				}//con_LogStart

				@Override
				Operand operate(Operand[] operands) {
					throw new ShouldNotOperateException(getOperatorStr());
				}//operate

				@Override
				int getDimension() {
					return LOG_START_DIMENSION;
				}//getDimension

				@Override
				boolean isNeedOperate() {
					return IS_NEED_OPERATE;
				}//isNeedOperate

				@Override
				boolean isNeedPush() {
					return IS_NEED_PUSH;
				}//isNeedPush
			}//class_LogStart
			
			/*
			 * 表达式开始标记
			 */
			private static class StartFlag extends OpenOperator {
				private static final int FLAG_END_DIMENSION = 0;
				private static final boolean IS_NEED_PUSH = false;
				private static final boolean IS_NEED_OPERATE = false;


				private StartFlag(String operatorStr, int id) {
					super(operatorStr, id);
				}//con_StartFlag

				@Override
				Operand operate(Operand[] operands) {
					throw new ShouldNotOperateException(getOperatorStr());
				}//operate

				@Override
				int getDimension() {
					return FLAG_END_DIMENSION;
				}//getDimension

				@Override
				boolean isNeedOperate() {
					return IS_NEED_OPERATE;
				}//isNeedOperate

				@Override
				boolean isNeedPush() {
					return IS_NEED_PUSH;
				}//isNeedPush
			}//class_StartFlag
			
			/*
			 * 右绝对值
			 */
			private static class RightAbs extends CloseOperator {
				private static final int RIGHT_ABS_DIMENSION = 0;
				private static final boolean IS_NEED_PUSH = false;
				private static final boolean IS_NEED_OPERATE = false;
				private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = false;


				private RightAbs(String operatorStr, int id) {
					super(operatorStr, id);
				}//con_RightAbs

				@Override
				Operand operate(Operand[] operands) {
					throw new ShouldNotOperateException(getOperatorStr());
				}//operate

				@Override
				int getDimension() {
					return RIGHT_ABS_DIMENSION;
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
			}//class_RightAbs
			
			/*
			 * 右括号
			 */
			private static class RightBrackets extends CloseOperator {
				private static final int RIGHT_BRACKETS_DIMENSION = 0;
				private static final boolean IS_NEED_PUSH = false;
				private static final boolean IS_NEED_OPERATE = false;
				private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = false;


				private RightBrackets(String operatorStr, int id) {
					super(operatorStr, id);
				}//con_RightBrackets

				@Override
				Operand operate(Operand[] operands) {
					throw new ShouldNotOperateException(getOperatorStr());
				}//operate

				@Override
				int getDimension() {
					return RIGHT_BRACKETS_DIMENSION;
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
			}//RightBrackets
			
			/*
			 * log运算符结束运算符（~）
			 */
			private static class LogEnd extends CloseOperator {
				private static final int LOG_END_DIMENSION = 2;
				private static final boolean IS_NEED_PUSH = true;
				private static final boolean IS_NEED_OPERATE = true;
				private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = true;
				
				private final int mRightPrior;


				private LogEnd(int rightPrior, String operatorStr, int id) {
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
					
					return new Operand(Math.log(rightOperand) / Math.log(leftOperand));
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
		
			/*
			 * 表达式结束标记
			 */
			private static class EndFlag extends CloseOperator {
				private static final int FLAG_END_DIMENSION = 0;
				private static final boolean IS_NEED_PUSH = false;
				private static final boolean IS_NEED_OPERATE = false;
				private static final boolean IS_RIGHT_DIR_PRIOR_EXIST = false;


				private EndFlag(String operatorStr, int id) {
					super(operatorStr, id);
				}//con_EndFlag

				@Override
				Operand operate(Operand[] operands) {
					throw new ShouldNotOperateException(getOperatorStr());
				}//operate

				@Override
				int getDimension() {
					return FLAG_END_DIMENSION;
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
			}//class_EndFlag
		}//class_CertainOperator
		
		static abstract class UncertainOperator extends Operator {
			private static final boolean IS_CERTAIN = false;


			private UncertainOperator(String operatorStr) {
				super(operatorStr);
			}//con_UncertainOperator

			@Override
			boolean isCertain() {
				return IS_CERTAIN;
			}//isCertain
			
			//通过该方法来确认出该未确定类型运算符的具体类型
			abstract Operator getCertainOperator(ExpItem preItem);
			
			
			private static class Hyphen extends UncertainOperator {
				private Hyphen(String operatorStr) {
					super(operatorStr);
				}//con_Hyphen

				@Override
				Operator getCertainOperator(ExpItem preItem) {
					ItemType itemType = preItem.getItemType();
					if (itemType == ItemType.OPERAND || itemType == ItemType.VARIABLE) {
						//如果'-'前边的项是一个操作数或者变量，那么这个'-'的含义必然为减号
						return OperatorAssistant.getOperator(OperatorAssistant.SUB);
					}//if
					
					CertainOperator operator = (CertainOperator)preItem;
					if (operator.getCertainOperatorType() != CertainOperatorType.OPEN && !operator.isRightDirPriorExist()) {
						//如果之前的项是不拥有右方向优先级的运算符，那么这个'-'的含义必然为减号
						return OperatorAssistant.getOperator(OperatorAssistant.SUB);
					}//if
					
					return OperatorAssistant.getOperator(OperatorAssistant.NEGATE);
				}//getCertainOperator
			}//class_Hyphen
			
			private static class VerticalLine extends UncertainOperator {
				private VerticalLine(String operatorStr) {
					super(operatorStr);
					
				}//con_VerticalLine

				@Override
				Operator getCertainOperator(ExpItem preItem) {
					ItemType itemType = preItem.getItemType();
					if (itemType == ItemType.OPERAND || itemType == ItemType.VARIABLE) {
						return OperatorAssistant.getOperator(OperatorAssistant.RIGHT_ABS);
					}//if
					
					CertainOperator operator = (CertainOperator)preItem;
					if (operator.getCertainOperatorType() != CertainOperatorType.OPEN && !operator.isRightDirPriorExist()) {
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
		private static class OperatorAssistant {
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
			
			private static Operator getOperator(String operatorStr) {
				if (!sStrFlagMap.containsKey(operatorStr)) {
					throw new OperatorNotExistException(operatorStr);
				}//if
				
				int operatorFlag = sStrFlagMap.get(operatorStr);
				
				return getOperator(operatorFlag);
			}//getOperator
			
			private static Operator getOperator(int operatorFlag) {
				return sFlagOperatorMap.computeIfAbsent(operatorFlag, OperatorAssistant::newOperator);
			}//getOperator
			
			private static boolean isIdentifierAlreadyExist(String identifierStr) {
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
		private static final Map<String, Operand> sConstantsMap = new HashMap<>();
		
		static {
			//初始化常量表
			sConstantsMap.put("pi", new Operand(Math.PI));
			sConstantsMap.put("e", new Operand(Math.E));
			
		}//static
		
		//操作数的值
		private double mValue;
		
		
		public static Operand getOperand(String operandStr) {
			return new Operand(Double.parseDouble(operandStr));
		}//getOperand
		
		public static Operand getOperand(double operandValue) {
			return new Operand(operandValue);
		}//getOperand
		
		public static boolean hasConstant(String constantStr) {
			return sConstantsMap.containsKey(constantStr);
		}//hasContant
		
		public static Operand getConstant(String constantStr) {
			if (hasConstant(constantStr)) {
				return sConstantsMap.get(constantStr);
			}//if
			
			throw new ConstantNotExistException(constantStr);
		}//getConstant
		
		private Operand(double value) {
			super(ItemType.OPERAND);
			
			mValue = DigitUtil.reserveSignificantDigits(value, SIGNIFICANCE_DIGIT);
		}//con_Operand
		
		public double getValue() {
			return mValue;
		}//getValue
		
		@Override
		public String toString() {
			return String.valueOf(mValue);
		}//toString
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof Operand && getValue() == ((Operand) obj).getValue();
		}//equals
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
		
		
		//私有构造函数防止直接实例化
		private Variable(String flagStr, Operand lowerLimit, boolean isLowerOpen
				, Operand upperLimit, boolean isUpperOpen, Operand span) {
			super(ItemType.VARIABLE);
			
			//变量的字符串标识
			mFlagStr = flagStr;
			
			//判断变量的上下限是否合法
			if (lowerLimit.getValue() > upperLimit.getValue()) {
				throw new VariableDomainErrorException();
			}//if
			
			//变量的最小跨度，以上限为标准
			Operand minimuxSpan = new Operand(DigitUtil.getMinimumSpan(upperLimit.getValue(), Operand.SIGNIFICANCE_DIGIT));
			if (span.getValue() < minimuxSpan.getValue()) {
				span = minimuxSpan;
			}
			//变量的跨度
			mSpan = span.getValue();

			if (isLowerOpen) {
				lowerLimit = new Operand(lowerLimit.getValue() + mSpan);
			} //if
			if (isUpperOpen) {
				upperLimit = new Operand(upperLimit.getValue() - mSpan);
			}//if
			
			if (lowerLimit.getValue() > upperLimit.getValue()) {
				throw new VariableSpanNotSuitableException();
			}//if
			
			mUpperLimit = upperLimit.getValue();
			mLowerLimit = lowerLimit.getValue();
			
			mCurValue = mLowerLimit;
		}//con_Variable
		
		public boolean hasNext() {
			return mCurValue < mUpperLimit;
		}//hasNext
		
		public double nextValue() {
			if (hasNext()) {
				if (mCurValue + mSpan <= mUpperLimit) {
					mCurValue += mSpan;
				} else {
					mCurValue = mUpperLimit;
				}//if-else
				
				return mCurValue;
			}//if
			
			throw new NoNextValueException(mFlagStr);
		}//nextValue
		
		public double curValue() {
			return mCurValue;
		}//curValue
		
		@Override
		public String toString() {
			return mFlagStr;
		}//toString
		
		
		public static class VariableAssistant {
			private Map<String, Variable> mVariablesMap = new LinkedHashMap<>();
			

			public VariableAssistant addVariable(String flagStr, Operand lowerLimit, boolean isLowerOpen
				, Operand upperLimit, boolean isUpperOpen, Operand span) {
				if (mVariablesMap.containsKey(flagStr) || Operand.hasConstant(flagStr)
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
			
			public boolean hasNext() {
				for (String varStr : mVariablesMap.keySet()) {
					Variable var = mVariablesMap.get(varStr);
					if (var.hasNext()) {
						return true;
					}//if
				}//for
				
				return false;
			}//hasNext
			
			public void nextValue() {
				for (String varStr : mVariablesMap.keySet()) {
					Variable var = mVariablesMap.get(varStr);
					if (var.hasNext()) {
						var.nextValue();
						return;
					}//if					
				}//for
				
				throw new VarAssistHasNoNextValueException();
			}//nextValue
		}//class_VariableAssistant
	}//class_Variable
}//class_ExpItem
