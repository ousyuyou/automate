package check;
/**
 * test functions
 * @author 
 *
 */
public class IssueFunction {
	private String issueID;
	private String functionID;
	private String functionName;
	private String jobID;
	private String patternName;
	
	public IssueFunction(){
		
	}
	
	public String getIssueID() {
		return issueID;
	}
	public void setIssueID(String issueID) {
		this.issueID = issueID;
	}
	public String getFunctionID() {
		return functionID;
	}
	public void setFunctionID(String functionID) {
		this.functionID = functionID;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getJobID() {
		return jobID;
	}

	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	public String getPatternName() {
		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}
}
