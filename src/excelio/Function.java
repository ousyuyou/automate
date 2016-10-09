package excelio;

import java.util.ArrayList;

public class Function {
	//test No
	private String no = null;
	
	//‹@”\–¼
	private String functionName = null;
	
	private ArrayList<String> itable;
	private ArrayList<String> otable;
	private ArrayList<String> shell1;
	private ArrayList<String> shell2;
	private ArrayList<TableForCompare> comparetable;
	
	public Function(){
		this("","");
	}
	
	public Function(String no,String functionName){
		this.no = no;
		this.functionName = functionName;
		itable = new ArrayList<String>();
		otable = new ArrayList<String>();
		shell1 = new ArrayList<String>();
		shell2 = new ArrayList<String>();
		comparetable = new ArrayList<TableForCompare>();
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public TableForCompare[] getCompareTables() {
		TableForCompare[] compareTables = new TableForCompare[comparetable.size()];
		comparetable.toArray(compareTables);
		return compareTables;
	}
	
	public void addTableForCompare(TableForCompare table){
		comparetable.add(table);
	}

	public String[] getInputTables() {
		String[] inputTables = new String[itable.size()];
		itable.toArray(inputTables);
		return inputTables;
	}
	
	public void addInputTable(String inputTable){
		itable.add(inputTable);
	}

	public String[] getOutputTables() {
		String[] outputTables = new String[otable.size()];
		otable.toArray(outputTables);
		return outputTables;
	}
	
	public void addOutputTable(String outputTable){
		otable.add(outputTable);
	}

	public String[] getShellsVer1() {
		String[] shellVer1 = new String[shell1.size()];
		shell1.toArray(shellVer1);
		return shellVer1;
	}
	
	public void addShellVer1(String shellVer1){
		shell1.add(shellVer1);
	}

	public String[] getShellsVer2() {
		String[] shellVer2 = new String[shell2.size()];
		shell2.toArray(shellVer2);
		return shellVer2;
	}
	public void addShellVer2(String shellVer2){
		shell2.add(shellVer2);
	}

}
