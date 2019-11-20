package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.EntityPojo;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.utils.ProjectConstants;

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

	public List<Company> getAllCompanies() {
		List<Company> companies;
		try {
			companies = DatabaseManager.getInstance().createEntityManager().createNamedQuery(Company.QUERY_ALL, Company.class)
					.getResultList();
		} catch (NoResultException nre) {
			companies = null;
		}
		return companies;
	}

	public List<Company> getAllActiveCompanies() {
		List<Company> companies;
		try {
			companies = DatabaseManager.getInstance().createEntityManager().createNamedQuery(Company.QUERY_ALL, Company.class)
					.getResultList();

			companies.removeIf(EntityPojo::isDeleted);
		} catch (NoResultException nre) {
			companies = null;
		}
		return companies;
	}

	public List<Company> getCompanyById(long companyId) {
		List<Company> companies = new ArrayList<>();
		Company company;
		try {
			company =  DatabaseManager.getInstance().createEntityManager().createNamedQuery(Company.QUERY_BY_ID, Company.class)
					.setParameter(ProjectConstants.ID_VAR, companyId)
					.getSingleResult();

			companies.add(company);
		} catch (NoResultException nre) {
			companies = null;
		}
		return companies;
	}

	public Company getAdministrationCompany() {
		List<User> systemAdmins = UserFacade.getInstance().getSystemAdmins();

		if (systemAdmins.size() <= 0) {
			System.err.println("!!! COULD NOT RETRIEVE SYSTEM ADMINS !!!");
			return null;
		} else {
			return systemAdmins.get(0).getCompany();
		}
	}


	public boolean insert(Company company) {
		if (company == null) {
			System.err.println(this.getClass().getSimpleName() + " -> INSERT NULL COMPANY");
			return false;
		}

		try {
			DatabaseManager.getInstance().insert(company);
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> COMPANY INSERT FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean update(Company company) {
		if (company == null) {
			System.err.println(this.getClass().getSimpleName() + " -> UPDATE NULL COMPANY");
			return false;
		}

		Company companyInDatabase = null;

		if (company.getId() != null) {
			companyInDatabase = DatabaseManager.getInstance().find(Company.class, company.getId());
		}
		try {
			if (companyInDatabase == null) {
				return insert(company);
			} else {
				DatabaseManager.getInstance().update(company);
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> COMPANY UPDATE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean remove(Company company) {
		if (company == null) {
			System.err.println(this.getClass().getSimpleName() + " -> REMOVE NULL COMPANY");
			return false;
		}

		Company companyInDatabase = null;

		if (company.getId() != null) {
			companyInDatabase = DatabaseManager.getInstance().find(Company.class, company.getId());
		}

		try {
			if (companyInDatabase != null) {
				DatabaseManager.getInstance().remove(company);
			} else {
				System.err.println(this.getClass().getSimpleName() + " -> COMPANY: '" + company.getName() + "'NOT FOUND IN DATABASE");
				return false;
			}
		} catch (Exception e) {
			System.out.println(this.getClass().getSimpleName() + " -> COMPANY REMOVE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
