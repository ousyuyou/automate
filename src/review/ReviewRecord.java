package review;

public class ReviewRecord {
	private String issueID;
	/**
	 * 機能名
	 */
	private String functionName;
	/**
	 * 開発、UT、STなど
	 */
	private String reivewProject;
	/**
	 * ソース、ケース、テスト結果など
	 */
	private String resultKbn;
	/**
	 * レビュー対象内容,ソースファイル名またケースファイル名、テスト結果ファイル名
	 */
	private String resouceName;
	/**
	 * 指摘種類、指摘や確認や障害など
	 */
	private String criticizeKbn;
	/**
	 * 一次レビュー、二次レビュー、外部レビューなど
	 */
	private String reviewStep;
	/**
	 * 指摘要因
	 */
	private String criticizeReason;
	/**
	 * 指摘者
	 */
	private String reviewer;
	
	/**
	 * 対応担当者
	 */
	private String owner;
	/**
	 * review status
	 */
	private String stauts;

	public String getIssueID() {
		return issueID;
	}

	public void setIssueID(String issueID) {
		this.issueID = issueID;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getReivewProject() {
		return reivewProject;
	}

	public void setReivewProject(String reivewProject) {
		this.reivewProject = reivewProject;
	}

	public String getResultKbn() {
		return resultKbn;
	}

	public void setResultKbn(String resultKbn) {
		this.resultKbn = resultKbn;
	}

	public String getResouceName() {
		return resouceName;
	}

	public void setResouceName(String resouceName) {
		this.resouceName = resouceName;
	}

	public String getCriticizeKbn() {
		return criticizeKbn;
	}

	public void setCriticizeKbn(String criticizeKbn) {
		this.criticizeKbn = criticizeKbn;
	}

	public String getReviewStep() {
		return reviewStep;
	}

	public void setReviewStep(String reviewStep) {
		this.reviewStep = reviewStep;
	}

	public String getCriticizeReason() {
		return criticizeReason;
	}

	public void setCriticizeReason(String criticizeReason) {
		this.criticizeReason = criticizeReason;
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getStauts() {
		return stauts;
	}

	public void setStauts(String stauts) {
		this.stauts = stauts;
	}
	
}
