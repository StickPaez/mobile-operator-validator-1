package com.aldeamo.mobopervalid.repository.core;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.aldeamo.mobopervalid.entity.core.Operator;

public interface IOperator  extends CrudRepository<Operator, Integer>{

	List<Operator> findByCodeLike(String code);

	Operator findById(int operatorId);

	List<Operator> findByCountryIdAndStatus(int countryId, int status);
}
