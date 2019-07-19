package com.gmail.grigorij.backend.entities.transaction;

import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.inventory.HierarchyType;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.backend.entities.user.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table( name = "transactions")
public class Transaction extends EntityPojo {

	@Enumerated( EnumType.STRING )
	private TransactionOperation transactionOperation;

	@Enumerated( EnumType.STRING )
	private TransactionTarget transactionTarget;

	@Temporal( TemporalType.TIMESTAMP )
	private Date date;

	@OneToOne
	private User whoDid;


	@OneToOne
	private User sourceUser;

	@OneToOne
	private User destinationUser;

	@OneToOne
	private Company company;

	@OneToOne
	private InventoryEntity tool;

	private String message;


	public Transaction() {}

	public TransactionOperation getTransactionOperation() {
		return transactionOperation;
	}

	public void setTransactionOperation(TransactionOperation transactionOperation) {
		this.transactionOperation = transactionOperation;
	}

	public TransactionTarget getTransactionTarget() {
		return transactionTarget;
	}

	public void setTransactionTarget(TransactionTarget transactionTarget) {
		this.transactionTarget = transactionTarget;
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

	public User getSourceUser() {
		return sourceUser;
	}

	public void setSourceUser(User sourceUser) {
		this.sourceUser = sourceUser;
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

	public InventoryEntity getTool() {
		return tool;
	}

	public void setTool(InventoryEntity tool) {
		this.tool = tool;

		if (tool.getHierarchyType() == null) {
			System.err.println("Unknown HierarchyType of inventory: " + tool.getName() + " in Transaction");
		} else {
			if (tool.getHierarchyType().equals(HierarchyType.CATEGORY)) {
				this.setTransactionTarget(TransactionTarget.CATEGORY);
			} else if (tool.getHierarchyType().equals(HierarchyType.TOOL)) {
				this.setTransactionTarget(TransactionTarget.TOOL);
			} else {
				this.setTransactionTarget(null);
			}
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
