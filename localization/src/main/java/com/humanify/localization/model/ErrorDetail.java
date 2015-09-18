package com.humanify.localization.model;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ErrorDetail
{
	@JsonIgnore
	private final HttpStatus httpStatus;
	private final int status;
	private final String reason;
	private final String message;
	private String requestInfo;
	private Object additionalInfo;
	
	public ErrorDetail(HttpStatus status, Throwable ex)
	{
		this.httpStatus = status;
		this.status = status.value();
		this.reason = status.getReasonPhrase();
		this.message = ex.getMessage();
		this.requestInfo = null;
		this.additionalInfo = null;
	}
	
	public HttpStatus getHttpStatus() { return httpStatus; }
	public int getStatus() { return status; }
	public String getReason() { return reason; }
	public String getMessage() { return message; }
	public String getRequestInfo() { return requestInfo; }
	public Object getAdditionalInfo() { return additionalInfo; }

	public void setRequestInfo(String info) { this.requestInfo = info; }	
	
	public void setAdditionalInfo(Object info) { this.additionalInfo = info; }

}
