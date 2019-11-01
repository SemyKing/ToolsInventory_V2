package com.gmail.grigorij.backend.database.entities;

import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.entities.embeddable.ToolReport;
import com.gmail.grigorij.backend.database.enums.inventory.InventoryItemType;
import com.gmail.grigorij.backend.database.enums.inventory.ToolUsageStatus;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;


@Entity
@Table(name = "inventory_item")
@NamedQueries({
		@NamedQuery(name = InventoryItem.QUERY_ALL,
				query = "SELECT item FROM InventoryItem item"),

		@NamedQuery(name = InventoryItem.QUERY_ALL_BY_TYPE,
				query = "SELECT item FROM InventoryItem item WHERE" +
						" item.inventoryItemType = :" + ProjectConstants.VAR1),

		@NamedQuery(name = InventoryItem.QUERY_ALL_BY_COMPANY,
				query = "SELECT item FROM InventoryItem item WHERE" +
						" item.company.id = :" + ProjectConstants.ID_VAR),

		@NamedQuery(name = InventoryItem.QUERY_ALL_BY_COMPANY_BY_TYPE,
				query = "SELECT item FROM InventoryItem item WHERE" +
						" item.company.id = :" + ProjectConstants.ID_VAR + " AND" +
						" item.inventoryItemType = :" + ProjectConstants.VAR1),

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
						" item.parent.id = :" + ProjectConstants.ID_VAR),

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


	private String name = "";

	/*
	NULL parent is root
	 */
	@ManyToOne(cascade = {CascadeType.REFRESH})
	private InventoryItem parent;

	@OneToMany(mappedBy="parent", cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<InventoryItem> children = new ArrayList<>();

	/*
		Allows to keep track of item position in tree hierarchy and sort list -> Parent must be added before child
	 */
	private long level = 1;

	/*
		Allows to identify if Entity is a tool or a category
	 */
	@Enumerated(EnumType.STRING)
	private InventoryItemType inventoryItemType = InventoryItemType.TOOL;

	@OneToOne
	private Company company;


	/*
		TOOL PARAMETERS
	 */
	private String serialNumber = "";
	private String RF_Code = "";
	private String barcode = "";
	private String manufacturer = "";
	private String model = "";
	private String toolInfo = "";

	private boolean personal = false;

	@Enumerated(EnumType.STRING)
	private ToolUsageStatus usageStatus;

	@OneToOne
	private User currentUser;

	@OneToOne
	private User reservedUser;

	private LocalDate dateBought;
	private LocalDate dateNextMaintenance;

	private Double price;
	private Integer guarantee_months;

	@Embedded
	private Location currentLocation;

	private List<ToolReport> reports = new ArrayList<>();


	public InventoryItem() {}

	public InventoryItem(InventoryItem other) {
		this.name = other.name;
		this.parent = other.parent;
		this.children = other.children;
		this.level = other.level;
		this.inventoryItemType = other.inventoryItemType;
		this.company = other.company;
		this.serialNumber = other.serialNumber;
		this.RF_Code = other.RF_Code;
		this.barcode = other.barcode;
		this.manufacturer = other.manufacturer;
		this.model = other.model;
		this.toolInfo = other.toolInfo;
		this.personal = other.personal;
		this.usageStatus = other.usageStatus;
		this.currentUser = other.currentUser;
		this.reservedUser = other.reservedUser;
		this.dateBought = other.dateBought;
		this.dateNextMaintenance = other.dateNextMaintenance;
		this.price = other.price;
		this.guarantee_months = other.guarantee_months;
		this.currentLocation = other.currentLocation;
		this.reports = other.reports;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public InventoryItem getParent() {
		return parent;
	}
	public void setParent(InventoryItem parent) {
		if (parent == null) {
			this.parent = null;
			setLevel(1);
		} else {
			if (parent.equals(InventoryFacade.getInstance().getRootCategory())) {
				this.parent = null;
				setLevel(1);
			} else {
				this.parent = parent;
				setLevel((parent.getLevel() + 1));

				this.parent.addChild(this);
			}
		}
	}

	public List<InventoryItem> getChildren() {
		return children;
	}
	private void addChild(InventoryItem child) {
		children.add(child);
	}

	public long getLevel() {
		return level;
	}
	public void setLevel(long level) {
		this.level = level;
	}

	public InventoryItemType getInventoryItemType() {
		return inventoryItemType;
	}
	public void setInventoryItemType(InventoryItemType inventoryItemType) {
		this.inventoryItemType = inventoryItemType;
	}

	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}


	/*
		TOOL GETTERS & SETTERS
	 */
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

	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
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

	public boolean isPersonal() {
		return personal;
	}
	public void setPersonal(boolean personal) {
		this.personal = personal;
	}

	public ToolUsageStatus getUsageStatus() {
		return usageStatus;
	}
	public void setUsageStatus(ToolUsageStatus usageStatus) {
		this.usageStatus = usageStatus;
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
		return currentLocation;
	}
	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
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
