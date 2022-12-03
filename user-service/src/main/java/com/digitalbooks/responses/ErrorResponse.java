package com.digitalbooks.responses;

public class ErrorResponse {

	String message;
	Throwable cause;

	public ErrorResponse() {
	}

	public ErrorResponse(String message, Throwable throwable) {
		super();
		this.message = message;
		this.cause = throwable;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	@Override
	public String toString() {
		return "ErrorResponse [message=" + message + ", cause=" + cause + "]";
	}

}
