package com.aldeamo.mobopervalid.model;

import com.google.gson.Gson;

public class ValidationDetail {
	private String gsm;
	private int status;
	private int operatorId;
	private String operatorName;
	
	@Override
	public String toString() {
		return new Gson().toJson(this);	
	}
	
	public String getGsm() {
		return gsm;
	}
	public void setGsm(String gsm) {
		this.gsm = gsm;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
}
