package com.gmail.grigorij.backend.entities.tool;

import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.entities.location.Location;
import com.gmail.grigorij.backend.entities.user.User;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


/**
 * Tool entity class works as both Category and Tool
 *
 * Used by a TreeGrid in
 * @see com.gmail.grigorij.ui.views.navigation.inventory.Inventory
 *
 *
 * if {@link #children} is empty, entity is a tool else it is a Category
 *
 */

@Entity
@Table(name = "tools")
@NamedQueries({
		@NamedQuery(name="Tool.getAllToolsList",
				query="SELECT tool FROM Tool tool"),
		@NamedQuery(name="Tool.getAllToolsListInCompany",
				query="SELECT tool FROM Tool tool WHERE tool.companyId = :id")
})
public class Tool extends EntityPojo {

	@ManyToOne(cascade={CascadeType.ALL})
	@JoinColumn(name="parent_id")
	private Tool parent;

	@OneToMany(mappedBy = "parent", cascade={CascadeType.ALL}, fetch = FetchType.LAZY)
	private Set<Tool> children = new HashSet<>();


	/*
	Allows to keep track of item position in tree hierarchy and sort list -> Parent must be added before child
	 */
	@Column(name = "level")
	private Integer level = 1;

	@Enumerated(EnumType.STRING)
	private HierarchyType hierarchyType = HierarchyType.TOOL;


	public Tool() {
	}


	@Column(name = "name")
	private String name;

	@Column(name = "company_id")
	private long companyId;

	@Column(name = "manufacturer")
	private String manufacturer;

	@Column(name = "model")
	private String model;

	@Column(name = "tool_info")
	private String toolInfo;

	@Enumerated(EnumType.STRING)
	private ToolStatus status = null;

	@Column(name = "bPersonal")
	private boolean bPersonal;

	@Column(name = "owner")
	private String owner;

	@Column(name = "in_use_by_user_id")
	private long inUseByUserId = -1;

	@Column(name = "reserved_by_user_id")
	private long reservedByUserId = -1;

	@Column(name = "sn_code")
	private String snCode;

	@Column(name = "barcode")
	private String barcode;

	@Column(name = "date_bought")
	private LocalDate dateBought;

	@Column(name = "date_next_maintenance")
	private LocalDate dateNextMaintenance;

	@Column(name = "price")
	private Double price;

	@Column(name = "guarantee_months")
	private Integer guarantee_months;

	@Embedded
	private Location currentLocation;


	public Tool getParent() {
		return parent;
	}
	public void setParent(Tool parent) {
		this.parent = parent;
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

	public ToolStatus getStatus() {
		return status;
	}
	public void setStatus(ToolStatus status) {
		this.status = status;
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

	public Integer getLevel() {
		return level;
	}
	private void setLevel(Integer level) {
		this.level = level;
	}

	public HierarchyType getHierarchyType() {
		return hierarchyType;
	}
	public void setHierarchyType(HierarchyType hierarchyType) {
		this.hierarchyType = hierarchyType;
	}
}
