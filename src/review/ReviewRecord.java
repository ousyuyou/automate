package review;

public class ReviewRecord {
	private String issueID;
	/**
	 * �@�\��
	 */
	private String functionName;
	/**
	 * �J���AUT�AST�Ȃ�
	 */
	private String reivewProject;
	/**
	 * �\�[�X�A�P�[�X�A�e�X�g���ʂȂ�
	 */
	private String resultKbn;
	/**
	 * ���r���[�Ώۓ��e,�\�[�X�t�@�C�����܂��P�[�X�t�@�C�����A�e�X�g���ʃt�@�C����
	 */
	private String resouceName;
	/**
	 * �w�E��ށA�w�E��m�F���Q�Ȃ�
	 */
	private String criticizeKbn;
	/**
	 * �ꎟ���r���[�A�񎟃��r���[�A�O�����r���[�Ȃ�
	 */
	private String reviewStep;
	/**
	 * �w�E�v��
	 */
	private String criticizeReason;
	/**
	 * �w�E��
	 */
	private String reviewer;
	
	/**
	 * �Ή��S����
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
