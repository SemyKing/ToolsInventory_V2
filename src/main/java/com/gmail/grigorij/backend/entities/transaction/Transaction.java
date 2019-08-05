package com.gmail.grigorij.backend.entities.transaction;

import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.enums.InventoryHierarchyType;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.OperationTarget;
import com.gmail.grigorij.backend.enums.OperationType;

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
	private OperationType transactionOperation;

	@Enumerated( EnumType.STRING )
	private OperationTarget transactionTarget;

	@Temporal( TemporalType.TIMESTAMP )
	private Date date;

	private String fullName;

	private String shortName;

//	@OneToMany
	private User whoDid;

//	@OneToOne
	private User destinationUser;

//	@OneToOne
	private Company company;

//	@OneToOne
	private InventoryEntity inventoryEntity;


	public Transaction() {
		this.date = new Date();
	}



	public OperationType getTransactionOperation() {
		return transactionOperation;
	}

	public void setTransactionOperation(OperationType transactionOperation) {
		this.transactionOperation = transactionOperation;

		if (this.transactionOperation != null) {
			if (transactionTarget != null) {
				setNames();
			}
		}
	}

	public OperationTarget getTransactionTarget() {
		return transactionTarget;
	}

	public void setTransactionTarget(OperationTarget transactionTarget) {
		this.transactionTarget = transactionTarget;

		if (this.transactionTarget != null) {
			if (transactionOperation != null) {
				setNames();
			}
		}
	}

	private void setNames() {
		this.fullName = TransactionName.getTransactionFullName(transactionOperation, transactionTarget);
		this.shortName = TransactionName.getTransactionShortName(transactionOperation, transactionTarget);
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

	public InventoryEntity getInventoryEntity() {
		return inventoryEntity;
	}

	public void setInventoryEntity(InventoryEntity inventoryEntity) {
		this.inventoryEntity = inventoryEntity;

		if (inventoryEntity.getInventoryHierarchyType() == null) {
			System.err.println("NULL HierarchyType of InventoryEntity: " + inventoryEntity.getName() + " in Transaction");
		} else {
			if (inventoryEntity.getInventoryHierarchyType().equals(InventoryHierarchyType.CATEGORY)) {
				this.setTransactionTarget(OperationTarget.CATEGORY);
			} else if (inventoryEntity.getInventoryHierarchyType().equals(InventoryHierarchyType.TOOL)) {
				this.setTransactionTarget(OperationTarget.TOOL);
			} else {
				this.setTransactionTarget(null);
			}
		}
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
}
