package com.aldeamo.mobopervalid.entity.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.Gson;

@Entity
@Table(name = "Country", schema = "Core")
public class Country {

	@Id
	@GeneratedValue
	private int id;

	private String name;
	private String description;
	private int status;
	private String currency;
	@Column(name = "timeZone")
	private String timeZone;
	@Column(name = "languageCode")
	private String languageCode;
	@Column(name = "callCode")
	private int callCode;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public String getLanguageCode() {
		return languageCode;
	}
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	public int getCallCode() {
		return callCode;
	}
	public void setCallCode(int callCode) {
		this.callCode = callCode;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);	
	}
}
