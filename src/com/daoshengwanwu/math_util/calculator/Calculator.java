package com.daoshengwanwu.math_util.calculator;


import java.util.List;

import com.daoshengwanwu.math_util.calculator.ExpItem.Operand;
import com.daoshengwanwu.math_util.calculator.ExpItem.Operator.CertainOperator;
import com.daoshengwanwu.math_util.calculator.ExpItem.Operator.CertainOperator.OperatorType;
import com.daoshengwanwu.math_util.calculator.ExpItem.Variable;
import com.daoshengwanwu.math_util.calculator.ExpItem.Variable.VariableAssistant;
import com.daoshengwanwu.math_util.calculator.exception.OperatorNotMatchException;
import com.daoshengwanwu.math_util.calculator.util.Stack;


public class Calculator {
	private static Stack<Operand> sOperandStack = new Stack<>();
	private static Stack<CertainOperator> sOperatorStack = new Stack<>();
	
	
	public static double calculate(String expStr) {
		AriExp ariExp = new AriExp(expStr);
		return calculate(ariExp);
	}//calculate
	
	public static ResultGenerator calculate(String expStr, VariableAssistant varAssist) {
		VarAriExp varAriExp = new VarAriExp(expStr, varAssist);
		return calculate(varAriExp);
	}//calculate
	
	public static double calculate(AriExp ariExp) {
		return calculateCurrentValue(ariExp);
	}//calculate
	
	public static ResultGenerator calculate(VarAriExp varAriExp) {
		return new ResultGenerator(varAriExp);
	}//calculate
	
	private static double calculateCurrentValue(VarAriExp varAriExp) {
		//每次计算前清空运算符栈和操作数栈
		sOperandStack.clear();
		sOperatorStack.clear();
		
		//如果表达式元素不都是确定的，则使之确定
		if (!varAriExp.isCertain()) {
			varAriExp.ensureAriExp();
		}//if
		
		double curValue;
		int curIndex = 0;
		int curLeftPrior;
		int topRightPrior;
		ExpItem curItem = null;
		CertainOperator curOperator = null;
		CertainOperator topOperator = null;
		List<ExpItem> expItems = varAriExp.getExpItemList();
		while (curIndex < expItems.size()) {
			curItem = expItems.get(curIndex);
			
			switch (curItem.getItemType()) {
			case OPERAND: {
				sOperandStack.push((Operand)curItem);
				curIndex++;
			} break;
			case OPERATOR: {
				curOperator = (CertainOperator)curItem;
				switch (curOperator.getOperatorType()) {
				case NORMAL: {
					if (curOperator.isLeftDirPriorExist()) {
						topOperator = sOperatorStack.getTop();
						switch (topOperator.getOperatorType()) {
						case NORMAL: 
						case CLOSE: {
							if (topOperator.isRightDirPriorExist()) {
								curLeftPrior = curOperator.getLeftDirPriority();
								topRightPrior = topOperator.getRightDirPriority();
								
								if (curLeftPrior <= topRightPrior) {
									makeOperate();
								} else {
									sOperatorStack.push(curOperator);
									curIndex++;
								}//if-else
							} else {
								makeOperate();
							}//if-else
						} break;
						case OPEN: {
							sOperatorStack.push(curOperator);
							curIndex++;
						} break;
						}//switch-case
						
					} else {
						sOperatorStack.push(curOperator);
						curIndex++;
					}//if-else
				} break;
				case OPEN: {
					//如果当前运算符是OPEN性质的，直接入栈
					sOperatorStack.push(curOperator);
					curIndex++;
				} break;
				case CLOSE: {
					topOperator = sOperatorStack.getTop();
					makeOperate();
					
					if (topOperator.getOperatorType() == OperatorType.OPEN) {
						if (topOperator.getId() == curOperator.getId()) {
							if (curOperator.isNeedPush()) {
								sOperatorStack.push(curOperator);
							}//if
							curIndex++;
						} else {
							throw new OperatorNotMatchException(
									topOperator.getOperatorStr(), curOperator.getOperatorStr());
						}//if-else
					}//if
				} break;
				}//switch
			} break;
			case VARIABLE: {
				curValue = ((Variable)curItem).curValue();
				sOperandStack.push(Operand.getOperand(curValue));
				curIndex++;
			} break;
			}//switch-case
		} //while
		
		return sOperandStack.pop().getValue();
	}//calculateCurrentValue
	
	private static void makeOperate() {		
		CertainOperator operator = sOperatorStack.pop();
		
		if (!operator.isNeedOperate()) {
			return;
		}//if
		
		int operandNum = operator.getDimension();
		Operand[] operands = new Operand[operandNum];
		
		for (int i = 1; i <= operandNum; i++) {
			operands[operands.length - i] = sOperandStack.pop();
		}//for
		
		sOperandStack.push(operator.operate(operands));
	}//makeOperate
	
	
	public static class ResultGenerator {
		private VarAriExp mVarAriExp;
		private VariableAssistant mVarAssist;
		
		
		public ResultGenerator(VarAriExp varAriExp) {
			mVarAriExp = varAriExp;
			mVarAssist = mVarAriExp.getVariableAssistant();
		}//con_ResultGenerator
		
		public double curValue() {
			return Calculator.calculateCurrentValue(mVarAriExp);
		}//curValue
		
		public boolean hasNext() {
			if (null == mVarAssist) {
				return false;
			}//if
			
			return mVarAssist.hasNext();
		}//hasNext
		
		public double nextValue() {
			mVarAssist.nextValue();
			return Calculator.calculateCurrentValue(mVarAriExp);
		}//nextValue
	}//class_ResultGenerator
}//class_Calculator
