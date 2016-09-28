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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		
		getIssueBaseInfo();
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
	public static Issue[] getIssueBaseInfo() throws IOException{
		ConfigFile config = new ConfigFile(new File(CONFIG_FILE_PATH));
		String configListFile = config.getPropertyValue("check", "issue_list_file");
		
		String[] strColumns = new String[]{"B","E","K","O","P"};
		Map<String,String> columnNameMap = new HashMap<String, String>();
		columnNameMap.put(ISSUE_ID, "B");
		columnNameMap.put(ISSUE_REVIEWER, "E");
		columnNameMap.put(ISSUE_STATUS, "K");
		columnNameMap.put(ISSUE_OWNER_ID, "O");
		columnNameMap.put(RESEARCH_STATUS, "P");
		
		Issue[] issues = readIssuesFromConfig(configListFile,strColumns,"L=ñ¢ÉäÉäÅ[ÉX",columnNameMap);
		
		for(Issue issue:issues){
			System.out.println(issue.getId()+ " "+issue.getReviewer()+ " "+issue.getStatus() + " "+issue.getResearchStatus()+" "+issue.getOwner());
		}
		return issues;
	}
	
	private static IssueModule[] readIssueModeleFromConfig(String fromExcel,String destColAlpha[],Issue[] issues,Map<String,String> columnNameMap) throws IOException{
		Map<String,String>[] mapTarget = ExcelUtil.readContentFromExcelMult(fromExcel,1,destColAlpha,"");//sheet 1
		
		IssueModule[] issueModels = new IssueModule[mapTarget.length];
//		for(int i = 0 ; i<mapTarget.length;i++){
//			issues[i] = new Issue();
//			issues[i].setId(mapTarget[i].get(columnNameMap.get(ISSUE_ID)));
//			issues[i].setReviewer(mapTarget[i].get(columnNameMap.get(ISSUE_REVIEWER)));
//			issues[i].setOwner(mapTarget[i].get(columnNameMap.get(ISSUE_OWNER_ID)));
//			issues[i].setStatus(mapTarget[i].get(columnNameMap.get(ISSUE_STATUS)));
//			issues[i].setResearchStatus(mapTarget[i].get(columnNameMap.get(RESEARCH_STATUS)));
//		}
		
		return issueModels;
	}
	
	private static Issue[] readIssuesFromConfig(String fromExcel,String destColAlpha[],String filterPattern,Map<String,String> columnNameMap) throws IOException{
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
