package com.gmail.grigorij.backend;

import com.gmail.grigorij.backend.enums.permissions.AccessGroup;
import com.gmail.grigorij.backend.database.facades.*;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.embeddable.Location;
import com.gmail.grigorij.backend.embeddable.Person;
import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.enums.inventory.ToolStatus;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.backend.enums.transactions.TransactionType;
import com.gmail.grigorij.backend.enums.transactions.TransactionTarget;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.permissions.Permission;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDummyInsert {

	public static boolean entitiesGenerated = false;

	private final String ADMINISTRATION_COMPANY_NAME = "ADMINISTRATION";

	private int companiesCount = 3; //excluding 1st company: ADMINISTRATION
	private int usersPerCompany = 10;

	private int toolsCount = 10;
	private int toolCategoriesCount = 5;
	private int subCategories = 2;
	private int toolsPerCategory = toolsCount / toolCategoriesCount;

	private List<User> users;
	private List<Company> companies, companiesFromDB;
	private List<InventoryItem> tools;

	private User transactionUser;

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
		for (int compInd = 0; compInd < (companiesFromDB.size()); compInd++) {

			Company company = companiesFromDB.get(compInd);

			for (int userInd = 0; userInd < usersPerCompany; userInd++) {

				User user = new User();
				user.setUsername("user" + (compInd++) + "." + userInd);
				user.setPassword("password");
				user.setCompany(company);
				user.setAccessGroup(AccessGroup.COMPANY_ADMIN);
				user.setAccessRights(AccessRightFacade.getInstance().constructAccessRights(Permission.YES, Permission.YES, Permission.NO, Permission.YES));


				String rf = RandomStringUtils.randomAlphabetic(1);
				String rl = RandomStringUtils.randomAlphabetic(1);

				Person p = new Person();

				p.setFirstName(rf + "_FirstName");
				p.setLastName(rl + "_LastName");
				p.setPhoneNumber("046" + compInd + "." + userInd);
				p.setEmail(rf+rl +  "@mail.com");

				Location location = new Location();
				location.setName("Location Name" + compInd + "." + userInd);
				location.setAddressLine1("Street Name" + compInd + "." + userInd);
				location.setCountry("Country Name" + compInd + "." + userInd);
				location.setCity("City Name" + compInd + "." + userInd);
				location.setPostcode("Postcode" + compInd + "." + userInd);

				user.setAddress(location);
				user.setPerson(p);

				users.add(user);
			}
		}

		insertUsers();
	}

	private void generateCompanies() {
		Company aCompany = new Company();
		aCompany.setName(ADMINISTRATION_COMPANY_NAME);
		aCompany.setVat("012345");
		aCompany.setDeleted(false);

		Person cp = new Person();

		cp.setFirstName(RandomStringUtils.randomAlphabetic(1) + "_FirstName ");
		cp.setLastName(RandomStringUtils.randomAlphabetic(1) + "_LastName ");
		cp.setEmail(RandomStringUtils.randomAlphabetic(1)+RandomStringUtils.randomAlphabetic(1) +  "@mail.com");

		Location companyLocation = new Location();
		companyLocation.setName("Main Office");
		companyLocation.setAddressLine1("Huurrekuja");
		companyLocation.setCountry("Finland");
		companyLocation.setCity("Vantaa");
		companyLocation.setPostcode("01530");

		aCompany.setAddress(companyLocation);
		aCompany.setContactPerson(cp);
		aCompany.setAdditionalInfo("ADMINISTRATION COMPANY FOR ADMINS ONLY");

		companies.add(aCompany);

		for (int i = 1; i < (companiesCount+1); i++) {
			Company company = new Company();
			company.setName("company " + i + " name");
			company.setVat("" + i + ""  + i + "" + i + "" + i + "" + i);
			company.setDeleted(false);

			String rf = RandomStringUtils.randomAlphabetic(1);
			String rl = RandomStringUtils.randomAlphabetic(1);

			Person p = new Person();
			p.setFirstName(rf + "PersonFirstName" + i);
			p.setLastName(rl + "PersonLastName" + i);
			p.setEmail(rf+rl +  "@mail.com");

			company.setContactPerson(p);

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
		insertCompanies();
	}

	@SuppressWarnings( "deprecation" )
	private void generateTools() {
		int categoryCounter = 1;
		int subCategoryCounter = 1;
		int toolCounter = 1;

		for (int comp = 0; comp < (companiesFromDB.size()); comp++) {

			Company company = companiesFromDB.get(comp);
			List<User> companyUsers = UserFacade.getInstance().getUsersInCompany(company.getId());

			for (int i = 0; i < toolCategoriesCount; i++) {
				InventoryItem p = new InventoryItem();
				p.setName("Category " + categoryCounter);
				p.setCompany(company);

				for (int j = 0; j < subCategories; j++) {
					InventoryItem c = new InventoryItem();
					c.setName("Sub Category " + subCategoryCounter + " (P: " + categoryCounter  +")");
					c.setParentCategory(p);
					c.setCompany(company);

					p.addChild(c);

					for (int k = 0; k < toolsPerCategory; k++) {
						InventoryItem cc = new InventoryItem();

//						int random = (int )(Math.random() * 5);
//						ToolStatus status = ToolStatus.IN_USE;
//
//						if (random == 0) {
//							status = ToolStatus.FREE;
//						} else if (random == 1) {
//							status = ToolStatus.IN_USE;
//						} else if (random == 2) {
//							status = ToolStatus.LOST;
//						} else if (random == 3) {
//							status = ToolStatus.RESERVED;
//						} else if (random == 4) {
//							status = ToolStatus.BROKEN;
//						}

						cc.setUsageStatus(ToolStatus.FREE);

//						if (status.equals(ToolStatus.IN_USE)) {
//							int randomUserIndex = (int )(Math.random() * (companyUsers.size()));
//							User user = companyUsers.get(randomUserIndex);
//							cc.setUser(user);
//						}
//
//						if (status.equals(ToolStatus.RESERVED)) {
//							int randomUserIndex1 = (int )(Math.random() * (companyUsers.size()));
//							int randomUserIndex2 = (int )(Math.random() * (companyUsers.size()));
//							User user1 = companyUsers.get(randomUserIndex1);
//							User user2 = companyUsers.get(randomUserIndex2);
//
//							cc.setReservedByUser(user1);
//							cc.setUser(user2);
//						}


						cc.setCompany(company);
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

						c.addChild(cc);

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

	private void insertCompanies() {
		for (Company company : companies) {
			CompanyFacade.getInstance().insert(company);

			Transaction transaction = new Transaction();
			transaction.setWhoDid(transactionUser);
			transaction.setTransactionOperation(TransactionType.ADD);
			transaction.setTransactionTarget(TransactionTarget.COMPANY);
			transaction.setCompany(company);

			TransactionFacade.getInstance().insert(transaction);
		}

		companiesFromDB = CompanyFacade.getInstance().getAllCompanies();

		Company administration = null;

		for (Company c : companiesFromDB) {
			if (c.getName().equals(ADMINISTRATION_COMPANY_NAME)) {
				administration = c;
				break;
			}
		}


		User admin = new User();
		admin.setUsername("system_admin");
		admin.setPassword("password");
		admin.setCompany(administration);
		admin.setThemeVariant(LumoStyles.LIGHT);
		admin.setAdditionalInfo("System Administrator");

		admin.setAccessRights(AccessRightFacade.getInstance().constructAccessRights(Permission.YES, Permission.YES, Permission.YES, Permission.YES));
		admin.setAccessGroup(AccessGroup.SYSTEM_ADMIN);

		Person adminP = new Person();
		adminP.setFirstName("Grigorij");
		adminP.setLastName("Semykin");
		adminP.setPhoneNumber("046123456");
		adminP.setEmail("gs@mail.com");

		admin.setPerson(adminP);

		UserFacade.getInstance().insert(admin);
		transactionUser = UserFacade.getInstance().getUserByUsername(admin.getUsername());
	}

	private void insertUsers() {
		for (User user : users) {
			UserFacade.getInstance().insert(user);

			Transaction transaction = new Transaction();
			transaction.setWhoDid(transactionUser);
			transaction.setTransactionOperation(TransactionType.ADD);
			transaction.setTransactionTarget(TransactionTarget.USER);
			transaction.setDestinationUser(user);

			TransactionFacade.getInstance().insert(transaction);
		}
	}

	private void insertTools() {
		for (InventoryItem ie : tools) {
			InventoryFacade.getInstance().insert(ie);

			Transaction transaction = new Transaction();
			transaction.setWhoDid(transactionUser);
			transaction.setTransactionOperation(TransactionType.ADD);
			transaction.setTransactionTarget(TransactionTarget.CATEGORY);
			transaction.setInventoryEntity(ie); // <--TransactionTarget is set automatically TOOL/CATEGORY

			TransactionFacade.getInstance().insert(transaction);
		}
	}
}
