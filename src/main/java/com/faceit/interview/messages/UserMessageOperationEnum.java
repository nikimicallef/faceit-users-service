package com.faceit.interview.messages;

public enum UserMessageOperationEnum {
	CREATE("CREATE"),
	UPDATE("UPDATE"),
	DELETE("DELETE");

	private final String operation;

	UserMessageOperationEnum(String operation) {
		this.operation = operation;
	}

	public String getOperation() {
		return operation;
	}
}
