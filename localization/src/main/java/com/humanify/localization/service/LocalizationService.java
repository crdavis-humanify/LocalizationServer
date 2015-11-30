package com.humanify.localization.service;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.humanify.localization.entity.Locale;
import com.humanify.localization.entity.Message;
import com.humanify.localization.entity.Source;
import com.humanify.localization.exception.InconsistentDataException;
import com.humanify.localization.model.LocalizedMessage;
import com.humanify.localization.model.LocalizedMessageResult;
import com.humanify.localization.model.LocalizedMessageSet;
import com.humanify.localization.model.UpdateResults;
import com.humanify.localization.repo.LocaleRepository;
import com.humanify.localization.repo.MessageRepository;
import com.humanify.localization.repo.SourceRepository;

@Component
public class LocalizationService
{
	
	private SourceTable sourceTbl;
	
	@Autowired
	private SourceRepository sourceRepo;
	
	@Autowired
	private LocaleRepository localeRepo;
	
	@Autowired
	private MessageRepository messageRepo;
	
	@Autowired
	PlatformTransactionManager txManager;
	
	LocalizationService()
	{
		sourceTbl = new SourceTable();
	}
	
	@PostConstruct
	synchronized void initializeFromDb() throws InconsistentDataException
	{
		sourceTbl = new SourceTable();
		
		for (Source source : sourceRepo.findAll())
		{
			initializeSourceFromDb(source);
		}
	}
	
	private void initializeSourceFromDb(Source source) throws InconsistentDataException
	{
		for (Locale locale : localeRepo.findBySource(source))
		{
			initializeLocaleFromDb(source, locale);
		}
	}
	
	private void initializeLocaleFromDb(Source source, Locale locale) throws InconsistentDataException
	{
		LocaleTable localeTbl = sourceTbl.get(source.getName());
		if (null == localeTbl)
		{
			localeTbl = sourceTbl.add(source);
		}
		
		MessageTable messageTbl = localeTbl.get(locale.getTag());
		if (null == messageTbl)
		{
			messageTbl = localeTbl.addLocale(locale);
		}
		else
		{
			messageTbl = localeTbl.resetLocale(locale);
		}
		
		for (Message message : messageRepo.findByLocale(locale))
		{
			messageTbl.add(message);
		}
	}
	
	synchronized Set<String> listSources()
	{
		Set<String> result = new HashSet<String>();
		result.addAll(sourceTbl.listSources());
		return result;
	}
	
	synchronized Set<String> listLocales(String sourceName)
	{
		Set<String> result = new HashSet<String>();
		LocaleTable localeTbl = sourceTbl.get(sourceName);
		if (null != localeTbl)
		{
			result.addAll(localeTbl.listBaseLocales());
		}
		
		return result;
	}
	
	synchronized LocalizedMessageResult resolveLocalizedMessage(String sourceName, String key, LocalePath localePath)
	{
		LocalizedMessageResult result = new LocalizedMessageResult();
		result.source = sourceName;
		result.locale = localePath.getBaseLocale();
		result.tenant = localePath.getTenant();
		result.message = resolveMessage(sourceName, key, localePath);
		return result;
	}
	
	synchronized LocalizedMessageSet resolveLocalizedMessageSet(String sourceName, LocalePath localePath)
	{
		LocalizedMessageSet result = resolveMessageSet(sourceName, localePath);
		result.source = sourceName;
		result.locale = localePath.getBaseLocale();
		result.tenant = localePath.getTenant();
		return result;
	}
	
	synchronized LocalizedMessageSet retrieveMessageSet(String sourceName, LocalePath localePath)
	{
		LocalizedMessageSet result = getMessageSet(sourceName, localePath);
		result.source = sourceName;
		result.locale = localePath.getBaseLocale();
		result.tenant = localePath.getTenant();
		return result;
	}
	
	synchronized Set<LocalizedMessageSet> retrieveAllTenantOverrides(String tenant)
	{
		Set<LocalizedMessageSet> result = new HashSet<LocalizedMessageSet>();
		for (String sourceName : sourceTbl.listSources())
		{
			LocaleTable localeTbl = sourceTbl.get(sourceName);
			for (String localeString : localeTbl.listLocales())
			{
				LocalePath localePath = new LocalePath(localeString);
				if (tenant.equals(localePath.getTenant()))
				{
					LocalizedMessageSet messageSet = getMessageSet(sourceName, localePath);
					messageSet.source = sourceName;
					messageSet.locale = localePath.getBaseLocale();
					messageSet.tenant = localePath.getTenant();
					result.add(messageSet);
				}
			}
		}
		return result;
	}
	
	synchronized Set<LocalizedMessageSet> retrieveTenantOverridesBySource(String tenant, String sourceName)
	{
		Set<LocalizedMessageSet> result = new HashSet<LocalizedMessageSet>();
		LocaleTable localeTbl = sourceTbl.get(sourceName);
		for (String localeString : localeTbl.listLocales())
		{
			LocalePath localePath = new LocalePath(localeString);
			if (tenant.equals(localePath.getTenant()))
			{
				LocalizedMessageSet messageSet = getMessageSet(sourceName, localePath);
				messageSet.source = sourceName;
				messageSet.locale = localePath.getBaseLocale();
				messageSet.tenant = localePath.getTenant();
				result.add(messageSet);
			}
		}
		
		return result;
	}
	
	synchronized Set<LocalizedMessageSet> retrieveTenantOverridesByLocale(LocalePath desiredLocale)
	{
		Set<LocalizedMessageSet> result = new HashSet<LocalizedMessageSet>();
		for (String sourceName : sourceTbl.listSources())
		{
			LocaleTable localeTbl = sourceTbl.get(sourceName);
			for (String localeString : localeTbl.listLocales())
			{
				LocalePath localePath = new LocalePath(localeString);
				if ((null != desiredLocale.getTenant()) && desiredLocale.getTenant().equals(localePath.getTenant()))
				{
					LocalizedMessageSet messageSet = getMessageSet(sourceName, localePath);
					messageSet.source = sourceName;
					messageSet.locale = desiredLocale.getBaseLocale();
					messageSet.tenant = desiredLocale.getTenant();
					result.add(messageSet);
				}
			}
		}
		
		return result;
	}
	
	synchronized Set<String> listTenants()
	{
		Set<String> tenants = new HashSet<String>();
		for (String sourceName : sourceTbl.listSources())
		{
			LocaleTable localeTbl = sourceTbl.get(sourceName);
			for (String localeString : localeTbl.listLocales())
			{
				LocalePath path = new LocalePath(localeString);
				if (path.hasTenantOverride())
					tenants.add(path.getTenant());
			}
		}
		
		return tenants;
	}
	
	synchronized Set<String> listTenantSources(String tenant)
	{
		return sourceTbl.listSources()
			.stream()
			.filter(s -> sourceTbl.get(s).listLocales()
						.stream()
						.map(ls -> new LocalePath(ls))
						.anyMatch(lp -> tenant.equals(lp.getTenant())))
			.collect(Collectors.toSet());		
	}
	
	synchronized Set<String> listTenantLocales(String tenant)
	{
		Set<String> locales = new HashSet<String>();
		
		for (String sourceName : sourceTbl.listSources())
		{
			LocaleTable localeTbl = sourceTbl.get(sourceName);
			locales = localeTbl.listLocales()
					.stream()
					.map(ls -> new LocalePath(ls))
					.filter(lp -> tenant.equals(lp.getTenant()))
					.map(lp -> lp.getBaseLocale())
					.collect(Collectors.toSet());
		}
		
		return locales;
	}
	
	synchronized UpdateResults updateMessageSet(String sourceName, LocalePath localePath, Map<String, String> definitions, boolean allowDelete) throws InconsistentDataException
	{
		
		UpdateResults results = updateMessageSetInDb(sourceName, localePath, definitions, allowDelete);
		replaceMessageSetFromDb(sourceName, localePath);
		return results;
	}
	
	private UpdateResults updateMessageSetInDb(String sourceName, LocalePath localePath, Map<String, String> definitions, boolean allowDelete)
	{
		Source newSource = null;
		Locale newLocale = null;
		Locale workingLocale = null;
		
		UpdateResults results = new UpdateResults();
		
		TransactionStatus dbTransaction = txManager.getTransaction(new DefaultTransactionDefinition());
		
		//
		// Create new source and locale if needed
		//
		
		MessageTable oldMessageTbl = null;
		LocaleTable localeTbl = sourceTbl.get(sourceName);
		if (null == localeTbl)
		{
			// This is a new source and locale
			newSource = sourceRepo.save(new Source(sourceName));
			newLocale = localeRepo.save(new Locale(newSource, localePath.getLocaleString()));
			workingLocale = newLocale;
		}
		else
		{
			oldMessageTbl = localeTbl.get(localePath.getLocaleString());
			if (null == oldMessageTbl)
			{
				// This is an existing source but a new locale
				newLocale = localeRepo.save(new Locale(localeTbl.getSource(), localePath.getLocaleString()));
				workingLocale = newLocale;
			}
			else
			{
				// This is an existing source and locale.  Update locale timestamp.
				workingLocale = new Locale(oldMessageTbl.getLocale());
				workingLocale.setLastModified(Instant.now().toEpochMilli());
				workingLocale = localeRepo.save(oldMessageTbl.getLocale());
			}
		}
	
		//
		// Make a pass through the message definitions
		//
		
		for (String key : definitions.keySet())
		{
			String text = definitions.get(key);
			
			if (null != newLocale)
			{
				// new locale - all messages are new
				messageRepo.save(new Message(workingLocale, key, text));
				++results.numCreated;
			}
			else 
			{
				Message oldMessage = oldMessageTbl.get(key);
				if (null == oldMessage)
				{
					// Create a new message
					messageRepo.save(new Message(workingLocale, key, text));
					++results.numCreated;
				}
				else if ((null == text) && allowDelete)
				{
					// Delete an existing message
					messageRepo.delete(oldMessage);
					++results.numDeleted;
				}
				else if (!text.equals(oldMessage.getText()))
				{
					// Update an existing message
					Message updMessage = new Message(oldMessage);
					updMessage.setText(text);
					updMessage = messageRepo.save(updMessage);
					++results.numUpdated;
				}
				else
				{
					++results.numUnchanged;
				}
			}
		}
		
		if ((0 == results.numCreated) && (0 == results.numUpdated) && (0 == results.numDeleted))
		{
			txManager.rollback(dbTransaction);
			return results.ok("No updates were needed.");
		}
		
		workingLocale.setModified();
		localeRepo.save(workingLocale);
		
		txManager.commit(dbTransaction);
		return results.ok("Update completed successfully.");			
	}
	
	private void replaceMessageSetFromDb(String sourceName, LocalePath localePath) throws InconsistentDataException
	{
		Source source = sourceRepo.findOneByName(sourceName);
		Locale locale = localeRepo.findOneBySourceAndTag(source, localePath.getLocaleString());
		initializeLocaleFromDb(source, locale);
	}
	
	synchronized Set<LocalizedMessageSet> dump()
	{
		Set<LocalizedMessageSet> result = new HashSet<LocalizedMessageSet>();
		
		for (String source : sourceTbl.listSources())
		{
			LocaleTable localeTbl = sourceTbl.get(source);
			for (String locale : localeTbl.listLocales())
			{
				LocalizedMessageSet messageSet = retrieveMessageSet(source, new LocalePath(locale));
				result.add(messageSet);
			}
		}
		
		return result;
	}
	
	Object dumpDatabaseTable(String table)
	{
		if (table.equals("source"))
		{
			return sourceRepo.findAll();
		}
		else if (table.equals("locale"))
		{
			return localeRepo.findAll();
		}
		else if (table.equals("message"))
		{
			return messageRepo.findAll();
		}
		else
		{
			return "Unknown table.";
		}
	}
	
	private LocalizedMessageSet getMessageSet(String sourceName, LocalePath localePath)
	{
		LocalizedMessageSet result = new LocalizedMessageSet();
		result.messages = new HashSet<LocalizedMessage>();
		
		LocaleTable localeTbl = sourceTbl.get(sourceName);
		if (null != localeTbl)
		{
			MessageTable messageTbl = localeTbl.get(localePath.getLocaleString());
			if (null != messageTbl)
			{
				result.lastModified = messageTbl.getLocale().getLastModified();
				for (String key : messageTbl.getKeys())
				{
					Message message = messageTbl.get(key);
					LocalizedMessage lm = new LocalizedMessage();
					lm.key = key;
					lm.text = message.getText();
					result.messages.add(lm);
				}
			}
		}
		
		return result;
	}
	
	private LocalizedMessage resolveMessage(String sourceName, String key, LocalePath localePath)
	{
		LocaleTable localeTbl = sourceTbl.get(sourceName);
		if (null != localeTbl)
		{
			Iterator<String> lsi = localePath.iterator();
			while (lsi.hasNext())
			{
				MessageTable messageTbl = localeTbl.get(lsi.next());
				if (null != messageTbl)
				{
					Message msg = messageTbl.get(key);
					if (null != msg)
					{
						LocalizedMessage result = new LocalizedMessage();
						result.key = key;
						result.text = msg.getText();
						return result;
					}
				}
			}
		}
		
		LocalizedMessage oops = new LocalizedMessage();
		oops.key = key;
		oops.text = String.format("[[Undefined message %s.%s in locale %s]]", sourceName, key, localePath.getBaseLocale());
		return oops;
	}
	
	private LocalizedMessageSet resolveMessageSet(String sourceName, LocalePath localePath)
	{
		LocalizedMessageSet result = new LocalizedMessageSet();
		result.messages = new HashSet<LocalizedMessage>();
		
		Set<String> keySet = getSourceKeys(sourceName);
		
		for (String key : keySet)
		{
			LocalizedMessage msg = resolveMessage(sourceName, key, localePath);
			result.messages.add(msg);
		}
		
		return result;
	}
	
	private Set<String> getSourceKeys(String sourceName)
	{
		Set<String> result = new HashSet<String>();
		
		LocaleTable localeTbl = sourceTbl.get(sourceName);
		if (null != localeTbl)
		{	
			for (String tag : localeTbl.listBaseLocales())
			{
				MessageTable messageTbl = localeTbl.get(tag);
				result.addAll(messageTbl.getKeys());
			}
		}
		
		return result;
	}

}
