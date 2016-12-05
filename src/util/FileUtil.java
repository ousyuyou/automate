package util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

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
	                    	listAbsoluteFiles(fileArray[i],arrayOut);
	                    }
	                }
	            } else {
	            	if(f.getName().contains(containKey)){
	            		arrayOut.add(f);
	            	}
	            }
	     }
	}

}
