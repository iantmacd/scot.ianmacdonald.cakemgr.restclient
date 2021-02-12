package scot.ianmacdonald.cakemgr.restclient.model;

import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hash(debugMessage, message, status);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CakeServiceError)) {
			return false;
		}
		CakeServiceError other = (CakeServiceError) obj;
		return Objects.equals(debugMessage, other.debugMessage) && Objects.equals(message, other.message)
				&& status == other.status;
	}
	
	

}
