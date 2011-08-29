package paksu.finbert;

public class NetworkException extends Exception {

	public NetworkException(Exception e) {
		super(e);
	}

	public NetworkException(String reason) {
		super(reason);
	}

}
