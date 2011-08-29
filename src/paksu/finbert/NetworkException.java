package paksu.finbert;

public class NetworkException extends Exception {
	private static final long serialVersionUID = 3067655620736606464L;

	public NetworkException(Exception e) {
		super(e);
	}

	public NetworkException(String reason) {
		super(reason);
	}

}
