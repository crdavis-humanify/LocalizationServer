package com.humanify.localization.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.humanify.localization.entity.Source;

@Repository
public interface SourceRepository extends CrudRepository<Source, Long>
{
	Source findOneByName(String name);
}
