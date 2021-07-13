package com.aldeamo.mobopervalid.dto;

import java.util.List;

import com.google.gson.Gson;

public class ValidationRequest {
	private String land;
	private int userId;
	private List<String> gsmList;
	
	@Override
	public String toString() {
		return new Gson().toJson(this);	
	}
	
	public String getLand() {
		return land;
	}
	public void setLand(String land) {
		this.land = land;
	}
	public List<String> getGsmList() {
		return gsmList;
	}
	public void setGsmList(List<String> gsmList) {
		this.gsmList = gsmList;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
