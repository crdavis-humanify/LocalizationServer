package com.humanify.localization.service;

import java.util.Iterator;

public class LocalePath implements Iterable<String>
{
	private final String userLocale;	// the user-visible locale
	private final String tenant;		// the tenant name for overrides, or null

	LocalePath(String locale, String tenant)
	{
		this.userLocale = locale;
		this.tenant = tenant;
	}
	
	// Construct from string which may or may not include tenant override
	LocalePath(String locale)
	{
		int ix = locale.indexOf("-[");
		if (ix < 0)
		{
			this.userLocale = locale;
			this.tenant = null;
		}
		else
		{
			this.userLocale = locale.substring(0,  ix);
			this.tenant = locale.substring(ix + 2, locale.lastIndexOf(']'));
		}
	}
	
	public boolean hasTenantOverride()
	{
		return (tenant != null);
	}
	
	public String getOverrideLocale()
	{
		return String.format("%s-[%s]", userLocale, tenant);
	}
	
	public String getUserLocale()
	{
		return userLocale;
	}
	
	public String getTenant()
	{
		return tenant;
	}
	
	public String getLocaleString()
	{
		if (hasTenantOverride())
			return getOverrideLocale();
		else
			return getUserLocale();
	}
	
	
	@Override
	public Iterator<String> iterator()
	{
		return new LocaleIterator();
	}
	
	
	// Locale path iterator
	// Provides a path from most-specific to least-specific locale, with tenant overrides interleaved at each level
	class LocaleIterator implements Iterator<String>
	{
		private boolean applyTenant;
		private int lastIndex;
		private String tenantTag;
		
		LocaleIterator()
		{
			applyTenant = (null != tenant);
			lastIndex = userLocale.length();
			tenantTag = (null != tenant) ? "-[" + tenant + "]" : null;
		}

		@Override
		public boolean hasNext()
		{
			return (lastIndex > 0);
		}

		@Override
		public String next()
		{
			String value = userLocale.substring(0, lastIndex);
			if (applyTenant)
			{
				value = value + tenantTag;
				applyTenant = false;
			}
			else
			{
				lastIndex = userLocale.substring(0, lastIndex).lastIndexOf('-');
				applyTenant = (null != tenant);
			}
			return value;
		}
		
	}
	
}