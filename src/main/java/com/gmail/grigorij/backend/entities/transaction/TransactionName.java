package com.gmail.grigorij.backend.entities.transaction;

import com.gmail.grigorij.backend.enums.transactions.TransactionTarget;
import com.gmail.grigorij.backend.enums.transactions.TransactionType;

//TODO: TEMPORARY SOLUTION

public class TransactionName {

	public static String getTransactionFullName(TransactionType operation, TransactionTarget target) {
		String ed = "ed";
		String hasHave = (target.equals(TransactionTarget.USER_ACCESS_RIGHTS)) ? "have been " : "has been ";

		if (operation.equals(TransactionType.LOGIN)) {
			return target.getStringValue() + " Logged In";
		}

		if (operation.equals(TransactionType.LOGOUT)) {
			return target.getStringValue() + " Logged Out";
		}

		return target.getStringValue() + " " + hasHave + operation.getStringValue().toLowerCase() + ed;
	}

	public static String getTransactionShortName(TransactionType operation, TransactionTarget target) {
		if (operation.equals(TransactionType.LOGIN)) {
			return operation.getStringValue();
		}

		if (operation.equals(TransactionType.LOGOUT)) {
			return operation.getStringValue();
		}

		return operation.getStringValue() + " " + target.getStringValue();
	}
}
