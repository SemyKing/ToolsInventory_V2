package com.gmail.grigorij.ui;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.Address;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.user.AccessGroups;
import com.gmail.grigorij.backend.entities.user.Person;
import com.gmail.grigorij.backend.entities.user.User;
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
		generateUsers();
		generateCompanies();
	}

	public void insertUsersAndCompanies() {
		if (!usersInserted)
			insertUsers();

		if (!companiesInserted)
			insertCompanies();
	}


	private void generateUsers() {
		User user1 = new User();
		user1.setUsername("u");
		user1.setPassword("p");
		user1.setCompany_id(0);
		user1.setAccess_group(AccessGroups.ADMIN.value());
		user1.setDeleted(false);

		user1.setFirstName("Grigorij");
		user1.setLastName("Semykin");
		user1.setEmail("gs@mail.com");

		users.add(user1);

		for (int i = 1; i < 10; i++) {
			User user = new User();
			user.setUsername("" + i);
			user.setPassword("" + i);
			user.setCompany_id(1);
			user.setAccess_group(AccessGroups.NORMAL_USER.value());
			user.setDeleted(false);

			String rf = RandomStringUtils.randomAlphabetic(1);
			String rl = RandomStringUtils.randomAlphabetic(1);

			user.setFirstName(rf + "UserFirstName" + i);
			user.setLastName(rl + "UserLastName" + i);
			user.setEmail(rf+rl +  "@mail.com");

			users.add(user);
		}
		System.out.println("users generated");
	}

	private void generateCompanies() {
		for (int i = 0; i < 10; i++) {
			Company company = new Company();
			company.setCompanyName("company " + i + " name");
			company.setCompanyVAT("" + i + ""  + i + "" + i + "" + i + "" + i);
			company.setDeleted(false);

			String rf = RandomStringUtils.randomAlphabetic(1);
			String rl = RandomStringUtils.randomAlphabetic(1);
			company.setFirstName(rf + "PersonFirstName " + i);
			company.setLastName(rl + "PersonLastName " + i);
			company.setEmail(rf+rl +  "@mail.com");

			RandomStringUtils.randomAlphabetic(1);

			Address address = new Address();
			address.setAddressLine1("Huurrekuja " + i);
			address.setCountry("Finland");
			address.setPostArea("Vantaa");
			address.setPostcode("01530");

			company.setAddress(address);
		}
		System.out.println("companies generated");
	}

	private void insertUsers() {
		for (User user : users) {
			System.out.println("insertUsers(): " + user.getFirstName());
			DatabaseManager.getInstance().insert(user);
		}
		System.out.println("users inserted");

		usersInserted = true;
	}

	private void insertCompanies() {
		for (Company company : companies) {
			System.out.println("insertUsers(): " + company.getCompanyName());
			DatabaseManager.getInstance().insert(company);
		}
		System.out.println("companies inserted");

		companiesInserted = true;
	}
}
