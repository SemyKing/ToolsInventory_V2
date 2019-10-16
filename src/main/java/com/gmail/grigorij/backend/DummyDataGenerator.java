package com.gmail.grigorij.backend;

import com.gmail.grigorij.backend.database.facades.*;
import com.gmail.grigorij.backend.embeddable.Location;
import com.gmail.grigorij.backend.embeddable.Person;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.inventory.ToolUsageStatus;
import com.gmail.grigorij.backend.enums.permissions.PermissionLevel;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public class DummyDataGenerator {

	private static boolean entitiesGenerated = false;


	private int companiesCount = 3; //excluding 1st company: ADMINISTRATION
	private int usersPerCompany = 10;

	private int toolsCount = 10;
	private int toolCategoriesCount = 5;
	private int subCategories = 2;
	private int toolsPerCategory = toolsCount / toolCategoriesCount;

	private List<Company> companies = new ArrayList<>();
	private List<User> users = new ArrayList<>();
	private List<InventoryItem> tools = new ArrayList<>();


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

		Location companyLocation = new Location();
		companyLocation.setName("Main Office");
		companyLocation.setAddressLine1("Street Name 1");
		companyLocation.setCountry("Finland");
		companyLocation.setCity("Vantaa");
		companyLocation.setPostcode("01530");

		administrationCompany.setAddress(companyLocation);
		administrationCompany.setContactPerson(contactPerson);
		administrationCompany.setAdditionalInfo("ADMINISTRATION COMPANY FOR ADMINS ONLY");

		companies.add(administrationCompany);

		for (int i = 1; i < (companiesCount+1); i++) {
			Company company = new Company();
			company.setName("Company " + i + " Name");
			company.setVat("" + i + ""  + i + "" + i + "" + i + "" + i);

			r1 = getRandomStrings(1);
			r2 = getRandomStrings(1);

			Person p = new Person();
			p.setFirstName(r1 + "_firstName" + i);
			p.setLastName(r2 + "_lLastName" + i);
			p.setEmail(r1+r2 +  "@mail.com");

			company.setContactPerson(p);

			for (int j = 0; j < 3; j++) {
				Location location = new Location();
				location.setName("LocationName_" + j);
				location.setAddressLine1("LocationAddress_" + j);
				location.setCountry("LocationCountry_" + j);
				location.setCity("LocationCity_" + j);
				location.setPostcode("LocationPostcode_" + j);
				company.addLocation(location);
			}

			companies.add(company);
		}

		for (Company company : companies) {
			CompanyFacade.getInstance().insert(company);
		}
	}

	private void generateUsers() {
		companies = CompanyFacade.getInstance().getAllCompanies();

		User admin = new User();
		admin.setUsername("system_admin");
		admin.setPassword("password");
		admin.setCompany(companies.get(0));
		admin.setThemeVariant(LumoStyles.LIGHT);
		admin.setAdditionalInfo("System Administrator");

//		admin.setAccessRights(AccessRightFacade.getInstance().constructAccessRights(Permission.YES, Permission.YES, Permission.YES, Permission.YES));
//		admin.setAccessGroup(AccessGroup.SYSTEM_ADMIN);

		admin.setPermissionLevel(PermissionLevel.SYSTEM_ADMIN);

		Person adminP = new Person();
		adminP.setFirstName("Grigorij");
		adminP.setLastName("Semykin");
		adminP.setPhoneNumber("046123456");
		adminP.setEmail("gs@mail.com");

		admin.setPerson(adminP);

		users.add(admin);

		for (int compInd = 0; compInd < companies.size(); compInd++) {

			Company company = companies.get(compInd);

			for (int userInd = 0; userInd < usersPerCompany; userInd++) {

				User user = new User();
				user.setUsername("user" + compInd + "." + userInd);
				user.setPassword("password");
				user.setCompany(company);
//				user.setAccessGroup(AccessGroup.COMPANY_ADMIN);
//				user.setAccessRights(AccessRightFacade.getInstance().constructAccessRights(Permission.YES, Permission.YES, Permission.NO, Permission.YES));

				user.setPermissionLevel(PermissionLevel.USER);
				user.setPermissions(PermissionFacade.getInstance().getDefaultUserPermissions());

				String r1 = getRandomStrings(1);
				String r2 = getRandomStrings(1);

				Person p = new Person();
				p.setFirstName(r1 + "_FirstName");
				p.setLastName(r2 + "_LastName");
				p.setPhoneNumber("046" + compInd + "." + userInd);
				p.setEmail(r1 + r2 + "@mail.com");

				Location location = new Location();
				location.setName("LocationName_" + compInd + "." + userInd);
				location.setAddressLine1("LocationAddress_" + compInd + "." + userInd);
				location.setCountry("LocationCountry_" + compInd + "." + userInd);
				location.setCity("LocationCity_" + compInd + "." + userInd);
				location.setPostcode("LocationPostcode_" + compInd + "." + userInd);

				user.setAddress(location);
				user.setPerson(p);

				users.add(user);
			}
		}

		for (User user : users) {
			UserFacade.getInstance().insert(user);
		}
	}

	@SuppressWarnings( "deprecation" )
	private void generateTools() {
		int categoryCounter = 1;
		int subCategoryCounter = 1;
		int toolCounter = 1;

		for (Company company : companies) {
			for (int i = 0; i < toolCategoriesCount; i++) {
				InventoryItem p = new InventoryItem();
				p.setName("Category " + categoryCounter);
				p.setCompany(company);

				for (int j = 0; j < subCategories; j++) {
					InventoryItem c = new InventoryItem();
					c.setName("Sub Category " + subCategoryCounter + " (P: " + categoryCounter + ")");
					c.setParentCategory(p);
					c.setCompany(company);

					p.addChild(c);

					for (int k = 0; k < toolsPerCategory; k++) {
						InventoryItem cc = new InventoryItem();
						cc.setToolUsageStatus(ToolUsageStatus.FREE);
						cc.setCompany(company);
						cc.setName("Tool " + toolCounter + " (P: " + categoryCounter + ", SP: " + subCategoryCounter + ")");
						cc.setManufacturer(RandomStringUtils.randomAlphabetic(5));
						cc.setModel(RandomStringUtils.randomNumeric(10));
						cc.setToolInfo(RandomStringUtils.randomAlphabetic(10));
						cc.setSnCode(RandomStringUtils.randomNumeric(7));
						cc.setBarcode(RandomStringUtils.randomNumeric(10));
						cc.setPrice(999.93);

//						cc.setDateBought(new Date(100, 12, 31));
//						cc.setDateNextMaintenance(new Date(102, 1, 1));

						cc.setGuarantee_months(Integer.parseInt(RandomStringUtils.randomNumeric(2)));

						cc.setParentCategory(c);

						c.addChild(cc);

						toolCounter++;
					}

					subCategoryCounter++;
				}

				tools.add(p);
				categoryCounter++;
			}
		}

		for (InventoryItem ie : tools) {
			InventoryFacade.getInstance().insert(ie);
		}
	}

	private String getRandomStrings(int count) {
		return RandomStringUtils.randomAlphabetic(count).toUpperCase();
	}
}
