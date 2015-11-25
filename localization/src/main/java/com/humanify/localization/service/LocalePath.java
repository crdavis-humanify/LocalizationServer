package com.humanify.localization.service;

import java.util.Iterator;

public class LocalePath implements Iterable<String>
{
	public static final String OVERRIDE_PREFIX = "-[";
	public static final String OVERRIDE_SUFFIX = "]";
	
	private final String baseLocale;	// the user-visible locale
	private final String tenant;		// the tenant name for overrides, or null

	LocalePath(String locale, String tenant)
	{
		this.baseLocale = locale;
		this.tenant = tenant;
	}
	
	// Construct from string which may or may not include tenant override
	LocalePath(String locale)
	{
		int ix = locale.indexOf(OVERRIDE_PREFIX);
		if (ix < 0)
		{
			this.baseLocale = locale;
			this.tenant = null;
		}
		else
		{
			this.baseLocale = locale.substring(0,  ix);
			this.tenant = locale.substring(ix + 2, locale.lastIndexOf(OVERRIDE_SUFFIX));
		}
	}
	
	public boolean hasTenantOverride()
	{
		return (tenant != null);
	}
	
	public String getOverrideLocale()
	{
		return String.format("%s%s%s%s", baseLocale, OVERRIDE_PREFIX, tenant, OVERRIDE_SUFFIX);
	}
	
	public String getBaseLocale()
	{
		return baseLocale;
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
			return getBaseLocale();
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
			lastIndex = baseLocale.length();
			tenantTag = (null != tenant) ? OVERRIDE_PREFIX + tenant + OVERRIDE_SUFFIX : null;
		}

		@Override
		public boolean hasNext()
		{
			return (lastIndex > 0);
		}

		@Override
		public String next()
		{
			String value = baseLocale.substring(0, lastIndex);
			if (applyTenant)
			{
				value = value + tenantTag;
				applyTenant = false;
			}
			else
			{
				lastIndex = baseLocale.substring(0, lastIndex).lastIndexOf('-');
				applyTenant = (null != tenant);
			}
			return value;
		}
		
	}
	
}
