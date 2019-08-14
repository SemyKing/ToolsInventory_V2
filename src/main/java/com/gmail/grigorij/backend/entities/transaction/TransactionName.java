package com.gmail.grigorij.backend.entities.transaction;

import com.gmail.grigorij.backend.enums.OperationTarget;
import com.gmail.grigorij.backend.enums.OperationType;

public class TransactionName {

	public static String getTransactionFullName(OperationType operation, OperationTarget target) {
		String name = "";
		String hasHave = (target.equals(OperationTarget.ACCESS_RIGHTS)) ? "have" : "has";

		if (operation.equals(OperationType.ADD)) {
			return target.getStringValue() + " " + hasHave + " been added";
		}

		if (operation.equals(OperationType.EDIT)) {
			return target.getStringValue() + " " + hasHave + " been edited";
		}

		if (operation.equals(OperationType.CHANGE)) {
			return target.getStringValue() + " " + hasHave + " been changed";
		}

		if (operation.equals(OperationType.UPDATE)) {
			return target.getStringValue() + " " + hasHave + " been updated";
		}

		if (operation.equals(OperationType.DELETE)) {
			return target.getStringValue() + " " + hasHave + " been deleted";
		}


		if (operation.equals(OperationType.LOGIN)) {
			return target.getStringValue() + " Logged In";
		}

		if (operation.equals(OperationType.LOGOUT)) {
			return target.getStringValue() + " Logged Out";
		}


		return name;
	}

	public static String getTransactionShortName(OperationType operation, OperationTarget target) {
		if (operation.equals(OperationType.LOGIN)) {
			return operation.getStringValue();
		}

		if (operation.equals(OperationType.LOGOUT)) {
			return operation.getStringValue();
		}

		return operation.getStringValue() + " " + target.getStringValue();
	}
}
