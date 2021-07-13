package com.aldeamo.mobopervalid.repository.portability;

import org.springframework.data.repository.CrudRepository;

import com.aldeamo.mobopervalid.entity.portability.PortedNumber;
import com.aldeamo.mobopervalid.entity.portability.PortedNumberIdentity;


public interface IPortedNumber  extends CrudRepository<PortedNumber, PortedNumberIdentity>{

	PortedNumber findFirstByGsmAndCountryId(Long gsm, int countryId);
}