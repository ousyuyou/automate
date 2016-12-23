package util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;

import svn.SVNUtil;

public class FileUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub


	}
	public static void listFiles(String strPath,ArrayList<String> arrayOut){
		 listFiles(new File(strPath), arrayOut);
	}
	/**
	 * 
	 * list all file
	 * @param f
	 * @param arrayOut
	 */
	public static void listFiles(File f,ArrayList<String> arrayOut){
		 if(f != null){
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
	 * list all file
	 * @param strPath
	 * @param arrayOut
	 */
	public static void listAbsoluteFiles(String strPath,ArrayList<File> arrayOut){
		listAbsoluteFiles(new File(strPath), arrayOut);
	}
	
	/**
	 * 
	 * list all file
	 * @param f
	 * @param arrayOut
	 */
	public static void listAbsoluteFiles(File path,ArrayList<File> arrayOut){
		listAbsoluteFiles(path,arrayOut,"");
	}
	/**
	 * list all files contain the specified key
	 * @param f
	 * @param arrayOut
	 * @param containKey
	 */
	public static void listAbsoluteFiles(File path,ArrayList<File> arrayOut,String specKey){
		 if(path != null){
	            if(path.isDirectory()){
	                File[] fileArray = path.listFiles();
	                if(fileArray!=null){
	                    for (int i = 0; i < fileArray.length; i++) {
	                    	listAbsoluteFiles(fileArray[i],arrayOut,specKey);
	                    }
	                }
	            } else {
	            	if(StringUtils.isBlank(specKey)){ //no filter key
	            		arrayOut.add(path);
	            	} else {
		            	if(path.getName().contains(specKey)){
		            		arrayOut.add(path);
		            	}
	            	}
	            }
	     }
	}
	
	/**
	 * 
	 * @param f
	 * @param arrayOut
	 * @param specKey
	 * @param recurse
	 */
	public static void listAbsoluteFilesNotRecurse(File path,ArrayList<File> arrayOut,String specKey){
		 if(path != null){
	            if(path.isDirectory()){
	                File[] fileArray = path.listFiles();
	                
	                if(fileArray != null){
	                    for (int i = 0; i < fileArray.length; i++) {
	                    	if(fileArray[i].isFile() && fileArray[i].getName().contains(specKey)){
	                    		arrayOut.add(fileArray[i]);
	                    	}
	                    }
	                }
	            } else {
	            	;
	            }
	     }
	}
	
	/**
	 * list all sub directory
	 * @param f
	 * @param arrayOut
	 */
	public static void listSubDirectorys(File path,ArrayList<File> arrayOut){
		 if(path != null){
	            if(path.isDirectory()){
            		arrayOut.add(path);
            		
	                File[] fileArray = path.listFiles();
	                if(fileArray != null){
	                    for (int i = 0; i < fileArray.length; i++) {
	                    	if(fileArray[i].isDirectory()){
		                    	listSubDirectorys(fileArray[i],arrayOut);
	                    	}
	                    }
	                }
	            } else {
	            	;
	            }
	     }
	}
	
	/**
	 * list all mps sources
	 * @param sourcePaths
	 * @param svnInstallPath
	 * @return key:file name or relative path name(file double),value:absolute file name
	 */
	public static HashMap<String, String> listSources(String[] sourcePaths,String svnInstallPath){
		ArrayList<File> array = new ArrayList<File>();
		
		for(String path:sourcePaths){
			SVNUtil.updateSvnByTortoiseSvn(path, svnInstallPath);
			listAbsoluteFiles(path, array);
		}

		HashMap<String, String> map = new HashMap<String, String>();
		HashSet<String> repeatKeys = new HashSet<String>();
		//ファイル名のみの場合、ファイル名の重複するケースある
		for(File f:array){
			if(map.containsKey(f.getName())){
				if(!repeatKeys.contains(f.getName())){
					repeatKeys.add(f.getName());//put重複ファイ名to map
				}
			} else {
				map.put(f.getName(), f.getAbsolutePath());
			}
		}
		//再度全てループし、重複のファイル名をディレクトリ名に置き換える
		for(File f:array){
			if(repeatKeys.contains(f.getName())){
				map.remove(f.getName());
				String absolutePath = f.getAbsolutePath();
				if(absolutePath.indexOf("\\FMS-CORE") >= 0){
					//例：311.xmlを/FMS-CORE/conf/settings/formula/expense/311.xmlに置き換える
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
	
}
