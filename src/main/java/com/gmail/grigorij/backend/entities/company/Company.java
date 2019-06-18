package com.gmail.grigorij.backend.entities.company;

import com.gmail.grigorij.backend.entities.user.Address;
import com.gmail.grigorij.backend.entities.user.Person;

import javax.persistence.*;


@Entity
@Table(name = "companies")
@NamedQueries({
		@NamedQuery(name="Company.findCompanyById",
				query="SELECT company FROM Company company WHERE company.id = :company_id"),
		@NamedQuery(name="Company.getAllCompanies",
				query="SELECT company FROM Company company ORDER BY company.companyName ASC")
})
public class Company extends Person {

	@Column(name = "name")
	private String companyName;

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

	public static Company getEmptyCompany() {
		Company company = new Company();
		company.setCompanyName("");
		company.setCompanyVAT("");
		company.setDeleted(false);
		company.setFirstName("");
		company.setLastName("");
		company.setEmail("");

		company.setAddress(Address.getEmptyAddress());

		return company;
	}
}
