package paksu.finbert;

public class ParsedResult {
	private Responsecode status;

	public static enum Responsecode {
		SUCCESS, FAILURE_INVALID_MESSAGE, FAILURE_OTHER
	}

	public ParsedResult(Responsecode status) {
		setStatus(status);
	}

	public Responsecode getStatus() {
		return status;
	}

	public void setStatus(Responsecode status) {
		this.status = status;
	}

}
