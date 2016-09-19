import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class CheckFile {
	private static final String SVN_INSTALL_PATH = "C:/Program Files/TortoiseSVN/bin/TortoiseProc.exe";
	private static final String SPACE = " ";
	private static final int DEFAULT_EXCEL_COLUMN_DIGIT_WIDTH = 2;
	private static HashMap<String,Integer> map = new HashMap<String,Integer>();
	
	private static final String CHANGE_LIST_FILE = "E:/svn/xxx/00_管理/05_進捗管理/10_残案件対応/案件状況一覧.xlsx";
	private static final String RESULT_OUTPUT_PATH = "E:/svn/xxx/30_内部設計/25_修正影響調査";
	static final String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M",
		"N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		
//		setColumnNameIndex(DEFAULT_EXCEL_COLUMN_DIGIT_WIDTH,map,DEFAULT_EXCEL_COLUMN_DIGIT_WIDTH);
//		for(String str:map.keySet()){
//			System.setOut(new PrintStream(new FileOutputStream("e:\\out.txt",true)));
//			System.out.println(str + " " + String.valueOf(map.get(str)));
//		}
		
		ArrayList<String> out = checkFileExistsFromExcel(CHANGE_LIST_FILE,"B","L=未リリース",RESULT_OUTPUT_PATH);
		for(String str:out){
			System.out.println(str);
		}
		
	}
	
	/**
	 * 
	 * @param fromExcel
	 * @param destColNumber
	 * @param filterPattern:just support
	 * @param destDirectory
	 */
	public static ArrayList<String> checkFileExistsFromExcel(String fromExcel,String destColAlpha,String filterPattern,String destDirectory) throws IOException{
		//update svn
		updateSvn(destDirectory);
		ArrayList<String> out = new ArrayList<String>();
		
		ArrayList<String> arrTarget = readTargetFromExcel(fromExcel,destColAlpha,filterPattern);
//		System.out.println("Target from");
//		for(String str:arrTarget){
//			System.out.println(str);
//		}
//		System.out.println("Target end");
		
		ArrayList<String> arrResult = new ArrayList<String>();
		listFiles(new File(destDirectory), arrResult);
		
		Collections.sort(arrTarget);
		Collections.sort(arrResult);
		
		String[] strTarget = new String[arrTarget.size()]; 
		arrTarget.toArray(strTarget);
		String[] strResult = new String[arrResult.size()]; 
		arrResult.toArray(strResult);
		
		int startIndex=0;
		for(int i = 0; i < strTarget.length;i++){
			boolean match = false;
			
			for(int j=startIndex;j <strResult.length;j++){
				if(strResult[j].contains(strTarget[i])){
					swap(strResult,j,startIndex);
					startIndex++;
					match = true;
					break;
				}
			}
			
			if(match==false){
				out.add(strTarget[i]);
			}
		}
		
		return out;
	}
	
	private static void swap(String[] array,int i,int j){
		String tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}
	
	/**
	 * list all file
	 * @param f
	 * @param arrayOut
	 */
	public static void listFiles(File f,ArrayList<String> arrayOut){
		 if(f!=null){
	            if(f.isDirectory()){
	                File[] fileArray=f.listFiles();
	                if(fileArray!=null){
	                    for (int i = 0; i < fileArray.length; i++) {
	                    	listFiles(fileArray[i],arrayOut);
	                    }
	                }
	            } else {
	            	arrayOut.add(f.getName());
	            }
	     }
	}

	/**
	 * update svn
	 * @param destDirectory
	 */
	public static void updateSvn(String destDirectory){
		StringBuffer strBufClean = new StringBuffer();

		//clean
		strBufClean.append("/command:cleanup /path:");
		strBufClean.append(destDirectory);
		strBufClean.append(SPACE);
		strBufClean.append("/notempfile /noui /closeonend:3");
		String[] commandClean = new String[]{SVN_INSTALL_PATH, strBufClean.toString()};

		//update
		StringBuffer strBufUpdate = new StringBuffer();
		strBufUpdate.append("/command:update /path:");
		strBufUpdate.append(destDirectory);
		strBufUpdate.append(SPACE);
		strBufUpdate.append("/notempfile /closeonend:3");
		String[] commandUpdate = new String[]{SVN_INSTALL_PATH,strBufUpdate.toString()};
		
		try{
			Runtime.getRuntime().exec(commandClean);
			Runtime.getRuntime().exec(commandUpdate);
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void updateSvnFromBat(String batFile){
		String command[]  = new String[]{batFile};
		try{
			Runtime.getRuntime().exec(command);
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private static ArrayList<String> readTargetFromExcel(String fromExcel,String destColAlpha,String filterPattern) throws IOException{
		ArrayList<String> arrayTarget = new ArrayList<String>();
		FileInputStream fileIn = null;
		
		try{
			fileIn = new FileInputStream(fromExcel);
	        Workbook wb = new XSSFWorkbook(new FileInputStream(fromExcel));
	        Sheet sheet = wb.getSheetAt(0);
	        
	        //parse filterPattern,just support &
	        String[] strPatterns = filterPattern.split("&");
	        HashMap<String, String> hashPattern = new HashMap<String, String>();
	        
	        for(String str:strPatterns){
	        	String[] split = str.split("=");
	        	if(split.length == 2){
	        		hashPattern.put(split[0], split[1]);
	        	}
	        }
			setColumnNameIndex(DEFAULT_EXCEL_COLUMN_DIGIT_WIDTH,map,DEFAULT_EXCEL_COLUMN_DIGIT_WIDTH);

	        int destColNumber = getColunIndexFromAlpha(destColAlpha); 
	        for(int i = 0; i <= sheet.getLastRowNum();i++){
	        	Row row = sheet.getRow(i);
	        	boolean matchOk = true;
	        	
	        	for(String alphaIndex:hashPattern.keySet()){//filter by pattern
	        		String value = "";
	        		int columnIndex = getColunIndexFromAlpha(alphaIndex);
	        		//bug?
	        		if(row.getCell(columnIndex) == null){
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

	        		if(!matchValue(hashPattern.get(alphaIndex),value)){
	        			matchOk = false;
	        			break;
	        		}
	        	}
	        	
	        	if(matchOk){
	        		String target = row.getCell(destColNumber).getStringCellValue().trim();
	        		arrayTarget.add(target);
	        	}
	        }
		} finally{
			if(fileIn!=null){
				fileIn.close();
			}
		}
		
		return arrayTarget;
		
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
	
	/**
	 * get excel column index from column alpha name 
	 * @param alpha
	 * @return
	 */
	private static int getColunIndexFromAlpha(String alpha){
		return map.get(alpha.toUpperCase()).intValue();
	}

	private static HashMap<String,Integer> setColumnNameIndex_bak(int digit){
		String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M",
				"N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		int index = 0;
		HashMap<String,Integer> map = new HashMap<String, Integer>();
		ArrayList<String> arrayConnect = new ArrayList<String>();
		ArrayList<String> arrayCreate = new ArrayList<String>();
		
		//init to ""
		arrayConnect.add("");
		
//		for(String alpha:alphabet){
//			map.put(alpha, Integer.valueOf(index++));
//			arrayConnect.add(alpha);
//		}
		
		int digit_loop = 0;
		StringBuffer buf = new StringBuffer();
		while(digit_loop<digit){
			for(String alpha:alphabet){//A~Z
				buf.append(alpha);
				for(String alphaArr:arrayConnect){//first time is ""
					buf.append(alphaArr);
					map.put(buf.toString(), Integer.valueOf(index++));
					arrayCreate.add(buf.toString());
					buf.delete(alpha.length(), buf.capacity()-1);
				}
				//clear
				buf.delete(0, buf.capacity()-1);
			}
			arrayConnect.clear();
			arrayConnect = new ArrayList<String>(arrayCreate);
			arrayCreate.clear();
			
			digit_loop++;
		}
		
		return map;
	}
	
	static int index = 0;
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
