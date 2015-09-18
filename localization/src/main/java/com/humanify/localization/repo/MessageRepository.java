package com.humanify.localization.repo;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.humanify.localization.entity.Locale;
import com.humanify.localization.entity.Message;

@Repository
public interface MessageRepository extends CrudRepository<Message, String>
{
	
	public Set<Message> findByLocale(Locale locale);

}
