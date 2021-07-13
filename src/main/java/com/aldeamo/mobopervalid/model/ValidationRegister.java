package com.aldeamo.mobopervalid.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.aldeamo.mobopervalid.dto.ValidationRequest;
import com.google.gson.Gson;

@Document(collection = "validationRegister")
public class ValidationRegister {
	@Id
	private String id;
	private LocalDateTime dateStored;
	private ValidationRequest request;
	private ValidationResult result;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public LocalDateTime getDateStored() {
		return dateStored;
	}
	public void setDateStored(LocalDateTime dateStored) {
		this.dateStored = dateStored;
	}
	public ValidationRequest getRequest() {
		return request;
	}
	public void setRequest(ValidationRequest request) {
		this.request = request;
	}
	public ValidationResult getResult() {
		return result;
	}
	public void setResult(ValidationResult result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);	
	}
}
