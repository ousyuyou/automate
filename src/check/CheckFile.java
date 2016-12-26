package check;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
	private static String CONFIG_FILE_PATH = "E:/cert/config_utf8";
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
	private static final String IKOU_RESOURCE = "IKOU_RESOURCE";

	private static final String YOTEI_KOUSU_RESEARCH = "YOTEI_KOUSU_RESEARCH";
	private static final String YOTEI_KOUSU_CD = "YOTEI_KOUSU_CD";
	private static final String YOTEI_KOUSU_UT = "YOTEI_KOUSU_UT";
	private static final String YOTEI_KOUSU_ST = "YOTEI_KOUSU_ST";

	private static final String JISEKI_KOUSU_RESEARCH = "YOTEI_KOUSU_RESEARCH";
	private static final String JISEKI_KOUSU_CD = "JISEKI_KOUSU_CD";
	private static final String JISEKI_KOUSU_UT = "JISEKI_KOUSU_UT";
	private static final String JISEKI_KOUSU_ST = "JISEKI_KOUSU_ST";

	private static final String RESEARCH_PERCENT = "RESEARCH_PERCENT";
	private static final String CD_PERCENT = "CD_PERCENT";
	private static final String UT_PERCENT = "UT_PERCENT";
	private static final String ST_PERCENT = "ST_PERCENT";

	private static final String DDL_SQL = "DDL_SQL";
	private static final String PATCH_YOUHI = "PATCH_YOUHI";
	private static final String UT_SHEET_NAME = "UT_SHEET_NAME";

	private static final String JOB_ID = "JOB_ID";
	private static final String PATTERN_NAME = "PATTERN_NAME";

	private static final String RELEASE_VERSION = "RELEASE_VERSION";

	private static final boolean DEBUG = false;
	private static final boolean CONSOLE = true;
	private static ConfigFile config = new ConfigFile(
			new File(CONFIG_FILE_PATH));
	private static String svnInstallPath = config.getPropertyValue("global",
			"svn_install_path");

	private static int REVIEW_KIGEN = 5;
	private static ArrayList<String> messageOut = new ArrayList<String>();
	/**
	 * issue list
	 */
	private static Map<String, String> columnNameMapIssueList = new HashMap<String, String>();
	static {
		// owner,status
		columnNameMapIssueList.put(ISSUE_ID, "B");
		columnNameMapIssueList.put(ISSUE_OWNER_ID, "J");
		columnNameMapIssueList.put(ISSUE_REVIEWER, "K");
		columnNameMapIssueList.put(DEAL_FLAG, "L");
		columnNameMapIssueList.put(RESEARCH_STATUS, "M");
		columnNameMapIssueList.put(ISSUE_STATUS, "N");
		// 予定工数
		columnNameMapIssueList.put(YOTEI_KOUSU_RESEARCH, "T");
		columnNameMapIssueList.put(YOTEI_KOUSU_CD, "U");
		columnNameMapIssueList.put(YOTEI_KOUSU_UT, "V");
		columnNameMapIssueList.put(YOTEI_KOUSU_ST, "W");
		// WBS
		columnNameMapIssueList.put(PLAN_START_DATE, "X");//
		columnNameMapIssueList.put(PLAN_FINISH_DATE, "Y");
		columnNameMapIssueList.put(DELAY_STATUS, "Z");
		columnNameMapIssueList.put(ACTUAL_START_DATE, "AA");
		columnNameMapIssueList.put(ACTUAL_FINISH_DATE, "AB");

		// 実績工数
		columnNameMapIssueList.put(JISEKI_KOUSU_RESEARCH, "AC");
		columnNameMapIssueList.put(JISEKI_KOUSU_CD, "AD");
		columnNameMapIssueList.put(JISEKI_KOUSU_ST, "AE");
		columnNameMapIssueList.put(JISEKI_KOUSU_UT, "AF");
		// delay comment
		columnNameMapIssueList.put(DELAY_COMMENT, "AG");
		// release status
		columnNameMapIssueList.put(RELEASE_STATUS, "AH");
		columnNameMapIssueList.put(IKOU_RESOURCE, "AO");
		// release version
		columnNameMapIssueList.put(RELEASE_VERSION, "AJ");
	}
	/**
	 * module list,source
	 */

	private static Map<String, String> columnNameMapModuleList = new HashMap<String, String>();
	static {
		columnNameMapModuleList.put(ISSUE_ID, "B");
		columnNameMapModuleList.put(FUNCTION_NAME, "C");
		columnNameMapModuleList.put(PROJECT_ID, "D");
		columnNameMapModuleList.put(MODULE_ID, "E");
		columnNameMapModuleList.put(MODULE_PATH, "F");
		columnNameMapModuleList.put(RELEASE_STATUS, "L");
		columnNameMapModuleList.put(UT_SHEET_NAME, "N");
	}
	/**
	 * module list,database,shell's setting...
	 */
	private static Map<String, String> columnNameMapCommonModuleList = new HashMap<String, String>();
	static {
		columnNameMapCommonModuleList.put(ISSUE_ID, "B");
		columnNameMapCommonModuleList.put(MODULE_ID, "C");
		columnNameMapCommonModuleList.put(MODULE_PATH, "D");
		columnNameMapCommonModuleList.put(RELEASE_STATUS, "I");
		// K,L列：DDL句とパッチ要否チェック
		columnNameMapCommonModuleList.put(DDL_SQL, "K");
		columnNameMapCommonModuleList.put(PATCH_YOUHI, "L");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, SVNException {
		// check scope
		String checkScopeNotRelease = "(RELEASE_STATUS=統合済|RELEASE_STATUS=未リリース|RELEASE_STATUS=部分リリース済)";
		// String checkScopeStep47 = "RELEASE_VERSION⊃FS_TS_LK_V3.20.9.B01";
		boolean checkScope = true;

		String condition = checkScopeNotRelease;
	    Issue[] issues = getIssueInfo(condition+"&DEAL_FLAG=○", false); 
	    
	    if(checkScope){ 
		    //check scopes：仕様変更＆mantis
	    	checkScope(issues);
			 //check scopes：受入課題、移行検証課題など }
	    	checkScopeKadai(issues); 
	    }
		
		//check research status：調査要否 
		checkResearchStatus(issues); 
		 //check finish date：予定完了日など 
		checkDate(issues);
		// check research file：調査必要な対応は調査ファイルが存在するか
		// 移行関連修正案件は調査ファイルが別対応のため、チェック対象外
		Issue[] researchIssues = getIssueInfo(
				condition
						+ "&RESEARCH_STATUS=○&DEAL_FLAG=○&(ISSUE_STATUS=調査済|ISSUE_STATUS=CD済|ISSUE_STATUS=UT済|"
						+ "ISSUE_STATUS=内部結合実施中|ISSUE_STATUS=内部結合実施済|ISSUE_STATUS=内部結合一次レビュー済|ISSUE_STATUS=内部結合済|ISSUE_STATUS=内部結合完了)&IKOU_RESOURCE!○",
				false);

		Map<String, ResearchResult> researchResults = checkResearchFile(researchIssues);

		// check予定工数
		checkWorkDay(researchIssues,1);

		// 移行関連修正案件は別PRJのため、チェック対象外
		Issue[] issuesforSource = getIssueInfo(
				condition
						+ "&DEAL_FLAG=○&(ISSUE_STATUS=CD済|ISSUE_STATUS=UT済|"
						+ "ISSUE_STATUS=内部結合実施中|ISSUE_STATUS=内部結合実施済|ISSUE_STATUS=内部結合一次レビュー済|ISSUE_STATUS=内部結合済|ISSUE_STATUS=内部結合完了)&IKOU_RESOURCE!○",
				true);
		// compare research modules with modules list
		checkModules(issuesforSource,researchResults);
		// check source commit
		checkSourceCommit(issuesforSource);
		// check si test file
		Issue[] issuesforST = getIssueInfo(
				condition
						+ "&DEAL_FLAG=○&(ISSUE_STATUS=内部結合実施済|ISSUE_STATUS=内部結合一次レビュー済|ISSUE_STATUS=内部結合済|ISSUE_STATUS=内部結合完了)&IKOU_RESOURCE!○",
				false);
		// check ut test file
		Issue[] issuesforUT = getIssueInfo(
				condition
						+ "&DEAL_FLAG=○&(ISSUE_STATUS=UT済|"
						+ "ISSUE_STATUS=内部結合実施中|ISSUE_STATUS=内部結合実施済|ISSUE_STATUS=内部結合一次レビュー済|ISSUE_STATUS=内部結合済|ISSUE_STATUS=内部結合完了)",
				true);
		checkUT(issuesforUT);
		
		Map<String, Integer> caseCountMap = checkST(issuesforST,
				researchResults);
		for (String iuuseID : caseCountMap.keySet()) {
			System.out.println("issue: " + iuuseID + " case数: "
					+ caseCountMap.get(iuuseID));
		}
		//show the review error
		readReviewRecord(issuesforST,caseCountMap);
		// check実績工数
		checkWorkDay(issuesforST,2);
	}
	
	private static void readReviewRecord(Issue[] issues,Map<String, Integer> caseCountMap)
			throws IOException {
		String filePath = config.getPropertyValue("review",
				"review_record_file_inside");
		
		SVNUtil.updateSvnByTortoiseSvn(filePath,svnInstallPath);

		Map<String, String> columnNameMapReviewList = new HashMap<String, String>();
		columnNameMapReviewList.put(ISSUE_ID, "B");
		columnNameMapReviewList.put(FUNCTION_NAME, "C");
		columnNameMapReviewList.put("REVIEW_PROJECT", "D");
		columnNameMapReviewList.put("RESULT_KBN", "E");
		columnNameMapReviewList.put("RESOURCE_NAME", "F");
		columnNameMapReviewList.put("REVIEWER", "G");
		columnNameMapReviewList.put("CRITICIZE_KBN", "I");
		columnNameMapReviewList.put("REVIEW_STEP", "J");
		columnNameMapReviewList.put("CRITICIZE_REASON", "L");
		columnNameMapReviewList.put("OWNER", "M");
		columnNameMapReviewList.put("STATUS", "O");
		//cd~ut
		Map<String, String>[] listReviewInsideCdUT = ExcelUtil
				.readContentFromExcelMult(filePath, 0, columnNameMapReviewList,
						"CRITICIZE_KBN!確認", 2);
		
		//count cd error by issue
		Map<String,Integer> cdResearchMap = initMapToZero(issues);
		countErrorByIssue(listReviewInsideCdUT,"REVIEW_PROJECT","影響調査", cdResearchMap);
		
		//count cd error by issue
		Map<String,Integer> cdErrorMap = initMapToZero(issues);
		countErrorByIssue(listReviewInsideCdUT,"REVIEW_PROJECT","開発", cdErrorMap);

		//count ut error by issue
		Map<String,Integer> utErrorMap = initMapToZero(issues);
		countErrorByIssue(listReviewInsideCdUT,"REVIEW_PROJECT","単体テスト", utErrorMap);

		//st
		Map<String, String>[] listReviewInsideST = ExcelUtil
				.readContentFromExcelMult(filePath, 1, columnNameMapReviewList,
						"CRITICIZE_KBN!確認", 2);
		
		Map<String,Integer> stErrorFirstMap = initMapToZero(issues);
		
		countErrorByIssue(listReviewInsideST,"REVIEW_STEP","一次RV", stErrorFirstMap);

		Map<String,Integer> stErrorSecondMap = initMapToZero(issues);
		countErrorByIssue(listReviewInsideST,"REVIEW_STEP","二次RV", stErrorSecondMap);

		Map<String,Integer> stErrorTotalMap = initMapToZero(issues);
		
		for(Issue issue:issues){
			int total = stErrorFirstMap.get(issue.getId()).intValue() + stErrorSecondMap.get(issue.getId()).intValue();
			stErrorTotalMap.put(issue.getId(), new Integer(total));
		}
		//OUT
		String filePathOut = config.getPropertyValue("review","review_record_file_outside");
		SVNUtil.updateSvnByTortoiseSvn(filePathOut,svnInstallPath);
		Map<String, String>[] listReviewOutSideST = ExcelUtil.readContentFromExcelMult(filePathOut,
				0, columnNameMapReviewList,"CRITICIZE_KBN!確認", 1);
		
		Map<String,Integer> stErrorOutSTMap = initMapToZero(issues);
		countErrorByIssue(listReviewOutSideST,"REVIEW_PROJECT","結合テスト", stErrorOutSTMap);
		
		int errSTInsideSum = 0;
		int errSTOutsideSum = 0;
		
		int stCaseSum = 0;
		for(String issueId:stErrorTotalMap.keySet()){
			errSTInsideSum += stErrorTotalMap.get(issueId).intValue();
			errSTOutsideSum += stErrorOutSTMap.get(issueId).intValue();
			
			stCaseSum += caseCountMap.get(issueId);
			System.out.println(issueId +" 指摘数 影響調査: "+ cdResearchMap.get(issueId).intValue() +
					" CD: "+ cdErrorMap.get(issueId).intValue() +
					" UT: "+ utErrorMap.get(issueId).intValue() +
//					" ST一次: "+ stErrorFirstMap.get(issueId).intValue() + 
//					" ST二次: "+ stErrorSecondMap.get(issueId).intValue() +
//					" ST内部合計: "+ stErrorTotalMap.get(issueId).intValue() +
					" ST内部密度: "+ getReviewErrorPercent(stErrorTotalMap.get(issueId).intValue(),caseCountMap.get(issueId)) +
					" ST外部密度: "+ getReviewErrorPercent(stErrorOutSTMap.get(issueId).intValue() ,caseCountMap.get(issueId))
					);
		}
		System.out.println("ST内部密度(平均): "+ getReviewErrorPercent(errSTInsideSum,stCaseSum));
		System.out.println("ST外部密度(平均): "+ getReviewErrorPercent(errSTOutsideSum,stCaseSum));
		
	}
	
	private static String getReviewErrorPercent(int errors,int caseNums){
		return String.valueOf((int)(errors*1.0/caseNums*10000)*1.0/100) + "件/100ケース";
	}
	
	private static Map<String,Integer> initMapToZero(Issue[] issues){
		Map<String,Integer> errorMap = new HashMap<String, Integer>();
		for(Issue issue:issues){
			errorMap.put(issue.getId(), new Integer(0));
		}
		return errorMap;
	}
	
	/**
	 * 
	 * @param listReviewInsideCdUT
	 * @param countColumnName
	 * @param columnValue
	 * @param errorMap:in & out,edit the map;init is all zero
	 */
	private static void countErrorByIssue(Map<String, String>[] listReviewInsideCdUT,
			String countColumnName,String columnValue,Map<String,Integer> errorMap){
		
		for(Map<String,String> map:listReviewInsideCdUT){
			String issueId = map.get(ISSUE_ID);
			String reviewProject= map.get(countColumnName);
			
			if(columnValue.equals(reviewProject)){
				int value = errorMap.containsKey(issueId)?errorMap.get(issueId).intValue()+1 : 1;
				errorMap.put(issueId, new Integer(value));
			}
		}

	}

	//TODO:use weka to show any dimension data
	private static void countByField(Map<String, String>[] reviewList,
			String field) {
		
	}

	private static void setReviewRecord(Map<String, String>[] reviewList) {

	}

	/**
	 * 
	 * @param issues
	 * @param results
	 * @param kbn
	 *            1:予定工数、2:実績工数
	 */
	public static void checkWorkDay(Issue[] issues, int kbn) {
		for (Issue issue : issues) {
			switch (kbn) {
			case 1:
				if (StringUtils.isBlank(issue.getYoteiKousuResearch())
						|| StringUtils.isBlank(issue.getYoteiKousuCD())
						|| StringUtils.isBlank(issue.getYoteiKousuUT())
						|| StringUtils.isBlank(issue.getYoteiKousuST())) {
					printMessage(issue.getId() + " 予定工数が記入されていない");
				}
				break;
			case 2:
				if (StringUtils.isBlank(issue.getJisekiResearch())
						|| StringUtils.isBlank(issue.getJisekiKousuCD())
						|| StringUtils.isBlank(issue.getJisekiKousuUT())
						|| StringUtils.isBlank(issue.getJisekiKousuST())) {
					printMessage(issue.getId() + " 実績工数が記入されていない");
				}
			}
		}
	}

	public static void checkModules(Issue[] issues,
			Map<String, ResearchResult> results) {
		for (Issue issue : issues) {
			IssueModule[] models = issue.getModules();
			if (results.get(issue.getId()) == null) {
				if ("○".equals(issue.getResearchStatus())) {
					printMessage(issue.getId() + " 調査結果ファイルにモジュールが記載されていない");
				}
				continue;
			}
			IssueModule[] researchModules = results.get(issue.getId())
					.getModules();

			HashSet<String> set = new HashSet<String>();
			for (IssueModule module : researchModules) {
				set.add(module.getModuleID());
			}

			for (IssueModule module : models) {
				if (!set.contains(module.getModuleID())) {
					printMessage(issue.getId() + " " + module.getModuleID()
							+ " 資材一覧と調査結果モジュールが不一致");
				}
			}

			if (results.get(issue.getId()).getFunctions().length == 0) {
				printMessage(issue.getId() + " テスト対象一覧が記載されていない");
			}
		}

	}

	private static String[] getUTModuleIDs(IssueModule[] modules) {
		ArrayList<String> idList = new ArrayList<String>();
		for (IssueModule module : modules) {
			if (module.getModelType() == 1) {
				if (!StringUtils.isBlank(module.getUtSheetName())) {
					idList.add(module.getUtSheetName());
				} else {
					idList.add(module.getModuleID());
				}
			}
		}

		return idList.toArray(new String[idList.size()]);
	}

	public static void checkUT(Issue[] issues) throws IOException {
		String path = config.getPropertyValue("check", "ut_file_path");
		SVNUtil.updateSvnByTortoiseSvn(path, svnInstallPath);

		ArrayList<File> fileList = new ArrayList<File>();
		FileUtil.listAbsoluteFiles(new File(path), fileList, "ケース");// ケースのみチェック

		for (Issue issue : issues) {
			IssueModule[] modules = issue.getModules();
			String[] moduleIDs = getUTModuleIDs(modules);

			int[] sheetExists = new int[moduleIDs.length];

			for (File f : fileList) {
				if (f.getName().endsWith("xlsx") || f.getName().endsWith("xls")) {
					if (f.getName().toUpperCase().contains(
							issue.getId().toUpperCase())) {
						// System.out.println(f.getAbsolutePath());
						int[] sheetNos = ExcelUtil.findSheetExistByName(f
								.getAbsolutePath(), moduleIDs);
						if (sheetExists.length != sheetNos.length) {
							throw new IllegalStateException("sheet個数不一致");
						}
						for (int i = 0; i < sheetExists.length; i++) {
							sheetExists[i] = sheetExists[i] | sheetNos[i];
						}
					}
				}
			}

			for (int i = 0; i < sheetExists.length; i++) {
				if (sheetExists[i] == 0) {
					if (moduleIDs[i].toUpperCase().contains("BEAN")) {
						printMessage(issue.getId() + " モジュール: " + moduleIDs[i]
								+ " Beanクラス、単体テストケースなし");
					} else {
						printMessage(issue.getId() + " モジュール: " + moduleIDs[i]
								+ " 単体テストケースが見つからない");
					}

				}
			}
		}
	}

	public static Map<String, Integer> checkST(Issue[] issues,
			Map<String, ResearchResult> results) throws IOException {

		String path1 = config.getPropertyValue("check", "st_file_path1");
		SVNUtil.updateSvnByTortoiseSvn(path1, svnInstallPath);
		String path2 = config.getPropertyValue("check", "st_file_path2");
		SVNUtil.updateSvnByTortoiseSvn(path2, svnInstallPath);
		String path3 = config.getPropertyValue("check", "st_file_path3");
		SVNUtil.updateSvnByTortoiseSvn(path3, svnInstallPath);
		String path4 = config.getPropertyValue("check", "st_file_path4");
		SVNUtil.updateSvnByTortoiseSvn(path4, svnInstallPath);
		// String path5 = config.getPropertyValue("check", "st_file_path5");
		// SVNUtil.updateSvnByTortoiseSvn(path5, svnInstallPath);
		// String path6 = config.getPropertyValue("check", "st_file_path6");
		// SVNUtil.updateSvnByTortoiseSvn(path6, svnInstallPath);
		// String path7 = config.getPropertyValue("check", "st_file_path7");
		// SVNUtil.updateSvnByTortoiseSvn(path7, svnInstallPath);
		
		ArrayList<File> arrDirectory = new ArrayList<File>();
		FileUtil.listSubDirectorys(new File(path1), arrDirectory);
		FileUtil.listSubDirectorys(new File(path2), arrDirectory);
		FileUtil.listSubDirectorys(new File(path3), arrDirectory);
		FileUtil.listSubDirectorys(new File(path4), arrDirectory);

		// FileUtil.listSubDirectorys(new File(path5), arrDirectory);
		// FileUtil.listSubDirectorys(new File(path6), arrDirectory);
		// FileUtil.listSubDirectorys(new File(path7), arrDirectory);

		Map<String, Integer> caseCountMap = new HashMap<String, Integer>();
		// issue_id + funciton_name
		for (Issue issue : issues) {
			int caseSum = 0;
			ResearchResult result = results.get(issue.getId());
			// System.out.println("issue id : "+issue.getId()+" result "+result);
			if (result == null) {
				continue;
			}

			IssueFunction[] functions = result.getFunctions();
			for (IssueFunction function : functions) {
				boolean match = false;
				for (File folder : arrDirectory) {
					if (folder.getAbsolutePath().contains(issue.getId())) {// get the issue's sub directory
						ArrayList<File> fileList = new ArrayList<File>();
						FileUtil.listAbsoluteFilesNotRecurse(folder, fileList,
								"ケース");// get all case files
						//printFile(fileList);
						//print(issue.getId() + ":"+function.getFunctionName());
						
						for (File f : fileList) {// loop case files
							if (f.getName().toUpperCase().contains(
									issue.getId().toUpperCase())
									&& f.getName().contains(
											function.getFunctionName())) {// match
																			// ok
								// get file's case no sum
								caseSum += getTotalCaseSum(f.getAbsolutePath());
								//print("f.getName(): case "+caseSum);
								match = true;
							}
						}
					}
				}

				if (!match) {
					printMessage(issue.getId() + ":"
							+ function.getFunctionName()
							+ " 結合試験ケースファイルが見つからない");
				}
			}

			caseCountMap.put(issue.getId(), new Integer(caseSum));
		}

		return caseCountMap;
	}
	
	private static void print(String str){
		System.out.println(str);
	}
	
	private static void printString(String[] list){
		for(String str:list){
			System.out.println(str);
		}
	}
	private static void printFile(ArrayList<File> list){
		for(File f:list){
			System.out.println(f.getAbsolutePath());
		}
	}
	
	/**
	 * 
	 * @param fileName
	 *            excel sheet name
	 * @param exceptSheetIndex
	 *            sheet not be counted
	 * @param rownum
	 *            case num's rowno
	 * @param columnAlpha
	 *            case num's column
	 * @return
	 * @throws IOException
	 */
	private static int getTotalCaseSum(String fileName) throws IOException {
		int caseSum = 0;
		int nums = ExcelUtil.getSheetNums(fileName);

		int[] sheetNos = new int[nums];

		for (int i = 0; i < nums; i++) {
			sheetNos[i] = i;
		}

		String[] valueKbns = ExcelUtil.getValueByRowColumn(fileName, sheetNos,
				1, "A");
		String[] values = ExcelUtil.getValueByRowColumn(fileName, sheetNos, 1,
				"H");

		for (int i = 0; i < valueKbns.length; i++) {
			if ("SI1(業務試験)".equals(valueKbns[i])) {
				try {
					caseSum += (int) Double.parseDouble(values[i]);
				} catch (NumberFormatException nume) {
					printMessage(fileName + " sheet no: " + i
							+ " formatが正しくない,ケース数取得出来ない");

					nume.printStackTrace();
					throw new IOException(nume);
				}
			}
		}
		return caseSum;
	}

	public static void checkDate(Issue[] issues) {
		for (Issue issue : issues) {
			if ("内部結合済".equals(issue.getStatus())
					|| "内部結合完了".equals(issue.getStatus())) {
				if (StringUtils.isBlank(issue.getActualStartDate())
						|| StringUtils.isBlank(issue.getActualFinishDate())) {
					printMessage(issue.getId() + "「開始実績日」または「完了実績日」未記載");

				} else {
					// check外部review期限
					if ("内部結合済".equals(issue.getStatus())) {
						String finishDate = issue.getActualFinishDate();
						Calendar cal = getDateFromExcel(finishDate);

						// 5 days after
						cal.add(Calendar.DAY_OF_MONTH, REVIEW_KIGEN);

						Calendar currDate = Calendar.getInstance();
						currDate.setTimeInMillis(System.currentTimeMillis());
						SimpleDateFormat formater = new SimpleDateFormat(
								"yyyy-MM-dd");
						switch (formater.format(cal.getTime()).compareTo(
								formater.format(currDate.getTime()))) {
						case 0:
						case -1:
							printMessage(issue.getId() + " 内部完了日が"
									+ REVIEW_KIGEN + "日過ぎたが、外部レビュー終わっていない");
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

				if (StringUtils.isBlank(issue.getPlanFinishDate())) {
					printMessage(issue.getId() + "「完了予定日」未記載");
				} else {
					// check finish date
					String[] planFinishDate = issue.getPlanFinishDate()
							.replaceAll("\n", "").split("⇒|->");
					String lastFinishDate = planFinishDate[planFinishDate.length - 1];

					Calendar calFinishDate = getDateFromExcel(lastFinishDate);

					switch (formater.format(calFinishDate.getTime()).compareTo(
							formater.format(currDate.getTime()))) {
					case 0:
						printMessage(issue.getId() + " 予定完了日が到達している");
						break;
					case -1:
						printMessage(issue.getId() + " 予定完了日が過ぎている");
						break;
					case 1:
						break;
					}
				}
				// check plan start date
				if ((StringUtils.isBlank(issue.getStatus()) || "未着手"
						.equals(issue.getStatus()))
						&& !StringUtils.isBlank(issue.getPlanStartDate())) {
					String startDate = issue.getPlanStartDate();

					Calendar calPlanStart = getDateFromExcel(startDate);
					switch (formater.format(calPlanStart.getTime()).compareTo(
							formater.format(currDate.getTime()))) {
					case -1:
						printMessage(issue.getId() + " 予定開始日が過ぎているが、未着手");
						break;
					}
				}
			}
		}
	}

	private static Calendar getDateFromExcel(String date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		// Calendar currDate = (Calendar)cal.clone();

		if (isNumber(date)) {
			cal.set(1900, 0, 1);
			cal.add(Calendar.DAY_OF_MONTH, (int) Double.parseDouble(date) - 2);
		} else {
			String regex = "(\\d\\d\\d\\d)?[/-]?(1[0-2]|0?[1-9])[/-]([1-2][0-9]|3[0-1]|0?[1-9]|)";

			String ymd = date.replaceAll(regex, "$1,$2,$3");
			String[] ymds = ymd.split(",");

			if (StringUtils.isBlank(ymds[0])) {
				cal.set(Calendar.MONTH, Integer.parseInt(ymds[1]) - 1);
				cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(ymds[2]));
			} else {
				cal.set(Integer.parseInt(ymds[0]),
						Integer.parseInt(ymds[1]) - 1, Integer
								.parseInt(ymds[2]));
			}
		}

		return cal;
	}

	public static void checkResearchStatus(Issue[] issues) {
		for (Issue issue : issues) {
			if (StringUtils.isBlank(issue.getResearchStatus())) {
				printMessage(issue.getId() + "「調査要否」未記載");
			}
		}
	}

	public static void checkSourceCommit(Issue[] issues) throws SVNException {
		// check source commit
		String sourceFile1 = config.getPropertyValue("check",
				"mps_source_path1");
		String sourceFile2 = config.getPropertyValue("check",
				"mps_source_path2");
		String sourceFile3 = config
				.getPropertyValue("check", "if_source_path1");
		String sourceFile4 = config.getPropertyValue("check",
				"core_source_path1");
		String sourceFile5 = config.getPropertyValue("check",
				"core_source_path2");
		String[] sourcePaths = new String[] { sourceFile1, sourceFile2,
				sourceFile3, sourceFile4, sourceFile5 };
		HashMap<String, String> sources = FileUtil.listSources(sourcePaths,
				svnInstallPath);

		for (Issue issue : issues) {
			IssueModule[] modules = issue.getModules();

			if (modules.length == 0) {
				printMessage(issue.getId() + " がモジュール一覧に存在しない");

			}

			for (IssueModule module : modules) {
				if (sources.containsKey(module.getModulePath())) {
					List<SVNLogEntry> list = SVNUtil.getHistory(sources
							.get(module.getModulePath()), getStartDate(-90),
							getEndDate(), module.getIssueID());
					if (list.size() == 0) {
						printMessage(module.getIssueID() + " "
								+ module.getModulePath()
								+ " Warning: 該当するコミット履歴が見つからない");
					} else {
						// System.out.println(module.getIssueID() + " "
						// +module.getModulePath() +" Info: commit ok");
					}

					if (DEBUG) {
						printLog(list);
					}
				} else {// 共通資材
					if ("データベース設計書".equals(module.getModuleID())
							|| "データベース設計書（料率）".equals(module.getModuleID())
							|| "コードリスト（DB設定）".equals(module.getModuleID())) {
						if (StringUtils.isBlank(module.getDdlSql())
								|| StringUtils.isBlank(module.getPatchYouhi())) {
							printMessage(module.getIssueID() + " "
									+ module.getModuleID() + "\\"
									+ " Warning:DB資材はパッチ要否が記入されていない");
						}
					}
					printMessage(module.getIssueID() + " "
							+ module.getModuleID() + "\\"
							+ module.getModulePath() + " Info:ソースではない");
				}
			}
		}
	}

	public static Map<String, ResearchResult> checkResearchFile(Issue[] issues)
			throws IOException {
		Map<String, ResearchResult> mapResearchResult = new HashMap<String, ResearchResult>();
		// ArrayList<ResearchResult> arrResearchResult = new
		// ArrayList<ResearchResult>();

		// update svn
		String researchFilePath = config.getPropertyValue("check",
				"research_result_path");
		SVNUtil.updateSvnByTortoiseSvn(researchFilePath, svnInstallPath);

		ArrayList<File> arrResult = new ArrayList<File>();
		FileUtil.listAbsoluteFiles(new File(researchFilePath), arrResult);
		String[] strPaths = new String[arrResult.size()];
		for (int i = 0; i < strPaths.length; i++) {
			strPaths[i] = arrResult.get(i).getAbsolutePath();
		}

		String[] strTarget = new String[issues.length];
		for (int i = 0; i < strTarget.length; i++) {
			strTarget[i] = issues[i].getId();
		}

		Arrays.sort(strTarget);
		Arrays.sort(strPaths);
		ArrayList<String> out = new ArrayList<String>();

		int startIndex = 0;
		for (int i = 0; i < strTarget.length; i++) {
			boolean match = false;

			for (int j = startIndex; j < strPaths.length; j++) {
				String regex = ".+\\\\(.+)$";
				String fileName = strPaths[j].replaceAll(regex, "$1");
				if (fileName.contains("同件調査") || fileName.contains("横展開")) {// 同件調査ファイルを対象外にする
					continue;
				}
				if (fileName.toLowerCase().contains(strTarget[i])) {
					ResearchResult result = null;
					if (mapResearchResult.containsKey(strTarget[i])) {
						result = mapResearchResult.get(strTarget[i]);
					} else {
						result = new ResearchResult(strTarget[i]);
					}
					// swap(strResult,j,startIndex);
					// startIndex++; //一つの調査結果に複数の案件番号が含まれているため、一回当てても対象外にならない
					match = true;

					// read research file info;modules and test functions
					// module name,module path
					Map<String, String> listColNames = new HashMap<String, String>();
					listColNames.put(MODULE_ID, "B");
					listColNames.put(MODULE_PATH, "C");
					// set research file's modules
					// System.out.println(strTarget[i]+" "+strResult[j]);
					try {
						Map<String, String>[] modules = ExcelUtil
								.readContentFromExcelMult(strPaths[j], 0,
										listColNames, "MODULE_ID! ", 1);
						for (Map<String, String> map : modules) {
							IssueModule module = new IssueModule();
							module.setIssueID(strTarget[i]);
							module.setModuleID(map.get(MODULE_ID));
							module.setModulePath(map.get(MODULE_PATH));
							if (StringUtils.isBlank(map.get(MODULE_PATH))) {
								module.setModulePath(map.get(MODULE_ID));
							}
							result.addIssueModule(module);
						}
						// set research file's test functions
						listColNames = new HashMap<String, String>();
						listColNames.put(FUNCTION_NAME, "B");
						listColNames.put(JOB_ID, "C");
						listColNames.put(PATTERN_NAME, "D");
						// TODO
						Map<String, String>[] functions = ExcelUtil
								.readContentFromExcelMult(strPaths[j], 1,
										listColNames, "FUNCTION_NAME! ", 1);
						// IssueFunction function = null;
						String functionName = null;
						for (Map<String, String> map : functions) {
							String patternName = map.get(PATTERN_NAME);
							functionName = StringUtils.isBlank(map
									.get(FUNCTION_NAME)) ? functionName : map
									.get(FUNCTION_NAME);
							if (StringUtils.isBlank(patternName)) {
								printMessage(strTarget[i] + " 機能名:"
										+ functionName
										+ " Warning:調査結果にテストパターン名が記入されていない");
							}
							if (StringUtils.isBlank(map.get(FUNCTION_NAME))) {
								continue;
							}
							IssueFunction function = new IssueFunction();
							function.setIssueID(strTarget[i]);
							function.setFunctionName(map.get(FUNCTION_NAME));

							result.addIssueFunction(function);
						}
					} catch (Exception e) {
						System.err.println(strTarget[i] + " " + strPaths[j]
								+ " read excel error");
						e.printStackTrace();
					}
					mapResearchResult.put(strTarget[i], result);

					// arrResearchResult.add(result);
				}
			}

			if (match == false) {
				out.add(strTarget[i]);
			}
		}

		for (String str : out) {
			printMessage(str + " 調査結果ファイルが存在しない");
		}
		if (out.size() == 0) {
			printMessage("Info:調査結果ファイルチェックOK");
		}

		return mapResearchResult;
		// return arrResearchResult.toArray(new
		// ResearchResult[arrResearchResult.size()]);
	}

	/**
	 * check the change and mantis exists
	 */
	public static void checkScopeKadai(Issue[] issues) throws IOException {
		// check research file
		String kadaiListFile1 = config.getPropertyValue("check",
				"kadai_list_file1");
		SVNUtil.updateSvnByTortoiseSvn(kadaiListFile1, svnInstallPath);

		// change list
		Map<String, String> listName = new HashMap<String, String>();
		listName.put("no", "DO");
		listName.put("release_status", "DP");
		// listName.put("release_status", "I");

		Map<String, String>[] listKadai1 = ExcelUtil.readContentFromExcelMult(
				kadaiListFile1, 0, listName, "no! &release_status!リリース済", 0);

		HashSet<String> issueNoSet = new HashSet<String>();
		for (Issue issue : issues) {
			issueNoSet.add(issue.getId());
		}

		ArrayList<String> array = new ArrayList<String>();
		// check kadai list
		for (Map<String, String> map : listKadai1) {
			String[] nos = map.get("no").split("\\n");

			for (String no : nos) {
				if (!issueNoSet.contains(no)) {
					array.add(no);
					printMessage(no + " が案件状況一覧に存在しない若しくは課題一覧のリリースステータスが不正");
				}
			}
		}

	}

	/**
	 * check the change and mantis exists
	 */
	public static void checkScope(Issue[] issues) throws IOException {
		// check research file
		String changeListFile = config.getPropertyValue("check",
				"change_list_file");
		SVNUtil.updateSvnByTortoiseSvn(changeListFile, svnInstallPath);

		String mantisListFile = config.getPropertyValue("check",
				"mantis_list_file");
		SVNUtil.updateSvnByTortoiseSvn(mantisListFile, svnInstallPath);

		// change list
		Map<String, String> listColNames = new HashMap<String, String>();
		listColNames.put("no", "A");
		listColNames.put("releaseStatus", "O");

		Map<String, String>[] changeList = ExcelUtil.readContentFromExcelMult(
				changeListFile, 0, listColNames,
				"releaseStatus= |releaseStatus=未リリース&no! ", 0);

		// mantis list
		Map<String, String> mantisColNames = new HashMap<String, String>();
		mantisColNames.put("no", "A");
		mantisColNames.put("releaseStatus", "AC");
		mantisColNames.put("permitDate", "Z");

		Map<String, String>[] mantisList = ExcelUtil.readContentFromExcelMult(
				mantisListFile, 0, mantisColNames,
				"releaseStatus= |releaseStatus=未リリース&no! ", 0);

		HashSet<String> issueNoSet = new HashSet<String>();
		for (Issue issue : issues) {
			issueNoSet.add(issue.getId());
		}

		ArrayList<String> array = new ArrayList<String>();
		// check change list
		for (Map<String, String> map : changeList) {
			String regex = "(\\d+)[.]?\\d*";
			String no = "仕様変更" + map.get("no").replaceAll(regex, "$1");// replace
																		// 759.0
																		// to
																		// 759

			if (!issueNoSet.contains(no)) {
				array.add(no);
				printMessage(no + " が案件状況一覧に存在しない若しくは仕様変更一覧のリリースステータスが不正");
			}
		}

		for (Map<String, String> map : mantisList) {
			String regex = "(\\d+)[.]?\\d*";
			String no = "mantis" + map.get("no").replaceAll(regex, "$1");// replace
																			// 759.0
																			// to
																			// 759
			String permitDate = map.get("permitDate");

			if (StringUtils.isBlank(permitDate)) {
				printMessage(no + " が承認されていない(承認日付が記載されていない)");
			} else {
				if (!issueNoSet.contains(no)) {
					array.add(no);
					printMessage(no + " が案件状況一覧に存在しない若しくはmantis一覧のリリースステータスが不正");
				}
			}
		}
	}

	private static void printLog(List<SVNLogEntry> history) {
		for (SVNLogEntry logEntry : history) {
			System.out.println("---------------------------------------------");
			System.out.println("revision: " + logEntry.getRevision());
			System.out.println("author: " + logEntry.getAuthor());
			System.out.println("date: " + logEntry.getDate());
			System.out.println("log message: " + logEntry.getMessage());

			if (logEntry.getChangedPaths().size() > 0) {
				System.out.println();
				System.out.println("changed paths:");
				Set changedPathsSet = logEntry.getChangedPaths().keySet();

				for (Iterator<SVNLogEntryPath> changedPaths = changedPathsSet
						.iterator(); changedPaths.hasNext();) {
					SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry
							.getChangedPaths().get(changedPaths.next());
					System.out.println(" "
							+ entryPath.getType()
							+ " "
							+ entryPath.getPath()
							+ ((entryPath.getCopyPath() != null) ? " (from "
									+ entryPath.getCopyPath() + " revision "
									+ entryPath.getCopyRevision() + ")" : ""));
				}
			}
		}
	}

	// 30 days before
	private static Date getStartDate(int minusDay) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.add(Calendar.DAY_OF_MONTH, minusDay);
		return cal.getTime();
	}

	// today
	private static Date getEndDate() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Issue[] getIssueInfo(String issueListFilterPattern,
			boolean readModuleInfo) throws IOException {
		String configListFile = config.getPropertyValue("check",
				"issue_list_file");
		SVNUtil.updateSvnByTortoiseSvn(configListFile, svnInstallPath);
		// read base info
		Map<String, String>[] mapTarget = ExcelUtil.readContentFromExcelMult(
				configListFile, 0, columnNameMapIssueList,
				issueListFilterPattern, 0);// sheet 0

		Issue[] issues = new Issue[mapTarget.length];
		for (int i = 0; i < mapTarget.length; i++) {
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
			issues[i].setIkouResource(mapTarget[i].get(IKOU_RESOURCE));
			// 予定工数
			issues[i].setYoteiKousuResearch(mapTarget[i]
					.get(YOTEI_KOUSU_RESEARCH));
			issues[i].setYoteiKousuCD(mapTarget[i].get(YOTEI_KOUSU_CD));
			issues[i].setYoteiKousuUT(mapTarget[i].get(YOTEI_KOUSU_UT));
			issues[i].setYoteiKousuST(mapTarget[i].get(YOTEI_KOUSU_ST));
			// 実績工数
			issues[i]
					.setJisekiResearch(mapTarget[i].get(JISEKI_KOUSU_RESEARCH));
			issues[i].setJisekiKousuCD(mapTarget[i].get(JISEKI_KOUSU_CD));
			issues[i].setJisekiKousuUT(mapTarget[i].get(JISEKI_KOUSU_UT));
			issues[i].setJisekiKousuST(mapTarget[i].get(JISEKI_KOUSU_ST));
			// percent
			issues[i].setResearchPercent(mapTarget[i].get(RESEARCH_PERCENT));
			issues[i].setCdPercent(mapTarget[i].get(CD_PERCENT));
			issues[i].setUtPercent(mapTarget[i].get(UT_PERCENT));
			issues[i].setStPercent(mapTarget[i].get(ST_PERCENT));
			// release version
			issues[i].setRlsVersion(mapTarget[i].get(RELEASE_VERSION));
		}

		if (readModuleInfo) {
			// read module info
			setIssueModule(issues);
		}

		return issues;
	}

	private static void setIssueModule(Issue[] issues) throws IOException {
		String moduleListFile = config.getPropertyValue("check",
				"module_list_file");
		SVNUtil.updateSvnByTortoiseSvn(moduleListFile, svnInstallPath);

		Map<String, String>[] mapTarget = ExcelUtil.readContentFromExcelMult(
				moduleListFile, 1, columnNameMapModuleList,
				"RELEASE_STATUS!リリース不要", 0);// sheet 1

		HashMap<String, Issue> issueMap = new HashMap<String, Issue>();
		for (Issue issue : issues) {
			issueMap.put(issue.getId(), issue);
		}

		for (int i = 0; i < mapTarget.length; i++) {
			String issueID = mapTarget[i].get(ISSUE_ID);
			if (issueMap.containsKey(issueID)) {
				Issue issue = issueMap.get(issueID);
				IssueModule module = new IssueModule();
				module.setIssueID(issueID);
				String modulePath = mapTarget[i].get(MODULE_PATH);
				module.setModulePath(mapTarget[i].get(MODULE_PATH));

				if (StringUtils.isBlank(modulePath)) {// mapping key
					module.setModulePath(mapTarget[i].get(MODULE_ID));
				}
				module.setModuleID(mapTarget[i].get(MODULE_ID));

				module.setProjectID(mapTarget[i].get(PROJECT_ID));
				module.setFunctionName(mapTarget[i].get(FUNCTION_NAME));
				module.setUtSheetName(mapTarget[i].get(UT_SHEET_NAME));// ut
																		// sheet
																		// name

				// java source,xml
				module.setModelType(1);
				issue.addIssueModule(module);
			}
		}

		mapTarget = ExcelUtil.readContentFromExcelMult(moduleListFile, 2,
				columnNameMapCommonModuleList, "RELEASE_STATUS!リリース不要", 0);// sheet
																			// 2
		for (int i = 0; i < mapTarget.length; i++) {
			String issueID = mapTarget[i].get(ISSUE_ID);

			if (issueMap.containsKey(issueID)) {
				Issue issue = issueMap.get(issueID);
				IssueModule module = new IssueModule();
				module.setIssueID(issueID);

				module.setModulePath(mapTarget[i].get(MODULE_PATH));
				module.setModuleID(mapTarget[i].get(MODULE_ID));
				module.setModelType(2);
				module.setDdlSql(mapTarget[i].get(DDL_SQL));
				module.setPatchYouhi(mapTarget[i].get(PATCH_YOUHI));
				// add
				issue.addIssueModule(module);
			}
		}
	}

	private static void swap(String[] array, int i, int j) {
		String tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}

	/**
	 * update svn
	 * 
	 * @param destDirectory
	 *            private static void updateSvn(String destDirectory){
	 *            StringBuffer strBufClean = new StringBuffer(); String
	 *            svnInstallPath = config.getPropertyValue("global",
	 *            "svn_install_path"); //clean
	 *            strBufClean.append("/command:cleanup /path:");
	 *            strBufClean.append(destDirectory.substring(0,
	 *            destDirectory.lastIndexOf("/"))); strBufClean.append(SPACE);
	 *            strBufClean.append("/notempfile /noui /closeonend:1");
	 *            String[] commandClean = new String[]{svnInstallPath,
	 *            strBufClean.toString()};
	 * 
	 *            //update StringBuffer strBufUpdate = new StringBuffer();
	 *            strBufUpdate.append("/command:update /path:");
	 *            strBufUpdate.append(destDirectory);
	 *            strBufUpdate.append(SPACE);
	 *            strBufUpdate.append("/notempfile /closeonend:1"); String[]
	 *            commandUpdate = new
	 *            String[]{svnInstallPath,strBufUpdate.toString()};
	 * 
	 *            try{ java.lang.Process process1 =
	 *            Runtime.getRuntime().exec(commandClean); process1.waitFor();
	 *            java.lang.Process process2 =
	 *            Runtime.getRuntime().exec(commandUpdate); process2.waitFor();
	 *            }catch (Exception e){ e.printStackTrace(); }
	 * 
	 *            }
	 */
	private static boolean isNumber(String input) {
		String regex = "^[1-9][0-9]*[.]?(\\d)*$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		return m.find();
	}

	private static void printMessage(String message) {
		messageOut.add(message);
		if (CONSOLE) {
			System.out.println(message);
		}
	}

}
