package com.gmail.grigorij.backend.entities.company;

import com.gmail.grigorij.backend.entities.location.Location;
import com.gmail.grigorij.backend.entities.user.Person;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "companies")
@NamedQueries({
		@NamedQuery(
				name="Company.findCompanyById",
				query="SELECT company FROM Company company WHERE company.id = :company_id"),
		@NamedQuery(
				name="Company.getAllCompanies",
				query="SELECT company FROM Company company ORDER BY company.companyName ASC")
})
public class Company extends Person {

	@Column(name = "name")
	private String companyName;

	@Column(name = "vat")
	private String companyVAT;


	@ManyToOne(cascade={CascadeType.ALL})
	@JoinColumn(name="location_id")
	private Location location;

	/*
	List of locations related to company: warehouses, construction sites, etc...
	 */
	@OneToMany(mappedBy = "location", cascade={CascadeType.ALL}, fetch = FetchType.LAZY)
	private Set<Location> locations = new HashSet<>();


	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyVAT() {
		return companyVAT;
	}
	public void setCompanyVAT(String companyVAT) {
		this.companyVAT = companyVAT;
	}

	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}

	public List<Location> getLocations() {
		return new ArrayList<>(locations);
	}
	public void setLocations(Set<Location> locations) {
		this.locations = locations;
	}
	public void addLocation(Location location) {
		this.locations.add(location);
	}

	public static Company getEmptyCompany() {
		Company company = new Company();
		company.setCompanyName("");
		company.setCompanyVAT("");
		company.setDeleted(false);
		company.setFirstName("");
		company.setLastName("");
		company.setEmail("");

		company.setLocation(Location.getEmptyLocation());

		return company;
	}
}
