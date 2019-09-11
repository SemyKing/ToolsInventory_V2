package com.gmail.grigorij.backend.entities.inventory;

import com.gmail.grigorij.backend.embeddable.Location;
import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.inventory.InventoryHierarchyType;
import com.gmail.grigorij.backend.enums.inventory.ToolStatus;
import com.gmail.grigorij.ui.views.application.Inventory;

import javax.persistence.*;
import java.sql.Date;
import java.util.*;


/**
 * Entity works both as Category and Tool
 *
 * Grid element only accepts one object type
 *
 * Used by a TreeGrid in
 * @see Inventory
 *
 *
 * {@link #inventoryHierarchyType} defines if entity is a Category or a Tool
 *
 */
@Entity
@Table(name = "inventory_entities")
@NamedQueries({

		@NamedQuery(name="getAll",
				query="SELECT ie FROM InventoryItem ie"),

		@NamedQuery(name="getAllByHierarchyType",
				query="SELECT ie FROM InventoryItem ie WHERE" +
						" ie.inventoryHierarchyType = :type_var"),

		@NamedQuery(name="getAllInCompany",
				query="SELECT ie FROM InventoryItem ie WHERE" +
						" ie.company IS NOT NULL AND" +
						" ie.company.id = :company_id_var"),

		@NamedQuery(name="getAllCategoriesInCompany",
				query="SELECT ie FROM InventoryItem ie WHERE" +
						" ie.company IS NOT NULL AND" +
						" ie.company.id = :company_id_var and" +
						" ie.inventoryHierarchyType = :type_var"),

		@NamedQuery(name="getAllToolsInCompany",
				query="SELECT ie FROM InventoryItem ie WHERE" +
						" ie.company IS NOT NULL AND" +
						" ie.company.id = :company_id_var AND" +
						" ie.inventoryHierarchyType = :type_var"),

		@NamedQuery(name="getAllToolsInUseByUser",
				query="SELECT ie FROM InventoryItem ie WHERE" +
						" ie.inUseByUser.id = :user_id_var AND" +
						" ie.inventoryHierarchyType = :type_var"),

		@NamedQuery(name="getAllToolsReservedByUser",
				query="SELECT ie FROM InventoryItem ie WHERE" +
						" ie.reservedByUser.id = :user_id_var AND" +
						" ie.inventoryHierarchyType = :type_var"),

		@NamedQuery(name="getToolById",
				query="SELECT ie FROM InventoryItem ie WHERE" +
						" ie.id = :id_var"),

		@NamedQuery(name="getToolByCode",
				query="SELECT ie FROM InventoryItem ie WHERE" +
						" ie.qrCode = :code_var OR ie.barcode = :code_var"),

		@NamedQuery( name = "getToolsCount",
				query = "SELECT COUNT( ie.id ) FROM InventoryItem ie WHERE" +
						" ie.inventoryHierarchyType = :type_var"),
})
public class InventoryItem extends EntityPojo {

	/*
	NULL parentCategory is root category
	 */
	@ManyToOne(cascade={CascadeType.REFRESH})
	@JoinColumn(name="parent_id")
	private InventoryItem parentCategory;

	@OneToMany(mappedBy = "parentCategory", cascade={CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<InventoryItem> children = new HashSet<>();

	/*
	Allows to keep track of item position in tree hierarchy and sort list -> Parent must be added before child
	 */
	@Column(name = "level")
	private Integer level = 1;

	/*
	Allows to easily identify if Entity is a tool or a category
	 */
	@Enumerated( EnumType.STRING )
	private InventoryHierarchyType inventoryHierarchyType = InventoryHierarchyType.TOOL;


	@Column(name = "name")
	private String name = "";

	@Column(name = "qr_code")
	private String qrCode = "";

	@Column(name = "serial_number")
	private String serialNumber = "";

	@Column(name = "sn_code")
	private String snCode = "";

	@Column(name = "rf_code")
	private String rfCode = "";

	@Column(name = "barcode")
	private String barcode = "";

	@Column(name = "manufacturer")
	private String manufacturer = "";

	@Column(name = "model")
	private String model = "";

	@Column(name = "tool_info")
	private String toolInfo = "";

	@Column(name = "personal")
	private boolean personal = false;

	@Column(name = "owner")
	private String owner = "";

	@Column(name = "report_title")
	private String reportTitle = "";

	@Column(name = "report_message")
	private String reportMessage = "";

	@Enumerated(EnumType.STRING)
	private ToolStatus usageStatus;

	@OneToOne
	private Company company;

	@OneToOne
	private User inUseByUser;

	@OneToOne
	private User reservedByUser;

	@Column(name = "date_bought")
	private Date dateBought;

	@Column(name = "date_next_maintenance")
	private Date dateNextMaintenance;

	@Column(name = "price")
	private Double price = 0.00;

	@Column(name = "guarantee_months")
	private Integer guarantee_months = 0;

	@Embedded
	private Location currentLocation;


	//TODO: Last known GeoLocation


	public InventoryItem() {}

	public InventoryItem(InventoryItem tool) {
		this.setName(tool.getName());
		this.setManufacturer(tool.getManufacturer());
		this.setModel(tool.getModel());
		this.setToolInfo(tool.getToolInfo());
		this.setSnCode(tool.getSnCode());
		this.setBarcode(tool.getBarcode());
		this.setCompany(tool.getCompany());
		this.setParentCategory(tool.getParentCategory());
		this.setUsageStatus(tool.getUsageStatus());
		this.setDateBought(tool.getDateBought());
		this.setDateNextMaintenance(tool.getDateNextMaintenance());
		this.setPrice(tool.getPrice());
		this.setGuarantee_months(tool.getGuarantee_months());
		this.setAdditionalInfo(tool.getAdditionalInfo());
	}

	public InventoryItem getParentCategory() {
		return parentCategory;
	}
	public void setParentCategory(InventoryItem parentCategory) {
		this.parentCategory = parentCategory;
	}

	public Set<InventoryItem> getChildren() {
		return children;
	}
	public void setChildren(Set<InventoryItem> children) {
		if (children != null) {
			if (children.size() > 0) {
				for (InventoryItem child : children) {
					child.setLevel((this.level+1));
				}
			}
		}
		this.inventoryHierarchyType = InventoryHierarchyType.CATEGORY;
		this.children = children;
	}
	public void addChild(InventoryItem ie) {
		ie.setLevel((this.level+1));
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

	public ToolStatus getUsageStatus() {
		return usageStatus;
	}
	public void setUsageStatus(ToolStatus usageStatus) {
		this.usageStatus = usageStatus;
	}

	public boolean isPersonal() {
		return personal;
	}
	public void setPersonal(boolean personal) {
		this.personal = personal;
	}

	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getSnCode() {
		return snCode;
	}
	public void setSnCode(String snCode) {
		this.snCode = snCode;
	}

	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Date getDateBought() {
		return dateBought;
	}
	public void setDateBought(Date dateBought) {
		this.dateBought = dateBought;
	}

	public Date getDateNextMaintenance() {
		return dateNextMaintenance;
	}
	public void setDateNextMaintenance(Date dateNextMaintenance) {
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

	public Integer getLevel() {
		return level;
	}
	private void setLevel(Integer level) {
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

	public String getRfCode() {
		return rfCode;
	}
	public void setRfCode(String rfCode) {
		this.rfCode = rfCode;
	}

	public User getInUseByUser() {
		return inUseByUser;
	}
	public void setInUseByUser(User inUseByUser) {
		this.inUseByUser = inUseByUser;
	}

	public User getReservedByUser() {
		return reservedByUser;
	}
	public void setReservedByUser(User reservedByUser) {
		this.reservedByUser = reservedByUser;
	}

	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	public String getQrCode() {
		return qrCode;
	}
	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}


//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		sb.append("\nName: ").append(this.name);
//		sb.append("\nCompany: ").append(this.company.getName());
//		sb.append("\nHierarchyType ").append(this.inventoryHierarchyType.toString());
//
//		if (this.parentCategory != null) {
//			sb.append("\nParent Info: ").append(this.parentCategory);
//		}
//
//		return sb.toString();
//	}
}
