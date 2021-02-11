package scot.ianmacdonald.cakemgr.restclient.model;

import org.springframework.http.HttpStatus;

public class CakeServiceError {
	
	private HttpStatus status;
	private String message;
	private String debugMessage;
	
	public CakeServiceError() {
		
	}

	public CakeServiceError(HttpStatus status, String message, Throwable ex) {
		this.status = status;
		this.message = message;
		this.debugMessage = ex.getLocalizedMessage();
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

}
