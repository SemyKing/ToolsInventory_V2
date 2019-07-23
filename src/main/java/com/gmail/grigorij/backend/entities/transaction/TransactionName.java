package com.gmail.grigorij.backend.entities.transaction;

public class TransactionName {

	public static String getTransactionFullName(TransactionOperation operation, TransactionTarget target) {
		String name = "";
		String hasHave = (target.equals(TransactionTarget.ACCESS_RIGHTS)) ? "have" : "has";

		if (operation.equals(TransactionOperation.ADD)) {
			return target.getStringValue() + " " + hasHave + " been added";
		}

		if (operation.equals(TransactionOperation.EDIT)) {
			return target.getStringValue() + " " + hasHave + " been edited";
		}

		if (operation.equals(TransactionOperation.UPDATE)) {
			return target.getStringValue() + " " + hasHave + " been updated";
		}

		if (operation.equals(TransactionOperation.DELETE)) {
			return target.getStringValue() + " " + hasHave + " been deleted";
		}

		if (operation.equals(TransactionOperation.LOGIN)) {
			return target.getStringValue() + " Logged In";
		}

		if (operation.equals(TransactionOperation.LOGOUT)) {
			return target.getStringValue() + " Logged Out";
		}


		return name;
	}

	public static String getTransactionShortName(TransactionOperation operation, TransactionTarget target) {
		if (operation.equals(TransactionOperation.LOGIN)) {
			return operation.getStringValue();
		}

		if (operation.equals(TransactionOperation.LOGOUT)) {
			return operation.getStringValue();
		}

		return operation.getStringValue() + " " + target.getStringValue();
	}
}
