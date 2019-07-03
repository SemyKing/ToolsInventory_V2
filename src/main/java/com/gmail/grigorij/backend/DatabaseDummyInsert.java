package com.gmail.grigorij.backend;

import com.gmail.grigorij.backend.access.AccessGroups;
import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.ToolFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.location.Location;
import com.gmail.grigorij.backend.entities.tool.Tool;
import com.gmail.grigorij.backend.entities.tool.ToolStatus;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDummyInsert {

	public static boolean entitiesGenerated = false;

	private int usersCount = 10;
	private int companiesCount = 3; //excluding 1st company: ADMINISTRATION
	private int usersPerCompany = usersCount / companiesCount;

	private int toolsCount = 10;
	private int toolCategoriesCount = 5;
	private int subCategories = 3;
	private int toolsPerCategory = toolsCount / toolCategoriesCount;

	private List<User> users;
	private List<Company> companies, companiesFromDB;
	private List<Tool> tools;


	public DatabaseDummyInsert() {
		users = new ArrayList<>();
		companies = new ArrayList<>();
		tools = new ArrayList<>();
	}

	public void generateAndInsert() {
		if (!entitiesGenerated) {
			generateCompanies();

			generateUsers();

			generateTools();
		}
		entitiesGenerated = true;
	}

	private void generateUsers() {
		User user1 = new User();
		user1.setUsername("u");
		user1.setPassword("p");
		user1.setCompanyId(1);
		user1.setAccessGroup(AccessGroups.ADMIN.getIntValue());
		user1.setDeleted(false);

		user1.setFirstName("Grigorij");
		user1.setLastName("Semykin");
		user1.setPhoneNumber("046123456");
		user1.setEmail("gs@mail.com");

		user1.setThemeVariant(LumoStyles.LIGHT);
		user1.setAdditionalInfo("ABCDEFG");

		users.add(user1);


		int companyId = 2;

		for (int i = 1; i < usersCount; i++) {

			if (i >= usersPerCompany) {
				companyId++;
				usersPerCompany += usersPerCompany;
			}

			User user = new User();
			user.setUsername("uname" + i);
			user.setPassword("upass" + i);
			user.setCompanyId(companyId);
			user.setAccessGroup(AccessGroups.EMPLOYEE.getIntValue());
			user.setDeleted(false);

			String rf = RandomStringUtils.randomAlphabetic(1);
			String rl = RandomStringUtils.randomAlphabetic(1);

			user.setFirstName(rf + "UserFirstName");
			user.setLastName(rl + "UserLastName");
			user.setPhoneNumber("046" + i);
			user.setEmail(rf+rl +  "@mail.com");

			Location location = new Location();
			location.setAddressLine1("UserAddress" + i);
			location.setCountry("UserCountry" + i);
			location.setCity("UserCity" + i);
			location.setPostcode("UserPostCode" + i);

			user.setAddress(location);

			users.add(user);
		}
		System.out.println("users generated");

		insertUsers();
	}

	private void generateCompanies() {
		Company aCompany = new Company();
		aCompany.setName("Administration");
		aCompany.setVat("012345ABCD");
		aCompany.setDeleted(false);

		aCompany.setFirstName(RandomStringUtils.randomAlphabetic(1) + "PersonFirstName ");
		aCompany.setLastName(RandomStringUtils.randomAlphabetic(1) + "PersonLastName ");
		aCompany.setEmail(RandomStringUtils.randomAlphabetic(1)+RandomStringUtils.randomAlphabetic(1) +  "@mail.com");

		Location companyLocation = new Location();
		companyLocation.setName("Main Office");
		companyLocation.setAddressLine1("Huurrekuja");
		companyLocation.setCountry("Finland");
		companyLocation.setCity("Vantaa");
		companyLocation.setPostcode("01530");

		aCompany.setAddress(companyLocation);
		aCompany.setAdditionalInfo("ADMINISTRATION COMPANY FOR ADMINS ONLY");

		companies.add(aCompany);


		for (int i = 1; i < (companiesCount+1); i++) {
			Company company = new Company();
			company.setName("company " + i + " name");
			company.setVat("" + i + ""  + i + "" + i + "" + i + "" + i);
			company.setDeleted(false);

			String rf = RandomStringUtils.randomAlphabetic(1);
			String rl = RandomStringUtils.randomAlphabetic(1);
			company.setFirstName(rf + "PersonFirstName" + i);
			company.setLastName(rl + "PersonLastName" + i);
			company.setEmail(rf+rl +  "@mail.com");


			for (int j = 0; j < 3; j++) {
				Location location = new Location();
				location.setName("LocationN" + j);
				location.setAddressLine1("locationAddress" + j);
				location.setCountry("locationCountry" + j);
				location.setCity("locationCity" + j);
				location.setPostcode("locationPostCode" + j);
				company.addLocation(location);
			}

			companies.add(company);
		}
		System.out.println("companies generated");
		insertCompanies();
	}

	@SuppressWarnings( "deprecation" )
	private void generateTools() {
		int categoryCounter = 1;
		int subCategoryCounter = 1;
		int toolCounter = 1;

		for (int comp = 1; comp < (companiesCount+1); comp++) {

			Company company = companiesFromDB.get(comp);
			List<User> companyUsers = UserFacade.getInstance().getUsersByCompanyId(company.getId());
			System.out.println("USERS IN COMPANY: " + companyUsers.size());

			for (int i = 0; i < toolCategoriesCount; i++) {
				Tool p = new Tool();
				p.setName("Category " + categoryCounter);
				p.setCompanyId(company.getId());

				for (int j = 0; j < subCategories; j++) {
					Tool c = new Tool();
					c.setName("Sub Category " + subCategoryCounter + " (P: " + categoryCounter  +")");
					c.setParentCategory(p);
					c.setCompanyId(company.getId());

					p.addTool(c);

					for (int k = 0; k < toolsPerCategory; k++) {
						Tool cc = new Tool();

						int random = (int )(Math.random() * 3);
						ToolStatus status = ToolStatus.IN_USE;

						if (random == 0) {
							status = ToolStatus.FREE;
						} else if (random == 1) {
							status = ToolStatus.IN_USE;
						} else if (random == 2) {
							status = ToolStatus.LOST;
						}

						cc.setUsageStatus(status);

						if (status.equals(ToolStatus.IN_USE)) {
							int randomUserIndex = (int )(Math.random() * (companyUsers.size()));
							User user = companyUsers.get(randomUserIndex);
							cc.setInUseByUserId(user.getId());
						}

						cc.setCompanyId(company.getId());
						cc.setName("Tool " + toolCounter + " (P: " + categoryCounter  +", SP: "+ subCategoryCounter+ ")");
						cc.setManufacturer(RandomStringUtils.randomAlphabetic(5));
						cc.setModel(RandomStringUtils.randomNumeric(10));
						cc.setToolInfo(RandomStringUtils.randomAlphabetic(10));
						cc.setSnCode(RandomStringUtils.randomNumeric(7));
						cc.setBarcode(RandomStringUtils.randomNumeric(10));
						cc.setPrice(999.93);

						cc.setDateBought(new Date(100, 12, 31));
						cc.setDateNextMaintenance(new Date(102, 1, 1));

						cc.setGuarantee_months(Integer.parseInt(RandomStringUtils.randomNumeric(2)));

						cc.setParentCategory(c);

						c.addTool(cc);

						toolCounter++;
					}

					subCategoryCounter++;
				}

				tools.add(p);
				categoryCounter++;
			}
		}

		insertTools();
	}


	private void insertUsers() {
		for (User user : users) {
			UserFacade.getInstance().insert(user);
		}
	}

	private void insertCompanies() {
		for (Company company : companies) {
			CompanyFacade.getInstance().insert(company);
		}

		companiesFromDB = CompanyFacade.getInstance().getAllCompanies();
	}

	private void insertTools() {
		for (Tool tool : tools) {
			ToolFacade.getInstance().insert(tool);
		}
	}
}
