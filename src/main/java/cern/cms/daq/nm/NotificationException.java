package cern.cms.daq.nm;

public class NotificationException extends RuntimeException {
	
	private static final long serialVersionUID = -913775774199172489L;
	
	private final String message;
	
	public NotificationException(String message){
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
}
