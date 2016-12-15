package util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
	
	public static void listAbsoluteFiles(String strPath,ArrayList<File> arrayOut){
		listAbsoluteFiles(new File(strPath), arrayOut);
	}
	/**
	 * 
	 * list all file
	 * @param f
	 * @param arrayOut
	 */
	public static void listAbsoluteFiles(File f,ArrayList<File> arrayOut){
		 if(f != null){
	            if(f.isDirectory()){
	                File[] fileArray=f.listFiles();
	                if(fileArray!=null){
	                    for (int i = 0; i < fileArray.length; i++) {
	                    	listAbsoluteFiles(fileArray[i],arrayOut);
	                    }
	                }
	            } else {
	            	arrayOut.add(f);
	            }
	     }
	}
	
	public static void listAbsoluteFiles(File f,ArrayList<File> arrayOut,String containKey){
		 if(f != null){
	            if(f.isDirectory()){
	                File[] fileArray=f.listFiles();
	                if(fileArray!=null){
	                    for (int i = 0; i < fileArray.length; i++) {
	                    	listAbsoluteFiles(fileArray[i],arrayOut,containKey);
	                    }
	                }
	            } else {
	            	if(f.getName().contains(containKey)){
	            		arrayOut.add(f);
	            	}
	            }
	     }
	}
	
	public static HashMap<String, String> listSources(String[] sourcePaths,String svnInstallPath){
		ArrayList<File> array = new ArrayList<File>();
		
		for(String path:sourcePaths){
			SVNUtil.updateSvnByTortoiseSvn(path, svnInstallPath);
			listAbsoluteFiles(path, array);
		}

		HashMap<String, String> map = new HashMap<String, String>();
		HashSet<String> repeatKeys = new HashSet<String>();
		//�t�@�C�����݂̂̏ꍇ�A�t�@�C�����̏d������P�[�X����
		for(File f:array){
			if(map.containsKey(f.getName())){
				if(!repeatKeys.contains(f.getName())){
					repeatKeys.add(f.getName());//put�d���t�@�C��to map
				}
			} else {
				map.put(f.getName(), f.getAbsolutePath());
			}
		}
		//�ēx�S�ă��[�v���A�d���̃t�@�C�������f�B���N�g�����ɒu��������
		for(File f:array){
			if(repeatKeys.contains(f.getName())){
				map.remove(f.getName());
				String absolutePath = f.getAbsolutePath();
				if(absolutePath.indexOf("\\FMS-CORE") >= 0){
					//��F311.xml��/FMS-CORE/conf/settings/formula/expense/311.xml�ɒu��������
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
