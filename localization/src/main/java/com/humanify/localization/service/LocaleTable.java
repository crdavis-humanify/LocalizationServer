package com.humanify.localization.service;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import com.humanify.localization.entity.Locale;
import com.humanify.localization.entity.Source;
import com.humanify.localization.exception.InconsistentDataException;

// A table representing all of the locales defined for a given source.
// Key is locale string, value is the MessageTable for that locale.
//
public class LocaleTable
{
	
	private final Source source;
	private HashMap<String, MessageTable> localeMap;
	
	public Source getSource() { return source; }
	
	public LocaleTable(Source source)
	{
		this.source = source;
		this.localeMap = new HashMap<String, MessageTable>();
	}
	
	public MessageTable get(String tag)
	{
		return localeMap.get(tag);
	}
	
	public MessageTable addLocale(Locale locale) throws InconsistentDataException
	{
		if (localeMap.containsKey(locale.getTag()))
		{
			throw new InconsistentDataException(
					String.format("Duplicate locale '%s' in source '%s'.", locale.getTag(), source.getName()));
		}
		
		if (source.getId() != locale.getSource().getId())
		{
			throw new InconsistentDataException(
					String.format("Inconsistent references to source '%s' in locale '%s'.", source.getName(), locale.getTag()));
		}
		
		MessageTable messageTbl = new MessageTable(locale);
		localeMap.put(locale.getTag(), messageTbl);
		return messageTbl;
	}
	
	public MessageTable resetLocale(Locale locale) throws InconsistentDataException
	{
		if (!localeMap.containsKey(locale.getTag()))
		{
			throw new InconsistentDataException(
					String.format("Locale '%s' not found in source '%s'.", locale.getTag(), locale.getSource().getName()));
		}
		
		MessageTable messageTbl = new MessageTable(locale);
		localeMap.put(locale.getTag(), messageTbl);
		return messageTbl;
	}
	
	public Set<String> listAllLocales()
	{
		return localeMap.keySet();
	}
	
	public Set<String> listUserLocales()
	{
		return localeMap.keySet().stream().filter(k -> !(new LocalePath(k).hasTenantOverride())).collect(Collectors.toSet());
	}
	

}
