package com.aldeamo.mobopervalid.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class TransactionId {

	public static final String STRING_TRANSACTION_ID = "transactionId";
	public static final String KEY_LAND = "land";
	public static final String VALUE_DEFAULT_LAND = "57";

	private TransactionId() {}

	public static String generateUniqueTransactionId(String land) {

		SimpleDateFormat sdf = new SimpleDateFormat("MMddHH");
		sdf.setTimeZone(TimeZone.getTimeZone("America/Bogota"));

		StringBuilder strTransaction = new StringBuilder();
		strTransaction.append(UUID.randomUUID().toString()).append("T").append(sdf.format(new Date()));
		if (land != null && !land.trim().isEmpty()) {
			strTransaction.append("L").append(land.toLowerCase());
		}

		return strTransaction.toString();
	}

	public static String getTransactionId(String txId, String land) {
		if (!txId.trim().isEmpty()) {
			return txId;
		} else {
			return generateUniqueTransactionId(land);
		}
	}
}
