package run;

public class Shell {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		execute();
	}
	
	public static void execute(){
		String command[]  = new String[]{"C:/Program Files (x86)/teraterm/ttpmacro.exe","E:/004_soft/eclipse/workspace/AutoTest/config/connect.ttl"};
		try{
			Runtime.getRuntime().exec(command);
			
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
