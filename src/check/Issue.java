package check;
import java.util.ArrayList;


public class Issue {
	private String id;
	private String owner;
	private String status;
	private String reviewer;
	private String researchStatus;
	private String planStartDate;
	private String planFinishDate;
	private String delay;
	private String delayComment;
	private String actualStartDate;
	private String actualFinishDate;
	private String dealFlag;
	private String ikouResource;
	
	private String yoteiKousuResearch;
	private String yoteiKousuCD;
	private String yoteiKousuUT;
	private String yoteiKousuST;
	
	private String jisekiResearch;
	private String jisekiKousuCD;
	private String jisekiKousuUT;
	private String jisekiKousuST;
	
	private String rlsVersion;
	
	private String researchPercent;
	private String cdPercent;
	private String utPercent;
	private String stPercent;
	
//	private IssueModule[] modules;
	private IssueFunction[] functions;
	private ArrayList<IssueModule> arrModules;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	public Issue(){
		arrModules = new ArrayList<IssueModule>();
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
		return arrModules.toArray(new IssueModule[arrModules.size()]);
	}

	public void setModules(IssueModule[] modules) {
		for(IssueModule module:modules){
			arrModules.add(module);
		}
	}

	public IssueFunction[] getFunctions() {
		return functions;
	}

	public void setFunctions(IssueFunction[] functions) {
		this.functions = functions;
	}
	
	public void addIssueModule(IssueModule module){
		if(arrModules == null){
			arrModules = new ArrayList<IssueModule>();
		}
		arrModules.add(module);
	}

	public String getPlanFinishDate() {
		return planFinishDate;
	}

	public void setPlanFinishDate(String planFinishDate) {
		this.planFinishDate = planFinishDate;
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public String getDelayComment() {
		return delayComment;
	}

	public void setDelayComment(String delayComment) {
		this.delayComment = delayComment;
	}

	public String getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(String actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public String getActualFinishDate() {
		return actualFinishDate;
	}

	public void setActualFinishDate(String actualFinishDate) {
		this.actualFinishDate = actualFinishDate;
	}

	public String getDealFlag() {
		return dealFlag;
	}

	public void setDealFlag(String dealFlag) {
		this.dealFlag = dealFlag;
	}

	public String getPlanStartDate() {
		return planStartDate;
	}

	public void setPlanStartDate(String planStartDate) {
		this.planStartDate = planStartDate;
	}

	public String getIkouResource() {
		return ikouResource;
	}

	public void setIkouResource(String ikouResource) {
		this.ikouResource = ikouResource;
	}

	public String getYoteiKousuCD() {
		return yoteiKousuCD;
	}

	public void setYoteiKousuCD(String yoteiKousuCD) {
		this.yoteiKousuCD = yoteiKousuCD;
	}

	public String getYoteiKousuUT() {
		return yoteiKousuUT;
	}

	public void setYoteiKousuUT(String yoteiKousuUT) {
		this.yoteiKousuUT = yoteiKousuUT;
	}

	public String getYoteiKousuST() {
		return yoteiKousuST;
	}

	public void setYoteiKousuST(String yoteiKousuST) {
		this.yoteiKousuST = yoteiKousuST;
	}

	public String getJisekiKousuCD() {
		return jisekiKousuCD;
	}

	public void setJisekiKousuCD(String jisekiKousuCD) {
		this.jisekiKousuCD = jisekiKousuCD;
	}

	public String getJisekiKousuUT() {
		return jisekiKousuUT;
	}

	public void setJisekiKousuUT(String jisekiKousuUT) {
		this.jisekiKousuUT = jisekiKousuUT;
	}

	public String getJisekiKousuST() {
		return jisekiKousuST;
	}

	public void setJisekiKousuST(String jisekiKousuST) {
		this.jisekiKousuST = jisekiKousuST;
	}

	public String getRlsVersion() {
		return rlsVersion;
	}

	public void setRlsVersion(String rlsVersion) {
		this.rlsVersion = rlsVersion;
	}

	public String getYoteiKousuResearch() {
		return yoteiKousuResearch;
	}

	public void setYoteiKousuResearch(String yoteiKousuResearch) {
		this.yoteiKousuResearch = yoteiKousuResearch;
	}

	public String getJisekiResearch() {
		return jisekiResearch;
	}

	public void setJisekiResearch(String jisekiResearch) {
		this.jisekiResearch = jisekiResearch;
	}

	public String getResearchPercent() {
		return researchPercent;
	}

	public void setResearchPercent(String researchPercent) {
		this.researchPercent = researchPercent;
	}

	public String getUtPercent() {
		return utPercent;
	}

	public void setUtPercent(String utPercent) {
		this.utPercent = utPercent;
	}

	public String getStPercent() {
		return stPercent;
	}

	public void setStPercent(String stPercent) {
		this.stPercent = stPercent;
	}

	public String getCdPercent() {
		return cdPercent;
	}

	public void setCdPercent(String cdPercent) {
		this.cdPercent = cdPercent;
	}

}
