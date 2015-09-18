package com.humanify.localization.model;

public class UpdateResults
{
	public String info;
	public int numCreated;
	public int numUpdated;
	public int numDeleted;
	public int numUnchanged;
	
	public UpdateResults()
	{
		info = null;
		numCreated = 0;
		numUpdated = 0;
		numDeleted = 0;
		numUnchanged = 0;
	}
	
	public UpdateResults ok(String detail)
	{
		info = detail;
		return this;
	}
}
