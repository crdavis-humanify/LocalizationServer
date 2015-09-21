package com.humanify.localization.repo;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.humanify.localization.entity.Locale;
import com.humanify.localization.entity.Source;

@Repository
public interface LocaleRepository extends CrudRepository<Locale, Long>
{
	
	public Set<Locale> findBySource(Source source);	
	public Locale findOneBySourceAndTag(Source source, String tag);

}
