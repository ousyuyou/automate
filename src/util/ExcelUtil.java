package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import calculate.Calculator;

public class ExcelUtil {
	private static final int DEFAULT_EXCEL_COLUMN_DIGIT_WIDTH = 2;
	private static final String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M",
		"N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	
	private static HashMap<String,Integer> map = new HashMap<String,Integer>();
	private static int index = 0;
	/**
	 * 
	 * @param fileName excel file name
	 * @param sheetNo excel sheet no
	 * @param destColNames key=ISSUE_ID value=B;key=ISSUE_REVIEWER value=E...
	 * @param filterPattern ISSUE_ID=XXX100&ISSUE_REVIEWER=XXX101
	 * @return ISSUE_ID=value1,ISSUE_REVIEWER=value2...
	 * @throws IOException
	 */
	public static Map<String, String>[] readContentFromExcelMult(String fileName,int sheetNo,Map<String,String> destColNames,
			String columnNameFilterExpress,int startRow) throws IOException{
		ArrayList<TreeMap<String,String>> arrayTarget = new ArrayList<TreeMap<String,String>>();
		FileInputStream fileIn = null;
		
//		System.out.println("fileName: "+fileName);
		try{
			fileIn = new FileInputStream(fileName);
	        Workbook wb = new XSSFWorkbook(new FileInputStream(fileName));
	        Sheet sheet = wb.getSheetAt(sheetNo);
	        
	        //init
	        if(map == null || map.size() == 0){
	        	setColumnNameIndex(DEFAULT_EXCEL_COLUMN_DIGIT_WIDTH,map,DEFAULT_EXCEL_COLUMN_DIGIT_WIDTH);
	        }
	        
	        //convert column alpha to column index
	        TreeMap<String, Integer> destColIndexs = convertColumnAlphaToIndex(destColNames);
	        
	        for(int i = startRow; i <= sheet.getLastRowNum();i++){
	        	Row row = sheet.getRow(i);
	        	if(row == null)//bug fix,why null?
	        		continue;
	        	String calculateValue = "";
	        	
	        	if(!StringUtils.isBlank(columnNameFilterExpress)){//filter calculate
		        	String strCalculate = new String(columnNameFilterExpress);
		        	for(String columnName:destColIndexs.keySet()){
		        		if(columnNameFilterExpress.contains(columnName)){
		        			int columnIndex = destColIndexs.get(columnName).intValue();
		        			String cellValue = escapeString(getCellValue(row, columnIndex));
		        			//replace "" blank to " " for calculate
		        			if(StringUtils.isBlank(cellValue)){
		        				cellValue = " ";
		        			}
		        			strCalculate = strCalculate.replaceAll(columnName, cellValue);
		        		}
		        	}
		    		calculateValue = Calculator.calculate(strCalculate,false);
	        	}
	        	
	        	if(StringUtils.isBlank(columnNameFilterExpress) || 
	        			"1".equals(calculateValue)){
	        		TreeMap<String, String> map = new TreeMap<String, String>();
	        		
	        		for(String columnName:destColNames.keySet()){
	        			map.put(columnName, getCellValue(row,destColIndexs.get(columnName).intValue()));
	        		}
	        		
	        		arrayTarget.add(map);
	        	}
	    		

	        }
		} finally{
			if(fileIn!=null){
				fileIn.close();
			}
		}
		Map[] mapArr = new TreeMap[arrayTarget.size()];
		arrayTarget.toArray(mapArr);
		return mapArr;
	}
	
	private static String getCellValue(Row row,int columnIndex){
		String value = "";
		if(row.getCell(columnIndex) == null){//bug?
			//default set to space
			value = "";
		}else {
   			switch(row.getCell(columnIndex).getCellType()){
        		case Cell.CELL_TYPE_NUMERIC:
        		case Cell.CELL_TYPE_FORMULA:
        			try{
        				value = String.valueOf(row.getCell(columnIndex).getNumericCellValue()).trim();
        			} catch(IllegalStateException e){
        				value = String.valueOf(row.getCell(columnIndex).getRichStringCellValue()).trim();
        			}
        			break;
        		default:
        			value = row.getCell(columnIndex).getStringCellValue().trim();
        			break;
   			}
		}
		return value;
	}
	
	private static TreeMap<String, Integer> convertColumnAlphaToIndex(Map<String,String> destColNames){
		TreeMap<String, Integer> destColIndexs = new TreeMap<String, Integer>();
		
		for(String columnName:destColNames.keySet()){
			destColIndexs.put(columnName, map.get(destColNames.get(columnName)));
		}
		
		return destColIndexs;
	}
	
	/**
	 * replace keyword
	 * @param in
	 * @return
	 */
	public static String escapeString(String in){
		String strOut = new String(in);
		strOut = strOut.replaceAll("[&]", "and__escape");
		strOut = strOut.replaceAll("[|]", "or__escape");
		strOut = strOut.replaceAll("[(]", "leftbracket__escape");
		strOut = strOut.replaceAll("[)]", "rightbracket__escape");
		strOut = strOut.replaceAll("[+]", "plus__escape");
		strOut = strOut.replaceAll("[-]", "minus__escape");
		strOut = strOut.replaceAll("[*]", "mulpity__escape");
		strOut = strOut.replaceAll("[/]", "divide__escape");
		strOut = strOut.replaceAll("[=]", "equal__escape");
		strOut = strOut.replaceAll("[!]", "notequal__escape");
		strOut = strOut.replaceAll("[>]", "greater__escape");
		strOut = strOut.replaceAll("[<]", "lesses__escape");
		
		return strOut;
	}
	
	/**
	 * replace &amp; with& 
	 * @param in
	 * @return
	 */
//	public static String unescapeAmp(String in){
//		return in.replaceAll("&amp;", "&");
//	}
	
	
	private static String[] setColumnNameIndex(int digit,HashMap<String,Integer> out,int digitLoop){
		ArrayList<String> arrayConnect = new ArrayList<String>();
		
		if(digitLoop>1){
			String[] lower = setColumnNameIndex(digit, out,digitLoop-1);
			StringBuffer buf = new StringBuffer();
			for(String alpha:alphabet){
				buf.append(alpha);
				for(String strRtn:lower){
					buf.append(strRtn);
					out.put(buf.toString(), Integer.valueOf(index++));
					arrayConnect.add(buf.toString());
					buf.delete(alpha.length(), buf.capacity()-1);
				}
				//clear
				buf.delete(0, buf.capacity()-1);
			}
			String[] strRtn = new String[arrayConnect.size()];
			arrayConnect.toArray(strRtn);
			arrayConnect = null;
			return strRtn;
		} else {
			for(String str:alphabet){
				out.put(str, Integer.valueOf(index++));
			}
			return alphabet;
		}
	}
}
