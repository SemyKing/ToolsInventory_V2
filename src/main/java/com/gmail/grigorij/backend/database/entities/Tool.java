package com.gmail.grigorij.backend.database.entities;

import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.entities.embeddable.ToolReport;
import com.gmail.grigorij.backend.database.enums.tools.ToolUsageStatus;
import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tool")
@NamedQueries({
		@NamedQuery(name = Tool.QUERY_ALL,
				query = "SELECT tool FROM Tool tool ORDER BY tool.name ASC"),

		@NamedQuery(name = Tool.QUERY_ALL_BY_COMPANY_ID,
				query = "SELECT tool FROM Tool tool WHERE" +
						" tool.company.id = :" + ProjectConstants.ID_VAR + " ORDER BY tool.name ASC"),

		@NamedQuery(name = Tool.QUERY_ALL_BY_CURRENT_USER,
				query = "SELECT tool FROM Tool tool WHERE" +
						" tool.currentUser.id = :" + ProjectConstants.ID_VAR + " ORDER BY tool.name ASC"),

		@NamedQuery(name = Tool.QUERY_ALL_BY_RESERVED_USER,
				query = "SELECT tool FROM Tool tool WHERE" +
						" tool.reservedUser.id = :" + ProjectConstants.ID_VAR + " ORDER BY tool.name ASC"),

		@NamedQuery(name = Tool.QUERY_BY_ID,
				query = "SELECT tool FROM Tool tool WHERE" +
						" tool.id = :" + ProjectConstants.ID_VAR),

		@NamedQuery(name = Tool.QUERY_BY_CODE_VAR,
				query = "SELECT tool FROM Tool tool WHERE" +
						" tool.serialNumber = :" + ProjectConstants.VAR1 + " OR tool.barcode = :" + ProjectConstants.VAR1)
})
public class Tool extends EntityPojo {

	public static final String QUERY_ALL = "get_all_tools";
	public static final String QUERY_ALL_BY_COMPANY_ID = "get_all_tools_by_company_id";


	public static final String QUERY_ALL_BY_CURRENT_USER = "get_all_tools_by_current_user";
	public static final String QUERY_ALL_BY_RESERVED_USER = "get_all_by_tools_reserved_user";

	public static final String QUERY_BY_ID = "get_tool_by_id";
	public static final String QUERY_BY_CODE_VAR = "get_tools_by_code";



	@OneToOne
	private Company company;

	private String name = "";
	private String serialNumber = "";
	private String RF_Code = "";
	private String barcode = "";
	private String manufacturer = "";
	private String model = "";
	private String toolInfo = "";

	@Enumerated(EnumType.STRING)
	private ToolUsageStatus usageStatus;

	@OneToOne
	private User currentUser;

	@OneToOne
	private User reservedUser;

	private LocalDate dateBought;
	private LocalDate dateNextMaintenance;

	private Double price = 0.00;
	private Integer guarantee_months = 0;

	@Embedded
	private Location currentLocation;

	@OneToOne
	private Category category;

	private List<ToolReport> reports = new ArrayList<>();


	public Tool() {}

	public Tool(Tool other) {
		this.name = other.name;
		this.company = other.company;
		this.serialNumber = other.serialNumber;
		this.RF_Code = other.RF_Code;
		this.barcode = other.barcode;
		this.manufacturer = other.manufacturer;
		this.model = other.model;
		this.toolInfo = other.toolInfo;
		this.usageStatus = other.usageStatus;
		this.currentUser = other.currentUser;
		this.reservedUser = other.reservedUser;
		this.dateBought = other.dateBought;
		this.dateNextMaintenance = other.dateNextMaintenance;
		this.price = other.price;
		this.guarantee_months = other.guarantee_months;
		this.currentLocation = other.currentLocation;
		this.reports = other.reports;
		this.category = other.category;
	}



	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}


	public String getCurrentLocationString() {
		String location = "";

		if (currentLocation != null) {
			location = currentLocation.getName();
		}

		return location;
	}

	public String getUsageStatusString() {
		String status = "";

		if (usageStatus != null) {
			status = usageStatus.getName();
		}

		return status;
	}

	public String getCategoryString() {
		String category = "";

		if (this.category != null) {
			category = this.category.getName();
		}

		return category;
	}

	public String getCompanyString() {
		String company = "";

		if (this.company != null) {
			company = this.company.getName();
		}

		return company;
	}

	public String getCurrentUserString() {
		String currentUser = "";

		if (this.currentUser != null) {
			currentUser = this.currentUser.getFullName();
		}

		return currentUser;
	}

	public String getReservedUserString() {
		String reservedUser = "";

		if (this.reservedUser != null) {
			reservedUser = this.reservedUser.getFullName();
		}

		return reservedUser;
	}
}
