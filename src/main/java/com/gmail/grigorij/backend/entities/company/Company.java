package com.gmail.grigorij.backend.entities.company;

import com.gmail.grigorij.backend.entities.EntityPojo;

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
public class Company extends EntityPojo {

	@Column(name = "name")
	private String companyName;

	public String getCompanyName() {
		return companyName;
	}
}
