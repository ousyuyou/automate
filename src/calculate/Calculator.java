package calculate;

import java.math.BigDecimal;
import java.util.ListIterator;
import java.util.Stack;

public class Calculator {
	private static Stack<String> operand  = new Stack<String>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = "FS_DC_MPS_V3.20.8.B01\nFS_DC_MPS_V3.20.9.B01½FS_DC_MPS_V3.20.9.B01";
//		String str = "(abc>ab)&(3!2|2>1)";

		FormularParser parser = new FormularParser();
		Calculator cal = new Calculator();
		Stack<String> ope = parser.parse(str,false);
		System.out.println(parser.toString());
				
		String dec = calculate(ope,false);
		System.out.println(dec);
	}
	
	public Calculator(){
	}
	
	/**
	 * 
	 * @param expression
	 * @param mathExpression 
	 * @return
	 */
	public static String calculate(String expression, boolean mathExpression){
		FormularParser parser = new FormularParser();
		Stack<String> ope = parser.parse(expression,mathExpression);
				
		String value = calculate(ope,mathExpression);
		return value;
	}
	
	private static String calculate(Stack<String> ope,boolean mathExpression){
		String v = new String();
		ListIterator<String> iter = ope.listIterator();
		while(iter.hasNext()){
			String str = iter.next();
			if(str.length() == 1 && 
					Operator.isOperator(str.charAt(0),mathExpression)){
				switch(str.charAt(0)){
					case '+':
					case '-':
					case '*':
					case '/':
					case '>':
					case '<':
					case '=':
					case '&':
					case '|':
					case '!':
					case '½':
						String op2 = operand.pop();
						String op1 = operand.pop();
						v = fundamentalCalculate(str.charAt(0),op1,op2);
						operand.push(v);
					default:
						break;
				}
			} else {
				operand.push(str);
			}
		}
		return v;
	}
	
	private static String fundamentalCalculate(char operator,String strOp1,String strOp2) throws IllegalArgumentException{
		int v = 0;
		BigDecimal op1 = new BigDecimal(0);
		BigDecimal op2 = new BigDecimal(0);
		switch(operator){
			case '>':
				v = strOp1.compareTo(strOp2)>0?1:0;
				return String.valueOf(v);
			case '<':
				v = strOp1.compareTo(strOp2)<0?1:0;
				return String.valueOf(v);
			case '=':
				v = strOp1.equals(strOp2)?1:0;
				return String.valueOf(v);
			case '!':
				v = strOp1.equals(strOp2)?0:1;
				return String.valueOf(v);
			case '½':
				v = strOp1.contains(strOp2)?1:0;
				return String.valueOf(v);
			case '+':
			case '-':
			case '*':
			case '/':
			case '&':
			case '|':
				op1 = new BigDecimal(strOp1);
				op2 = new BigDecimal(strOp2); 
				break;
			default:
				throw new IllegalArgumentException("not supported");
		}
		
		switch(operator){
			case '+':
				return String.valueOf(op1.add(op2));
			case '-':
				return String.valueOf(op1.subtract(op2));
			case '*':
				return String.valueOf(op1.multiply(op2));
			case '/':
				return String.valueOf(op1.divide(op2));
			case '&':
				return String.valueOf(op1.intValue()&op2.intValue());
			case '|':
				return String.valueOf(op1.intValue()|op2.intValue());
		}
		
		return null;
	}
}
