package com.aldeamo.mobopervalid.repository.core;

import org.springframework.data.repository.CrudRepository;

import com.aldeamo.mobopervalid.entity.core.Country;

public interface ICountry  extends CrudRepository<Country, Integer>{

	Country findFirstByCallCode(int callCode);
}
