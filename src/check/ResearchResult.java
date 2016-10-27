package check;

import java.util.ArrayList;

public class ResearchResult {
	private String issueId;
	
	private ArrayList<IssueFunction> arrFunctions;
	private ArrayList<IssueModule> arrModules;
	
	public ResearchResult(String issueId){
		this.issueId = issueId;
		arrFunctions = new ArrayList<IssueFunction>();
		arrModules = new ArrayList<IssueModule>();
	}
	
	public void addIssueModule(IssueModule module){
		if(arrModules == null){
			arrModules = new ArrayList<IssueModule>();
		}
		arrModules.add(module);
	}
	
	public IssueModule[] getModules() {
		return arrModules.toArray(new IssueModule[arrModules.size()]);
	}
	
	
	public void addIssueFunction(IssueFunction function){
		if(arrFunctions == null){
			arrFunctions = new ArrayList<IssueFunction>();
		}
		arrFunctions.add(function);
	}
	
	public IssueFunction[] getFunctions() {
		return arrFunctions.toArray(new IssueFunction[arrFunctions.size()]);
	}

	public String getIssueId() {
		return issueId;
	}

	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}
}
