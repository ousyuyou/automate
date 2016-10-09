package excelio;

public class TableForCompare {
	private String tableID= null;
	private String compareSettingFile = null;

	public TableForCompare(String tabelID,String compareSettingFile){
		this.tableID = tabelID;
		this.compareSettingFile = compareSettingFile;
	}
	
	public String getTableID() {
		return tableID;
	}

	public void setTableID(String tableID) {
		this.tableID = tableID;
	}

	public String getCompareSettingFile() {
		return compareSettingFile;
	}

	public void setCompareSettingFile(String compareSettingFile) {
		this.compareSettingFile = compareSettingFile;
	}
}
