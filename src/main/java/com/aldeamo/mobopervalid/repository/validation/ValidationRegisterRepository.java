package com.aldeamo.mobopervalid.repository.validation;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.aldeamo.mobopervalid.model.ValidationRegister;

public interface ValidationRegisterRepository extends MongoRepository<ValidationRegister, String> {

}
