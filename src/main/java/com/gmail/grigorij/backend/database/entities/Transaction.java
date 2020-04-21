package com.gmail.grigorij.backend.database.entities;

import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.utils.DateConverter;
import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table( name = "transactions")
@NamedQueries({

		@NamedQuery(name=Transaction.QUERY_ALL,
				query="SELECT transaction FROM Transaction transaction"),

		@NamedQuery(name=Transaction.QUERY_ALL_BY_COMPANY,
				query="SELECT transaction FROM Transaction transaction WHERE" +
						" Transaction.company.id = :" + ProjectConstants.ID_VAR)
})
public class Transaction extends EntityPojo {

	public static final String QUERY_ALL = "get_all_transactions";
	public static final String QUERY_ALL_BY_COMPANY = "get_all_transactions_by_company";


	private Date date;

	@Enumerated( EnumType.STRING )
	private Operation operation;

	@Enumerated( EnumType.STRING )
	private OperationTarget operationTarget1;

	@Enumerated( EnumType.STRING )
	private OperationTarget operationTarget2;

	private User user;

	private Company company;

	private List<String> changes = new ArrayList<>();

	private String targetDetails = "";


	public Transaction() {
		this.date = new Date();
	}


	public Date getDate() {
		return date;
	}
	private void setDate(Date date) {
		this.date = date;
	}

	public Operation getOperation() {
		return operation;
	}
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public OperationTarget getOperationTarget1() {
		return operationTarget1;
	}
	public void setOperationTarget1(OperationTarget operationTarget1) {
		this.operationTarget1 = operationTarget1;
	}

	public OperationTarget getOperationTarget2() {
		return operationTarget2;
	}
	public void setOperationTarget2(OperationTarget operationTarget2) {
		this.operationTarget2 = operationTarget2;
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	public List<String> getChanges() {
		return changes;
	}
	public void setChanges(List<String> changes) {
		this.changes = changes;
	}
	public void addChange(String change) {
		changes.add(change);
	}

	public String getTargetDetails() {
		return targetDetails;
	}
	public void setTargetDetails(String targetDetails) {
		this.targetDetails = targetDetails;
	}

	public String getDescription(boolean withTargetDetails) {
		StringBuilder stringBuilder = new StringBuilder();

		if (operation != null) {
			stringBuilder.append(operation.getName());
			stringBuilder.append(" ");
		}
		if (operationTarget1 != null) {
			stringBuilder.append(operationTarget1.getName());
			stringBuilder.append(" ");
		}
		if (operationTarget2 != null) {
			stringBuilder.append(operationTarget2.getName());
			stringBuilder.append(" ");
		}

		if (withTargetDetails) {
			stringBuilder.append(targetDetails);
		}


		return stringBuilder.toString();
	}

	public String getDateWithTimeString() {
		try {
			if (date == null) {
				return "";
			} else {
				return DateConverter.dateToStringWithTime(date);
			}
		} catch (Exception e) {
			return "";
		}
	}
}
