
public class Issue {
	private String id;
	private String owner;
	private String status;
	private String reviewer;
	private String researchStatus;
	private IssueModule[] modules;
	private IssueFunction[] functions;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

	public Issue(){
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

	public String getResearchStatus() {
		return researchStatus;
	}

	public void setResearchStatus(String researchStatus) {
		this.researchStatus = researchStatus;
	}

	public IssueModule[] getModules() {
		return modules;
	}

	public void setModules(IssueModule[] modules) {
		this.modules = modules;
	}

	public IssueFunction[] getFunctions() {
		return functions;
	}

	public void setFunctions(IssueFunction[] functions) {
		this.functions = functions;
	}

}
