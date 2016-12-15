package util;

import java.util.ArrayList;

public class MergeHeroDirCompareResult {

	private ArrayList<String> dirLeftOnly = new ArrayList<String>();
	private ArrayList<String> dirRightOnly = new ArrayList<String>();
	private ArrayList<String> fileLeftOnly = new ArrayList<String>();
	private ArrayList<String> fileRightOnly = new ArrayList<String>();
	private ArrayList<String> fileDiff = new ArrayList<String>();
	private String m_strComResultDetail;
	private Object[][] data;
	private String strError;
	
	int m_uiTotalItems = 0;
	int m_uiTotalDiffs = 0;
	int m_uiTotalSames = 0;
	int m_uiLeftOnlyFiles = 0;
	int m_uiRightOnlyFiles = 0;
	int m_uiLeftOnlyDirs = 0;
	int m_uiRightOnlyDirs = 0;
	int m_uiSameNameDirs = 0;
	int m_uiCompareError = 0;
	
	public ArrayList<String> getDirLeftOnly() {
		return dirLeftOnly;
	}
	public void setDirLeftOnly(ArrayList<String> dirLeftOnly) {
		this.dirLeftOnly = dirLeftOnly;
	}
	public ArrayList<String> getDirRightOnly() {
		return dirRightOnly;
	}
	public void setDirRightOnly(ArrayList<String> dirRightOnly) {
		this.dirRightOnly = dirRightOnly;
	}
	public ArrayList<String> getFileLeftOnly() {
		return fileLeftOnly;
	}
	public void setFileLeftOnly(ArrayList<String> fileLeftOnly) {
		this.fileLeftOnly = fileLeftOnly;
	}
	public ArrayList<String> getFileRightOnly() {
		return fileRightOnly;
	}
	public void setFileRightOnly(ArrayList<String> fileRightOnly) {
		this.fileRightOnly = fileRightOnly;
	}
	public ArrayList<String> getFileDiff() {
		return fileDiff;
	}
	public void setFileDiff(ArrayList<String> fileDiff) {
		this.fileDiff = fileDiff;
	}
	public String getM_strComResultDetail() {
		return m_strComResultDetail;
	}
	public void setM_strComResultDetail(String comResultDetail) {
		m_strComResultDetail = comResultDetail;
	}
	public Object[][] getData() {
		return data;
	}
	public void setData(Object[][] data) {
		this.data = data;
	}
	public int getM_uiTotalItems() {
		return m_uiTotalItems;
	}
	public void setM_uiTotalItems(int totalItems) {
		m_uiTotalItems = totalItems;
	}
	public int getM_uiTotalDiffs() {
		return m_uiTotalDiffs;
	}
	public void setM_uiTotalDiffs(int totalDiffs) {
		m_uiTotalDiffs = totalDiffs;
	}
	public int getM_uiTotalSames() {
		return m_uiTotalSames;
	}
	public void setM_uiTotalSames(int totalSames) {
		m_uiTotalSames = totalSames;
	}
	public int getM_uiLeftOnlyFiles() {
		return m_uiLeftOnlyFiles;
	}
	public void setM_uiLeftOnlyFiles(int leftOnlyFiles) {
		m_uiLeftOnlyFiles = leftOnlyFiles;
	}
	public int getM_uiRightOnlyFiles() {
		return m_uiRightOnlyFiles;
	}
	public void setM_uiRightOnlyFiles(int rightOnlyFiles) {
		m_uiRightOnlyFiles = rightOnlyFiles;
	}
	public int getM_uiLeftOnlyDirs() {
		return m_uiLeftOnlyDirs;
	}
	public void setM_uiLeftOnlyDirs(int leftOnlyDirs) {
		m_uiLeftOnlyDirs = leftOnlyDirs;
	}
	public int getM_uiRightOnlyDirs() {
		return m_uiRightOnlyDirs;
	}
	public void setM_uiRightOnlyDirs(int rightOnlyDirs) {
		m_uiRightOnlyDirs = rightOnlyDirs;
	}
	public int getM_uiSameNameDirs() {
		return m_uiSameNameDirs;
	}
	public void setM_uiSameNameDirs(int sameNameDirs) {
		m_uiSameNameDirs = sameNameDirs;
	}
	public int getM_uiCompareError() {
		return m_uiCompareError;
	}
	public void setM_uiCompareError(int compareError) {
		m_uiCompareError = compareError;
	}
	public String getStrError() {
		return strError;
	}
	public void setStrError(String strError) {
		this.strError = strError;
	}
	
}
