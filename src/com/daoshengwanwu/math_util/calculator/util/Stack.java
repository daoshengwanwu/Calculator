package com.daoshengwanwu.math_util.calculator.util;


import java.util.ArrayList;


public class Stack<T> {
    private ArrayList<T> mArrayList = new ArrayList<>();
    
    
    public boolean push(T elem) {
        return mArrayList.add(elem);
    }//push
    
    public T pop() {
        return mArrayList.remove(mArrayList.size() - 1);
    }//pop
    
    public T getTop() {
        return mArrayList.get(mArrayList.size() - 1);
    }//getTop
    
    public int size() {
        return mArrayList.size();
    }//size
    
    public boolean isEmpty() {
        return mArrayList.isEmpty();
    }//isEmpty
    
    public void clear() {
        mArrayList.clear();
    }//clear
}//class_Stack
