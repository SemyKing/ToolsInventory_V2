package com.gmail.grigorij.backend.entities.tool;

import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.entities.location.Location;

import javax.persistence.*;
import java.sql.Date;
import java.util.*;


/**
 * Tool entity class works both as Category and Tool
 *
 * Grid element only accepts one object type
 *
 * Used by a TreeGrid in
 * @see com.gmail.grigorij.ui.views.navigation.inventory.Inventory
 *
 *
 * if {@link #children} is empty or {@link #hierarchyType} is HierarchyType.TOOL, entity is a tool else it is a Category
 *
 */

@Entity
@Table(name = "tools")
@NamedQueries({
		@NamedQuery(name="Tool.getAll",
				query="SELECT tool FROM Tool tool"),
		@NamedQuery(name="Tool.getAllInCompany",
				query="SELECT tool FROM Tool tool WHERE tool.companyId = :id_var")
})
public class Tool extends EntityPojo {

	/*
	NULL parentCategory is root category
	 */
	@ManyToOne(cascade={CascadeType.REFRESH})
	@JoinColumn(name="parent_id")
	private Tool parentCategory;

	@OneToMany(mappedBy = "parentCategory", cascade={CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Tool> children = new HashSet<>();

	/*
	Allows to keep track of item position in tree hierarchy and sort list -> Parent must be added before child
	 */
	@Column(name = "level")
	private Integer level = 1;

	/*
	Allows to easily identify if Tool is a tool or category
	 */
	@Enumerated(EnumType.STRING)
	private HierarchyType hierarchyType = HierarchyType.TOOL;


	@Column(name = "name")
	private String name;

	@Column(name = "company_id")
	private long companyId;

	@Column(name = "serial_number")
	private String serialNumber;

	@Column(name = "sn_code")
	private String snCode;

	@Column(name = "rf_code")
	private String rfCode;

	@Column(name = "barcode")
	private String barcode;

	@Column(name = "manufacturer")
	private String manufacturer;

	@Column(name = "model")
	private String model;

	@Column(name = "tool_info")
	private String toolInfo;

	@Enumerated(EnumType.STRING)
	private ToolStatus usageStatus = null;

	@Column(name = "bPersonal")
	private boolean bPersonal;

	@Column(name = "owner")
	private String owner;

	@Column(name = "in_use_by_user_id")
	private long inUseByUserId = -1;

	@Column(name = "reserved_by_user_id")
	private long reservedByUserId = -1;

	@Column(name = "date_bought")
	private Date dateBought;

	@Column(name = "date_next_maintenance")
	private Date dateNextMaintenance;

	@Column(name = "price")
	private Double price;

	@Column(name = "guarantee_months")
	private Integer guarantee_months;

	@Embedded
	private Location currentLocation;

	//TODO: Last known GeoLocation


	public Tool() {}

	public Tool(Tool tool) {
		this.setName(tool.getName());
		this.setManufacturer(tool.getManufacturer());
		this.setModel(tool.getModel());
		this.setToolInfo(tool.getToolInfo());
		this.setSnCode(tool.getSnCode());
		this.setBarcode(tool.getBarcode());
		this.setCompanyId(tool.getCompanyId());
		this.setParentCategory(tool.getParentCategory());
		this.setUsageStatus(tool.getUsageStatus());
		this.setDateBought(tool.getDateBought());
		this.setDateNextMaintenance(tool.getDateNextMaintenance());
		this.setPrice(tool.getPrice());
		this.setGuarantee_months(tool.getGuarantee_months());
		this.setAdditionalInfo(tool.getAdditionalInfo());
	}

	public Tool getParentCategory() {
		return parentCategory;
	}
	public void setParentCategory(Tool parentCategory) {
		this.parentCategory = parentCategory;
	}

	public Set<Tool> getChildren() {
		return children;
	}
	public void setChildren(Set<Tool> children) {
		if (children != null) {
			if (children.size() > 0) {
				for (Tool child : children) {
					child.setLevel((this.level+1));
				}
			}
		}
		this.hierarchyType = HierarchyType.CATEGORY;
		this.children = children;
	}
	public void addTool(Tool tool) {
		tool.setLevel((this.level+1));
		this.hierarchyType = HierarchyType.CATEGORY;
		this.children.add(tool);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
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

	public boolean isbPersonal() {
		return bPersonal;
	}
	public void setbPersonal(boolean bPersonal) {
		this.bPersonal = bPersonal;
	}

	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}

	public long getInUseByUserId() {
		return inUseByUserId;
	}
	public void setInUseByUserId(long inUseByUserId) {
		this.inUseByUserId = inUseByUserId;
	}

	public long getReservedByUserId() {
		return reservedByUserId;
	}
	public void setReservedByUserId(long reservedByUserId) {
		this.reservedByUserId = reservedByUserId;
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
	public void setLevel(Integer level) {
		this.level = level;
	}

	public HierarchyType getHierarchyType() {
		return hierarchyType;
	}
	public void setHierarchyType(HierarchyType hierarchyType) {
		this.hierarchyType = hierarchyType;
	}

	public boolean hasChildren() {
		return (this.getChildren().size() > 0);
	}


	public static Tool getEmptyTool() {
		Tool t = new Tool();
		t.setName("");
		t.setParentCategory(null);
		t.setCompanyId(-1);
		t.setManufacturer("");
		t.setModel("");
		t.setToolInfo("");
		t.setOwner("");
		t.setSnCode("");
		t.setBarcode("");
		t.setDateBought(null);
		t.setDateNextMaintenance(null);
		t.setPrice(null);
		t.setGuarantee_months(null);

		return t;
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


//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		sb.append("\nName: ").append(this.name);
//		sb.append("\nCompany Id: ").append(this.companyId);
//		sb.append("\nHierarchyType ").append(this.hierarchyType.toString());
//
//		if (this.parentCategory != null) {
//			sb.append("\nParent Info: ").append(this.parentCategory);
////			sb.append("\nParent Name: ").append(this.parentCategory.getName());
//		}
//
//		return sb.toString();
//	}
}
