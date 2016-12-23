package check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;

public class ResearchResult {
	private String issueId;
	
	private ArrayList<IssueFunction> arrFunctions;
	private ArrayList<IssueModule> arrModules;
	HashSet<String> fcNameSet;
	
	public ResearchResult(String issueId){
		this.issueId = issueId;
		arrFunctions = new ArrayList<IssueFunction>();
		arrModules = new ArrayList<IssueModule>();
		fcNameSet = new HashSet<String>();
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
		//remove the repeat function
		if(StringUtils.isBlank(function.getFunctionName()) ||
				fcNameSet.contains(function.getFunctionName())){
			;
		} else {
			arrFunctions.add(function);
			fcNameSet.add(function.getFunctionName());
		}
		
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
