import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import svn.ConfigFile;
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

	private static final String[] strColumnsIssueList = new String[]{"B","E","K","O","P"};
	
	private static Map<String,String> columnNameMapIssueList = new HashMap<String, String>();
	static {
		columnNameMapIssueList.put(ISSUE_ID, "B");
		columnNameMapIssueList.put(ISSUE_REVIEWER, "E");
		columnNameMapIssueList.put(ISSUE_STATUS, "K");
		columnNameMapIssueList.put(ISSUE_OWNER_ID, "O");
		columnNameMapIssueList.put(RESEARCH_STATUS, "P");
	}
	
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
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
//		checkResearchFile();
		
//		Issue[] issues = getIssueInfo("L=ñ¢ÉäÉäÅ[ÉX","L=");
//
//		for(Issue issue:issues){
//			System.out.println(issue.getId()+ " "+issue.getReviewer()+ " "+issue.getStatus() + " "+issue.getResearchStatus()+" "+issue.getOwner());
//			IssueModule[] modules = issue.getModules();
//			for(IssueModule module:modules){
//				System.out.println(module.getIssueID() + " "+ module.getModuleID() + " "+module.getModulePath() + " "+module.getProjectID()+ " "+module.getFunctionName());
//			}
//		}
		
		HashMap<String, String> sources = listSources();
		int i = 0;
		for(String key:sources.keySet()){
			System.out.println(i++ +":"+ key + "=" + sources.get(key));
		}
		
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
		
		HashMap<String, String> repeatMap = new HashMap<String, String>();
		ArrayList<String> repeatKeys = new ArrayList<String>();
		for(File f:array){
			if(map.containsKey(f.getName())){
				if(!repeatKeys.contains(f.getName())){
					repeatKeys.add(f.getName());
				}
				//TODO for matching module list
				repeatMap.put(f.getAbsolutePath(), f.getAbsolutePath());
			} else {
				map.put(f.getName(), f.getAbsolutePath());
			}
		}
		
		for(String repeatKey:repeatKeys){
			map.remove(repeatKey);
		}
		map.putAll(repeatMap);
		
		return map;
	}
	/**
	 * 
	 * @throws IOException
	 */
	public static void checkResearchFile() throws IOException{
		ConfigFile config = new ConfigFile(new File(CONFIG_FILE_PATH));
		String configListFile = config.getPropertyValue("check", "issue_list_file");
		String resultPath = config.getPropertyValue("check", "result_out_path");
		
		ArrayList<String> out = checkFileExistsFromExcel(configListFile,"B","P=Åõ",resultPath);
		for(String str:out){
			System.out.println(str);
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Issue[] getIssueInfo(String issueListFilterPattern,String moduleListFiterPattern) throws IOException{
		ConfigFile config = new ConfigFile(new File(CONFIG_FILE_PATH));
		String configListFile = config.getPropertyValue("check", "issue_list_file");  
		String moduleListFile = config.getPropertyValue("check", "module_list_file");
		
		//read base info
		Issue[] issues = readIssuesFromConfig(configListFile,strColumnsIssueList,issueListFilterPattern,columnNameMapIssueList);
		//read module info
		setIssueModuleFromConfig(moduleListFile,strColumnsModuleList,moduleListFiterPattern,columnNameMapModuleList,issues);
		
		return issues;
	}
	
	private static void setIssueModuleFromConfig(String fromExcel,String destColAlpha[],String filterPattern,
			Map<String,String> columnNameMap,Issue[] issues) throws IOException{
		updateSvn(fromExcel);
		Map<String,String>[] mapTarget = ExcelUtil.readContentFromExcelMult(fromExcel,1,destColAlpha,filterPattern);//sheet 1
		
		HashMap<String, Issue> issueMap = new HashMap<String, Issue>();
		for(Issue issue:issues){
			issueMap.put(issue.getId(), issue);
		}
		
		for(int i = 0 ; i<mapTarget.length;i++){
			String issueID = mapTarget[i].get(columnNameMap.get(ISSUE_ID));
			if(issueMap.containsKey(issueID)){
				Issue issue = issueMap.get(issueID);
				IssueModule module = new IssueModule();
				module.setIssueID(issueID);
				module.setModuleID(mapTarget[i].get(columnNameMap.get(MODULE_ID)));
				module.setModulePath(mapTarget[i].get(columnNameMap.get(MODULE_PATH)));
				module.setProjectID(mapTarget[i].get(columnNameMap.get(PROJECT_ID)));
				module.setFunctionName(mapTarget[i].get(columnNameMap.get(FUNCTION_NAME)));
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
	private static ArrayList<String> checkFileExistsFromExcel(String fromExcel,String destColAlpha,String filterPattern,String destDirectory) throws IOException{
		//update svn
		updateSvn(fromExcel);
		updateSvn(destDirectory);
		
		ArrayList<String> out = new ArrayList<String>();
		Map[] mapTarget = ExcelUtil.readContentFromExcel(fromExcel,0,destColAlpha,filterPattern);//sheet 0
		String[] strTarget = new String[mapTarget.length];
		
		for(int i = 0 ;i < mapTarget.length;i++){
			strTarget[i] = (String)mapTarget[i].get(destColAlpha);
		}
//		System.out.println("Target from");
//		for(String str:arrTarget){
//			System.out.println(str);
//		}
//		System.out.println("Target end");
		
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
		strBufClean.append(destDirectory);
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
