package com.gmail.grigorij.backend;

import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.entities.embeddable.Person;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;

import java.util.List;

/**
 * FOR PRODUCTION SINGLE TIME USE
 */
public class AdminDataGenerator {

	private static boolean generated = false;


	public AdminDataGenerator() {
		if (!generated) {
			generateCompany();

			generateUser();

			generated = true;
		}
	}


	private void generateCompany() {
		Company administrationCompany = new Company();
		administrationCompany.setName("System Administration");
		administrationCompany.setVat("0123456789");

		Person contactPerson = new Person();

		contactPerson.setFirstName("");
		contactPerson.setLastName("");
		contactPerson.setEmail("");

		Location companyAddress = new Location();
		companyAddress.setName("Main Office");
		companyAddress.setAddressLine1("");
		companyAddress.setCountry("");
		companyAddress.setCity("");
		companyAddress.setPostcode("");

		administrationCompany.setAddress(companyAddress);
		administrationCompany.setContactPerson(contactPerson);
		administrationCompany.setAdditionalInfo("ADMINISTRATION COMPANY FOR ADMINS ONLY");

		CompanyFacade.getInstance().insert(administrationCompany);
	}

	private void generateUser() {
		List<Company> companies = CompanyFacade.getInstance().getAllCompanies();

		User admin = new User();
		admin.setUsername("system_admin");
		admin.setPassword("password");
		admin.setCompany(companies.get(0));
		admin.setAdditionalInfo("System Administrator");

		admin.setPermissionLevel(PermissionLevel.SYSTEM_ADMIN);

		Person adminP = new Person();
		adminP.setFirstName("System");
		adminP.setLastName("Admin");
		adminP.setPhoneNumber("");
		adminP.setEmail("");

		admin.setPerson(adminP);

		UserFacade.getInstance().insert(admin);
	}
}
