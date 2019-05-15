package com.gmail.grigorij.backend.database.Facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.company.Company;

import java.util.List;

public class CompanyFacade {

	private static CompanyFacade mInstance;
	private CompanyFacade() {}
	public static CompanyFacade getInstance() {
		if (mInstance == null) {
			mInstance = new CompanyFacade();
		}
		return mInstance;
	}


	public Company findCompanyInDatabaseById(long id) {
		Company company = (Company) DatabaseManager.getInstance().createEntityManager().createNamedQuery("Company.findCompanyInDatabaseById")
				.setParameter("company_id", id)
				.getSingleResult();
		return company;
	}

	public List<Company> listAllCompanies() {
		List<Company> companies = DatabaseManager.getInstance().createEntityManager().createNamedQuery("Company.listAllCompanies", Company.class)
				.getResultList();

		return companies;
	}

}
