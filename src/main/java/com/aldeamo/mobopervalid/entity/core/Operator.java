package com.aldeamo.mobopervalid.entity.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.Gson;

@Entity
@Table(name="Operator", schema="Core")
public class Operator {
	
	@Id
	@GeneratedValue
	private int id;
	
	private String name;
	private String description;
	private String code;
	private int status;
	@Column(name="gsmRegExIn")
	private String gsmRegExIn;
	@Column(name="gsmRegExOut")
	private String gsmRegExOut;
	@Column(name="shortCodeRegExIn")
	private String shortCodeRegExIn;
	@Column(name="shortCoderegExOut")
	private String shortCoderegExOut;
	@Column(name="Country_id")
	private int countryId;
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getGsmRegExIn() {
		return gsmRegExIn;
	}
	public void setGsmRegExIn(String gsmRegExIn) {
		this.gsmRegExIn = gsmRegExIn;
	}
	public String getGsmRegExOut() {
		return gsmRegExOut;
	}
	public void setGsmRegExOut(String gsmRegExOut) {
		this.gsmRegExOut = gsmRegExOut;
	}
	public String getShortCodeRegExIn() {
		return shortCodeRegExIn;
	}
	public void setShortCodeRegExIn(String shortCodeRegExIn) {
		this.shortCodeRegExIn = shortCodeRegExIn;
	}
	public String getShortCoderegExOut() {
		return shortCoderegExOut;
	}
	public void setShortCoderegExOut(String shortCoderegExOut) {
		this.shortCoderegExOut = shortCoderegExOut;
	}
	public int getCountryId() {
		return countryId;
	}
	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);	
	}
	
}
