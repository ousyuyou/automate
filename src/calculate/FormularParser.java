package calculate;

import java.util.ListIterator;
import java.util.Stack;

public class FormularParser {
	private Stack<Operator> operatorStack;
	private Stack<String> operandStack;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = "(a+b)*c*(d+2)-(a+b)/e";
		FormularParser parser = new FormularParser();
		parser.parse(str,true);
		System.out.println(parser.toString());
	}
	
	public FormularParser(){
		operatorStack = new Stack<Operator>();
		operandStack = new Stack<String>();
	}
	
	/**
	 * formula parser
	 * @param formula
	 * @return
	 */
	public Stack<String> parse(String formula,boolean mathExpression){
		StringBuffer buf = new StringBuffer();
		
		for(char c:formula.toCharArray()){
//			char c = formula.charAt(i);
			if(Operator.isOperator(c,mathExpression)){
				//push buf to operand
				if(buf.length() > 0){
					operandStack.push(buf.toString());
					buf.delete(0, buf.capacity()-1);
				}
				//handle the operator
				handleOperator(Operator.getOperator(c));			
			} else {
				buf.append(c);
			}
		}
		if(buf.length() > 0){
			operandStack.push(buf.toString());
		}
		
		Operator ope = null;
		while(!operatorStack.isEmpty()){
			ope = operatorStack.pop();
			operandStack.push(String.valueOf(ope.getOperator()));
		}
		
		return operandStack;
	}
	
	public String toString(){
		ListIterator<String> iter = operandStack.listIterator();
		StringBuffer value = new StringBuffer();
		while(iter.hasNext()){
			value.append(iter.next());
		}
		return value.toString();
	}
	
	private void handleOperator(Operator opeCurrent){
		//operator stack is empty
		if(operatorStack.isEmpty()){
			operatorStack.push(opeCurrent);
			return;
		}
		
		switch(opeCurrent.getOperator()){
			case '(':
				operatorStack.push(opeCurrent);
				return;
			case ')':
				dealRightparentheses(opeCurrent);
				return;
			default:
				break;
		}
		
		Operator opeStack = operatorStack.lastElement();
		if(opeStack.getOperator() == '(' ||
				opeCurrent.getPriority() > opeStack.getPriority()){
			operatorStack.push(opeCurrent);
		} else if(opeCurrent.getPriority() <= opeStack.getPriority()){//if curr operator's priotiry is lower
			Operator ope = null;
			while(!operatorStack.isEmpty()){
				ope = operatorStack.pop();
				if(opeCurrent.getPriority() > ope.getPriority() ||
						ope.getOperator() == '(' ||
						ope.getOperator() == ')' ){
					break;
				}
				operandStack.push(String.valueOf(ope.getOperator()));
			}
			operatorStack.push(opeCurrent);
		}
	}
	
	private void dealRightparentheses(Operator opeCurrent){
		Operator ope = null;
		while(!operatorStack.isEmpty()){
			ope = operatorStack.pop();
			if(ope.getOperator() == '('){
				break;
			}
			operandStack.push(String.valueOf(ope.getOperator()));
		}
	}
}
