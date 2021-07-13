package com.aldeamo.mobopervalid.entity.portability;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.google.gson.Gson;

@Entity
@Table(name = "PortedNumber", schema = "PortabilityCore")
@IdClass(PortedNumberIdentity.class)
public class PortedNumber {
	
	@Id
	@Column(name = "gsm")
	private Long gsm;
	@Id
	@Column(name = "Country_id")
	private int countryId;
	@Column(name = "Operator_id")
	private int operatorId;
	@Column(name = "dateCreated")
	private Date dateCreated;
	@Column(name = "modifiedBy")
	private int modifiedBy;
	@Column(name = "dateModified")
	private Date dateModified;
	@Column(name = "extraInfo")
	private String extraInfo;
	
	@Override
	public String toString() {
		return new Gson().toJson(this);	
	}
	
	public Long getGsm() {
		return gsm;
	}
	public void setGsm(Long gsm) {
		this.gsm = gsm;
	}
	public int getCountryId() {
		return countryId;
	}
	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}
	public int getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public int getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(int modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Date getDateModified() {
		return dateModified;
	}
	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}
	public String getExtraInfo() {
		return extraInfo;
	}
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
}
