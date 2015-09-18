package com.humanify.localization.model;

public class ServerResponse
{
	private final boolean success;
	private final Object data;
	
	public boolean getSuccess() { return success; }
	public Object getData() { return data; }
	
	protected ServerResponse(boolean success, Object data)
	{
		this.success = success;
		this.data = data;
	}
}
