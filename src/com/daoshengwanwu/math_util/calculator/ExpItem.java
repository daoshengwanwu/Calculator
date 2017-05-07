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
		public static final String START_FLAT = "START_FLAG";
		
		private final String mOperatorStr; //运算符的字符串描述
		
		
		public static Operator getOperator(String operatorStr) throws IllegalIdentifierException {
			return OperatorAssistant.getOperator(operatorStr);
		}//getOperator
		
		public Operator(String operatorStr) {
			super(ItemType.OPERATOR);
			
			mOperatorStr = operatorStr;
		}//con_Operator		
		
		public int getLeftDirPriority() throws SpecDirPriorNotExistException {
			throw new SpecDirPriorNotExistException(getOperatorStr(), "左");
		}//getLeftDirPriority
		
		public int getRightDirPriority() throws SpecDirPriorNotExistException {
			throw new SpecDirPriorNotExistException(getOperatorStr(), "右");
		}//getRightDirPriority
		
		public String getOperatorStr() {
			return mOperatorStr;
		}//getOperatorStr
		
		public abstract Operand operate(Operand[] operands) throws OperandNumException;
		
		public abstract int getDimension();
		
		public abstract OperatorType getOperatorType();
		
		protected void checkOperandNumCorrect(int actualOperandNum) throws OperandNumException {
			if (actualOperandNum != getDimension()) {
				throw new OperandNumException(getOperatorStr(), getDimension(), actualOperandNum);
			}//if
		}//ifOperandNumCorrect
		
		
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
		
		private static abstract class DoubleDirOperator extends Operator {
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
		}
		
		private static abstract class LeftSingleDirOperator extends Operator {
			private final int mLeftDirPriority;
			
			
			public LeftSingleDirOperator(int leftDirPriority, String operatorStr) {
				super(operatorStr);
				
				mLeftDirPriority = leftDirPriority;
			}//con_LeftSingleDirOperator
			
			@Override
			public int getLeftDirPriority() {
				return mLeftDirPriority;
			}//getLeftDirPriority
		}//class_LeftSingleDirOperator
		
		private static abstract class RightSingleDirOperator extends Operator {
			private final int mRightDirPriority;
			
			
			public RightSingleDirOperator(int rightDirPriority, String operatorStr) {
				super(operatorStr);
				
				mRightDirPriority = rightDirPriority;
			}//con_LeftSingleDirOperator
			
			@Override
			public int getRightDirPriority() {
				return mRightDirPriority;
			}//getLeftDirPriority
		}//class_LeftSingleDirOperator
		
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
