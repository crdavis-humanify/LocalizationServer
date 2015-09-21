package com.humanify.localization.service;

import java.util.HashMap;
import java.util.Set;

import com.humanify.localization.entity.Locale;
import com.humanify.localization.entity.Message;
import com.humanify.localization.exception.InconsistentDataException;

// A table of messages for a given source and locale.
// Key is the message key, value is the message text.
//
public class MessageTable
{
	private final Locale locale;
	private HashMap<String, Message> table;
	
	public Locale getLocale() { return locale; }
	
	public MessageTable(Locale locale)
	{
		this.locale = locale;
		this.table = new HashMap<String, Message>();
	}
	
	public Message get(String key)
	{
		return table.get(key);
	}
	
	public void add(Message msg) throws InconsistentDataException
	{
		if (table.containsKey(msg.getKey()))
		{
			throw new InconsistentDataException(
					String.format("Duplicate key '%s' in locale '%s' in source '%s'.", msg.getKey(), locale.getTag(), locale.getSource().getName()));
		}
		
		if (locale.getId() != msg.getLocale().getId())
		{
			throw new InconsistentDataException(
					String.format("Inconsistent references to locale '%s' from source '%s'.", locale.getTag(), locale.getSource().getName()));
		}
		
		table.put(msg.getKey(), msg);
	}
	
	public void update(Message msg) throws InconsistentDataException
	{
		if (!table.containsKey(msg.getKey()))
		{
			throw new InconsistentDataException(
					String.format("Attempt to update nonexistent key '%s' in locale '%s' in source '%s'.", msg.getKey(), locale.getTag(), locale.getSource().getName()));
		}
		
		table.put(msg.getKey(), msg);
	}
	
	public void remove(String key)
	{
		if (!table.containsKey(key))
			return;
		
		table.remove(key);
	}
	
	public Set<String> getKeys()
	{
		return table.keySet();
	}

}
