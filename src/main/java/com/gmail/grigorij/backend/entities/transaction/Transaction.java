package com.gmail.grigorij.backend.entities.transaction;

import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.transactions.TransactionTarget;
import com.gmail.grigorij.backend.enums.transactions.TransactionType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table( name = "transactions")
@NamedQueries({

		@NamedQuery(name="getAllTransactions",
				query="SELECT transaction FROM Transaction transaction"),

		@NamedQuery(name="getAllTransactionsInCompany",
				query="SELECT transaction FROM Transaction transaction WHERE" +
						" Transaction.company IS NOT NULL AND" +
						" Transaction.company.id = :company_id_var")
})
public class Transaction extends EntityPojo {

	@Enumerated( EnumType.STRING )
	private TransactionType transactionOperation;

	@Enumerated( EnumType.STRING )
	private TransactionTarget transactionTarget;

	private Date date;

	private String fullName;

	private String shortName;

	private User whoDid;

	private User destinationUser;

	private Company company;

	private InventoryItem inventoryEntity;


	public Transaction() {
		this.date = new Date();
	}



	public TransactionType getTransactionOperation() {
		return transactionOperation;
	}

	public void setTransactionOperation(TransactionType transactionOperation) {
		this.transactionOperation = transactionOperation;

		if (this.transactionOperation != null) {
			if (transactionTarget != null) {
				setNames();
			}
		}
	}

	public TransactionTarget getTransactionTarget() {
		return transactionTarget;
	}

	public void setTransactionTarget(TransactionTarget transactionTarget) {
		this.transactionTarget = transactionTarget;

		if (this.transactionTarget != null) {
			if (transactionOperation != null) {
				setNames();
			}
		}
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public User getWhoDid() {
		return whoDid;
	}

	public void setWhoDid(User whoDid) {
		this.whoDid = whoDid;
	}

	public User getDestinationUser() {
		return destinationUser;
	}

	public void setDestinationUser(User destinationUser) {
		this.destinationUser = destinationUser;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public InventoryItem getInventoryEntity() {
		return inventoryEntity;
	}

	public void setInventoryEntity(InventoryItem inventoryEntity) {
		this.inventoryEntity = inventoryEntity;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}


	private void setNames() {
		this.fullName = TransactionName.getTransactionFullName(transactionOperation, transactionTarget);
		this.shortName = TransactionName.getTransactionShortName(transactionOperation, transactionTarget);
	}
}
