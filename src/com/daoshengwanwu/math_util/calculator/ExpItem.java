package com.daoshengwanwu.math_util.calculator;


import java.util.HashMap;
import java.util.Map;

import com.daoshengwanwu.math_util.calculator.exception.IllegalIdentifierException;
import com.daoshengwanwu.math_util.calculator.exception.OperandNumException;
import com.daoshengwanwu.math_util.calculator.exception.SpecDirPriorNotExistException;


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
		public static final Operator START_FLAG = null;
		public static final Operator END_FLAG = null;
		
		private final String mOperatorStr; //运算符的字符串描述
		
		//通过该静态方法，来获取operatorStr确定的Operator对象
		public static Operator getOperator(String operatorStr) throws IllegalIdentifierException {
			return OperatorAssistant.getOperator(operatorStr);
		}//getOperator
		
		public Operator(String operatorStr) {
			super(ItemType.OPERATOR);
			
			mOperatorStr = operatorStr;
		}//con_Operator		
		
		//获取运算符的左侧优先级，若不存在该侧优先级则抛出异常
		public int getLeftDirPriority() throws SpecDirPriorNotExistException {
			throw new SpecDirPriorNotExistException(getOperatorStr(), "左");
		}//getLeftDirPriority
		
		//获取运算符的右侧优先级，若不存在该侧优先级则抛出异常
		public int getRightDirPriority() throws SpecDirPriorNotExistException {
			throw new SpecDirPriorNotExistException(getOperatorStr(), "右");
		}//getRightDirPriority
		
		//获取运算符的字符串描述
		public String getOperatorStr() {
			return mOperatorStr;
		}//getOperatorStr
		
		//判断运算符是否需要入栈的方法，一般都需要入栈，特殊情况重写该方法即可
		public boolean needPush() {
			return true; 
		}//needPush
		
		//返回运算符的标识id，一般情况下都返回-1即可，特殊情况重写以返回指定id
		public int getId() {
			return -1;
		}//getId
		
		//通过调用该方法来执行对应运算符的计算，将计算结果封装为一个Operand对象并返回
		public abstract Operand operate(Operand[] operands) throws OperandNumException;
		
		//获取运算符运算时需要的操作数个数
		public abstract int getDimension();
		
		//获取运算符的类型
		public abstract OperatorType getOperatorType();
		
		//检查实际传入的操作数个数和运算符要求的运算符个数是否相等
		protected void checkOperandNumCorrect(int actualOperandNum) throws OperandNumException {
			if (actualOperandNum != getDimension()) {
				throw new OperandNumException(getOperatorStr(), getDimension(), actualOperandNum);
			}//if
		}//ifOperandNumCorrect
		
		
		/*
		 * 该枚举定义了Operator的几种类型
		 */
		public static enum OperatorType {
			LEFT_REQUIRED, RIGHT_REQUIRED, DOUBLE_REQUERED, OPEN, CLOSE;
		}//enum_OperatorType
		
		
		/*
		 * 运算符助手静态内部类
		 * 可以根据运算符的字符串描述返回对应的Operator对象, 并且这个Operator在应用的整个运行过程中，只会存在一个实例
		 */
		public static class OperatorAssistant {
			private static Map<String, Operator> sStrOperatorMap = new HashMap<>();
			
			
			public static Operator getOperator(String operatorStr) throws IllegalIdentifierException {
				Operator operator = sStrOperatorMap.get(operatorStr);
				
				if (null == operator) {
					operator = newOperator(operatorStr);
					sStrOperatorMap.put(operatorStr, operator);
				}//if

				return operator;
			}//getOperator
			
			private static Operator newOperator(String operatorStr) throws IllegalIdentifierException {
				Operator operator = null;
				
				//--------------在这个switch里添加运算符的字符串描述与实际运算符类的对应--------------------
				switch (operatorStr) {
				case "+": operator = new Add(1, 1, "+"); break;
				case "-": operator = new Sub(1, 1, "-"); break;
				case "*": operator = new Mul(2, 2, "*"); break;
				case "/": operator = new Div(2, 2, "/"); break;
				default: throw new IllegalIdentifierException(operatorStr);
				}//switch
				
				return operator;
			}//newOperator
		}//class_OperatorAssistant
		
		/*
		 * 具有双侧优先级的运算符的基类
		 */
		private static abstract class DoubleDirOperator extends Operator {
			private static final OperatorType OPERATOR_TYPE = OperatorType.DOUBLE_REQUERED;
			
			private final int mLeftDirPriority;
			private final int mRightDirPriority;
			
			
			public DoubleDirOperator(int leftPriority, int rightPriority, String operatorStr) {
				super(operatorStr);
				
				mLeftDirPriority = leftPriority;
				mRightDirPriority = rightPriority;
			}//con_DoubleDirOperator
			
			@Override
			public int getLeftDirPriority() {
				return mLeftDirPriority;
			}//getLeftDirPriority
			
			@Override
			public int getRightDirPriority() {
				return mRightDirPriority;
			}//getRightDirPriority
			
			@Override
			public OperatorType getOperatorType() {
				return OPERATOR_TYPE;
			}//getOperatorType
		}//class_DoubleDirOperator
		
		/*
		 * 具有左单侧优先级的运算符的基类
		 */
		private static abstract class LeftSingleDirOperator extends Operator {
			private static final OperatorType OPERATOR_TYPE = OperatorType.LEFT_REQUIRED;
			
			private final int mLeftDirPriority;
			
			
			public LeftSingleDirOperator(int leftDirPriority, String operatorStr) {
				super(operatorStr);
				
				mLeftDirPriority = leftDirPriority;
			}//con_LeftSingleDirOperator
			
			@Override
			public int getLeftDirPriority() {
				return mLeftDirPriority;
			}//getLeftDirPriority
			
			@Override
			public OperatorType getOperatorType() {
				return OPERATOR_TYPE;
			}//getOperatorType
		}//class_LeftSingleDirOperator
		
		/*
		 * 具有右单侧优先级的运算符的基类
		 */
		private static abstract class RightSingleDirOperator extends Operator {
			private static final OperatorType OPERATOR_TYPE = OperatorType.RIGHT_REQUIRED;
			
			private final int mRightDirPriority;
			
			
			public RightSingleDirOperator(int rightDirPriority, String operatorStr) {
				super(operatorStr);
				
				mRightDirPriority = rightDirPriority;
			}//con_LeftSingleDirOperator
			
			@Override
			public int getRightDirPriority() {
				return mRightDirPriority;
			}//getLeftDirPriority
			
			@Override
			public OperatorType getOperatorType() {
				return OPERATOR_TYPE;
			}//getOperatorType
		}//class_LeftSingleDirOperator
		
		/*
		 * OPEN类型运算符的基类
		 */
		private static abstract class OpenOperator extends Operator {
			private static final OperatorType OPERATOR_TYPE = OperatorType.OPEN;
			
			public OpenOperator(String operatorStr) {
				super(operatorStr);
			}//con_OpenOperator
			
			@Override
			public OperatorType getOperatorType() {
				return OPERATOR_TYPE;
			}//getOperatorType
		}//class_OpenOperator

		/*
		 * CLOSE类型的运算符的基类
		 */
		private static abstract class CloseOperator extends Operator {
			private static final OperatorType OPERATOR_TYPE = OperatorType.CLOSE;
			
			public CloseOperator(String operatorStr) {
				super(operatorStr);
			}//con_CloseOperator
			
			@Override
			public OperatorType getOperatorType() {
				return OPERATOR_TYPE;
			}//getOperatorType
		}//class_CloseOperator
		
		/*
		 * 加法运算符对应的类
		 */
		private static class Add extends DoubleDirOperator {
			private static final OperatorType ADD_OPERATOR_TYPE = OperatorType.DOUBLE_REQUERED;
			private static final int ADD_OPERATOR_DIMENSION = 2;
			
			
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

			@Override
			public int getDimension() {
				return ADD_OPERATOR_DIMENSION;
			}//getDimentsion

			@Override
			public OperatorType getOperatorType() {
				return ADD_OPERATOR_TYPE;
			}//getOperatorType
		}//class_Add
		
		/*
		 * 减法运算符对应的类
		 */
		private static class Sub extends DoubleDirOperator {
			private static final OperatorType SUB_OPERATOR_TYPE = OperatorType.DOUBLE_REQUERED;
			private static final int SUB_OPERATOR_DIMENSION = 2;
			

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

			@Override
			public int getDimension() {
				return SUB_OPERATOR_DIMENSION;
			}

			@Override
			public OperatorType getOperatorType() {
				return SUB_OPERATOR_TYPE;
			}
		}//class_Sub
		
		/*
		 * 乘法运算符对应的类
		 */
		private static class Mul extends DoubleDirOperator {
			private static final OperatorType MUL_OPERATOR_TYPE = OperatorType.DOUBLE_REQUERED;
			private static final int MUL_OPERATOR_DIMENSION = 2;
			
			
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

			@Override
			public int getDimension() {
				return MUL_OPERATOR_DIMENSION;
			}

			@Override
			public OperatorType getOperatorType() {
				return MUL_OPERATOR_TYPE;
			}
		}//class_Mul
		
		/*
		 * 除法运算符对应的类
		 */
		private static class Div extends DoubleDirOperator {
			private static final OperatorType DIV_OPERATOR_TYPE = OperatorType.DOUBLE_REQUERED;
			private static final int DIV_OPERATOR_DIMENSION = 2;
			
			
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

			@Override
			public int getDimension() {
				return DIV_OPERATOR_DIMENSION;
			}

			@Override
			public OperatorType getOperatorType() {
				return DIV_OPERATOR_TYPE;
			}
		}//class_Div
	}//class_Operator
	
	/*
	 * 操作数类
	 */
	public static class Operand extends ExpItem {
		private static final double PRECISION = 10e-15;
		private static final double AUXILIARY = 1 / PRECISION;
		
		private double mValue;
		
		
		public Operand(double value) {
			super(ItemType.OPERAND);
			
			mValue = precision(value);
		}//con_Operand
		
		public void setValue(double value) {
			mValue = value;
		}//setValue
		
		public double getValue() {
			return mValue;
		}//getValue
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Operand)) {
				return false;
			}//if
			
			Operand operand = (Operand)obj;
			return Math.abs(Math.abs(mValue) - Math.abs(operand.getValue())) < PRECISION;
		}//equals
		
		@Override
		public String toString() {
			return String.valueOf(mValue);
		}//toString
		
		//将value置为PRECISION指定的精度
		private double precision(double value) {
			return Math.round(value * AUXILIARY) / AUXILIARY;
		}//precision
	}//class_Operand
	
	/*
	 * 变量类
	 */
	public static class Variable extends ExpItem {
		public Variable() {
			super(ItemType.VARIABLE);
		}//con_Variable
	}//class_Variable
}//class_ExpItem
