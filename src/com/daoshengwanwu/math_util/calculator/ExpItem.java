package com.daoshengwanwu.math_util.calculator;


public abstract class ExpItem {
    private final ItemType mItemType;
    
    
    ExpItem(ItemType itemType) {
        mItemType = itemType;
    }//con_ExpItem
    
    ItemType getItemType() {
        return mItemType;
    }//getItemType
    

    public enum ItemType {
        OPERATOR, OPERAND, VARIABLE
    }//enum_ItemType
}//class_ExpItem
