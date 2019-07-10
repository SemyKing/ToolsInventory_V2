package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.transaction.Transaction;


public class TransactionFacade {

	private static TransactionFacade mInstance;
	private TransactionFacade() {}
	public static TransactionFacade getInstance() {
		if (mInstance == null) {
			mInstance = new TransactionFacade();
		}
		return mInstance;
	}


	public boolean insert(Transaction transaction) {
		System.out.println("Transaction INSERT");
		if (transaction == null)
			return false;

		try {
			DatabaseManager.getInstance().insert(transaction);
		} catch (Exception e) {
			System.out.println("Transaction INSERT fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("Transaction INSERT successful");
		return true;
	}


	public boolean update(Transaction transaction) {
		System.out.println("Transaction UPDATE");
		if (transaction == null)
			return false;

		Transaction transactionInDatabase = null;

		if (transactionInDatabase.getId() != null) {
			transactionInDatabase = DatabaseManager.getInstance().find(Transaction.class, transactionInDatabase.getId());
		}

		System.out.println("transactionInDatabase: " + transactionInDatabase);

		try {
			if (transactionInDatabase == null) {
				return insert(transaction);
			} else {
				DatabaseManager.getInstance().update(transaction);
			}
		} catch (Exception e) {
			System.out.println("Transaction UPDATE fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("Transaction UPDATE successful");
		return true;
	}


	public boolean remove(Transaction transaction) {
		System.out.println("Transaction REMOVE");
		if (transaction == null)
			return false;

		Transaction transactionInDatabase = DatabaseManager.getInstance().find(Transaction.class, transaction.getId());
		System.out.println("transactionInDatabase: " + transactionInDatabase);

		try {
			if (transactionInDatabase != null) {
				DatabaseManager.getInstance().remove(transaction);
			}
		} catch (Exception e) {
			System.out.println("Transaction REMOVE fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("Transaction REMOVE successful");
		return true;
	}
}
