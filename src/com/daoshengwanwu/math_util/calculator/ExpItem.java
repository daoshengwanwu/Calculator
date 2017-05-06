package com.daoshengwanwu.math_util.calculator;


abstract class ExpItem {
	private ItemType mItemType;
	
	
	ExpItem(ItemType itemType) {
		mItemType = itemType;
	}//con_ExpItem
	
	ItemType getItemType() {
		return mItemType;
	}//getItemType
	
	
	static enum ItemType {
		OPERATOR, OPERAND, VARIABLE;
	}//enum_ItemType	
	
	
	static class Operator extends ExpItem {
		Operator() {
			super(ItemType.OPERATOR);
		}//con_Operator		
	}//class_Operator
	
	static class Operand extends ExpItem {
		Operand() {
			super(ItemType.OPERAND);
		}//con_Operand
	}//class_Operand
	
	static class Variable extends ExpItem {
		Variable() {
			super(ItemType.VARIABLE);
		}//con_Variable
	}//class_Variable
}//class_ExpItem
