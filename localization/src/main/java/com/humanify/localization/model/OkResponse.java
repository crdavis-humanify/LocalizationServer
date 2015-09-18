package com.humanify.localization.model;

import org.springframework.http.ResponseEntity;

public class OkResponse extends ServerResponse
{
	private OkResponse(Object data)
	{
		super(true, data);
	}
	
	public static ResponseEntity<ServerResponse> create(Object data)
	{
		return ResponseEntity.ok(new OkResponse(data));
	}
}


