package util;

import java.io.File;
import java.util.ArrayList;

public class FileUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

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

}
