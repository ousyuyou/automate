package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class DiffTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String str1 = "ABCABBA";
//		String str2 = "CBABAC";
		List<String> s1 = fileToLines("E:/diff/build.xml");
		List<String> s2 = fileToLines("E:/diff/build_rls.xml");
		
		Patch<String> patch = DiffUtils.diff(s1, s2);
        if(patch.getDeltas().size() > 0){
	        for (Delta<String> delta: patch.getDeltas()) {
	            System.out.println(delta);
	        }
        }

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
}
