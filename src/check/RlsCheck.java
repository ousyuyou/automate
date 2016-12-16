package check;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import svn.ConfigFile;
import svn.SVNUtil;
import util.ExcelUtil;
import util.FileUtil;
import util.MergeHeroDirCompare;
import util.MergeHeroDirCompareResult;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * @author
 * �@�\�T�v�F
 * 
 * �����[�X�ΏۈČ����i����
 * 1.�J���Ŏ��ނƃ����[�X�Ŏ��ނ��R���y�A
 * �������鎑�ނɑ΂��āA�������o�͂���i�t�@�C���P�ʃR���y�A�j
 * 
 * 2.�����[�X�Ŏ��ނɑ΂��āA�O��̃����[�X�łƍ����
 * �����[�X�łƃf�B���N�g���P�ʂŃR���y�A
 * �t�@�C���P�ʂ̍����ƃ����[�X���ވꗗ�����S�Ɉ�v����
 * ���Ƃ��m�F����
 */
public class RlsCheck {
	private static ConfigFile appConfig = null;
	static {
		appConfig = new ConfigFile(new File("e:/config/config.dat"));
	}
	//rls version
//	private static String previous_rls_version = appConfig.getPropertyValue("global", "previous_rls_version");
	private static String current_rls_version = appConfig.getPropertyValue("global", "current_rls_version");
	private static String current_ikou_rls_version = appConfig.getPropertyValue("global", "current_ikou_rls_version");
	//dev source path & rls path source
	private static String mps_dev_path = appConfig.getPropertyValue("global", "mps_dev_path");
	private static String mps_rls_path = appConfig.getPropertyValue("global", "mps_rls_path");
	private static String if_dev_path = appConfig.getPropertyValue("global", "if_dev_path");
	private static String if_rls_path = appConfig.getPropertyValue("global", "if_rls_path");
	private static String core_dev_path = appConfig.getPropertyValue("global", "core_dev_path");
	private static String core_rls_path = appConfig.getPropertyValue("global", "core_rls_path");
	//prev rls source
	private static String mps_prev_rls_path = appConfig.getPropertyValue("global", "mps_prev_rls_path");
	private static String if_prev_rls_path = appConfig.getPropertyValue("global", "if_prev_rls_path");
	private static String core_prev_rls_path = appConfig.getPropertyValue("global", "core_prev_rls_path");
	//compare subfolder
	private static String mps_path_apps = appConfig.getPropertyValue("check", "mps_path_apps");
	private static String mps_path_src = appConfig.getPropertyValue("check", "mps_path_src");
	private static String mps_path_layout = appConfig.getPropertyValue("check", "mps_path_layout");
	private static String mps_path_shell = appConfig.getPropertyValue("check", "mps_path_shell");
	
	private static String if_path_src = appConfig.getPropertyValue("check", "if_path_src");
	private static String core_path_src = appConfig.getPropertyValue("check", "core_path_src");
	private static String core_path_conf = appConfig.getPropertyValue("check", "core_path_conf");
	
	private static String svnInstallPath = appConfig.getPropertyValue("global", "svn_install_path");

	private static final boolean CONSOLE = true;
	private static ArrayList<String> messageOut = new ArrayList<String>();

	/**
	 * module list,source
	 */

	private static Map<String,String> columnNameMapModuleList = new HashMap<String, String>();
	static{
		columnNameMapModuleList.put("ISSUE_ID", "B");
		columnNameMapModuleList.put("FUNCTION_NAME", "C");
		columnNameMapModuleList.put("PROJECT_ID", "D");
		columnNameMapModuleList.put("MODULE_ID", "E");
		columnNameMapModuleList.put("MODULE_PATH", "F");
		columnNameMapModuleList.put("RELEASE_VERSION", "K");
		columnNameMapModuleList.put("RELEASE_STATUS", "L");
		columnNameMapModuleList.put("UPDATE_KBN", "G");
	}
	/**
	 * module list,database,shell's setting...
	 */
	private static Map<String,String> columnNameMapCommonModuleList = new HashMap<String, String>();
	static {
		columnNameMapCommonModuleList.put("ISSUE_ID", "B");
		columnNameMapCommonModuleList.put("MODULE_ID", "C");
		columnNameMapCommonModuleList.put("MODULE_PATH", "D");
		columnNameMapCommonModuleList.put("RELEASE_STATUS", "I");
		//K,L��FDDL��ƃp�b�`�v�ۃ`�F�b�N
		columnNameMapCommonModuleList.put("DDL_SQL", "K");
		columnNameMapCommonModuleList.put("PATCH_YOUHI", "L");
		columnNameMapCommonModuleList.put("RELEASE_VERSION", "H");
	}
	
	
	/**
	 * module list,database,shell's setting...
	 */
	private static Map<String,String> columnNameMapIkouModuleList = new HashMap<String, String>();
	static {
		columnNameMapIkouModuleList.put("ISSUE_ID", "B");
		columnNameMapIkouModuleList.put("PROJECT_ID", "D");
		columnNameMapIkouModuleList.put("MODULE_PATH", "E");
		columnNameMapIkouModuleList.put("RELEASE_VERSION", "K");
		columnNameMapIkouModuleList.put("RELEASE_STATUS", "L");
		columnNameMapIkouModuleList.put("UPDATE_KBN", "G");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		// check suritoukei
		checkSuritoukei();
		//check ikou
		checkIkou();
	}
	
	private static void checkIkou() throws IOException{
		Issue[] issues = getIssueInfo("RELEASE_VERSION��"+current_ikou_rls_version, true,true);
		
		String dev_sourc1 = appConfig.getPropertyValue("ikou_check", "ikou_dev_path1");//MPS_IKOU/configuration
		String dev_sourc2 = appConfig.getPropertyValue("ikou_check", "ikou_dev_path2");//MPS_IKOU/src
		String dev_sourc3 = appConfig.getPropertyValue("ikou_check", "ikou_dev_path3");//MPS_IKOU_CHECK/src
		String[] dev_sources = new String[]{dev_sourc1,dev_sourc2,dev_sourc3};
		Map<String, String> devSourceMap = FileUtil.listSources(dev_sources, svnInstallPath);
		
		String rls_sourc1 = appConfig.getPropertyValue("ikou_check", "ikou_rls_path1");//MPS_IKOU/configuration
		String rls_sourc2 = appConfig.getPropertyValue("ikou_check", "ikou_rls_path2");//MPS_IKOU/src
		String rls_sourc3 = appConfig.getPropertyValue("ikou_check", "ikou_rls_path3");//MPS_IKOU_CHECK/src
		String[] rls_sources = new String[]{rls_sourc1,rls_sourc2,rls_sourc3};
		Map<String, String> rlsSourceMap = FileUtil.listSources(rls_sources, svnInstallPath);
		
		String prev_rls_sourc1 = appConfig.getPropertyValue("ikou_check", "ikou_prev_rls_path1");//MPS_IKOU/configuration
		String prev_rls_sourc2 = appConfig.getPropertyValue("ikou_check", "ikou_prev_rls_path2");//MPS_IKOU/src
		String prev_rls_sourc3 = appConfig.getPropertyValue("ikou_check", "ikou_prev_rls_path3");//MPS_IKOU_CHECK/src
		String[] rls_prev_sources = new String[]{prev_rls_sourc1,prev_rls_sourc2,prev_rls_sourc3};
//		Map<String, String> rlsPrevSourceMap = FileUtil.listSources(rls_prev_sources, svnInstallPath);

		//compare prev rls folder with current rls folder 
		MergeHeroDirCompareResult[] results = new MergeHeroDirCompareResult[rls_prev_sources.length];
		
		ArrayList<String> arrLeftFileOnly = new ArrayList<String>();
		ArrayList<String> arrRighFiletOnly = new ArrayList<String>();
		ArrayList<String> arrFileDiff = new ArrayList<String>();
		
		for(int i = 0 ; i < rls_prev_sources.length;i++){
			MergeHeroDirCompare compare = new MergeHeroDirCompare(rls_prev_sources[i],rls_sources[i],true);
			results[i] = compare.executeCompare();
			//
//			System.out.println(results[i].getM_strComResultDetail());
			
			for(String leftFileOnly:results[i].getFileLeftOnly()){
				arrLeftFileOnly.add(leftFileOnly.replaceAll("\\\\", "/"));
				//DEBUG
				System.out.println("left: "+rls_prev_sources[i] + " right: "+rls_sources[i]);
				System.out.println(leftFileOnly.replaceAll("\\\\", "/"));
//				System.out.println("left only: "+appendixs[i]+"/"+leftFileOnly.replaceAll("\\\\", "/"));
			}
			for(String rightFileOnly:results[i].getFileRightOnly()){
				arrRighFiletOnly.add(rightFileOnly.replaceAll("\\\\", "/"));
				//DEBUG
				System.out.println("left: "+rls_prev_sources[i] + " right: "+rls_sources[i]);
				System.out.println(rightFileOnly.replaceAll("\\\\", "/"));
			}
			for(String diffFile:results[i].getFileDiff()){
				arrFileDiff.add(diffFile.replaceAll("\\\\", "/"));
				//DEBUG
//				System.out.println("diff file: "+appendixs[i]+"/"+diffFile.replaceAll("\\\\", "/"));
			}
		}
		
		ArrayList<IssueModule> modules = new ArrayList<IssueModule>();
		
		for(Issue issue:issues){
			for(IssueModule module:issue.getModules()){
				String modulePath = module.getModulePath();
				String rlsStatus = module.getRlsStatus();
				modules.add(module);//put all the module to list for next check

				if(!"�����[�X��".equals(rlsStatus)){
					printMessage(issue.getId() + ": "+modulePath + " �ڍs�����[�X�X�e�[�^�X�L�ڕs���F"+rlsStatus);
				}
				
				String rlsVersion = module.getRlsVersion();
				if(!current_ikou_rls_version.equals(rlsVersion)){
					printMessage(issue.getId() + ": "+modulePath + " �ڍs�����[�XVer�L�ڕs���F"+rlsVersion);
				}
				
				//�J���łƃ����[�X�ł̔�r,source only
				if(module.getModelType() == 1){ //source
					//compare develop source with rls source
					String devModulePath = devSourceMap.get(modulePath);
					String rlsModulePath = rlsSourceMap.get(modulePath);
					if(StringUtils.isEmpty(devModulePath) || StringUtils.isEmpty(rlsModulePath)){
						printMessage(issue.getId() + ": "+modulePath + " �ڍs�\�[�X�t�@�C����������Ȃ��A�t�@�C���d�����邩�`�F�b�N���ĉ�����");
						System.err.println("dev path: "+devModulePath);
						System.err.println("rls path: "+rlsModulePath);
						continue;
					}
					List<String> original = fileToLines(devModulePath);
			        List<String> revised  = fileToLines(rlsModulePath);

			        // Compute diff. Get the Patch object. Patch is the container for computed deltas.
			        Patch<String> patch = DiffUtils.diff(original, revised);
			        if(patch.getDeltas().size() > 0){
						printMessage(issue.getId() + ": "+modulePath + " �ڍs�\�[�X�J���łƃ����[�X�ō�������B�m�F���������B");

				        for (Delta<String> delta: patch.getDeltas()) {
				            System.out.println(delta);
				        }
			        }
				} else {//db,script,message
					//�`�F�b�N�s�v
//					System.out.println("common resouce: "+module.getModuleID());
				}
				
				//���ވꗗ�̃��W���[�����m���Ƀt�@�C�������ɑ��݂��邱�Ƃ��`�F�b�N
				if(module.getModelType() == 1){ //source;java,xml
					String updateKbn = module.getUpdatKbn();
					if("�V�K".equals(updateKbn)){
						boolean contains = findModulePathFromDiffResult(arrRighFiletOnly,module.getModulePath());
						
						if(!contains){
							printMessage(issue.getId() + ": "+modulePath + " �ڍs���ވꗗ�ɐV�K�t�@�C�������A�O��RLS�łƍ���RLS�ł̃t�H���_��r�ɂĐV�K�ł͂Ȃ�");
						}
					} else if("�X�V".equals(updateKbn)){
						boolean contains = findModulePathFromDiffResult(arrFileDiff,module.getModulePath());

						if(!contains){
							printMessage(issue.getId() + ": "+modulePath + " �ڍs���ވꗗ�ɍX�V�t�@�C�������A�O��RLS�łƍ���RLS�ł̃t�H���_��r�ɂčX�V�ł͂Ȃ�");
						}
					} else if("�폜".equals(updateKbn)){
						boolean contains = findModulePathFromDiffResult(arrLeftFileOnly,module.getModulePath());
						
						if(!contains){
							printMessage(issue.getId() + ": "+modulePath + " �ڍs���ވꗗ�ɍ폜�t�@�C�������A�O��RLS�łƍ���RLS�ł̃t�H���_��r�ɂč폜�ł͂Ȃ�");
						}
					}
				} else {//script,message,application resource
					//now does not exists
				}
			}
		}
		
		//�t�H���_��r�ŐV�K�t�@�C�����m���Ɏ��ވꗗ�ɋL�ڂ���Ă��邩
		for(String rightFile:arrRighFiletOnly){
			boolean contains = containsJudge(modules,rightFile,true);
			
			if(!contains){
				printMessage("right only file :"+ rightFile+" �t�H���_�R���y�A�ŐV�K�t�@�C�������A�ڍs���ވꗗ�ɋL�ڂ���Ă��Ȃ�");
			}
		}
		//�t�H���_��r�ō폜�t�@�C�����m���Ɏ��ވꗗ�ɋL�ڂ���Ă��邩
		for(String leftFile:arrLeftFileOnly){
			boolean contains = containsJudge(modules,leftFile,true);
			if(!contains){
				printMessage("left only file :"+ leftFile+" �t�H���_�R���y�A�ō폜�t�@�C�������A�ڍs���ވꗗ�ɋL�ڂ���Ă��Ȃ�");
			}
		}
		
		//�t�H���_��r�ōX�V�t�@�C�����m���Ɏ��ވꗗ�ɋL�ڂ���Ă��邩
		for(String diffFile:arrFileDiff){
			boolean contains = containsJudge(modules,diffFile,true);
			if(!contains){
				printMessage("file diff :"+ diffFile+" �t�H���_�R���y�A�ōX�V�t�@�C�������A�ڍs���ވꗗ�ɋL�ڂ���Ă��Ȃ�");
			}
		}
	}
	
	private static void checkSuritoukei() throws IOException{
		Issue[] issues = getIssueInfo("RELEASE_VERSION��"+current_rls_version, true,false);
		
		//list develop sources
		String mps_source_apps = mps_dev_path + mps_path_apps;
		String mps_source_src = mps_dev_path + mps_path_src;
		String mps_source_layout = mps_dev_path + mps_path_layout;
		String mps_source_shell = mps_dev_path + mps_path_shell;
		String if_source_path = if_dev_path + if_path_src;
		String core_source_path = core_dev_path + core_path_src;
		String core_source_conf = core_dev_path + core_path_conf;
		String[] dev_sources = new String[]{mps_source_apps,mps_source_src,mps_source_layout,
				mps_source_shell,if_source_path,core_source_path,core_source_conf};
		Map<String, String> devSourceMap = FileUtil.listSources(dev_sources, svnInstallPath);
		
		//list release source
		String mps_rls_source_apps = mps_rls_path + mps_path_apps;
		String mps_rls_source_src = mps_rls_path + mps_path_src;
		String mps_rls_source_layout = mps_rls_path + mps_path_layout;
		String mps_rls_source_shell = mps_rls_path + mps_path_shell;
		String if_rls_source_path = if_rls_path + if_path_src;
		String core_rls_source_path = core_rls_path + core_path_src;
		String core_rls_source_conf = core_rls_path + core_path_conf;
		
		String[] rls_sources = new String[]{mps_rls_source_apps,mps_rls_source_src,mps_rls_source_layout,
				mps_rls_source_shell,if_rls_source_path,core_rls_source_path,core_rls_source_conf};
		Map<String, String> rlsSourceMap = FileUtil.listSources(rls_sources, svnInstallPath);
		
		//list prev rls source
		String mps_prev_rls_source_apps = mps_prev_rls_path + mps_path_apps;
		String mps_prev_rls_source_src = mps_prev_rls_path + mps_path_src;
		String mps_prev_rls_source_layout = mps_prev_rls_path + mps_path_layout;
		String mps_prev_rls_source_shell = mps_prev_rls_path + mps_path_shell;
		String if_prev_rls_source_path = if_prev_rls_path + if_path_src;
		String core_prev_rls_source_path = core_prev_rls_path + core_path_src;
		String core_prev_rls_source_conf = core_prev_rls_path + core_path_conf;
		
		String[] rls_prev_sources = new String[]{mps_prev_rls_source_apps,mps_prev_rls_source_src,mps_prev_rls_source_layout,
				mps_prev_rls_source_shell,if_prev_rls_source_path,core_prev_rls_source_path,core_prev_rls_source_conf};
//		Map<String, String> rlsPrevSourceMap = FileUtil.listSources(rls_prev_sources, svnInstallPath);
		
		//compare prev rls folder with current rls folder 
		MergeHeroDirCompareResult[] results = new MergeHeroDirCompareResult[rls_prev_sources.length];
		
		ArrayList<String> arrLeftFileOnly = new ArrayList<String>();
		ArrayList<String> arrRighFiletOnly = new ArrayList<String>();
		ArrayList<String> arrFileDiff = new ArrayList<String>();
		
		String[] appendixs = new String[]{"/MPS"+mps_path_apps,
											"/MPS"+mps_path_src,
											"/MPS"+mps_path_layout,
											"/MPS"+mps_path_shell,
											"/FMS-IF"+if_path_src,
											"/FMS-CORE"+core_path_src,
											"/FMS-CORE"+core_path_conf};
		
		for(int i = 0 ; i < rls_prev_sources.length;i++){
			MergeHeroDirCompare compare = new MergeHeroDirCompare(rls_prev_sources[i],rls_sources[i],true);
			results[i] = compare.executeCompare();
			//
//			System.out.println(results[i].getM_strComResultDetail());
			
			for(String leftFileOnly:results[i].getFileLeftOnly()){
				arrLeftFileOnly.add(appendixs[i]+"/"+leftFileOnly.replaceAll("\\\\", "/"));
				//DEBUG
//				System.out.println("left only: "+appendixs[i]+"/"+leftFileOnly.replaceAll("\\\\", "/"));
			}
			for(String rightFileOnly:results[i].getFileRightOnly()){
				arrRighFiletOnly.add(appendixs[i]+"/"+rightFileOnly.replaceAll("\\\\", "/"));
				//DEBUG
//				System.out.println("right only: "+appendixs[i]+"/"+rightFileOnly.replaceAll("\\\\", "/"));
			}
			for(String diffFile:results[i].getFileDiff()){
				arrFileDiff.add(appendixs[i]+"/"+diffFile.replaceAll("\\\\", "/"));
				//DEBUG
//				System.out.println("diff file: "+appendixs[i]+"/"+diffFile.replaceAll("\\\\", "/"));
			}
			
		}
		
		ArrayList<IssueModule> modules = new ArrayList<IssueModule>();
		
		for(Issue issue:issues){
			for(IssueModule module:issue.getModules()){
				String modulePath = module.getModulePath();
				String rlsStatus = module.getRlsStatus();
				modules.add(module);//put all the module to list for next check

				if(!"�����[�X��".equals(rlsStatus)){
					printMessage(issue.getId() + ": "+modulePath + " �����[�X�X�e�[�^�X�L�ڕs���F"+rlsStatus);
				}
				
				String rlsVersion = module.getRlsVersion();
				if(!current_rls_version.equals(rlsVersion)){
					printMessage(issue.getId() + ": "+modulePath + " �����[�XVer�L�ڕs���F"+rlsVersion);
				}
				
				//�J���łƃ����[�X�ł̔�r,source only
				if(module.getModelType() == 1){ //source
					//compare develop source with rls source
					String devModulePath = devSourceMap.get(modulePath);
					String rlsModulePath = rlsSourceMap.get(modulePath);
					if(StringUtils.isEmpty(devModulePath) || StringUtils.isEmpty(rlsModulePath)){
						printMessage(issue.getId() + ": "+modulePath + " �\�[�X�t�@�C����������Ȃ��A�t�@�C���d�����邩�`�F�b�N���ĉ�����");
						System.err.println("dev path: "+devModulePath);
						System.err.println("rls path: "+rlsModulePath);
						continue;
					}
					List<String> original = fileToLines(devModulePath);
			        List<String> revised  = fileToLines(rlsModulePath);

			        // Compute diff. Get the Patch object. Patch is the container for computed deltas.
			        Patch<String> patch = DiffUtils.diff(original, revised);
			        if(patch.getDeltas().size() > 0){
						printMessage(issue.getId() + ": "+modulePath + " �J���łƃ����[�X�ō�������B�m�F���������B");

				        for (Delta<String> delta: patch.getDeltas()) {
				            System.out.println(delta);
				        }
			        }
				} else {//db,script,message
					//�`�F�b�N�s�v
//					System.out.println("common resouce: "+module.getModuleID());
				}
				
				//���ވꗗ�̃��W���[�����m���Ƀt�@�C�������ɑ��݂��邱�Ƃ��`�F�b�N
				if(module.getModelType() == 1){ //source;java,xml
					String updateKbn = module.getUpdatKbn();
					if("�V�K".equals(updateKbn)){
						boolean contains = findModulePathFromDiffResult(arrRighFiletOnly,module.getModulePath());
						
						if(!contains){
							printMessage(issue.getId() + ": "+modulePath + " ���ވꗗ�ɐV�K�t�@�C�������A�O��RLS�łƍ���RLS�ł̃t�H���_��r�ɂĐV�K�ł͂Ȃ�");
						}
					} else if("�X�V".equals(updateKbn)){
						boolean contains = findModulePathFromDiffResult(arrFileDiff,module.getModulePath());

						if(!contains){
							printMessage(issue.getId() + ": "+modulePath + " ���ވꗗ�ɍX�V�t�@�C�������A�O��RLS�łƍ���RLS�ł̃t�H���_��r�ɂčX�V�ł͂Ȃ�");
						}
					} else if("�폜".equals(updateKbn)){
						boolean contains = findModulePathFromDiffResult(arrLeftFileOnly,module.getModulePath());
						
						if(!contains){
							printMessage(issue.getId() + ": "+modulePath + " ���ވꗗ�ɍ폜�t�@�C�������A�O��RLS�łƍ���RLS�ł̃t�H���_��r�ɂč폜�ł͂Ȃ�");
						}
					}
				} else {//script,message,application resource
					if(module.getModuleID().contains("���b�Z�[�W�ꗗ�i�������v�j")){//message file
						boolean contains = checkCommonDiffFileExists(arrFileDiff,"application-messages.properties");
						if(!contains){
							printMessage(issue.getId() + ": "+module.getModuleID() + " ���ވꗗ�Ƀ��b�Z�[�W�ꗗ�i�������v�j���L�ڂ��ꂽ���A�O��RLS�łƍ���RLS�ł�application-messages.properties�̍������Ȃ�");
						}
					}else if(module.getModuleID().contains("�v���p�e�B�ꗗ�i�������v�j")){//message file
						boolean contains = checkCommonDiffFileExists(arrFileDiff,"ApplicationResources.properties");
						if(!contains){
							printMessage(issue.getId() + ": "+module.getModuleID() + " ���ވꗗ�Ƀv���p�e�B�ꗗ�i�������v�j���L�ڂ��ꂽ���A�O��RLS�łƍ���RLS�ł�ApplicationResources.properties�̍������Ȃ�");
						}
					} else { //shell
						ArrayList<String> arrComplex = new ArrayList<String>(arrRighFiletOnly);
						arrComplex.addAll(arrFileDiff);
						arrComplex.addAll(arrLeftFileOnly);

						if(module.getModuleID().contains("�W���u�C���^�[�t�F�[�X")){//�W���u�C���^�[�t�F�[�X
							boolean contains = checkCommonDiffFileExists(arrComplex,".sh");
							
							if(!contains){
								printMessage(issue.getId() + ": "+module.getModuleID() + " ���ވꗗ�ɃW���u�C���^�[�t�F�[�X���L�ڂ��ꂽ���A�O��RLS�łƍ���RLS�ł�shell�̍������Ȃ�");
							}
						} else{//db update
							//now does not check
						}
					}
				}
			}
		}
		
		//�t�H���_��r�ŐV�K�t�@�C�����m���Ɏ��ވꗗ�ɋL�ڂ���Ă��邩
		for(String rightFile:arrRighFiletOnly){
			boolean contains = containsJudge(modules,rightFile,false);
			
			if(!contains){
				printMessage("right only file :"+ rightFile+" �t�H���_�R���y�A�ŐV�K�t�@�C�������A���ވꗗ�ɋL�ڂ���Ă��Ȃ�");
			}
		}
		//�t�H���_��r�ō폜�t�@�C�����m���Ɏ��ވꗗ�ɋL�ڂ���Ă��邩
		for(String leftFile:arrLeftFileOnly){
			boolean contains = containsJudge(modules,leftFile,false);
			if(!contains){
				printMessage("left only file :"+ leftFile+" �t�H���_�R���y�A�ō폜�t�@�C�������A���ވꗗ�ɋL�ڂ���Ă��Ȃ�");
			}
		}
		
		//�t�H���_��r�ōX�V�t�@�C�����m���Ɏ��ވꗗ�ɋL�ڂ���Ă��邩
		for(String diffFile:arrFileDiff){
			boolean contains = containsJudge(modules,diffFile,false);
			if(!contains){
				printMessage("file diff :"+ diffFile+" �t�H���_�R���y�A�ōX�V�t�@�C�������A���ވꗗ�ɋL�ڂ���Ă��Ȃ�");
			}
		}
	}
	
	/**
	 * 
	 * @param arrDiffFileName
	 * @param containsKey
	 * @return
	 */
	private static boolean checkCommonDiffFileExists(ArrayList<String> arrDiffFileName,String containsKey){
		for(String diffFileName:arrDiffFileName){
			if(diffFileName.contains(containsKey)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param arrayDiffs
	 * @param modulePath
	 * @return
	 */
	private static boolean findModulePathFromDiffResult(ArrayList<String> arrayDiffs,String modulePath){
		for(String diffFile:arrayDiffs){
			if(diffFile.toUpperCase().contains(modulePath.toUpperCase())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param modules
	 * @param diffFile,sql file is not included
	 * @return
	 */
	private static boolean containsJudge(ArrayList<IssueModule> modules,String diffFile,boolean ikouResource){
		if(ikouResource){
			return findJavaXmlResourceFromFileName(modules,diffFile);
		}
		//suritoukei resource
		boolean contains = false;
		if(diffFile.endsWith("application-messages.properties")){ //message file
			contains = findKeyFromModuleID(modules,"���b�Z�[�W�ꗗ�i�������v�j");
		} else if(diffFile.endsWith(".sh")){//shell file
			contains = findKeyFromModuleID(modules,"�W���u�C���^�[�t�F�[�X");
		} else if(diffFile.endsWith("ApplicationResources.properties")){//application file
			contains = findKeyFromModuleID(modules,"�v���p�e�B�ꗗ�i�������v�j");
		} else { //java or xml
			contains = findJavaXmlResourceFromFileName(modules,diffFile);
		}
		return contains;
	}
	
	/**
	 * find the key content from module id's
	 * @param modules
	 * @param key
	 * @return
	 */
	private static boolean findKeyFromModuleID(ArrayList<IssueModule> modules,String key){
		for(IssueModule module:modules){
			if(module.getModuleID().contains(key)){
				return true;
			}
		}
		return false;
	}
	/**
	 * mapping the file name with the java,xml module path
	 * @param fileName
	 * @param modules
	 * @return
	 */
	private static boolean findJavaXmlResourceFromFileName(ArrayList<IssueModule> modules,String fileName){
		for(IssueModule module:modules){
			if(fileName.toUpperCase().contains(module.getModulePath().toUpperCase())){
				return true;
			}
		}
		return false;
	}

	private static List<String> fileToLines(String filename) {
		List<String> lines = new LinkedList<String>();
		String line = "";
		BufferedReader in = null;
		try {
			if(filename.endsWith(".xml")){//xml
				in = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
			} else {//java
				in = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"SJIS"));
			}
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
					// ignore ... any errors should already have been
					// reported via an IOException from the final flush.
				}
			}
		}
		return lines;
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private static Issue[] getIssueInfo(String issueListFilterPattern,boolean readModuleInfo,boolean ikouResource) throws IOException{
		/**
		 * issue list
		 */
		Map<String,String> columnNameMapIssueList = new HashMap<String, String>();
		columnNameMapIssueList.put("ISSUE_ID", "B");
		columnNameMapIssueList.put("RELEASE_VERSION", "AJ");
	
		String configListFile = appConfig.getPropertyValue("check", "issue_list_file");  
		SVNUtil.updateSvnByTortoiseSvn(configListFile, svnInstallPath);
		
		//read base info
		Map<String,String>[] mapTarget = ExcelUtil.readContentFromExcelMult(configListFile,0,
				columnNameMapIssueList,issueListFilterPattern,0);//sheet 0
		
		Issue[] issues = new Issue[mapTarget.length];
		for(int i = 0 ; i<mapTarget.length;i++){
			issues[i] = new Issue();
			issues[i].setId(mapTarget[i].get("ISSUE_ID"));
			issues[i].setRlsVersion(mapTarget[i].get("RELEASE_VERSION"));
		}
		
		if(readModuleInfo){
			if(ikouResource == false){
				//read module info
				setIssueModule(issues);
			}else{
				setIkouIssueModel(issues);
			}
		}
		
		return issues;
	}
	
	private static void setIkouIssueModel(Issue[] issues) throws IOException{
		String moduleListFile = appConfig.getPropertyValue("ikou_check", "ikou_module_list_file");
		SVNUtil.updateSvnByTortoiseSvn(moduleListFile, svnInstallPath);
		
		Map<String,String>[] mapTarget = ExcelUtil.readContentFromExcelMult(moduleListFile,1,
				columnNameMapIkouModuleList,"RELEASE_STATUS!�����[�X�s�v",0);//sheet 1
		
		HashMap<String, Issue> issueMap = new HashMap<String, Issue>();
		for(Issue issue:issues){
			issueMap.put(issue.getId(), issue);
		}
		
		for(int i = 0 ; i<mapTarget.length;i++){
			String issueID = mapTarget[i].get("ISSUE_ID");
			if(issueMap.containsKey(issueID)){
				Issue issue = issueMap.get(issueID);
				IssueModule module = new IssueModule();
				module.setIssueID(issueID);
				
				module.setModulePath(mapTarget[i].get("MODULE_PATH"));
				
				module.setProjectID(mapTarget[i].get("PROJECT_ID"));
				
				module.setRlsStatus(mapTarget[i].get("RELEASE_STATUS"));//rls status
				module.setRlsVersion(mapTarget[i].get("RELEASE_VERSION"));//rls version
				module.setUpdatKbn(mapTarget[i].get("UPDATE_KBN"));//update kbn
				//java source,xml
				module.setModelType(1);
				issue.addIssueModule(module);
			}
		}
		//common source does not exists
//		mapTarget = ExcelUtil.readContentFromExcelMult(moduleListFile,2,
//				columnNameMapCommonModuleList,"RELEASE_STATUS!�����[�X�s�v",0);//sheet 2
//		for(int i = 0 ; i<mapTarget.length;i++){
//			String issueID = mapTarget[i].get("ISSUE_ID");
//			
//			if(issueMap.containsKey(issueID)){
//				Issue issue = issueMap.get(issueID);
//				IssueModule module = new IssueModule();
//				module.setIssueID(issueID);
//				
//				module.setModulePath(mapTarget[i].get("MODULE_PATH"));
//				module.setModuleID(mapTarget[i].get("MODULE_ID"));
//				//db,message,shell
//				module.setModelType(2);
//				module.setDdlSql(mapTarget[i].get("DDL_SQL"));
//				module.setPatchYouhi(mapTarget[i].get("PATCH_YOUHI"));
//				module.setRlsStatus(mapTarget[i].get("RELEASE_STATUS"));//rls status
//				module.setRlsVersion(mapTarget[i].get("RELEASE_VERSION"));//rls version
//				//add
//				issue.addIssueModule(module);
//			}
//		}
	}
	
	private static void setIssueModule(Issue[] issues) throws IOException{
		String moduleListFile = appConfig.getPropertyValue("check", "module_list_file");
		SVNUtil.updateSvnByTortoiseSvn(moduleListFile, svnInstallPath);
		
		Map<String,String>[] mapTarget = ExcelUtil.readContentFromExcelMult(moduleListFile,1,
				columnNameMapModuleList,"RELEASE_STATUS!�����[�X�s�v",0);//sheet 1
		
		HashMap<String, Issue> issueMap = new HashMap<String, Issue>();
		for(Issue issue:issues){
			issueMap.put(issue.getId(), issue);
		}
		
		for(int i = 0 ; i<mapTarget.length;i++){
			String issueID = mapTarget[i].get("ISSUE_ID");
			if(issueMap.containsKey(issueID)){
				Issue issue = issueMap.get(issueID);
				IssueModule module = new IssueModule();
				module.setIssueID(issueID);
				String modulePath = mapTarget[i].get("MODULE_PATH");
				module.setModulePath(mapTarget[i].get("MODULE_PATH"));
				
				if(StringUtils.isBlank(modulePath)){//mapping key
					module.setModulePath(mapTarget[i].get("MODULE_ID"));
				}
				module.setModuleID(mapTarget[i].get("MODULE_ID"));
				
				module.setProjectID(mapTarget[i].get("PROJECT_ID"));
				module.setFunctionName(mapTarget[i].get("FUNCTION_NAME"));
				
				module.setRlsStatus(mapTarget[i].get("RELEASE_STATUS"));//rls status
				module.setRlsVersion(mapTarget[i].get("RELEASE_VERSION"));//rls version
				module.setUpdatKbn(mapTarget[i].get("UPDATE_KBN"));//update kbn
				//java source,xml
				module.setModelType(1);
				issue.addIssueModule(module);
			}
		}

		mapTarget = ExcelUtil.readContentFromExcelMult(moduleListFile,2,
				columnNameMapCommonModuleList,"RELEASE_STATUS!�����[�X�s�v",0);//sheet 2
		for(int i = 0 ; i<mapTarget.length;i++){
			String issueID = mapTarget[i].get("ISSUE_ID");
			
			if(issueMap.containsKey(issueID)){
				Issue issue = issueMap.get(issueID);
				IssueModule module = new IssueModule();
				module.setIssueID(issueID);
				
				module.setModulePath(mapTarget[i].get("MODULE_PATH"));
				module.setModuleID(mapTarget[i].get("MODULE_ID"));
				//db,message,shell
				module.setModelType(2);
				module.setDdlSql(mapTarget[i].get("DDL_SQL"));
				module.setPatchYouhi(mapTarget[i].get("PATCH_YOUHI"));
				module.setRlsStatus(mapTarget[i].get("RELEASE_STATUS"));//rls status
				module.setRlsVersion(mapTarget[i].get("RELEASE_VERSION"));//rls version
				//add
				issue.addIssueModule(module);
			}
		}
	}
	
	private static void printMessage(String message){
		messageOut.add(message);
		if(CONSOLE){
			System.err.println(message);
		}
	}
}
