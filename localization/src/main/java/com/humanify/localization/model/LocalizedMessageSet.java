package com.humanify.localization.model;

import java.util.Set;

public class LocalizedMessageSet
{
	public String source;
	public String locale;
	public String tenant;
	public long lastModified;
	public Set<LocalizedMessage> messages;
}
