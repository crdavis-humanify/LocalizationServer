package com.humanify.localization.model;

import org.springframework.http.ResponseEntity;

public class ErrorResponse extends ServerResponse
{
	public ErrorResponse(ErrorDetail detail)
	{
		super(false, detail);
	}
	
	public static ResponseEntity<ServerResponse> create(ErrorDetail detail)
	{
		return new ResponseEntity<>(new ErrorResponse(detail), detail.getHttpStatus());
	}
}


