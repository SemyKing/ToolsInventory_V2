package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.ui.utils.UIUtils;

import javax.persistence.NoResultException;
import java.util.ArrayList;
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



	private List<Company> companiesList = new ArrayList<>();

	private void populateCompaniesList() {
		if (companiesList.size() <= 0) {
			try {
				companiesList = DatabaseManager.getInstance().createEntityManager().createNamedQuery("Company.getAllCompanies", Company.class)
						.getResultList();
			} catch (NoResultException nre) {
				nre.printStackTrace();
			}
		}
	}


	public Company findCompanyById(long id) {
		populateCompaniesList();

		for (Company company : companiesList) {
			if (company.getId() == id) {
				return company;
			}
		}

		Company company;
		try {
			company = (Company) DatabaseManager.getInstance().createEntityManager().createNamedQuery("Company.findCompanyById")
					.setParameter("company_id", id)
					.getSingleResult();
		} catch (NoResultException nre) {
			company = null;
		}
		return company;
	}


	public List<Company> getAllCompanies() {
		populateCompaniesList();
		return new ArrayList<>(companiesList);
	}


	public boolean insert(Company company) {
		System.out.println();
		System.out.println("CompanyFacade -> insert");

		if (company == null)
			return false;

		try {
			DatabaseManager.getInstance().insert(company);
			companiesList.add(company);
		} catch (Exception e) {
			System.out.println("Company INSERT fail");
			e.printStackTrace();
			return false;
		}

		System.out.println("Company INSERT successful");
		return true;
	}


	public boolean update(Company company) {
		System.out.println();
		System.out.println("CompanyFacade -> update");

		if (company == null)
			return false;

		Company companyInDatabase = null;
		int companyIndex = -1;

		if (company.getId() != null) {
//			companyInDatabase = DatabaseManager.getInstance().find(Company.class, company.getId());

			for (int i = 0; i < companiesList.size(); i++) {
				if (companiesList.get(i).getId().equals(company.getId())) {
					companyInDatabase = companiesList.get(i);
					companyIndex = i;
					break;
				}
			}
		}

		System.out.println("companyInDatabase: " + companyInDatabase);

		try {
			if (companyInDatabase == null) {
				return insert(company);
			} else {
				DatabaseManager.getInstance().update(company);
				companiesList.set(companyIndex, company);
			}
		} catch (Exception e) {
			System.out.println("Company UPDATE fail");
			e.printStackTrace();
			return false;
		}

		System.out.println("Company UPDATE successful");
		return true;
	}

	public boolean remove(Company company) {
		System.out.println();
		System.out.println("CompanyFacade -> remove");

		if (company == null)
			return false;

		int companyIndex = -1;
		Company companyInDatabase = null;

		for (int i = 0; i < companiesList.size(); i++) {
			if (companiesList.get(i).getId().equals(company.getId())) {
				companyInDatabase = companiesList.get(i);
				companyIndex = i;
				break;
			}
		}

//		Company companyInDatabase = DatabaseManager.getInstance().find(Company.class, company.getId());
		System.out.println("companyInDatabase: " + companyInDatabase);

		try {
			if (companyInDatabase != null) {
				DatabaseManager.getInstance().remove(company);
				companiesList.remove(companyIndex);
			} else {
				System.out.println("Company not found: " + company);
				return false;
			}
		} catch (Exception e) {
			System.out.println("Company REMOVE fail");
			e.printStackTrace();
			return false;
		}

		System.out.println("Company REMOVE successful");
		return true;
	}
}
