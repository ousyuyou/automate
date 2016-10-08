package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import calculate.*;

public class ExcelUtil {
	private static final int DEFAULT_EXCEL_COLUMN_DIGIT_WIDTH = 2;
	private static final String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M",
		"N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	
	private static HashMap<String,Integer> map = new HashMap<String,Integer>();
	private static int index = 0;
	public static final String CONNECT_CHAR = "&&";
	
	/**
	 * 
	 * @param fileName excel file path;the manager file's path
	 * @param sheetNo:index from 0
	 * @param destColAlpha:A,B,C
	 * @param filterPattern:just support;for example C=OK&&D=100
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String>[] readContentFromExcel(String fileName,int sheetNo,String destColAlpha,String filterPattern) throws IOException{
		return readContentFromExcelMult(fileName,sheetNo,new String[]{destColAlpha},filterPattern);
	}
	
	/**
	 * 
	 * @param fileName excel file path;the manager file's path
	 * @param sheetNo:index from 0
	 * @param destColAlpha:A,B,C
	 * @param filterPattern:just support;for example C=OK&&D=100
	 * @return  A=value1,B=value2...
	 */
	public static Map<String, String>[] readContentFromExcelMult(String fileName,int sheetNo,String[] destColAlphas,String columnAlphaFilter) throws IOException{
		ArrayList<TreeMap<String,String>> arrayTarget = new ArrayList<TreeMap<String,String>>();
		FileInputStream fileIn = null;
		
		try{
			fileIn = new FileInputStream(fileName);
	        Workbook wb = new XSSFWorkbook(new FileInputStream(fileName));
	        Sheet sheet = wb.getSheetAt(sheetNo);
	        
	        //parse filterPattern,just support &&
	        String[] strPatterns = columnAlphaFilter.split(CONNECT_CHAR);
	        TreeMap<String, String> hashPattern = new TreeMap<String, String>();
	        
	        for(String str:strPatterns){
	        	String[] split = str.split("=");
	        	if(split.length == 2){
	        		hashPattern.put(split[0], split[1]);
	        	}
	        }
	        //init
	        if(map == null || map.size() == 0){
	        	setColumnNameIndex(DEFAULT_EXCEL_COLUMN_DIGIT_WIDTH,map,DEFAULT_EXCEL_COLUMN_DIGIT_WIDTH);
	        }

	        int[] destColNumbers = getColunIndexFromAlphaMult(destColAlphas); 
	        for(int i = 0; i <= sheet.getLastRowNum();i++){
	        	Row row = sheet.getRow(i);
	        	boolean matchOk = true;
	        	
	        	for(String alphaIndex:hashPattern.keySet()){//filter by pattern
	        		String value = "";
	        		int columnIndex = getColunIndexFromAlpha(alphaIndex);
	        		value = getCellValue(row, columnIndex);
	        		
	        		if(!matchValue(hashPattern.get(alphaIndex),value)){
	        			matchOk = false;
	        			break;
	        		}
	        	}
	        	
	        	if(matchOk){
	        		TreeMap<String, String> map = new TreeMap<String, String>();
	        		for(int j = 0 ; j < destColNumbers.length;j++){
	        			map.put(destColAlphas[j], getCellValue(row, destColNumbers[j]));
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
			String columnNameFilterExpress) throws IOException{
		ArrayList<TreeMap<String,String>> arrayTarget = new ArrayList<TreeMap<String,String>>();
		FileInputStream fileIn = null;
		
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
	        
	        for(int i = 0; i <= sheet.getLastRowNum();i++){
	        	Row row = sheet.getRow(i);
	        	String strCalculate = new String(columnNameFilterExpress);
	        	for(String columnName:destColIndexs.keySet()){
	        		if(columnNameFilterExpress.contains(columnName)){
	        			int columnIndex = destColIndexs.get(columnName).intValue();
	        			String value = getCellValue(row, columnIndex);
	        			//replace "" blank to " " for calculate
	        			if(StringUtils.isBlank(value)){
	        				value = " ";
	        			}
	        			strCalculate = strCalculate.replaceAll(columnName, value);
	        		}
	        	}
//	        	System.out.print(strCalculate);
	    		String value = Calculator.calculate(strCalculate,false);
//	    		System.out.println(" value "+value);
	    		
	        	if("1".equals(value)){
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
        			value = String.valueOf(row.getCell(columnIndex).getNumericCellValue()).trim();
        			break;
        		default:
        			value = row.getCell(columnIndex).getStringCellValue().trim();
        			break;
   			}
		}
		return value;
	}
	
	/**
	 * get excel column index from column alpha name 
	 * @param alpha
	 * @return
	 */
	private static int[] getColunIndexFromAlphaMult(String[] alphas){
		int[] indexs = new int[alphas.length];
		
		int i = 0;
		for(String alpha:alphas){
			indexs[i] = map.get(alpha.toUpperCase()).intValue();
			i++;
		}
		return indexs;
	}
	
	private static int getColunIndexFromAlpha(String alpha){
		return getColunIndexFromAlphaMult(new String[]{alpha})[0]; 
	}
	
	private static TreeMap<String, Integer> convertColumnAlphaToIndex(Map<String,String> destColNames){
		TreeMap<String, Integer> destColIndexs = new TreeMap<String, Integer>();
		
		for(String columnName:destColNames.keySet()){
			destColIndexs.put(columnName, map.get(destColNames.get(columnName)));
		}
		
		return destColIndexs;
	}
	
	/**
	 * replace & with &amp;
	 * @param in
	 * @return
	 */
	public static String escapeAmp(String in){
		return in.replaceAll("&", "&amp;");
	}
	
	/**
	 * replace &amp; with& 
	 * @param in
	 * @return
	 */
	public static String unescapeAmp(String in){
		return in.replaceAll("&amp;", "&");
	}
	/**
	 * compare without upper or lower
	 * @param patternValue
	 * @param cellValue
	 * @return
	 */
	private static boolean matchValue(String patternValue,String cellValue){
		if(patternValue == cellValue || patternValue.toLowerCase().equals(cellValue.toLowerCase())){
			return true;
		}
		return false;
	}
	
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
