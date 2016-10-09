package check;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import svn.ConfigFile;
import svn.SVNUtil;
import util.ExcelUtil;
import util.FileUtil;
public class CheckFile {
	private static final String SPACE = " ";
	private static String CONFIG_FILE_PATH = "E:/cert/config";
	private static final String ISSUE_ID = "ISSUE_ID";
	private static final String ISSUE_REVIEWER = "ISSUE_REVIEWER";
	private static final String ISSUE_OWNER_ID = "ISSUE_OWNER_ID";
	private static final String ISSUE_STATUS = "ISSUE_STATUS";
	private static final String RESEARCH_STATUS = "RESEARCH_STATUS";

	private static final String MODULE_ID = "MODULE_ID";
	private static final String MODULE_PATH = "MODULE_PATH";
	private static final String PROJECT_ID = "PROJECT_ID";
	private static final String FUNCTION_NAME = "FUNCTION_NAME";
	private static final boolean DEBUG = false;

	/**
	 * issue list
	 */
	private static final String[] strColumnsIssueList = new String[]{"B","E","K","O","P"};
	private static Map<String,String> columnNameMapIssueList = new HashMap<String, String>();
	static {
		columnNameMapIssueList.put(ISSUE_ID, "B");
		columnNameMapIssueList.put(ISSUE_REVIEWER, "E");
		columnNameMapIssueList.put(ISSUE_STATUS, "K");
		columnNameMapIssueList.put(ISSUE_OWNER_ID, "O");
		columnNameMapIssueList.put(RESEARCH_STATUS, "P");
	}
	/**
	 * module list,source
	 */
	private static final String[] strColumnsModuleList = new String[]{"B","C","D","E","F"};
	private static Map<String,String> columnNameMapModuleList = new HashMap<String, String>();
	static {
		columnNameMapModuleList.put(ISSUE_ID, "B");
		columnNameMapModuleList.put(FUNCTION_NAME, "C");
		columnNameMapModuleList.put(PROJECT_ID, "D");
		columnNameMapModuleList.put(MODULE_ID, "E");
		columnNameMapModuleList.put(MODULE_PATH, "F");
	}
	
	/**
	 * module list,database,shell
	 */
	private static final String[] strColumnsCommonModuleList = new String[]{"B","C","D"};
	private static Map<String,String> columnNameMapCommonModuleList = new HashMap<String, String>();
	static {
		columnNameMapCommonModuleList.put(ISSUE_ID, "B");
		columnNameMapCommonModuleList.put(MODULE_ID, "C");
		columnNameMapCommonModuleList.put(MODULE_PATH, "D");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException,SVNException{
		//check research file
//		ConfigFile config = new ConfigFile(new File(CONFIG_FILE_PATH));
//		String configListFile = config.getPropertyValue("check", "issue_list_file");
//		String resultPath = config.getPropertyValue("check", "result_out_path");
//		
//		ArrayList<String> out = checkFileExistsFromExcel(configListFile,"B","P=Åõ",resultPath);
//		for(String str:out){
//			System.out.println(str + " research file does not exists");
//		}
//		
//		//check source commit
//		HashMap<String, String> sources = listSources();
//		Issue[] issues = getIssueInfo("L=ñ¢ÉäÉäÅ[ÉX");
//
//		for(Issue issue:issues){
//			IssueModule[] modules = issue.getModules();
//			
//			if(modules.length == 0){
//				System.out.println(issue.getId()+ " Waring:module list does not find modules");
//			}
//			
//			for(IssueModule module:modules){
//				if(sources.containsKey(module.getModulePath())){
//					List<SVNLogEntry> list = SVNUtil.getHistory(sources.get(module.getModulePath()), getStartDate(-30), getEndDate(), module.getIssueID());
//					if(list.size() ==0){
//						System.out.println(module.getIssueID() + " " +module.getModulePath() +" Warning: no match commit");
//					}else{
//						System.out.println(module.getIssueID() + " " +module.getModulePath() +" Info: commit ok");
//					}
//					
//					if(DEBUG){
//						printLog(list);
//					}
//				} else {
//					System.out.println(module.getIssueID() + " "+module.getModulePath() + " Waring: source path does not exists" );
//				}
//			}
//		}
		//check si test file
		
		//check ut test file
		
		testFilterExpression();
		
	}
	
	private static void testFilterExpression() throws IOException{
		ConfigFile config = new ConfigFile(new File(CONFIG_FILE_PATH));
		String configListFile = config.getPropertyValue("check", "issue_list_file");
		Map<String,String>[] mapTarget = ExcelUtil.readContentFromExcelMult(configListFile, 0, columnNameMapIssueList, "ISSUE_ID!éÛì¸â€ëË244&RESEARCH_STATUS=Åõ");
		
		for(int i = 0 ; i<mapTarget.length;i++){
			System.out.print("issue1  "+mapTarget[i].get(ISSUE_ID));
			System.out.print(" issue2  "+mapTarget[i].get(ISSUE_REVIEWER));
			System.out.print(" issue3  "+mapTarget[i].get(ISSUE_OWNER_ID));
			System.out.print(" issue4  "+mapTarget[i].get(ISSUE_STATUS));
			System.out.print(" issue5  "+mapTarget[i].get(RESEARCH_STATUS));
			System.out.println();
		}
	}
	
	private static void printLog(List<SVNLogEntry> history){
      for(SVNLogEntry logEntry:history){
		System.out.println( "---------------------------------------------" );
		System.out.println ("revision: " + logEntry.getRevision( ) );
		System.out.println( "author: " + logEntry.getAuthor( ) );
		System.out.println( "date: " + logEntry.getDate( ) );
		System.out.println( "log message: " + logEntry.getMessage( ) );
	
		if ( logEntry.getChangedPaths( ).size( ) > 0 ) {
		  System.out.println( );
		  System.out.println( "changed paths:" );
		  Set changedPathsSet = logEntry.getChangedPaths( ).keySet( );

		  for ( Iterator<SVNLogEntryPath> changedPaths = changedPathsSet.iterator( ); changedPaths.hasNext( ); ) {
		  SVNLogEntryPath entryPath = ( SVNLogEntryPath ) logEntry.getChangedPaths( ).get( changedPaths.next( ) );
		  	System.out.println( " "
		  	+ entryPath.getType( )
		 	+ " "
		  	+ entryPath.getPath( )
		  	+ ( ( entryPath.getCopyPath( ) != null ) ? " (from "
		  			+ entryPath.getCopyPath( ) + " revision "
		  			+ entryPath.getCopyRevision( ) + ")" : "" ) );
		  	}
		 }
      }
	}
	
	//30 days before
	private static Date getStartDate(int minusDay){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.add(Calendar.DAY_OF_MONTH, minusDay);
		return cal.getTime();
	}
	
	//today
	private static Date getEndDate(){
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}
	
	private static HashMap<String, String> listSources(){
		ConfigFile config = new ConfigFile(new File(CONFIG_FILE_PATH));
		String sourceFile1 = config.getPropertyValue("check", "mps_source_path1");
		String sourceFile2 = config.getPropertyValue("check", "mps_source_path2");
		String sourceFile3 = config.getPropertyValue("check", "if_source_path1");
		String sourceFile4 = config.getPropertyValue("check", "core_source_path1");
		String sourceFile5 = config.getPropertyValue("check", "core_source_path2");
		
		ArrayList<File> array = new ArrayList<File>();
		FileUtil.listAbsoluteFiles(sourceFile1, array);
		FileUtil.listAbsoluteFiles(sourceFile2, array);
		FileUtil.listAbsoluteFiles(sourceFile3, array);
		FileUtil.listAbsoluteFiles(sourceFile4, array);
		FileUtil.listAbsoluteFiles(sourceFile5, array);
		
		HashMap<String, String> map = new HashMap<String, String>();
		HashSet<String> repeatKeys = new HashSet<String>();
		
		for(File f:array){
			if(map.containsKey(f.getName())){
				if(!repeatKeys.contains(f.getName())){
					repeatKeys.add(f.getName());
				}
			} else {
				map.put(f.getName(), f.getAbsolutePath());
			}
		}
		
		for(File f:array){
			if(repeatKeys.contains(f.getName())){
				map.remove(f.getName());
				String absolutePath = f.getAbsolutePath();
				if(absolutePath.indexOf("\\FMS-CORE") >= 0){
					absolutePath = setMatchKey(absolutePath,"/FMS-CORE");
				}
				if(absolutePath.indexOf("\\FMS-IF") >= 0){
					absolutePath = setMatchKey(absolutePath,"/FMS-IF");
				}
				if(absolutePath.indexOf("\\MPS") >= 0){
					absolutePath = setMatchKey(absolutePath,"/MPS");
				}
				map.put(absolutePath, f.getAbsolutePath());
			}
		}
		
		return map;
	}
	
	private static String setMatchKey(String absolutePath,String findKey){
		String path = absolutePath.replace("\\", "/");
		path = path.substring(path.indexOf(findKey));
		
		return path;
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Issue[] getIssueInfo(String issueListFilterPattern) throws IOException{
		ConfigFile config = new ConfigFile(new File(CONFIG_FILE_PATH));
		String configListFile = config.getPropertyValue("check", "issue_list_file");  
		String moduleListFile = config.getPropertyValue("check", "module_list_file");
		
		//read base info
		Issue[] issues = readIssuesFromConfig(configListFile,strColumnsIssueList,issueListFilterPattern,columnNameMapIssueList);
		//read module info
		setIssueModuleFromConfig(moduleListFile,issues);
		
		return issues;
	}
	
	private static void setIssueModuleFromConfig(String fromExcel,Issue[] issues) throws IOException{
		updateSvn(fromExcel);
		
		Map<String,String>[] mapTarget = ExcelUtil.readContentFromExcelMult(fromExcel,1,strColumnsModuleList,"L=");//sheet 1
		
		HashMap<String, Issue> issueMap = new HashMap<String, Issue>();
		for(Issue issue:issues){
			issueMap.put(issue.getId(), issue);
		}
		
		for(int i = 0 ; i<mapTarget.length;i++){
			String issueID = mapTarget[i].get(columnNameMapModuleList.get(ISSUE_ID));
			if(issueMap.containsKey(issueID)){
				Issue issue = issueMap.get(issueID);
				IssueModule module = new IssueModule();
				module.setIssueID(issueID);
				String modulePath = mapTarget[i].get(columnNameMapModuleList.get(MODULE_PATH));
				module.setModulePath(mapTarget[i].get(columnNameMapModuleList.get(MODULE_PATH)));
				
				if(StringUtils.isBlank(modulePath)){
					module.setModulePath(mapTarget[i].get(columnNameMapModuleList.get(MODULE_ID)));
				}
				module.setModuleID(mapTarget[i].get(columnNameMapModuleList.get(MODULE_ID)));
				
				module.setProjectID(mapTarget[i].get(columnNameMapModuleList.get(PROJECT_ID)));
				module.setFunctionName(mapTarget[i].get(columnNameMapModuleList.get(FUNCTION_NAME)));
				issue.addIssueModule(module);
			}
		}

		mapTarget = ExcelUtil.readContentFromExcelMult(fromExcel,2,strColumnsCommonModuleList,"I=");//sheet 2
		for(int i = 0 ; i<mapTarget.length;i++){
			String issueID = mapTarget[i].get(columnNameMapCommonModuleList.get(ISSUE_ID));
			
			if(issueMap.containsKey(issueID)){
				Issue issue = issueMap.get(issueID);
				IssueModule module = new IssueModule();
				module.setIssueID(issueID);
				
				String modulePath = mapTarget[i].get(columnNameMapCommonModuleList.get(MODULE_PATH));
				module.setModuleID(mapTarget[i].get(columnNameMapCommonModuleList.get(MODULE_ID)));
				
				if(StringUtils.isNotBlank(modulePath)){
					module.setModulePath(mapTarget[i].get(columnNameMapCommonModuleList.get(MODULE_ID))
							+"\\"+mapTarget[i].get(columnNameMapCommonModuleList.get(MODULE_PATH)));
				}
				
				issue.addIssueModule(module);
			}
		}
	}
	
	private static Issue[] readIssuesFromConfig(String fromExcel,String destColAlpha[],String filterPattern,Map<String,String> columnNameMap) throws IOException{
		updateSvn(fromExcel);
		Map<String,String>[] mapTarget = ExcelUtil.readContentFromExcelMult(fromExcel,0,destColAlpha,filterPattern);//sheet 0
		
		Issue[] issues = new Issue[mapTarget.length];
		for(int i = 0 ; i<mapTarget.length;i++){
			issues[i] = new Issue();
			issues[i].setId(mapTarget[i].get(columnNameMap.get(ISSUE_ID)));
			issues[i].setReviewer(mapTarget[i].get(columnNameMap.get(ISSUE_REVIEWER)));
			issues[i].setOwner(mapTarget[i].get(columnNameMap.get(ISSUE_OWNER_ID)));
			issues[i].setStatus(mapTarget[i].get(columnNameMap.get(ISSUE_STATUS)));
			issues[i].setResearchStatus(mapTarget[i].get(columnNameMap.get(RESEARCH_STATUS)));
		}
		
		return issues;
	}
	
	/**
	 * 
	 * @param fromExcel:list excel file path;the manager file's path
	 * @param destColAlpha A,B,C....
	 * @param filterPattern:just support;for example C=OK&&D=100
	 * @param destDirectory result files's directory
	 */
	public static ArrayList<String> checkFileExistsFromExcel(String fromExcel,String destColAlpha,String filterPattern,String destDirectory) throws IOException{
		//update svn
		updateSvn(fromExcel);
		updateSvn(destDirectory);
		
		ArrayList<String> out = new ArrayList<String>();
		Map[] mapTarget = ExcelUtil.readContentFromExcel(fromExcel,0,destColAlpha,filterPattern);//sheet 0
		String[] strTarget = new String[mapTarget.length];
		
		for(int i = 0 ;i < mapTarget.length;i++){
			strTarget[i] = (String)mapTarget[i].get(destColAlpha);
		}
		
		ArrayList<String> arrResult = new ArrayList<String>();
		FileUtil.listFiles(new File(destDirectory), arrResult);
		String[] strResult = new String[arrResult.size()]; 
		arrResult.toArray(strResult);
		
		Arrays.sort(strTarget);
		Arrays.sort(strResult);
		
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
	 * update svn
	 * @param destDirectory
	 */
	private static void updateSvn(String destDirectory){
		StringBuffer strBufClean = new StringBuffer();
		ConfigFile config = new ConfigFile(new File(CONFIG_FILE_PATH));
		String svnInstallPath = config.getPropertyValue("global", "svn_install_path");
		//clean
		strBufClean.append("/command:cleanup /path:");
		strBufClean.append(destDirectory.substring(0, destDirectory.lastIndexOf("/")));
		strBufClean.append(SPACE);
		strBufClean.append("/notempfile /noui /closeonend:1");
		String[] commandClean = new String[]{svnInstallPath, strBufClean.toString()};

		//update
		StringBuffer strBufUpdate = new StringBuffer();
		strBufUpdate.append("/command:update /path:");
		strBufUpdate.append(destDirectory);
		strBufUpdate.append(SPACE);
		strBufUpdate.append("/notempfile /closeonend:1");
		String[] commandUpdate = new String[]{svnInstallPath,strBufUpdate.toString()};
		
		try{
			java.lang.Process process1 = Runtime.getRuntime().exec(commandClean);
			process1.waitFor();
			java.lang.Process process2 = Runtime.getRuntime().exec(commandUpdate);
			process2.waitFor();
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	private static void updateSvnFromBat(String batFile){
		String command[]  = new String[]{batFile};
		try{
			Runtime.getRuntime().exec(command);
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
