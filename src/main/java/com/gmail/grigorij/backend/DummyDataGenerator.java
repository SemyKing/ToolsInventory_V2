package com.gmail.grigorij.backend;

import com.gmail.grigorij.backend.database.entities.*;
import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.entities.embeddable.Person;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.tools.ToolUsageStatus;
import com.gmail.grigorij.backend.database.facades.*;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.utils.authentication.PasswordUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public class DummyDataGenerator {

	private static boolean entitiesGenerated = false;

	private final static String PASSWORD = "password";


	private int companiesCount = 3; //excluding 1st company: ADMINISTRATION
	private int usersPerCompany = 10;
	private int toolsPerCategoryCount = 20;
	private int categoriesCount = 5;


	private List<Company> companies = new ArrayList<>();
	private List<User> users = new ArrayList<>();
	private List<Tool> tools = new ArrayList<>();


	public DummyDataGenerator() {
		if (!entitiesGenerated) {

			generateCompanies();

			generateUsers();

			generateTools();
		}
		entitiesGenerated = true;
	}


	private void generateCompanies() {
		Company administrationCompany = new Company();
		administrationCompany.setName("Administration");
		administrationCompany.setVat("01234567");

		Person contactPerson = new Person();

		String r1 = getRandomStrings(1);
		String r2 = getRandomStrings(1);

		contactPerson.setFirstName(r1 + "_firstName ");
		contactPerson.setLastName(r2 + "_lastName ");
		contactPerson.setEmail(r1+r2 +  "@mail.com");

		Location companyAddress = new Location();
		companyAddress.setName("Main Office");
		companyAddress.setAddressLine1("Street Name 1");
		companyAddress.setCountry("Finland");
		companyAddress.setCity("Vantaa");
		companyAddress.setPostcode("01530");

		for (int j = 0; j < 3; j++) {
			Location location = new Location();
			location.setName("AC Location Name_" + j);
			location.setAddressLine1("AC Location Address_" + j);
			location.setCountry("AC Location Country_" + j);
			location.setCity("AC Location City_" + j);
			location.setPostcode("AC Location Postcode_" + j);
			administrationCompany.addLocation(location);
		}

		administrationCompany.setAddress(companyAddress);
		administrationCompany.setContactPerson(contactPerson);
		administrationCompany.setAdditionalInfo("ADMINISTRATION COMPANY FOR ADMINS ONLY");

		administrationCompany.setPdf_template(new PDF_Template());

		companies.add(administrationCompany);

		for (int i = 1; i < (companiesCount+1); i++) {
			Company company = new Company();
			company.setName("Company " + i + " Name");
			company.setVat("" + i + ""  + i + "" + i + "" + i + "" + i);

			r1 = getRandomStrings(1);
			r2 = getRandomStrings(1);

			Person p = new Person();
			p.setFirstName(r1 + "_FirstName" + i);
			p.setLastName(r2 + "_LastName" + i);
			p.setEmail(r1+r2 +  "@mail.com");
			company.setContactPerson(p);

			Location address = new Location();
			address.setName("C"+i+" Main Office");
			address.setAddressLine1("C"+i+" Address Line 1");
			address.setCountry("C"+i+" Finland");
			address.setCity("C"+i+" Vantaa");
			address.setPostcode("C"+i+" 01530");
			company.setAddress(address);


			for (int j = 0; j < 3; j++) {
				Location location = new Location();
				location.setName("C "+i+" Location Name_" + j);
				location.setAddressLine1("C "+i+" Location Address_" + j);
				location.setCountry("C "+i+" Location Country_" + j);
				location.setCity("C "+i+" Location City_" + j);
				location.setPostcode("C "+i+" Location Postcode_" + j);
				company.addLocation(location);
			}

			company.setPdf_template(new PDF_Template());

			companies.add(company);
		}

		for (Company company : companies) {
			CompanyFacade.getInstance().insert(company);
		}
	}

	private void generateUsers() {
		companies = CompanyFacade.getInstance().getAllCompanies();

		User admin = new User();
		admin.setUsername("sysadmin");


		String salt = PasswordUtils.getSalt(30);
		admin.setSalt(salt);
		admin.setPassword(PasswordUtils.generateSecurePassword(PASSWORD, salt));
		admin.setDummyPassword(PasswordUtils.generateDummyPassword(PASSWORD));

		admin.setCompany(companies.get(0));
		admin.setThemeVariant(LumoStyles.LIGHT);
		admin.setAdditionalInfo("System Administrator");
		admin.setPermissionLevel(PermissionLevel.SYSTEM_ADMIN);

		Person adminP = new Person();
		adminP.setFirstName("System");
		adminP.setLastName("Admin");
		adminP.setPhoneNumber("046123456");
		adminP.setEmail("some.mail@mail.com");
		admin.setPerson(adminP);

		Location adminAddress = new Location();
		adminAddress.setName("Admin Home Address");
		adminAddress.setAddressLine1("Admin Address Line 1");
		adminAddress.setCountry("Admin Country");
		adminAddress.setCity("Admin City");
		adminAddress.setPostcode("Admin Postcode");
		admin.setAddress(adminAddress);


		users.add(admin);

		// UNCOMMENT -> ONLY SYSTEM ADMIN IN ADMINISTRATION COMPANY

//		long id = companies.get(0).getId();
//		companies.removeIf(company -> company.getId().equals(id));

		boolean companyAdminSet;

		for (int compInd = 0; compInd < companies.size(); compInd++) {

			Company company = companies.get(compInd);
			companyAdminSet = false;

			for (int userInd = 0; userInd < usersPerCompany; userInd++) {

				User user = new User();
				user.setUsername("user" + compInd + "." + userInd);

				salt = PasswordUtils.getSalt(30);
				user.setSalt(salt);
				user.setPassword(PasswordUtils.generateSecurePassword(PASSWORD, salt));
				user.setDummyPassword(PasswordUtils.generateDummyPassword(PASSWORD));
				user.setCompany(company);

				if (!companyAdminSet) {
					user.setPermissionLevel(PermissionLevel.COMPANY_ADMIN);
					user.setPermissions(PermissionFacade.getInstance().getDefaultCompanyAdminPermissions());
				} else {
					user.setPermissionLevel(PermissionLevel.USER);
					user.setPermissions(PermissionFacade.getInstance().getDefaultUserPermissions());
				}
				companyAdminSet = true;

				String r1 = getRandomStrings(1);
				String r2 = getRandomStrings(1);

				Person p = new Person();
				p.setFirstName(r1 + "_FirstName");
				p.setLastName(r2 + "_LastName");
				p.setPhoneNumber("046" + compInd + "..." + userInd);
				p.setEmail(r1 + r2 + "@mail.com");

				Location userAddress = new Location();
				userAddress.setName("Home Address_" + compInd + "." + userInd);
				userAddress.setAddressLine1("Address Line 1_" + compInd + "." + userInd);
				userAddress.setCountry("Country_" + compInd + "." + userInd);
				userAddress.setCity("City_" + compInd + "." + userInd);
				userAddress.setPostcode("Postcode_" + compInd + "." + userInd);

				user.setAddress(userAddress);
				user.setPerson(p);

				users.add(user);
			}
		}


		for (User user : users) {
			UserFacade.getInstance().insert(user);
		}
	}

	private void generateTools() {

		int toolCounter = 1;
		int categoryCounter = 1;

		for (Company company : companies) {

			for (int i = 0; i < categoriesCount; i++) {
				Category category = new Category();
				category.setName("Category " + categoryCounter);
				category.setCompany(company);

				InventoryFacade.getInstance().insert(category);

				categoryCounter++;

				for (int j = 0; j < toolsPerCategoryCount; j++) {

					Tool tool = new Tool();
					tool.setName("Tool " + toolCounter);
					tool.setCompany(company);
					tool.setUsageStatus(ToolUsageStatus.FREE);
					tool.setManufacturer(RandomStringUtils.randomAlphabetic(5));
					tool.setModel(RandomStringUtils.randomNumeric(10));
					tool.setToolInfo(RandomStringUtils.randomAlphabetic(10));
					tool.setSerialNumber(RandomStringUtils.randomNumeric(7));
					tool.setBarcode(RandomStringUtils.randomNumeric(10));
					tool.setCurrentLocation(company.getLocations().get(0));
					tool.setPrice(999.93);
					tool.setGuarantee_months(Integer.parseInt(RandomStringUtils.randomNumeric(2)));

					tool.setCategory(category);

					tools.add(tool);

					toolCounter++;
				}
			}
		}

		for (Tool ie : tools) {
			InventoryFacade.getInstance().insert(ie);
		}
	}


	private String getRandomStrings(int count) {
		return RandomStringUtils.randomAlphabetic(count).toUpperCase();
	}
}
