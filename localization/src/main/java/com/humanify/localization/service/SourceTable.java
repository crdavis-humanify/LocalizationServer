package com.humanify.localization.service;

import java.util.HashMap;
import java.util.Set;

import com.humanify.localization.entity.Source;
import com.humanify.localization.exception.InconsistentDataException;

public class SourceTable
{
	
	private HashMap<String, LocaleTable> sourceMap;
	
	public SourceTable()
	{
		sourceMap = new HashMap<String, LocaleTable>();
	}
	
	public LocaleTable get(String name)
	{
		return sourceMap.get(name);
	}
	
	public LocaleTable add(Source source) throws InconsistentDataException
	{
		if (sourceMap.containsKey(source.getName()))
		{
			throw new InconsistentDataException(
					String.format("Duplicate source name '%s'.", source.getName()));
		}
		LocaleTable localeTbl = new LocaleTable(source);
		sourceMap.put(source.getName(), localeTbl);
		return localeTbl;
	}
	
	public Set<String> listSources()
	{
		return sourceMap.keySet();
	}
}

