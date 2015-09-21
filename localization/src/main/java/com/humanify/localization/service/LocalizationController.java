package com.humanify.localization.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.humanify.localization.exception.BadRequestException;
import com.humanify.localization.exception.LocalizationServerException;
import com.humanify.localization.model.LocalizedMessageSet;
import com.humanify.localization.model.OkResponse;
import com.humanify.localization.model.ServerResponse;
import com.humanify.localization.model.UpdateResults;

@RestController
public class LocalizationController
{
	
	@Autowired
	LocalizationService service;
	
	// List installed sources
	//
	@RequestMapping(value="/installed/source", method=RequestMethod.GET)
	public ResponseEntity<ServerResponse> listSources()
	{
		return OkResponse.create(service.listSources());
	}
	
	// List installed locales for a source
	//
	@RequestMapping(value="/installed/source/{source}/locale", method=RequestMethod.GET)
	public ResponseEntity<ServerResponse> listSourceLocales(@PathVariable("source") String source) throws LocalizationServerException
	{
		return OkResponse.create(service.listLocales(canonicalizeSource(source)));
	}
	
	// Retrieve the installed message set for a source / locale
	@RequestMapping(value="/installed/source/{source}/locale/{locale}", method=RequestMethod.GET)
	public ResponseEntity<ServerResponse> getInstalledMessages(
			@PathVariable("source") String sourceName,
			@PathVariable("locale") String localeString) throws LocalizationServerException
	{
		LocalePath localePath = new LocalePath(canonicalizeLocale(localeString));
		return OkResponse.create(service.retrieveMessageSet(canonicalizeSource(sourceName), localePath));
	}
	
	// Resolve a single message for a source / key / locale / tenant
	//
	@RequestMapping(value="/localized/source/{source}/key/{key}/locale/{locale}/tenant/{tenant}", method=RequestMethod.GET)
	public ResponseEntity<ServerResponse> getMessage(
			@PathVariable("source") String sourceName,
			@PathVariable("key") String key,
			@PathVariable("locale") String localeString,
			@PathVariable("tenant") String tenant) throws LocalizationServerException

	{
		LocalePath localePath = new LocalePath(canonicalizeLocale(localeString), canonicalizeTenant(tenant));
		return OkResponse.create(service.resolveLocalizedMessage(canonicalizeSource(sourceName), key, localePath));
	}
	
	// Resolve a single message for a source / key / locale
	//
	@RequestMapping(value="/localized/source/{source}/key/{key}/locale/{locale}", method=RequestMethod.GET)
	public ResponseEntity<ServerResponse> getMessage(
			@PathVariable("source") String sourceName,
			@PathVariable("key") String key,
			@PathVariable("locale") String localeString) throws LocalizationServerException
	{
		LocalePath localePath = new LocalePath(canonicalizeLocale(localeString));
		return OkResponse.create(service.resolveLocalizedMessage(canonicalizeSource(sourceName), key, localePath));
	}
	
	// Resolve the message set for a source / locale / tenant
	//
	@RequestMapping(value="/localized/source/{source}/locale/{locale}/tenant/{tenant}", method=RequestMethod.GET)
	public ResponseEntity<ServerResponse> getMessageSet(
			@PathVariable("source") String sourceName,
			@PathVariable("locale") String localeString,
			@PathVariable("tenant") String tenant) throws LocalizationServerException
	{
		LocalePath localePath = new LocalePath(canonicalizeLocale(localeString), canonicalizeTenant(tenant));
		return OkResponse.create(service.resolveLocalizedMessageSet(canonicalizeSource(sourceName), localePath));
	}
	
	// Resolve the message set for a source / locale
	//
	@RequestMapping(value="/localized/source/{source}/locale/{locale}", method=RequestMethod.GET)
	public ResponseEntity<ServerResponse> getMessageSet(
			@PathVariable("source") String sourceName,
			@PathVariable("locale") String localeString) throws LocalizationServerException
	{
		LocalePath localePath = new LocalePath(canonicalizeLocale(localeString));
		return OkResponse.create(service.resolveLocalizedMessageSet(canonicalizeSource(sourceName), localePath));
	}
	
	// Retrieve the tenant override message set for a tenant / source / locale
	//
	@RequestMapping(value="/tenant/{tenant}/source/{source}/locale/{locale}", method=RequestMethod.GET)
	public ResponseEntity<ServerResponse> retrieveTenantOverrides(
			@PathVariable("tenant") String tenant,
			@PathVariable("source") String sourceName,
			@PathVariable("locale") String localeString) throws LocalizationServerException
	{
		LocalePath localePath = new LocalePath(canonicalizeLocale(localeString), canonicalizeTenant(tenant));
		return OkResponse.create(service.retrieveMessageSet(canonicalizeSource(sourceName), localePath));
	}
	
	// Save the tenant override message set for a tenant / source / locale
	//
	@RequestMapping(value="/tenant/{tenant}/source/{source}/locale/{locale}", method=RequestMethod.POST)
	public ResponseEntity<ServerResponse> updateOverrideSet(
			@PathVariable("tenant") String tenant,
			@PathVariable("source") String sourceName,
			@PathVariable("locale") String localeString,
			@RequestBody LocalizedMessageSet overrides) throws LocalizationServerException
	{
		tenant = canonicalizeTenant(tenant);
		sourceName = canonicalizeSource(sourceName);
		localeString = canonicalizeLocale(localeString);
	
		if (!tenant.equals(canonicalizeTenant(overrides.tenant)))
			throw new BadRequestException("Tenant mismatch");
		if (!sourceName.equals(canonicalizeSource(overrides.source)))
			throw new BadRequestException("Source name mismatch");
		if (!localeString.equals(canonicalizeLocale(overrides.locale)))
			throw new BadRequestException("Locale mismatch");
		
		LocalePath localePath = new LocalePath(localeString, tenant);
		
		Map<String, String> defs = new HashMap<String, String>();
		overrides.messages.stream().forEach(m -> defs.put(m.key, m.text));
		UpdateResults results = service.updateMessageSet(sourceName, localePath, defs, true);
		return OkResponse.create(results);
	}
	
	// Retrieve the tenant override message sets for a tenant / source (all locales)
	//
	@RequestMapping(value="/tenant/{tenant}/source/{source}", method=RequestMethod.GET)
	public ResponseEntity<ServerResponse> retrieveTenantOverridesBySource(
			@PathVariable("tenant") String tenant,
			@PathVariable("source") String sourceName) throws LocalizationServerException
	{
		return OkResponse.create(service.retrieveTenantOverridesBySource(canonicalizeTenant(tenant), canonicalizeSource(sourceName)));
	}
	
	// Retrieve the tenant override message sets for a tenant / locale (all sources)
	//
	@RequestMapping(value="/tenant/{tenant}/locale/{locale}", method=RequestMethod.GET)
	public ResponseEntity<ServerResponse> retrieveTenantOverridesForLocale(
			@PathVariable("tenant") String tenant,
			@PathVariable("locale") String localeString) throws LocalizationServerException
	{
		LocalePath localePath = new LocalePath(canonicalizeLocale(localeString), canonicalizeTenant(tenant));
		return OkResponse.create(service.retrieveTenantOverridesByLocale(localePath));
	}
	
	// Retrieve all overrides for a tenant
	//
	@RequestMapping(value="/tenant/{tenant}", method=RequestMethod.GET)
	public ResponseEntity<ServerResponse> retrieveAllTenantOverrides(@PathVariable("tenant") String tenant) throws LocalizationServerException
	{
		return OkResponse.create(service.retrieveAllTenantOverrides(canonicalizeTenant(tenant)));
	}
	
	// Upload a properties file containing message definitions
	//
	@RequestMapping(value="/admin/upload", method=RequestMethod.POST)
	public ResponseEntity<ServerResponse> handleFileUpload(@RequestParam("file") MultipartFile file) throws LocalizationServerException, IOException
	{
		if (file.isEmpty())
			throw new BadRequestException("Definitions source file is empty.");
		
		InputStream is = file.getInputStream();
		Properties properties = new Properties();
		properties.load(is);
		String source = canonicalizeSource(properties.getProperty("SOURCE"));
		properties.remove("SOURCE");
		String localeString = canonicalizeLocale(properties.getProperty("LOCALE"));
		properties.remove("LOCALE");
		LocalePath localePath = new LocalePath(localeString);
		
		Map<String, String> defs = new HashMap<String, String>();
		properties.entrySet().stream().forEach(e -> defs.put((String) e.getKey(), (String) e.getValue()));
		UpdateResults results = service.updateMessageSet(source, localePath, defs, false);
		return OkResponse.create(results);
	}
	
	// Dump all data
	//
	@RequestMapping(value="/admin/dump", method=RequestMethod.GET)
	public ResponseEntity<ServerResponse> dump()
	{
		return OkResponse.create(service.dump());
	}
	
	// Force the server to reinitialize from the database
	//
	@RequestMapping(value="/admin/reinit", method=RequestMethod.PUT)
	public ResponseEntity<ServerResponse> reinit() throws LocalizationServerException
	{
		service.initializeFromDb();
		return OkResponse.create("Reinitialization from database successful.");
	}
	
	// Retrieve table from db
	//
	@RequestMapping(value="/admin/db/{table}", method=RequestMethod.GET)
	public ResponseEntity<ServerResponse> dumpDatabaseTable(@PathVariable("table") String table)
	{
		return OkResponse.create(service.dumpDatabaseTable(table));
	}
	
	private String canonicalizeSource(String sourceName) throws BadRequestException
	{
		if (null == sourceName)
			throw new BadRequestException("Missing source name");
		
		sourceName = sourceName.trim().toLowerCase();
		if (sourceName.isEmpty()){
			throw new BadRequestException("Invalid source name");
		}
		return sourceName;
	}
	
	private String canonicalizeLocale(String userLocale) throws BadRequestException
	{
		if (null == userLocale)
			throw new BadRequestException("Missing locale");
		
		userLocale = userLocale.trim().toLowerCase();
		if ((userLocale.indexOf('[') >= 0) || (userLocale.indexOf(']') >= 0) || userLocale.isEmpty())
		{
			throw new BadRequestException("Invalid locale string");
		}
		return userLocale;
	}
	
	private String canonicalizeTenant(String tenant) throws BadRequestException
	{
		tenant = tenant.trim().toLowerCase().replace('-', '_');
		if ((tenant.indexOf('[') >= 0) || (tenant.indexOf(']') >= 0) || tenant.isEmpty())
		{
			throw new BadRequestException("Invalid tenant name");
		}
		return tenant;
	}

}
