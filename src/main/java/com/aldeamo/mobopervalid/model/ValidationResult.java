package com.aldeamo.mobopervalid.model;

import java.util.List;

import com.google.gson.Gson;

public class ValidationResult {
	private int totalValid;
	private int totalInvalid;
	private int totalNull;
	private int totalEmpty;
	private int totalPorted;
	private List<ValidationDetail> validList;
	private List<ValidationDetail> invalidList;
	
	@Override
	public String toString() {
		return new Gson().toJson(this);	
	}
	
	public void setTotalNull(int totalNull) {
		this.totalNull = totalNull;
	}
	public int getTotalEmpty() {
		return totalEmpty;
	}
	public int getTotalPorted() {
		return totalPorted;
	}
	public int getTotalInvalid() {
		return totalInvalid;
	}
	public void setTotalPorted(int totalPorted) {
		this.totalPorted = totalPorted;
	}
	public List<ValidationDetail> getValidList() {
		return validList;
	}
	public void setTotalValid(int totalValid) {
		this.totalValid = totalValid;
	}
	public void setValidList(List<ValidationDetail> validList) {
		this.validList = validList;
	}
	public void setTotalEmpty(int totalEmpty) {
		this.totalEmpty = totalEmpty;
	}
	public List<ValidationDetail> getInvalidList() {
		return invalidList;
	}
	public int getTotalValid() {
		return totalValid;
	}
	public int getTotalNull() {
		return totalNull;
	}
	public void setInvalidList(List<ValidationDetail> invalidList) {
		this.invalidList = invalidList;
	}
	public void setTotalInvalid(int totalInvalid) {
		this.totalInvalid = totalInvalid;
	}
}
