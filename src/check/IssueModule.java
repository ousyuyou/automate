package check;
/**
 * changed modules
 * @author 
 *
 */
public class IssueModule {
	private String issueID;
	private String moduleID;
	/**
	 * the mapping key,when blank set moduleid
	 */
	private String modulePath;
	/**
	 * fms-core,fms-if,surittokei
	 */
	private String projectID;
	private String functionName;
	/**
	 * 1:java source,pdf xml
	 * 2:shell,db,code list
	 */
	private int modelType;
	
	private String ddlSql;
	private String patchYouhi;
	
	private String utSheetName;
	private String rlsStatus;
	private String rlsVersion;
	private String updatKbn; 
	
	public IssueModule(){
		
	}
	public String getIssueID() {
		return issueID;
	}
	public void setIssueID(String issueID) {
		this.issueID = issueID;
	}
	public String getModuleID() {
		return moduleID;
	}
	public void setModuleID(String moduleID) {
		this.moduleID = moduleID;
	}
	public String getModulePath() {
		return modulePath;
	}
	public void setModulePath(String modulePath) {
		this.modulePath = modulePath;
	}
	public String getProjectID() {
		return projectID;
	}
	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	/**
	 * 1:java source,pdf xml
	 * 2:shell,db,code list
	 * @return
	 */
	public int getModelType() {
		return modelType;
	}
	public void setModelType(int modelType) {
		this.modelType = modelType;
	}
	public String getDdlSql() {
		return ddlSql;
	}
	public void setDdlSql(String ddlSql) {
		this.ddlSql = ddlSql;
	}
	public String getPatchYouhi() {
		return patchYouhi;
	}
	public void setPatchYouhi(String patchYouhi) {
		this.patchYouhi = patchYouhi;
	}
	public String getUtSheetName() {
		return utSheetName;
	}
	public void setUtSheetName(String utSheetName) {
		this.utSheetName = utSheetName;
	}
	public String getRlsVersion() {
		return rlsVersion;
	}
	public void setRlsVersion(String rlsVersion) {
		this.rlsVersion = rlsVersion;
	}
	public String getRlsStatus() {
		return rlsStatus;
	}
	public void setRlsStatus(String rlsStatus) {
		this.rlsStatus = rlsStatus;
	}
	public String getUpdatKbn() {
		return updatKbn;
	}
	public void setUpdatKbn(String updatKbn) {
		this.updatKbn = updatKbn;
	}
}
