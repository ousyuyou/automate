package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;

import calculate.Calculator;

public class ExcelUtil {
	private static final int DEFAULT_EXCEL_COLUMN_DIGIT_WIDTH = 2;
	private static final String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M",
		"N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	
	private static HashMap<String,Integer> map = new HashMap<String,Integer>();
	private static int index = 0;
	
	public static String WRITE_KEY = "$WRITE_KEY$";
	private static final String ISSUE_ID = "ISSUE_ID";

	public static void main(String[] args) throws IOException{
	}
	
	/**
	 * 
	 * @param fileName excel file path
	 * @param sheetNames sheet name to get index 
	 * @return
	 * @throws IOException
	 */
	public static int[] findSheetExistByName(String fileName,String[] sheetNames) throws IOException{
		int[] sheetNos = new int[sheetNames.length];
		FileInputStream fileIn = null;
		try{
			fileIn = new FileInputStream(fileName);
	        Workbook wb = getWorkBookByExcelPath(fileName);
	        
	        for(int i = 0;i < sheetNames.length; i++){
	        	sheetNos[i] = wb.getSheetIndex(sheetNames[i]) == -1 ? 0 : 1;
	        }
		} finally{
			if(fileIn!=null){
				fileIn.close();
			}
		}
		
		return sheetNos;
	}
	
	/**
	 * get all sheet names
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String[] getAllSheetNames(String fileName) throws IOException{
		String[] sheetNames = null;
		FileInputStream fileIn = null;
		
		try{
			fileIn = new FileInputStream(fileName);
	        Workbook wb = getWorkBookByExcelPath(fileName);
	       
	        if(wb instanceof XSSFWorkbook){
	        	XSSFWorkbook xsf = (XSSFWorkbook)wb;
	        	CTWorkbook ctb = xsf.getCTWorkbook();
	        	CTSheet[] sheets =  ctb.getSheets().getSheetArray();
	        	sheetNames = new String[sheets.length];
	        	for(int i = 0 ; i < sheets.length; i++){
	        		sheetNames[i] = sheets[i].getName();
	        		//System.out.println(sheetNames[i]);
	        	}
	        }else if(wb instanceof HSSFWorkbook){
	        	HSSFWorkbook hsf = (HSSFWorkbook)wb;
	        	InternalWorkbook itwb = hsf.getInternalWorkbook();
	        	//itwb.get
	        	int num = itwb.getNumSheets();
	        	sheetNames = new String[num];
	        	for(int i = 0;i < num;i++){
	        		sheetNames[i] = itwb.getSheetName(i);
	        		//System.out.println(sheetNames[i]);
	        	}
	        }
	       
		} finally{
			if(fileIn!=null){
				fileIn.close();
			}
		}
		return sheetNames;
	}
	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static int getSheetNums(String fileName) throws IOException{
		FileInputStream fileIn = null;
		
		try{
			fileIn = new FileInputStream(fileName);
	        Workbook wb = getWorkBookByExcelPath(fileName);
	       
	        if(wb instanceof XSSFWorkbook){
	        	XSSFWorkbook xsf = (XSSFWorkbook)wb;
	        	CTWorkbook ctb = xsf.getCTWorkbook();
	        	return ctb.getSheets().getSheetArray().length;
	        }else if(wb instanceof HSSFWorkbook){
	        	HSSFWorkbook hsf = (HSSFWorkbook)wb;
	        	InternalWorkbook itwb = hsf.getInternalWorkbook();
	        	//itwb.get
	        	return itwb.getNumSheets();
	        }
	       
		} finally{
			if(fileIn!=null){
				fileIn.close();
			}
		}
		return 0;
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
			String columnNameFilterExpress,int startRow,HashSet<String> htIssueForWrite) throws IOException{
		ArrayList<TreeMap<String,String>> arrayTarget = new ArrayList<TreeMap<String,String>>();
		FileInputStream fileIn = null;
		
//		System.out.println("fileName: "+fileName);
		try{
			fileIn = new FileInputStream(fileName);
	        Workbook wb = getWorkBookByExcelPath(fileName);

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
	        			if(WRITE_KEY.equals(columnName)){//write the FILTER_EXPRESS_WRITE_KEY to excel
//	        				do not read
	        			} else {
	        				map.put(columnName, getCellValue(row,destColIndexs.get(columnName).intValue()));
	        			}
	        		}
	        		//for write
	        		if(destColNames.containsKey(WRITE_KEY)){
	        			String issueID = map.get(ISSUE_ID);
	        			if(htIssueForWrite!= null && htIssueForWrite.contains(issueID)){
	        				Cell cell = row.createCell(destColIndexs.get(WRITE_KEY).intValue());
	        				cell.setCellValue(issueID);
	        			}
	        		}
	        		
	        		arrayTarget.add(map);
	        	}
	        }
	        fileIn.close();
	        
	        if(destColNames.containsKey(WRITE_KEY)){
		        FileOutputStream out = new FileOutputStream(fileName);
		        wb.write(out);
		        out.close();
	        }
	        
		} finally{
			//
		}
		Map[] mapArr = new TreeMap[arrayTarget.size()];
		arrayTarget.toArray(mapArr);
		return mapArr;
	}
	
	/**
	 * 
	 * @param fileName
	 * @param sheetNo: start from zero
	 * @param rowno: start from zero
	 * @param alphaColumnName: column's alphabet name
	 * @return
	 * @throws IOException
	 */
	public static String[] getValueByRowColumn(String fileName,int sheetNos[],int rowno,String alphaColumnName)
							throws IOException{
		String values[] = new String[sheetNos.length];
		FileInputStream fileIn = null;
		try{
			fileIn = new FileInputStream(fileName);
	        Workbook wb = getWorkBookByExcelPath(fileName);
	        
	        //init
	        if(map == null || map.size() == 0){
	        	setColumnNameIndex(DEFAULT_EXCEL_COLUMN_DIGIT_WIDTH,map,DEFAULT_EXCEL_COLUMN_DIGIT_WIDTH);
	        }
	        Map<String,String> mapColumn = new HashMap<String, String>();
	        mapColumn.put("value", alphaColumnName);
	        
	        //convert column alpha to column index
	        TreeMap<String, Integer> destColIndexs = convertColumnAlphaToIndex(mapColumn);
	        
	        for(int i = 0 ; i < sheetNos.length; i++){
	        	Sheet sheet = wb.getSheetAt(sheetNos[i]);
		        Row row = sheet.getRow(rowno);
		        if(row == null){//blank row
		        	continue;
		        }
		        values[i] = getCellValue(row,destColIndexs.get("value").intValue());
	        }
	        
		} finally{
			if(fileIn!=null){
				fileIn.close();
			}
		}
		
		return values;
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
	
	private static Workbook getWorkBookByExcelPath(String filepath) throws IOException{
		if(filepath==null){  
            return null;  
        }  
        String ext = filepath.substring(filepath.lastIndexOf("."));
        
        Workbook wb = null;  
        
        InputStream is = new FileInputStream(filepath);
        try{
            if(".xls".equals(ext)){  
                wb = new HSSFWorkbook(is);  
            }else if(".xlsx".equals(ext)){  
                wb = new XSSFWorkbook(is);  
            } else {  
                wb=null;  
            }
        	
        } catch(Exception ioe){
        	System.err.println(filepath);
        	ioe.printStackTrace();
        	throw new IOException(ioe);
        }
        
        return wb;
 
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
