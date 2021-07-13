package com.aldeamo.mobopervalid.dto;

import java.util.HashMap;
import java.util.Map;

import com.aldeamo.mobopervalid.enumerator.ResponseStatusEnum;
import com.google.gson.Gson;

public class Response {

	private int status;
	private String reason;
	private Map<String, Object> data;

	public Response(int status, String reason) {
		this.status = status;
		this.reason = reason;
		this.data = new HashMap<>();
	}
	
	public Response(ResponseStatusEnum responseStatusEnum) {
		this.status = responseStatusEnum.getCode();
		this.reason = responseStatusEnum.getDescription();
	}
	
	public Response() {
		super();
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);	
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> details) {
		this.data = details;
	}

	public void setDataPair(String key, Object value) {
		if (this.data == null) {
			this.data = new HashMap<>();
		}
		
		this.data.put(key, value);
	}

}
