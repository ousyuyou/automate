package excelio;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import util.ExcelUtil;


public class FunctionFactory {
	//functions map
	private Map<String, Function> map = null;
	/**
	 * issue list
	 */
	private static Map<String,String> columnNameMap = new HashMap<String, String>();
	static {
		columnNameMap.put("no", "A");
		columnNameMap.put("functionName", "B");
		columnNameMap.put("compareTableID", "D");
		columnNameMap.put("compareSetFile", "E");
		columnNameMap.put("inputTable", "H");
		columnNameMap.put("outputTable", "I");
		columnNameMap.put("shellVer1", "J");
		columnNameMap.put("shellVer2", "K");
		columnNameMap.put("taisyo", "M");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException,ClassNotFoundException{
		FunctionFactory factory = new FunctionFactory();
		Map<String,Function> mapFunction = factory.readFunctionsFromExcel("e:/åüèÿëŒè€àÍóó.xlsx");

		for(String key:mapFunction.keySet()){
			Function function = mapFunction.get(key);
			System.out.println(function.getNo() + " " + function.getFunctionName() + " compareTableIDs "+function.getCompareTables().length
					+" inputTables: "+function.getInputTables().length + " outputTable: "+function.getOutputTables().length
					+" shell1: "+function.getShellsVer1().length  +" shell2: "+function.getShellsVer2().length);	
		}
		
	}
	
	public FunctionFactory(){
		//maintence the insert sequence
		map = new LinkedHashMap<String, Function>();
	}

	public Map<String,Function> readFunctionsFromExcel(String excelFilePath) throws IOException{
		Map<String,String>[] mapTarget = ExcelUtil.readContentFromExcelMult(excelFilePath, 0, columnNameMap, "(compareTableID! |inputTable! |outputTable! )&taisyo=Åõ",0);
		
		try{
			LineData lineDataBean = null;
			//if blank,set the previous no
			String prevNo = "";
			String prevFunctionName = "";
		
			for(int i = 0 ; i <mapTarget.length; i++){
				Class<?> c = Class.forName("excelio.LineData");
				lineDataBean = (LineData)c.newInstance();
				
				for(String key:mapTarget[i].keySet()){
					setLineDataBean(c,lineDataBean,key,mapTarget[i].get(key));
				}
				
				if(StringUtils.isBlank(lineDataBean.getNo())){//if blank,set the previous no
					lineDataBean.setNo(prevNo);
					lineDataBean.setFunctionName(prevFunctionName);
				} else {
					prevNo = lineDataBean.getNo();
					prevFunctionName = lineDataBean.getFunctionName();
				}
				constructLineData(lineDataBean);
			}

		}catch(ClassNotFoundException cnfe){
			cnfe.printStackTrace();
		}catch(IllegalAccessException iae){
			iae.printStackTrace();
		}catch (InstantiationException itie) {
			itie.printStackTrace();
		}
		
		return map;
	
	}
	
	@SuppressWarnings("unchecked")
	private void setLineDataBean(Class c,Object lineDataBean,String colunName,String value) {
		
		String methodName = "set" + StringUtils.upperCase(colunName.substring(0,1)) + colunName.substring(1);
		
		Method setMethod;
		try {
			setMethod = c.getMethod(methodName,new Class[]{String.class});
			setMethod.invoke(lineDataBean, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void constructLineData(LineData dataBean){
		//no is the key
		String no = dataBean.getNo();
		Function function = (Function)map.get(no);
		//does not exists
		if(function == null){
			function = new Function(dataBean.getNo(),dataBean.getFunctionName());
		}
		
		//compare tables
		if(!StringUtils.isBlank(dataBean.getCompareTableID())){
			TableForCompare table = new TableForCompare(dataBean.getCompareTableID(),dataBean.getCompareSetFile());
			function.addTableForCompare(table);
		}
		
		//input table
		if(!StringUtils.isBlank(dataBean.getInputTable())){
			function.addInputTable(dataBean.getInputTable());
		}

		//output table
		if(!StringUtils.isBlank(dataBean.getOutputTable())){
			function.addOutputTable(dataBean.getOutputTable());
		}
		//shell ver1
		if(!StringUtils.isBlank(dataBean.getShellVer1())){
			String[] strSplit = dataBean.getShellVer1().split("\n");
			for(int i = 0 ; i < strSplit.length;i ++){
				if(!StringUtils.isBlank(strSplit[i])){
					function.addShellVer1(strSplit[i].trim());
				}
			}
		}

		//shell ver2
		if(!StringUtils.isBlank(dataBean.getShellVer2())){
			String[] strSplit = dataBean.getShellVer2().split("\n");
			for(int i = 0 ; i < strSplit.length;i ++){
				if(!StringUtils.isBlank(strSplit[i])){
					function.addShellVer2(strSplit[i].trim());
				}
			}
		}
		//put into map&list
		if(!map.containsKey(no)){
			map.put(no, function);
		}
		
	}

}