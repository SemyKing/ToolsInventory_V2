package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.transaction.Transaction;

import javax.persistence.NoResultException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;


public class TransactionFacade {

	private static TransactionFacade mInstance;
	private TransactionFacade() {}
	public static TransactionFacade getInstance() {
		if (mInstance == null) {
			mInstance = new TransactionFacade();
		}
		return mInstance;
	}


	private List<Transaction> getAllTransactions() {
		List<Transaction> transaction;
		try {
			transaction = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getAllTransactions", Transaction.class)
					.getResultList();
		} catch (NoResultException nre) {
			transaction = null;
		}
		return transaction;
	}


	private List<Transaction> getAllTransactionsInCompany(long companyId) {
		List<Transaction> transaction;
		try {
			transaction = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getAllTransactionsInCompany", Transaction.class)
					.setParameter("company_id_var", companyId)
					.getResultList();
		} catch (NoResultException nre) {
			transaction = null;
		}
		return transaction;
	}


	private List<Transaction> getSortedList(List<Transaction> list, LocalDate start, LocalDate end) {

		final Date startDate = Date.valueOf(start);
		final Date endDate = Date.valueOf(end.plusDays(1));

		list.removeIf((Transaction transaction) -> transaction.getDate().before(startDate));
		list.removeIf((Transaction transaction) -> transaction.getDate().after(endDate));

		list.sort(Comparator.comparing(Transaction::getDate));

		return list;
	}


	public List<Transaction> getAllTransactionsInRange(LocalDate start, LocalDate end) {
		List<Transaction> transactions = getAllTransactions();

		return getSortedList(transactions, start, end);
	}


	public List<Transaction> getAllTransactionsInRangeInCompany(LocalDate start, LocalDate end, long companyId) {
		List<Transaction> transactions = getAllTransactionsInCompany(companyId);

		return getSortedList(transactions, start, end);
	}



	public boolean insert(Transaction transaction) {
		if (transaction == null) {
			System.err.println(this.getClass().getSimpleName() + " -> INSERT NULL TRANSACTION");
			return false;
		}

		try {
			DatabaseManager.getInstance().insert(transaction);
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> TRANSACTION INSERT FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean update(Transaction transaction) {
		if (transaction == null) {
			System.err.println(this.getClass().getSimpleName() + " -> UPDATE NULL TRANSACTION");
			return false;
		}

		Transaction transactionInDatabase = null;

		if (transaction.getId() != null) {
			transactionInDatabase = DatabaseManager.getInstance().find(Transaction.class, transaction.getId());
		}

		try {
			if (transactionInDatabase == null) {
				return insert(transaction);
			} else {
				DatabaseManager.getInstance().update(transaction);
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> TRANSACTION UPDATE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean remove(Transaction transaction) {
		if (transaction == null) {
			System.err.println(this.getClass().getSimpleName() + " -> REMOVE NULL TRANSACTION");
			return false;
		}

		Transaction transactionInDatabase = null;

		if (transaction.getId() != null) {
			transactionInDatabase = DatabaseManager.getInstance().find(Transaction.class, transaction.getId());
		}

		try {
			if (transactionInDatabase != null) {
				DatabaseManager.getInstance().remove(transaction);
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> TRANSACTION REMOVE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
