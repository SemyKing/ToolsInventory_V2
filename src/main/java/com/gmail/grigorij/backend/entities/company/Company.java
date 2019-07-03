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
				query="SELECT company FROM Company company ORDER BY company.name ASC")
})
public class Company extends Person {

	@Column(name = "name")
	private String name;

	@Column(name = "vat")
	private String vat;


	@Embedded
	private Location address;

	/*
	List of locations related to company: warehouses, construction sites, etc...
	 */
	@ElementCollection
	private Set<Location> locations = new HashSet<>();


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getVat() {
		return vat;
	}
	public void setVat(String vat) {
		this.vat = vat;
	}

	public Location getAddress() {
		return (this.address == null) ? new Location() : address;
	}
	public void setAddress(Location address) {
		this.address = address;
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
	public void removeLocation(Location location) {
		this.locations.remove(location);
	}

	public static Company getEmptyCompany() {
		Company company = new Company();
		company.setName("");
		company.setVat("");
		company.setDeleted(false);
		company.setFirstName("");
		company.setLastName("");
		company.setEmail("");

		company.setAddress(new Location());

		return company;
	}
}
