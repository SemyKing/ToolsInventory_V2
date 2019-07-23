package com.gmail.grigorij.backend;

import com.gmail.grigorij.backend.access.AccessGroups;
import com.gmail.grigorij.backend.database.facades.*;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.location.Location;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.backend.entities.inventory.ToolStatus;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.backend.entities.transaction.TransactionOperation;
import com.gmail.grigorij.backend.entities.transaction.TransactionTarget;
import com.gmail.grigorij.backend.entities.user.User;
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
	private int subCategories = 3;
	private int toolsPerCategory = toolsCount / toolCategoriesCount;

	private List<User> users;
	private List<Company> companies, companiesFromDB;

	private List<InventoryEntity> tools;

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
				user.setUsername("u" + compInd + "." + userInd);
				user.setPassword("p" + compInd + "." + userInd);
				user.setCompany(company);
				user.setAccessGroup(AccessGroups.EMPLOYEE.getIntValue());
				user.setDeleted(false);

				String rf = RandomStringUtils.randomAlphabetic(1);
				String rl = RandomStringUtils.randomAlphabetic(1);

				user.setFirstName(rf + "UserFirstName");
				user.setLastName(rl + "UserLastName");
				user.setPhoneNumber("046" + compInd + "." + userInd);
				user.setEmail(rf+rl +  "@mail.com");

				Location location = new Location();
				location.setAddressLine1("Street Name" + compInd + "." + userInd);
				location.setCountry("Country Name" + compInd + "." + userInd);
				location.setCity("City Name" + compInd + "." + userInd);
				location.setPostcode("Postcode" + compInd + "." + userInd);

				user.setAddress(location);

				users.add(user);
			}
		}

		insertUsers();
	}

	private void generateCompanies() {
		Company aCompany = new Company();
		aCompany.setName(ADMINISTRATION_COMPANY_NAME);
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

		for (int comp = 0; comp < (companiesFromDB.size()); comp++) {

			Company company = companiesFromDB.get(comp);
			List<User> companyUsers = UserFacade.getInstance().getUsersByCompanyId(company.getId());
			System.out.println("USERS IN COMPANY: " + companyUsers.size());

			for (int i = 0; i < toolCategoriesCount; i++) {
				InventoryEntity p = new InventoryEntity();
				p.setName("Category " + categoryCounter);
				p.setCompany(company);

				for (int j = 0; j < subCategories; j++) {
					InventoryEntity c = new InventoryEntity();
					c.setName("Sub Category " + subCategoryCounter + " (P: " + categoryCounter  +")");
					c.setParentCategory(p);
					c.setCompany(company);

					p.addChild(c);

					for (int k = 0; k < toolsPerCategory; k++) {
						InventoryEntity cc = new InventoryEntity();

						int random = (int )(Math.random() * 5);
						ToolStatus status = ToolStatus.IN_USE;

						if (random == 0) {
							status = ToolStatus.FREE;
						} else if (random == 1) {
							status = ToolStatus.IN_USE;
						} else if (random == 2) {
							status = ToolStatus.LOST;
						} else if (random == 3) {
							status = ToolStatus.RESERVED;
						} else if (random == 4) {
							status = ToolStatus.BROKEN;
						}

						cc.setUsageStatus(status);

						if (status.equals(ToolStatus.IN_USE)) {
							int randomUserIndex = (int )(Math.random() * (companyUsers.size()));
							User user = companyUsers.get(randomUserIndex);
							cc.setUser(user);
						}

						if (status.equals(ToolStatus.RESERVED)) {
							int randomUserIndex1 = (int )(Math.random() * (companyUsers.size()));
							int randomUserIndex2 = (int )(Math.random() * (companyUsers.size()));
							User user1 = companyUsers.get(randomUserIndex1);
							User user2 = companyUsers.get(randomUserIndex2);

							cc.setReservedByUser(user1);
							cc.setUser(user2);
						}


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
			transaction.setTransactionOperation(TransactionOperation.ADD);
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
		admin.setUsername("u");
		admin.setPassword("p");
		admin.setCompany(administration);
		admin.setAccessGroup(AccessGroups.ADMIN.getIntValue());
		admin.setDeleted(false);

		admin.setFirstName("Grigorij");
		admin.setLastName("Semykin");
		admin.setPhoneNumber("046123456");
		admin.setEmail("gs@mail.com");

		admin.setThemeVariant(LumoStyles.LIGHT);
		admin.setAdditionalInfo("ABCDEFG");

		UserFacade.getInstance().insert(admin);
		transactionUser = UserFacade.getInstance().getUserByUsername(admin.getUsername());
	}

	private void insertUsers() {
		for (User user : users) {
			UserFacade.getInstance().insert(user);

			Transaction transaction = new Transaction();
			transaction.setWhoDid(transactionUser);
			transaction.setTransactionOperation(TransactionOperation.ADD);
			transaction.setTransactionTarget(TransactionTarget.USER);
			transaction.setDestinationUser(user);

			TransactionFacade.getInstance().insert(transaction);
		}
	}

	private void insertTools() {
		for (InventoryEntity ie : tools) {
			InventoryFacade.getInstance().insert(ie);


			Transaction transaction = new Transaction();
			transaction.setWhoDid(transactionUser);
			transaction.setTransactionOperation(TransactionOperation.ADD);
			transaction.setInventoryEntity(ie); // <--TransactionTarget is set automatically TOOL/CATEGORY

			TransactionFacade.getInstance().insert(transaction);
		}
	}
}
