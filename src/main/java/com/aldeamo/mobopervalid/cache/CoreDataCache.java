package com.aldeamo.mobopervalid.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.aldeamo.mobopervalid.entity.core.Country;
import com.aldeamo.mobopervalid.entity.core.Operator;
import com.aldeamo.mobopervalid.repository.core.ICountry;
import com.aldeamo.mobopervalid.repository.core.IOperator;


@Component
public class CoreDataCache {
	@Autowired
	ICountry iCountry;
	@Autowired
	IOperator iOperator;
	
	@Cacheable("findFirstCountryByCallCode")
	public Country findCountryByCallCode(int callCode) {
		return iCountry.findFirstByCallCode(callCode);
	}
	
	@CacheEvict("findFirstCountryByCallCode")
	public void relaseCountryByCallCode(int callCode) {
		// Intentionally blank. Clear Core country cache by call code
	}
	
	@CacheEvict(cacheNames = "findFirstCountryByCallCode", allEntries = true)
	public void relaseAllCountries() {
		// Intentionally blank. Clear all Core country cache
	}
	
	
	@Cacheable("findActiveOperatorsByCountryId")
	public List<Operator> findActiveOperatorsByCountryId(int countryId) {
		return iOperator.findByCountryIdAndStatus(countryId, 1);
	}
	
	@CacheEvict("findActiveOperatorsByCountryId")
	public void relaseOperatorsByCountryId(int countryId) {
		// Intentionally blank. Clear Core operator list cache by country ID
	}
	
	@CacheEvict(cacheNames = "findActiveOperatorsByCountryId", allEntries = true)
	public void relaseAllOperatorsLists() {
		// Intentionally blank. Clear all Core operators list cache
	}
	
	@Cacheable("findOperatorById")
	public Operator findOperatorById(int operatorId) {
		return iOperator.findById(operatorId);
	}
	
	@CacheEvict("findOperatorById")
	public void relaseOperatorsById(int countryId) {
		// Intentionally blank. Clear Core operator list cache by ID
	}
	
	@CacheEvict(cacheNames = "findOperatorById", allEntries = true)
	public void relaseAllOperatorsByIdLists() {
		// Intentionally blank. Clear all Core by ID operators list cache
	}
}
