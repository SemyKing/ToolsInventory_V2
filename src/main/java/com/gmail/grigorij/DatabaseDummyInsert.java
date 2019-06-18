package com.gmail.grigorij;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.user.Address;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.access.AccessGroups;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public class DatabaseDummyInsert {

	public static boolean usersInserted = false;
	public static boolean companiesInserted = false;

	private List<User> users;
	private List<Company> companies;

	public DatabaseDummyInsert() {
		users = new ArrayList<>();
		companies = new ArrayList<>();
	}

	public void generateUsersAndCompanies() {
		generateCompanies();
		generateUsers();
	}

	public void insertUsersAndCompanies() {
		if (!companiesInserted)
			insertCompanies();

		if (!usersInserted)
			insertUsers();
	}

	private int usersCount = 100;
	private int companiesCount = 4;
	private int usersPerCompany = usersCount / companiesCount;

	private void generateUsers() {
		User user1 = new User();
		user1.setUsername("u");
		user1.setPassword("p");
		user1.setCompany_id(1);
		user1.setAccess_group(AccessGroups.ADMIN.getIntValue());
		user1.setDeleted(false);

		user1.setFirstName("Grigorij");
		user1.setLastName("Semykin");
		user1.setPhoneNumber("046123456");
		user1.setEmail("gs@mail.com");

		user1.setThemeVariant(LumoStyles.LIGHT);
		user1.setAdditionalInfo("AOMOANFOANFOANFONofoanfoNOFAfon ANDaondo an OANDOANDONAOD");

		users.add(user1);

		int companyId = 2;
		int substractUsersPerCompany = usersPerCompany;

		for (int i = 1; i < usersCount; i++) {

			if (i >= usersPerCompany) {
				companyId++;
				usersPerCompany += usersPerCompany;
			}

			User user = new User();
			user.setUsername("" + i);
			user.setPassword("" + i);
			user.setCompany_id(companyId);
			user.setAccess_group(AccessGroups.EMPLOYEE.getIntValue());
			user.setDeleted(false);

			String rf = RandomStringUtils.randomAlphabetic(1);
			String rl = RandomStringUtils.randomAlphabetic(1);

			user.setFirstName(rf + "UserFirstName");
			user.setLastName(rl + "UserLastName");
			user.setPhoneNumber("046" + i);
			user.setEmail(rf+rl +  "@mail.com");

			Address address = new Address();
			address.setAddressLine1("UserAddress" + i);
			address.setCountry("UserCountry" + i);
			address.setCity("UserCity" + i);
			address.setPostcode("UserPostCode" + i);

			user.setAddress(address);

			users.add(user);
		}
		System.out.println("users generated");
	}

	private void generateCompanies() {
		Company aCompany = new Company();
		aCompany.setCompanyName("Administration");
		aCompany.setCompanyVAT("012345ABCD");
		aCompany.setDeleted(false);

		aCompany.setFirstName(RandomStringUtils.randomAlphabetic(1) + "PersonFirstName ");
		aCompany.setLastName(RandomStringUtils.randomAlphabetic(1) + "PersonLastName ");
		aCompany.setEmail(RandomStringUtils.randomAlphabetic(1)+RandomStringUtils.randomAlphabetic(1) +  "@mail.com");

		Address aAddress = new Address();
		aAddress.setAddressLine1("Huurrekuja");
		aAddress.setCountry("Finland");
		aAddress.setCity("Vantaa");
		aAddress.setPostcode("01530");

		aCompany.setAddress(aAddress);

		aCompany.setAdditionalInfo("COMPANYyyyyyyyyyyyyy yyyyyy y y y yy y y yyyyyy");

		companies.add(aCompany);


		for (int i = 1; i < companiesCount; i++) {
			Company company = new Company();
			company.setCompanyName("company " + i + " name");
			company.setCompanyVAT("" + i + ""  + i + "" + i + "" + i + "" + i);
			company.setDeleted(false);

			String rf = RandomStringUtils.randomAlphabetic(1);
			String rl = RandomStringUtils.randomAlphabetic(1);
			company.setFirstName(rf + "PersonFirstName" + i);
			company.setLastName(rl + "PersonLastName" + i);
			company.setEmail(rf+rl +  "@mail.com");

			Address address = new Address();
			address.setAddressLine1("CompanyAddress" + i);
			address.setCountry("CompanyCountry" + i);
			address.setCity("CompanyCity" + i);
			address.setPostcode("CompanyPostCode" + i);

			company.setAddress(address);

			companies.add(company);
		}
		System.out.println("companies generated");
	}

	private void insertUsers() {
		for (User user : users) {
			System.out.println("insertUsers(): " + user.getFirstName());
			UserFacade.getInstance().insert(user);
		}
		System.out.println("users inserted");

		usersInserted = true;
	}

	private void insertCompanies() {
		for (Company company : companies) {
			System.out.println("insertUsers(): " + company.getCompanyName());

			CompanyFacade.getInstance().insert(company);
		}
		System.out.println("companies inserted");

		companiesInserted = true;
	}
}
