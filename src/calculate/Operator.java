package calculate;

public class Operator {
	/**
	 * +,-,*,/,(,)
	 */
	private char operator;
	private int priority;
	
	private Operator(){
	}
	
	public static boolean isOperator(char c,boolean mathExpression){
		if(mathExpression){
			switch(c){
				case '+':
				case '-':
				case '*':
				case '/':
					return true;
			}
		}
		
		switch(c){
			case '(':
			case ')':
			case '>':
			case '<':
			case '=':
			case '&':
			case '|':
			case '!':
			case '½':
				return true;
			default:
				return false;
		}
	}
	
	public static Operator getOperator(char c) throws IllegalArgumentException{
		Operator ope = new Operator();
		ope.operator = c;
		switch(c){
			case '+':
			case '-':
			case '>':
			case '<':
			case '=':
			case '!':
			case '½':
				ope.priority = 1;
				break;
			case '*':
			case '/':
				ope.priority = 2;
				break;
			case '&':
			case '|':
				ope.priority = -1;
				break;
			case '(':
			case ')':
				ope.priority = 0;
				break;
			default:
				throw new IllegalArgumentException("not supported operator");
		}
		return ope;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	public char getOperator() {
		return operator;
	}

	public void setOperator(char operator) {
		this.operator = operator;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}
