package com.gmail.grigorij.backend.database.entities;

import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.*;


@Entity
@Table(name = "categories")
@NamedQueries({
		@NamedQuery(name = Category.QUERY_ALL,
				query = "SELECT category FROM Category category"),

		@NamedQuery(name = Category.QUERY_ALL_BY_COMPANY_ID,
				query = "SELECT category FROM Category category WHERE" +
						" category.company.id = :" + ProjectConstants.ID_VAR),
})
public class Category extends EntityPojo {

	public static final String QUERY_ALL = "get_all_categories";
	public static final String QUERY_ALL_BY_COMPANY_ID = "get_all_categories_by_company_id";


	private String name = "";

	@OneToOne
	private Company company;


	public Category() {}

	public Category(Category other) {
		this.name = other.name;
		this.company = other.company;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	public String getCompanyString() {
		String company = "";

		if (this.company != null) {
			company = this.company.getName();
		}

		return company;
	}

}
