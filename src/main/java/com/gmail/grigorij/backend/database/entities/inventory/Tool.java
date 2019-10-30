package com.gmail.grigorij.backend.database.entities.inventory;

import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.entities.embeddable.ToolReport;
import com.gmail.grigorij.backend.database.enums.inventory.ToolUsageStatus;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Tool {

	private String name = "";
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

	private Double price = 0.00;
	private Integer guarantee_months = 0;

	@Embedded
	private Location currentLocation;

	private List<ToolReport> reports = new ArrayList<>();


	//TODO: Last known GeoLocation


	public Tool() {}


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
