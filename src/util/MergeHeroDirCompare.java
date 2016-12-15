package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import MergeHeroLib.CDynamicDirectoriesCompare;
import MergeHeroLib.CDynamicDirectoriesCompare.CDiffItemArray;
import MergeHeroLib.CDynamicDirectoriesCompare.SDiffItem;
import difflib.BaseDataObject;

public class MergeHeroDirCompare {
	private String m_strComResultDetail = "";
	private int m_uiTotalItems = 0;
	private int m_uiTotalDiffs = 0;
	private int m_uiTotalSames = 0;
	private int m_uiSameNameDirs = 0;
	private int m_uiLeftOnlyFiles = 0;
	private int m_uiRightOnlyFiles = 0;
	private int m_uiLeftOnlyDirs = 0;
	private int m_uiRightOnlyDirs = 0;
	private int m_uiCompareError = 0;
	public static final int ITEM_EQUAL	= 0;
	public static final int ITEM_NOT_EQUAL	= 1;
	public static final int ITEM_FOLDER = 2;
	public static final int ITEM_LEFT_FILE	= 3;
	public static final int ITEM_RIGHT_FILE = 4;
	public static final int ITEM_LEFT_FOLDER = 5;
	public static final int ITEM_RIGHT_FOLDER = 6;
	public static final int ITEM_ERROR	= 7;
	public static final int ITEM_UNKNOW = 8;
	
	protected SimpleDateFormat dateFormat = new SimpleDateFormat();
	Object[][] data = null;
	public boolean m_bActived = false;
	
	String[] columnNames = {"Name", "Directory", "Comparison Result", "Extension", "Left Size", "Right Size", 
			  "Left Date", "Right Date"};
	
	private int nTableRow = 0;
	private static int COUNTITEMS	= 8;
	
	String m_strCompareDir;
	String m_strToDir;
	boolean m_bRecursive;
	CDynamicDirectoriesCompare m_dirDiff = new CDynamicDirectoriesCompare();	
	CDiffItemArray m_aryDiffItemArray = m_dirDiff.new CDiffItemArray(); 
	MergeHeroDirCompareResult result = new MergeHeroDirCompareResult();
	BaseDataObject strError = new BaseDataObject();

	private ArrayList<String> dirLeftOnly = new ArrayList<String>();
	private ArrayList<String> dirRightOnly = new ArrayList<String>();
	private ArrayList<String> fileLeftOnly = new ArrayList<String>();
	private ArrayList<String> fileRightOnly = new ArrayList<String>();
	private ArrayList<String> fileDiff = new ArrayList<String>();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
	}
	
	public MergeHeroDirCompare(String m_strCompareDir,String m_strToDir,boolean m_bRecursive){
		this.m_strCompareDir = m_strCompareDir;
		this.m_strToDir = m_strToDir;
		this.m_bRecursive = m_bRecursive;
	}
	
	public MergeHeroDirCompareResult executeCompare(){
		// Compare directories
		m_aryDiffItemArray.clear();
		if(!m_dirDiff.CompareDirectories(this.m_strCompareDir, this.m_strToDir, this.m_bRecursive, this.m_aryDiffItemArray, strError))
		{
			System.err.println(strError.getStringValue());
		}
		ResetTableData();
		setResult();
		
		return result;
	}
	
	private void setResult(){
		result.setData(data);
		result.setM_strComResultDetail(m_strComResultDetail);
		result.setM_uiCompareError(m_uiCompareError);
		result.setM_uiLeftOnlyDirs(m_uiLeftOnlyDirs);
		result.setM_uiLeftOnlyFiles(m_uiLeftOnlyFiles);
		result.setM_uiRightOnlyDirs(m_uiRightOnlyDirs);
		result.setM_uiRightOnlyFiles(m_uiRightOnlyFiles);
		result.setM_uiSameNameDirs(m_uiSameNameDirs);
		result.setM_uiTotalDiffs(m_uiTotalDiffs);
		result.setM_uiTotalItems(m_uiTotalItems);
		result.setM_uiTotalSames(m_uiTotalSames);
		
		result.setDirLeftOnly(dirLeftOnly);
		result.setFileLeftOnly(fileLeftOnly);
		result.setDirRightOnly(dirRightOnly);
		result.setFileRightOnly(fileRightOnly);
		result.setFileDiff(fileDiff);
		result.setStrError(strError.getStringValue());
	}
	
	private void ResetTableData()
	{
		m_uiTotalItems = 0;
		m_uiTotalDiffs = 0;
		m_uiTotalSames = 0;
		m_uiLeftOnlyFiles = 0;
		m_uiRightOnlyFiles = 0;
		m_uiLeftOnlyDirs = 0;
		m_uiRightOnlyDirs = 0;
		m_uiSameNameDirs = 0;
		m_uiCompareError = 0;
		
	    createTable();

//	    System.out.println("m_strCompareDir: "+m_strCompareDir);
//	    System.out.println("m_strToDir: "+m_strToDir);
//	    
//	    System.out.println("Total Items: " + String.valueOf(m_uiTotalItems));
//	    System.out.println("Different files: " + String.valueOf(m_uiTotalDiffs));
//	    System.out.println("Identical files: " + String.valueOf(m_uiTotalSames));
//	    System.out.println("m_strComResultDetail "+m_strComResultDetail);
	}
	
	protected void createTable()
	{
	    nTableRow = m_aryDiffItemArray.size();
	    data = new Object[nTableRow][COUNTITEMS];
	    
	    for (int i = 0; i < nTableRow; i++)
	    {
	      CDynamicDirectoriesCompare.SDiffItem diffItem =  (SDiffItem) m_aryDiffItemArray.get(i);

			SDirDiffInfo dirDiffInfo = new SDirDiffInfo();
			GetDirDiffInfo(diffItem, dirDiffInfo);

			int j = 0;
			SImageColumn imgColumn = new SImageColumn();
			imgColumn.m_strName = dirDiffInfo.m_strName;
			imgColumn.m_iImageIndex = dirDiffInfo.m_iImageIndex;
			
			data[i][j++] = imgColumn;
			data[i][j++] = dirDiffInfo.m_strDirectory;
			data[i][j++] = dirDiffInfo.m_strDiffReslut;
			data[i][j++] = dirDiffInfo.m_strType;
			data[i][j++] = dirDiffInfo.m_strLeftSize;
			data[i][j++] = dirDiffInfo.m_strRightSize;
			data[i][j++] = dirDiffInfo.m_strLeftDate;
			data[i][j++] = dirDiffInfo.m_strRightDate;

			m_uiTotalItems++;
	    }
	    m_strComResultDetail += "Comparison result\nleft folder = " + m_strCompareDir;
	    m_strComResultDetail += "\nright folder = " + m_strToDir;
		m_strComResultDetail += "\n\nIdentical files = " + String.valueOf(m_uiTotalSames);		
		m_strComResultDetail += "\nDifferent files = " + String.valueOf(m_uiTotalDiffs);
		m_strComResultDetail += "\nFolders with the same name = ";
		m_strComResultDetail += String.valueOf(m_uiSameNameDirs) + "\nComparison errors = ";
		m_strComResultDetail += String.valueOf(m_uiCompareError)+ "\n\nComparison directories only in left folder = ";
		m_strComResultDetail += String.valueOf(m_uiLeftOnlyDirs) + "\nComparison directories only in right folder = ";
		m_strComResultDetail += String.valueOf(m_uiRightOnlyDirs) + "\n\nComparison files only in left folder = ";
		m_strComResultDetail += String.valueOf(m_uiLeftOnlyFiles) + "\nComparison files only in right folder = ";
		m_strComResultDetail += String.valueOf(m_uiRightOnlyFiles) + "\n\nTotal comparison files and directories = " ;
		m_strComResultDetail += String.valueOf(m_uiTotalItems);
  	}
	
	private void GetDirDiffInfo(CDynamicDirectoriesCompare.SDiffItem diffItem, SDirDiffInfo dirDiffInfo)
	{
		dirDiffInfo.m_iImageIndex = ITEM_UNKNOW;
		dirDiffInfo.m_strDirectory = diffItem.m_strSubDirName;
		dirDiffInfo.m_strDiffReslut = "";
	
		if (diffItem.IsSideLeft())
		{
			dirDiffInfo.m_strName = diffItem.m_leftDiffInfo.m_strName;

			dirDiffInfo.m_strLeftDate = dateFormat.format(new Date(diffItem.m_leftDiffInfo.m_mtime));
			if (!diffItem.IsDirectory())
			{
				dirDiffInfo.m_strLeftSize = String.valueOf(diffItem.m_leftDiffInfo.m_size);
			}
		}
		else if (diffItem.IsSideRight())
		{
			dirDiffInfo.m_strName = diffItem.m_rightDiffInfo.m_strName;

			dirDiffInfo.m_strRightDate = dateFormat.format(new Date(diffItem.m_rightDiffInfo.m_mtime));
			if (!diffItem.IsDirectory())
			{
				dirDiffInfo.m_strRightSize = String.valueOf(diffItem.m_rightDiffInfo.m_size);
			}
		}
		else
		{
			dirDiffInfo.m_strName = diffItem.m_rightDiffInfo.m_strName;

			dirDiffInfo.m_strLeftDate = dateFormat.format(new Date(diffItem.m_leftDiffInfo.m_mtime));
			dirDiffInfo.m_strRightDate = dateFormat.format(new Date(diffItem.m_rightDiffInfo.m_mtime));

			if (!diffItem.IsDirectory())
			{
				dirDiffInfo.m_strLeftSize = String.valueOf(diffItem.m_leftDiffInfo.m_size);
				dirDiffInfo.m_strRightSize = String.valueOf(diffItem.m_rightDiffInfo.m_size);
			}
		}
					
		if (diffItem.IsDirectory())
		{
			if (diffItem.IsSideLeft())
			{
				dirDiffInfo.m_iImageIndex = ITEM_LEFT_FOLDER;
				dirDiffInfo.m_strDiffReslut = "Left Only";
				
				if(StringUtils.isBlank(dirDiffInfo.m_strDirectory )){
					dirLeftOnly.add(diffItem.m_leftDiffInfo.m_strName);
				} else {
					dirLeftOnly.add(dirDiffInfo.m_strDirectory +"\\"+diffItem.m_leftDiffInfo.m_strName);
				}
				
				m_uiLeftOnlyDirs ++;
			}
			else if (diffItem.IsSideRight())
			{
				dirDiffInfo.m_iImageIndex = ITEM_RIGHT_FOLDER;
				dirDiffInfo.m_strDiffReslut = "Right Only";
				
				if(StringUtils.isBlank(dirDiffInfo.m_strDirectory )){
					dirRightOnly.add(diffItem.m_rightDiffInfo.m_strName);
				} else {
					dirRightOnly.add(dirDiffInfo.m_strDirectory +"\\"+diffItem.m_rightDiffInfo.m_strName);
				}
				
				
				m_uiRightOnlyDirs ++;
			}
			else
			{
				dirDiffInfo.m_iImageIndex = ITEM_FOLDER;

				m_uiSameNameDirs ++;
			}
		}
		else
		{
			dirDiffInfo.m_strType = diffItem.GetExtension();

			if (diffItem.IsSideLeft())
			{
				dirDiffInfo.m_iImageIndex = ITEM_LEFT_FILE;
				dirDiffInfo.m_strDiffReslut = "Left Only";
				
				if(StringUtils.isBlank(dirDiffInfo.m_strDirectory )){
					fileLeftOnly.add(diffItem.m_leftDiffInfo.m_strName);
				}else{
					fileLeftOnly.add(dirDiffInfo.m_strDirectory +"\\"+diffItem.m_leftDiffInfo.m_strName);
				}
				
				
				m_uiLeftOnlyFiles ++;
			}
			else if (diffItem.IsSideRight())
			{
				dirDiffInfo.m_iImageIndex = ITEM_RIGHT_FILE;
				dirDiffInfo.m_strDiffReslut = "Right Only";
				
				if(StringUtils.isBlank(dirDiffInfo.m_strDirectory )){
					fileRightOnly.add(diffItem.m_rightDiffInfo.m_strName);
				}else{
					fileRightOnly.add(dirDiffInfo.m_strDirectory +"\\"+diffItem.m_rightDiffInfo.m_strName);
				}
				
				m_uiRightOnlyFiles ++;
			}
			else
			{
				if (diffItem.IsResultSame())
				{
					dirDiffInfo.m_iImageIndex = ITEM_EQUAL;
					dirDiffInfo.m_strDiffReslut = "Identical";

					m_uiTotalSames ++;
				}
				
				if (diffItem.IsResultDiff())
				{
					dirDiffInfo.m_iImageIndex = ITEM_NOT_EQUAL;
					dirDiffInfo.m_strDiffReslut = "Different";
					
					if(StringUtils.isBlank(dirDiffInfo.m_strDirectory )){
						fileDiff.add(diffItem.m_rightDiffInfo.m_strName);
					} else {
						fileDiff.add(dirDiffInfo.m_strDirectory +"\\"+diffItem.m_rightDiffInfo.m_strName);
					}
					
//					System.out.println("diffrenet: "+dirDiffInfo.m_strDirectory +"\\"+diffItem.m_leftDiffInfo.m_strName);

					m_uiTotalDiffs ++;
				}
			}

		}

		if (diffItem.IsResultError())
		{
			dirDiffInfo.m_iImageIndex = ITEM_ERROR;
			dirDiffInfo.m_strDiffReslut = "Compare Error";

			m_uiCompareError ++;
		}
	}
	
	public class SDirDiffInfo
	{
		public String m_strName = "";
		public String m_strDirectory = "";
		public String m_strDiffReslut = "";
		public String m_strType = "";
		public String m_strLeftSize = "";
		public String m_strRightSize = "";
		public String m_strLeftDate = "";
		public String m_strRightDate = "";
	    public int m_iImageIndex = 0;
	}

	public class SImageColumn
	{
		public String m_strName = "";
		public int m_iImageIndex = 0;
		
		public String toString()
		{
		    return m_strName;
		}
	}
	
}

