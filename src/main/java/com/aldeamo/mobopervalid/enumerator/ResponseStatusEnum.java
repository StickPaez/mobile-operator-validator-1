package com.aldeamo.mobopervalid.enumerator;

public enum ResponseStatusEnum {
	SUCCESS(1,"Request Received"),
	UNAUTHORIZED(-1,"Authentication Error"),
	INVALID_ROUTE(-2,"No Route Configured"),
	INVALID_NUMBER(-3,"Invalid Mobile Number"),
	INSUFFICIENT_BALANCE(-4,"Insufficient Balance"),
	TRANSACTION_ERROR(-5,"Transaction Error"),
	INVALID_URL(-6,"Invalid URL to Shorten"),
	INVALID_DATE_TO_SEND(-7,"Invalid Date to Send"),
	BAD_REQUEST(-8,"Bad Request"),
	USER_BLOCKED_FOR_ATTEMPTS(-9,"User Blocked by Failed Attempts"),
	INVALID_COUNTRY(-10,"Country Land Code Not Valid"),
	INVALID_MESSAGE(-11,"Message body is invalid"),
	INVALID_TRANSACTION_ID(-12,"Invalid transaction ID"),
	ERROR_PROCESS_FILE(-13,"Error process file"),
	ERROR_OPEN_FILE(-14,"Error Open file - file or directory do not exist"),
	INVALID_FILE(-15,"Invalid file"),
	FTP_CONNECTION_ERROR(-16,"Could not connect to FTP host"),
	FTP_CREDENTIALS_ERROR(-17,"Error in FTP credentials"),
	FTP_DATA_TYPE_INVALID(-18,"FTP data type invalid"),
	EMPTY_RESPONSE(-19, "No data found by requested parameters"),
	INVALID_VALIDATION_TYPE(-20, "Invalid Validation Type"),
	NO_DOWNLOAD_URL(-21, "No download URL previously generated"),
	SERVICE_ERROR(-22, "Requested service internal error"),
	INVALID_DOMAIN(-23, "Invalid domain sent"),
	DB_INTEGRITY_VIOLATION(-24, "DB integrity violation for request sent"),
	PERMISSION_DENIED(-25,"You do not have sufficient permissions to perform this action");
	
	private int code;
	private String description;
	
	ResponseStatusEnum(int code, String description){
		this.code = code;
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
