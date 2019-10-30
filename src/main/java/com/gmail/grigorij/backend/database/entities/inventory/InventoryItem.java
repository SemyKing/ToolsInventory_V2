package com.gmail.grigorij.backend.database.entities.inventory;

import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.EntityPojo;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.entities.embeddable.ToolReport;
import com.gmail.grigorij.backend.database.enums.inventory.InventoryHierarchyType;
import com.gmail.grigorij.backend.database.enums.inventory.ToolUsageStatus;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.ui.application.views.InventoryView;
import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;


/**
 * Entity works both as Category and Tool
 * <p>
 * Grid element only accepts one object type
 * <p>
 * Used by a TreeGrid in
 *
 * @see InventoryView
 * <p>
 * <p>
 * {@link #inventoryHierarchyType} defines if entity is a Category or a Tool
 */
@Entity
@Table(name = "inventory_entities")
@NamedQueries({

		@NamedQuery(name = InventoryItem.QUERY_ALL,
				query = "SELECT item FROM InventoryItem item"),

		@NamedQuery(name = InventoryItem.QUERY_ALL_BY_TYPE,
				query = "SELECT item FROM InventoryItem item WHERE" +
						" item.inventoryHierarchyType = :" + ProjectConstants.VAR1),

		@NamedQuery(name = InventoryItem.QUERY_ALL_BY_COMPANY,
				query = "SELECT item FROM InventoryItem item WHERE" +
						" item.company.id = :" + ProjectConstants.ID_VAR),

		@NamedQuery(name = InventoryItem.QUERY_ALL_BY_COMPANY_BY_TYPE,
				query = "SELECT item FROM InventoryItem item WHERE" +
						" item.company.id = :" + ProjectConstants.ID_VAR + " AND" +
						" item.inventoryHierarchyType = :" + ProjectConstants.VAR1),

		@NamedQuery(name = InventoryItem.QUERY_ALL_BY_CURRENT_USER,
				query = "SELECT item FROM InventoryItem item WHERE" +
						" item.currentUser.id = :" + ProjectConstants.ID_VAR),

		@NamedQuery(name = InventoryItem.QUERY_ALL_BY_RESERVED_USER,
				query = "SELECT item FROM InventoryItem item WHERE" +
						" item.reservedUser.id = :" + ProjectConstants.ID_VAR),

		@NamedQuery(name = InventoryItem.QUERY_BY_ID,
				query = "SELECT item FROM InventoryItem item WHERE" +
						" item.id = :" + ProjectConstants.ID_VAR),

		@NamedQuery(name = InventoryItem.QUERY_ALL_BY_PARENT_ID,
				query = "SELECT item FROM InventoryItem item WHERE" +
						" item.parentCategory.id = :" + ProjectConstants.ID_VAR),

		@NamedQuery(name = InventoryItem.QUERY_BY_CODE_VAR,
				query = "SELECT item FROM InventoryItem item WHERE" +
						" item.serialNumber = :" + ProjectConstants.VAR1 + " OR item.barcode = :" + ProjectConstants.VAR1)
})
public class InventoryItem extends EntityPojo {

	public static final String QUERY_ALL = "get_all_inventory_items";
	public static final String QUERY_ALL_BY_TYPE = "get_all_inventory_items_by_type";
	public static final String QUERY_ALL_BY_COMPANY = "get_all_inventory_items_by_company";
	public static final String QUERY_ALL_BY_COMPANY_BY_TYPE = "get_all_inventory_items_by_company_by_type";


	public static final String QUERY_ALL_BY_CURRENT_USER = "get_all_inventory_items_by_current_user";
	public static final String QUERY_ALL_BY_RESERVED_USER = "get_all_by_inventory_items_reserved_user";

	public static final String QUERY_BY_ID = "get_inventory_item_by_id";
	public static final String QUERY_ALL_BY_PARENT_ID = "get_all_inventory_item_by_parent_id";
	public static final String QUERY_BY_CODE_VAR = "get_inventory_items_by_code";



	/*
	NULL parentCategory is root category
	 */
	@ManyToOne(cascade = {CascadeType.REFRESH})
	@JoinColumn(name = "parent_id")
	private InventoryItem parentCategory;

	@OneToMany(mappedBy = "parentCategory", cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<InventoryItem> children = new HashSet<>();

	/*
	Allows to keep track of item position in Category tree hierarchy and sort list -> Parent must be added before child
	 */
	@Column(name = "level")
	private int level = 1;

	/*
	Allows to identify if Entity is a tool or a category
	 */
	@Enumerated(EnumType.STRING)
	private InventoryHierarchyType inventoryHierarchyType = InventoryHierarchyType.TOOL;


	private String name = "";
	private String serialNumber = "";
	private String RF_Code = "";
	private String barcode = "";
	private String manufacturer = "";
	private String model = "";
	private String toolInfo = "";

	private boolean personal = false;
	private boolean reported = false;

	@Enumerated(EnumType.STRING)
	private ToolUsageStatus usageStatus;

	@OneToOne
	private User currentUser;

	@OneToOne
	private User reservedUser;

	@OneToOne
	private Company company;

	private LocalDate dateBought;
	private LocalDate dateNextMaintenance;

	private Double price = 0.00;
	private Integer guarantee_months = 0;

	@Embedded
	private Location currentLocation;


	private List<ToolReport> reports = new ArrayList<>();


	//TODO: Last known GeoLocation


	public InventoryItem() {}

	public InventoryItem(InventoryItem other) {
		this.parentCategory = other.parentCategory;
		this.children = other.children;
		this.level = other.level;
		this.inventoryHierarchyType = other.inventoryHierarchyType;
		this.name = other.name;
		this.serialNumber = other.serialNumber;
		this.RF_Code = other.RF_Code;
		this.barcode = other.barcode;
		this.manufacturer = other.manufacturer;
		this.model = other.model;
		this.toolInfo = other.toolInfo;
		this.personal = other.personal;
		this.reported = other.reported;
		this.usageStatus = other.usageStatus;
		this.currentUser = other.currentUser;
		this.reservedUser = other.reservedUser;
		this.company = other.company;
		this.dateBought = other.dateBought;
		this.dateNextMaintenance = other.dateNextMaintenance;
		this.price = other.price;
		this.guarantee_months = other.guarantee_months;
		this.currentLocation = other.currentLocation;
		this.reports = other.reports;
	}


	public InventoryItem getParentCategory() {
		return parentCategory;
	}
	public void setParentCategory(InventoryItem parentCategory) {

		if (parentCategory == null) {
			this.parentCategory = null;
			this.setLevel(1);
		} else {
			if (parentCategory.equals(InventoryFacade.getInstance().getRootCategory())) {
				this.parentCategory = null;
				this.setLevel(1);
			} else {
				this.parentCategory = parentCategory;
				this.setLevel((parentCategory.getLevel() + 1));
			}
		}
	}

	// FOR DUMMY DATA
	public void addChild(InventoryItem ie) {
		ie.setLevel((this.level + 1));
		this.inventoryHierarchyType = InventoryHierarchyType.CATEGORY;
		this.children.add(ie);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}

	public String getToolInfo() {
		return toolInfo;
	}
	public void setToolInfo(String toolInfo) {
		this.toolInfo = toolInfo;
	}

	public ToolUsageStatus getUsageStatus() {
		return usageStatus;
	}
	public void setUsageStatus(ToolUsageStatus usageStatus) {
		this.usageStatus = usageStatus;
	}

	public boolean isPersonal() {
		return personal;
	}
	public void setPersonal(boolean personal) {
		this.personal = personal;
	}

	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public LocalDate getDateBought() {
		return dateBought;
	}
	public void setDateBought(LocalDate dateBought) {
		this.dateBought = dateBought;
	}

	public LocalDate getDateNextMaintenance() {
		return dateNextMaintenance;
	}
	public void setDateNextMaintenance(LocalDate dateNextMaintenance) {
		this.dateNextMaintenance = dateNextMaintenance;
	}

	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getGuarantee_months() {
		return guarantee_months;
	}
	public void setGuarantee_months(Integer guarantee_months) {
		this.guarantee_months = guarantee_months;
	}

	public Location getCurrentLocation() {
		return (this.currentLocation == null) ? new Location() : currentLocation;
	}
	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}

	public InventoryHierarchyType getInventoryHierarchyType() {
		return inventoryHierarchyType;
	}
	public void setInventoryHierarchyType(InventoryHierarchyType inventoryHierarchyType) {
		this.inventoryHierarchyType = inventoryHierarchyType;
	}

	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getRF_Code() {
		return RF_Code;
	}
	public void setRF_Code(String RF_Code) {
		this.RF_Code = RF_Code;
	}

	public User getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public User getReservedUser() {
		return reservedUser;
	}
	public void setReservedUser(User reservedUser) {
		this.reservedUser = reservedUser;
	}

	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	public List<ToolReport> getReports() {
		return reports;
	}
	public void setReports(List<ToolReport> reports) {
		this.reports = reports;
	}


	public String getCurrentLocationName() {
		String location = "";

		if (currentLocation != null) {
			location = currentLocation.getName();
		}

		return location;
	}
}
