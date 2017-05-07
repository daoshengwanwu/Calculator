package com.daoshengwanwu.math_util.calculator;


/*
 * 构成表达式的项：构成AriExp以及VarAriExp的基本单位
 */
abstract class ExpItem {
	//该项的类型，取值分别有：OPERATOR(运算符)、OPERAND(操作数)、VARIABLE(变量)
	private final ItemType mItemType;
	
	
	ExpItem(ItemType itemType) {
		mItemType = itemType;
	}//con_ExpItem
	
	ItemType getItemType() {
		return mItemType;
	}//getItemType
	
	
	/*
	 * 项的类型的枚举定义
	 */
	static enum ItemType {
		OPERATOR, OPERAND, VARIABLE;
	}//enum_ItemType	
	
	
	/*
	 * 运算符类
	 */
	static class Operator extends ExpItem {
		Operator() {
			super(ItemType.OPERATOR);
		}//con_Operator		
	}//class_Operator
	
	/*
	 * 操作数类
	 */
	static class Operand extends ExpItem {
		Operand() {
			super(ItemType.OPERAND);
		}//con_Operand
	}//class_Operand
	
	/*
	 * 变量类
	 */
	static class Variable extends ExpItem {
		Variable() {
			super(ItemType.VARIABLE);
		}//con_Variable
	}//class_Variable
}//class_ExpItem
