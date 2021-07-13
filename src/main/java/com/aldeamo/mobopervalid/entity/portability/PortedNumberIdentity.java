package com.aldeamo.mobopervalid.entity.portability;
import java.io.Serializable;

import com.google.gson.Gson;

public class PortedNumberIdentity implements Serializable {
	private static final long serialVersionUID = 6076499686746135998L;
	
	private Long gsm;
	private int countryId;
	
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
}