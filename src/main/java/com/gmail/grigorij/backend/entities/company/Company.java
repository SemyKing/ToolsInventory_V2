package com.gmail.grigorij.backend.entities.company;

import com.gmail.grigorij.backend.entities.Address;
import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.entities.user.Person;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "companies")
@NamedQueries({
		@NamedQuery(name="Company.findCompanyInDatabaseById",
				query="SELECT company FROM Company company WHERE company.id = :company_id"),
		@NamedQuery(name="Company.listAllCompanies",
				query="SELECT company FROM Company company ORDER BY company.companyName ASC")
})
public class Company extends Person {

	@Column(name = "name")
	private String companyName;

	//Y-Tunnus
	@Column(name = "vat")
	private String companyVAT;


	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyVAT() {
		return companyVAT;
	}
	public void setCompanyVAT(String companyVAT) {
		this.companyVAT = companyVAT;
	}
}
