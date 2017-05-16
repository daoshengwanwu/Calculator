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
			NORMAL, OPEN, CLOSE;
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
		 * 普通运算符（所有不具有Open和Close特性的运算符均属于普通运算符）
		 */
		private static abstract class NormalOperator extends Operator {
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
		private static abstract class OpenOperator extends Operator {
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
		private static abstract class CloseOperator extends Operator {
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
		}
		
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
		}
	}//class_Operator
	
	/*
	 * 操作数类
	 */
	public static class Operand extends ExpItem {
		private static final double PRECISION = 10e-15; //操作数的精度，如果需要更改精度，修改此值即可
		private static final double AUXILIARY = 1 / PRECISION;
		
		//操作数的值
		private double mValue;
		
		
		public Operand(double value) {
			super(ItemType.OPERAND);
			
			mValue = precision(value);
		}//con_Operand
		
		public void setValue(double value) {
			mValue = precision(value);
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
