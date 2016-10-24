package check;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private static final String RELEASE_STATUS = "RELEASE_STATUS";

	private static final String MODULE_ID = "MODULE_ID";
	private static final String MODULE_PATH = "MODULE_PATH";
	private static final String PROJECT_ID = "PROJECT_ID";
	private static final String FUNCTION_NAME = "FUNCTION_NAME";
	private static final String PLAN_FINISH_DATE = "PLAN_FINISH_DATE";
	private static final String PLAN_START_DATE = "PLAN_START_DATE";
	private static final String DELAY_STATUS = "DELAY_STATUS";
	private static final String DELAY_COMMENT = "DELAY_COMMENT";
	private static final String ACTUAL_START_DATE = "ACTUAL_START_DATE";
	private static final String ACTUAL_FINISH_DATE = "ACTUAL_FINISH_DATE";
	private static final String DEAL_FLAG = "DEAL_FLAG";
	private static final boolean DEBUG = false;
	private static final boolean CONSOLE = true;
	private static ConfigFile config = new ConfigFile(new File(CONFIG_FILE_PATH));

	private static int REVIEW_KIGEN = 5;
	/**
	 * issue list
	 */
	private static Map<String,String> columnNameMapIssueList = new HashMap<String, String>();
	static {
		columnNameMapIssueList.put(ISSUE_ID, "B");
		columnNameMapIssueList.put(ISSUE_REVIEWER, "K");
		columnNameMapIssueList.put(ISSUE_STATUS, "N");
		columnNameMapIssueList.put(ISSUE_OWNER_ID, "J");
		columnNameMapIssueList.put(RESEARCH_STATUS, "M");
		columnNameMapIssueList.put(RELEASE_STATUS, "V");
		columnNameMapIssueList.put(PLAN_START_DATE, "P");//
		columnNameMapIssueList.put(PLAN_FINISH_DATE, "Q");
		columnNameMapIssueList.put(DELAY_STATUS, "R");
		columnNameMapIssueList.put(DELAY_COMMENT, "U");
		columnNameMapIssueList.put(ACTUAL_START_DATE, "S");
		columnNameMapIssueList.put(ACTUAL_FINISH_DATE, "T");
		columnNameMapIssueList.put(DEAL_FLAG, "L");
	}
	/**
	 * module list,source
	 */

	private static Map<String,String> columnNameMapModuleList = new HashMap<String, String>();
	static{
		columnNameMapModuleList.put(ISSUE_ID, "B");
		columnNameMapModuleList.put(FUNCTION_NAME, "C");
		columnNameMapModuleList.put(PROJECT_ID, "D");
		columnNameMapModuleList.put(MODULE_ID, "E");
		columnNameMapModuleList.put(MODULE_PATH, "F");
		columnNameMapModuleList.put(RELEASE_STATUS, "L");
	}
	/**
	 * module list,database,shell's setting...
	 */
	private static Map<String,String> columnNameMapCommonModuleList = new HashMap<String, String>();
	static {
		columnNameMapCommonModuleList.put(ISSUE_ID, "B");
		columnNameMapCommonModuleList.put(MODULE_ID, "C");
		columnNameMapCommonModuleList.put(MODULE_PATH, "D");
		columnNameMapCommonModuleList.put(RELEASE_STATUS, "I");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException,SVNException{
		
		ArrayList<String> messagesOut = new ArrayList<String>();
		
		Issue[] issues = getIssueInfo("RELEASE_STATUS=未リリース|RELEASE_STATUS=部分リリース済", false);
		//check scopes：仕様変更＆mantis
		checkScope(issues,messagesOut);
		
		//check scopes：受入課題、移行検証課題など
		checkScopeKadai(issues,messagesOut);
		
		issues = getIssueInfo("(RELEASE_STATUS=未リリース|RELEASE_STATUS=部分リリース済)&DEAL_FLAG=○", false);
		//check research status：調査要否
		checkResearchStatus(issues,messagesOut);
		//check finish date：予定完了日など
		checkDate(issues,messagesOut);

		//check research file：調査必要な対応は調査ファイルが存在するか
		Issue[] researchIssues = getIssueInfo("(RELEASE_STATUS=未リリース|RELEASE_STATUS=部分リリース済)&RESEARCH_STATUS=○&DEAL_FLAG=○&(ISSUE_STATUS=調査済|ISSUE_STATUS=CD済|ISSUE_STATUS=UT済|ISSUE_STATUS=内部結合済|ISSUE_STATUS=内部結合完了)", false);
		checkResearchFile(researchIssues,messagesOut);
		
		Issue[] issuesforSource = getIssueInfo("DEAL_FLAG=○&(RELEASE_STATUS=未リリース)&(ISSUE_STATUS=CD済|ISSUE_STATUS=UT済|ISSUE_STATUS=内部結合済|ISSUE_STATUS=内部結合完了)",
				true);
		//check source commit
		checkSourceCommit(issuesforSource,messagesOut);
		//check ut test file
		Issue[] issuesforUT = getIssueInfo("DEAL_FLAG=○&(RELEASE_STATUS=未リリース)&ISSUE_STATUS=UT済|ISSUE_STATUS=内部結合済|ISSUE_STATUS=内部結合完了)",
				true);
//		checkUT(issuesforUT,messagesOut);
		//check si test file
				
	}
	
	public static void checkUT(Issue[] issues,ArrayList<String> messagesOut){
		//check source commit
		HashMap<String, String> sources = listSources();
		
		for(Issue issue:issues){
			IssueModule[] modules = issue.getModules();
			
			if(modules.length == 0){
				if(CONSOLE){
					System.out.println(issue.getId()+ " がモジュール一覧に存在しない");
				}
				messagesOut.add(issue.getId()+ " がモジュール一覧に存在しない");
				
			}
			
//			for(IssueModule module:modules){
//				if(sources.containsKey(module.getModulePath())){
//					List<SVNLogEntry> list = SVNUtil.getHistory(sources.get(module.getModulePath()), getStartDate(-90), getEndDate(), module.getIssueID());
//					if(list.size() ==0){
//						messagesOut.add(module.getIssueID() + " " +module.getModulePath() +" Warning: 該当するコミット履歴が見つからない");
//						System.out.println(module.getIssueID() + " " +module.getModulePath() +" Warning: 該当するコミット履歴が見つからない");
//					}else{
////						System.out.println(module.getIssueID() + " " +module.getModulePath() +" Info: commit ok");
//					}
//					
//					if(DEBUG){
//						printLog(list);
//					}
//				} else {
//					if(CONSOLE){
//						System.out.println(module.getIssueID() + " "+module.getModulePath() + " Info:ソースではない" );
//					}
//					messagesOut.add(module.getIssueID() + " "+module.getModulePath() + " Info:ソースではない" );
//				}
//			}
		}
	}
	
	public static void checkST(){
		
	}
	
	public static void checkDate(Issue[] issues,ArrayList<String> messagesOut){
		for(Issue issue:issues){
			if("内部結合済".equals(issue.getStatus()) || 
					"内部結合完了".equals(issue.getStatus())){
				if(StringUtils.isBlank(issue.getActualStartDate()) ||
						StringUtils.isBlank(issue.getActualFinishDate())){
					if(CONSOLE){
						System.out.println(issue.getId() + "「開始実績日」または「完了実績日」未記載");	
					}
					messagesOut.add(issue.getId() + "「開始実績日」または「完了実績日」未記載");

				} else {
					//check外部review期限
					if("内部結合済".equals(issue.getStatus())){
						String finishDate = issue.getActualFinishDate();
						Calendar cal = getDateFromExcel(finishDate);

						//5 days after
						cal.add(Calendar.DAY_OF_MONTH, REVIEW_KIGEN);
						
						Calendar currDate = Calendar.getInstance();
						currDate.setTimeInMillis(System.currentTimeMillis());
						SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
						switch(formater.format(cal.getTime()).compareTo(formater.format(currDate.getTime()))){
							case 0:
							case -1:
								if(CONSOLE){
									System.out.println(issue.getId() + " 内部完了日が"+REVIEW_KIGEN+"日過ぎたが、外部レビュー終わっていない");	
								}
								messagesOut.add(issue.getId() + " 内部完了日が"+REVIEW_KIGEN+"日過ぎたが、外部レビュー終わっていない");
								break;
							default:
								break;
						}
					}
				}
				
			} else {
				Calendar currDate = Calendar.getInstance();
				currDate.setTimeInMillis(System.currentTimeMillis());
				SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");

				if(StringUtils.isBlank(issue.getPlanFinishDate())){
					if(CONSOLE){
						System.out.println(issue.getId() + "「完了予定日」未記載");	
					}
					messagesOut.add(issue.getId() + "「完了予定日」未記載");
				} else {
					//check finish date
					String[] planFinishDate = issue.getPlanFinishDate().replaceAll("\n", "").split("⇒|->");
					String lastFinishDate = planFinishDate[planFinishDate.length-1];
					
					Calendar calFinishDate = getDateFromExcel(lastFinishDate);

					switch(formater.format(calFinishDate.getTime()).compareTo(formater.format(currDate.getTime()))){
						case 0:
							if(CONSOLE){
								System.out.println(issue.getId()+ " 予定完了日が到達している");
							}
							messagesOut.add(issue.getId()+ " 予定完了日が到達している");
							break;
						case -1:
							if(CONSOLE){
								System.out.println(issue.getId()+ " 予定完了日が過ぎている");
							}
							messagesOut.add(issue.getId()+ " 予定完了日が過ぎている");
							
							if(StringUtils.isBlank(issue.getDelay()) || StringUtils.isBlank(issue.getDelayComment())){
								if(CONSOLE){
									System.out.println(issue.getId()+ " 遅延状況または遅延理由対策が記載していない");
								}
								messagesOut.add(issue.getId()+ " 遅延状況または遅延理由対策が記載していない");
							}
							break;
						case 1:
							break;
					}
				}
				//check plan start date
				if(StringUtils.isBlank(issue.getStatus()) || "未着手".equals(issue.getStatus())){
					String startDate = issue.getPlanStartDate();
					
					Calendar calPlanStart = getDateFromExcel(startDate);
					switch(formater.format(calPlanStart.getTime()).compareTo(formater.format(currDate.getTime()))){
						case -1:
							if(CONSOLE){
								System.out.println(issue.getId()+ " 予定開始日が過ぎているが、未着手");
							}
							messagesOut.add(issue.getId()+ " 予定開始日が過ぎているが、未着手");
							break;
					}
				}
			}
		}
	}
	
	private static Calendar getDateFromExcel(String date){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
//		Calendar currDate = (Calendar)cal.clone();
		
		if(isNumber(date)){
			cal.set(1900, 0, 1);
			cal.add(Calendar.DAY_OF_MONTH, (int)Double.parseDouble(date)-2);
		} else {
			String regex = "(\\d\\d\\d\\d)?[/-]?(1[0-2]|0?[1-9])[/-]([1-2][0-9]|3[0-1]|0?[1-9]|)";
			
			String ymd = date.replaceAll(regex, "$1,$2,$3");
			String[] ymds = ymd.split(",");
			       
			
			if(StringUtils.isBlank(ymds[0])){
				cal.set(Calendar.MONTH, Integer.parseInt(ymds[1])-1);
				cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(ymds[2]));
			} else {
				cal.set(Integer.parseInt(ymds[0]), Integer.parseInt(ymds[1])-1, Integer.parseInt(ymds[2]));
			}
		}
		
		return cal;
	}
	
	public static void checkResearchStatus(Issue[] issues,ArrayList<String> messagesOut){
		for(Issue issue:issues){
			if(StringUtils.isBlank(issue.getResearchStatus())){
				if(CONSOLE){
					System.out.println(issue.getId() + "「調査要否」未記載");
				}
				messagesOut.add(issue.getId() + "「調査要否」未記載");
			}
		}
	}
	
	public static void checkSourceCommit(Issue[] issues,ArrayList<String> messagesOut) throws SVNException{
		//check source commit
		HashMap<String, String> sources = listSources();
		
		for(Issue issue:issues){
			IssueModule[] modules = issue.getModules();
			
			if(modules.length == 0){
				if(CONSOLE){
					System.out.println(issue.getId()+ " がモジュール一覧に存在しない");
				}
				messagesOut.add(issue.getId()+ " がモジュール一覧に存在しない");
				
			}
			
			for(IssueModule module:modules){
				if(sources.containsKey(module.getModulePath())){
					List<SVNLogEntry> list = SVNUtil.getHistory(sources.get(module.getModulePath()), getStartDate(-90), getEndDate(), module.getIssueID());
					if(list.size() ==0){
						messagesOut.add(module.getIssueID() + " " +module.getModulePath() +" Warning: 該当するコミット履歴が見つからない");
						System.out.println(module.getIssueID() + " " +module.getModulePath() +" Warning: 該当するコミット履歴が見つからない");
					}else{
//						System.out.println(module.getIssueID() + " " +module.getModulePath() +" Info: commit ok");
					}
					
					if(DEBUG){
						printLog(list);
					}
				} else {
					if(CONSOLE){
						System.out.println(module.getIssueID() + " "+module.getModulePath() + " Info:ソースではない" );
					}
					messagesOut.add(module.getIssueID() + " "+module.getModulePath() + " Info:ソースではない" );
				}
			}
		}
	}
	
	public static void checkResearchFile(Issue[] issues,ArrayList<String> messageOut){
		//update svn
		String researchFilePath = config.getPropertyValue("check", "research_result_path");
		updateSvn(researchFilePath);
		
		ArrayList<String> arrResult = new ArrayList<String>();
		FileUtil.listFiles(new File(researchFilePath), arrResult);
		String[] strResult = new String[arrResult.size()]; 
		arrResult.toArray(strResult);
		
		String[] strTarget = new String[issues.length];
		for(int i = 0 ; i< strTarget.length;i++){
			strTarget[i] = issues[i].getId();
		}
		
		Arrays.sort(strTarget);
		Arrays.sort(strResult);
		ArrayList<String> out = new ArrayList<String>();
		
		int startIndex = 0;
		for(int i = 0; i < strTarget.length;i++){
			boolean match = false;
			
			for(int j=startIndex;j <strResult.length;j++){
				if(strResult[j].contains(strTarget[i])){
//					swap(strResult,j,startIndex);
//					startIndex++; //一つの調査結果に複数の案件番号が含まれているため、一回当てても対象外にならない
					match = true;
					break;
				}
			}
			
			if(match == false){
				out.add(strTarget[i]);
			}
		}
		
		for(String str:out){
			if(CONSOLE){
				System.out.println(str + " 調査結果ファイルが存在しない");
			}
			messageOut.add(str + " 調査結果ファイルが存在しない");
		}
		if(out.size() == 0){
			if(CONSOLE){
				System.out.println("Info:調査結果ファイルチェックOK");
			}
			messageOut.add("Info:調査結果ファイルチェックOK");
		}
			
	}
	/**
	 * check the change and mantis exists
	 */
	public static void checkScopeKadai(Issue[] issues,ArrayList<String> messageOut) throws IOException{
		//check research file
		String kadaiListFile1 = config.getPropertyValue("check", "kadai_list_file1");
		updateSvn(kadaiListFile1);
		
		//change list
		Map<String, String> listName = new HashMap<String, String>();
		listName.put("no", "DO");
//		listName.put("discuss_status", "I");
		
		Map<String, String>[] listKadai1 = ExcelUtil.readContentFromExcelMult(kadaiListFile1, 0, listName, "no! ");
		
		HashSet<String> issueNoSet = new HashSet<String>();
		for(Issue issue:issues){
			issueNoSet.add(issue.getId());
		}
		
		ArrayList<String> array = new ArrayList<String>();
		//check kadai list
		for(Map<String,String> map:listKadai1){
			String[] nos = map.get("no").split("\\n");
			
			for(String no:nos){
		    	if(!issueNoSet.contains(no)){
		    		array.add(no);
		    		if(CONSOLE){
		    			System.out.println(no + " が案件状況一覧に存在しない");
		    		}
		    		messageOut.add(no + " が案件状況一覧に存在しない");
		    	}
			}
		}
		
	}
	
	
	/**
	 * check the change and mantis exists
	 */
	public static void checkScope(Issue[] issues,ArrayList<String> messageOut) throws IOException{
		//check research file
		String changeListFile = config.getPropertyValue("check", "change_list_file");
		updateSvn(changeListFile);
		String mantisListFile = config.getPropertyValue("check", "mantis_list_file");
		updateSvn(mantisListFile);
		
		//change list
		Map<String, String> listColNames = new HashMap<String, String>();
		listColNames.put("no", "A");
		listColNames.put("releaseStatus", "O");
		
		Map<String, String>[] changeList = ExcelUtil.readContentFromExcelMult(changeListFile, 0, listColNames, "releaseStatus= |releaseStatus=未リリース&no! ");
		
		//mantis list
		Map<String, String> mantisColNames = new HashMap<String, String>();
		mantisColNames.put("no", "A");
		mantisColNames.put("releaseStatus", "AC");
		Map<String, String>[] mantisList = ExcelUtil.readContentFromExcelMult(mantisListFile, 0, mantisColNames, "releaseStatus= |releaseStatus=未リリース&no! ");
		
		HashSet<String> issueNoSet = new HashSet<String>();
		for(Issue issue:issues){
			issueNoSet.add(issue.getId());
		}
		
		ArrayList<String> array = new ArrayList<String>();
		//check change list
		for(Map<String,String> map:changeList){
			String regex = "(\\d+)[.]?\\d*";
			String no = "仕様変更" + map.get("no").replaceAll(regex, "$1");//replace 759.0 to 759
			
	    	if(!issueNoSet.contains(no)){
	    		array.add(no);
	    		if(CONSOLE){
	    			System.out.println(no + " が案件状況一覧に存在しない");
	    		}
	    		messageOut.add(no + " が案件状況一覧に存在しない");
	    	}
		}
		
		for(Map<String,String> map:mantisList){
			String regex = "(\\d+)[.]?\\d*";
			String no = "mantis" + map.get("no").replaceAll(regex, "$1");//replace 759.0 to 759
			
	    	if(!issueNoSet.contains(no)){
	    		array.add(no);
	    		if(CONSOLE){
	    			System.out.println(no + " が案件状況一覧に存在しない");
	    		}
	    		messageOut.add(no + " が案件状況一覧に存在しない");
	    	}
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
		String sourceFile1 = config.getPropertyValue("check", "mps_source_path1");
		String sourceFile2 = config.getPropertyValue("check", "mps_source_path2");
		String sourceFile3 = config.getPropertyValue("check", "if_source_path1");
		String sourceFile4 = config.getPropertyValue("check", "core_source_path1");
		String sourceFile5 = config.getPropertyValue("check", "core_source_path2");
		updateSvn(sourceFile1);
		updateSvn(sourceFile2);
		updateSvn(sourceFile3);
		updateSvn(sourceFile4);
		updateSvn(sourceFile5);
		
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
	public static Issue[] getIssueInfo(String issueListFilterPattern,boolean readModuleInfo) throws IOException{
		String configListFile = config.getPropertyValue("check", "issue_list_file");  
		updateSvn(configListFile);
		//read base info
		Map<String,String>[] mapTarget = ExcelUtil.readContentFromExcelMult(configListFile,0,columnNameMapIssueList,issueListFilterPattern);//sheet 0
		
		Issue[] issues = new Issue[mapTarget.length];
		for(int i = 0 ; i<mapTarget.length;i++){
			issues[i] = new Issue();
			issues[i].setId(mapTarget[i].get(ISSUE_ID));
			issues[i].setReviewer(mapTarget[i].get(ISSUE_REVIEWER));
			issues[i].setOwner(mapTarget[i].get(ISSUE_OWNER_ID));
			issues[i].setStatus(mapTarget[i].get(ISSUE_STATUS));
			issues[i].setResearchStatus(mapTarget[i].get(RESEARCH_STATUS));
			issues[i].setPlanStartDate(mapTarget[i].get(PLAN_START_DATE));
			issues[i].setPlanFinishDate(mapTarget[i].get(PLAN_FINISH_DATE));
			issues[i].setDelay(mapTarget[i].get(DELAY_STATUS));
			issues[i].setDelayComment(mapTarget[i].get(DELAY_COMMENT));
			issues[i].setActualStartDate(mapTarget[i].get(ACTUAL_START_DATE));
			issues[i].setActualFinishDate(mapTarget[i].get(ACTUAL_FINISH_DATE));
			issues[i].setDealFlag(mapTarget[i].get(DEAL_FLAG));
		}
		
		if(readModuleInfo){
			//read module info
			setIssueModule(issues);
		}
		
		return issues;
	}
	
	private static void setIssueModule(Issue[] issues) throws IOException{
		String moduleListFile = config.getPropertyValue("check", "module_list_file");
		updateSvn(moduleListFile);

		
		Map<String,String>[] mapTarget = ExcelUtil.readContentFromExcelMult(moduleListFile,1,columnNameMapModuleList,"RELEASE_STATUS= ");//sheet 1
		
		HashMap<String, Issue> issueMap = new HashMap<String, Issue>();
		for(Issue issue:issues){
			issueMap.put(issue.getId(), issue);
		}
		
		for(int i = 0 ; i<mapTarget.length;i++){
			String issueID = mapTarget[i].get(ISSUE_ID);
			if(issueMap.containsKey(issueID)){
				Issue issue = issueMap.get(issueID);
				IssueModule module = new IssueModule();
				module.setIssueID(issueID);
				String modulePath = mapTarget[i].get(MODULE_PATH);
				module.setModulePath(mapTarget[i].get(MODULE_PATH));
				
				if(StringUtils.isBlank(modulePath)){
					module.setModulePath(mapTarget[i].get(MODULE_ID));
				}
				module.setModuleID(mapTarget[i].get(MODULE_ID));
				
				module.setProjectID(mapTarget[i].get(PROJECT_ID));
				module.setFunctionName(mapTarget[i].get(FUNCTION_NAME));
				issue.addIssueModule(module);
			}
		}

		mapTarget = ExcelUtil.readContentFromExcelMult(moduleListFile,2,columnNameMapCommonModuleList,"RELEASE_STATUS= ");//sheet 2
		for(int i = 0 ; i<mapTarget.length;i++){
			String issueID = mapTarget[i].get(ISSUE_ID);
			
			if(issueMap.containsKey(issueID)){
				Issue issue = issueMap.get(issueID);
				IssueModule module = new IssueModule();
				module.setIssueID(issueID);
				
				String modulePath = mapTarget[i].get(MODULE_PATH);
				module.setModuleID(mapTarget[i].get(MODULE_ID));
				
				if(StringUtils.isNotBlank(modulePath)){
					module.setModulePath(mapTarget[i].get(MODULE_ID)
							+"\\"+mapTarget[i].get(MODULE_PATH));
				}
				
				issue.addIssueModule(module);
			}
		}
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
	
	private static boolean isNumber(String input){
		String regex = "^[1-9][0-9]*[.]?(\\d)*$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		return m.find();
	}

}
