package com.aldeamo.mobopervalid.enumerator;

public enum ValidationGsmStatusEnum {
	VALID_GSM(0, "Número valido"),
	VALID_GSM_PORTED(1, "Número valido portado"),
	PENDING(2, "Esperando la validación completa"),
	NO_OPERATOR_MATCH(3, "El número no coincide con ningún operador"),
	INVALID_FORMAT(4, "Número inválido");
	
	private int status;
	private String description;
	
	ValidationGsmStatusEnum(int code, String description){
		this.status = code;
		this.description = description;
	}

	public int getStatus() {
		return status;
	}

	public String getDescription() {
		return description;
	}
}
