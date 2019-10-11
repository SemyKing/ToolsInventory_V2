package com.gmail.grigorij.backend.entities.company;

import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.embeddable.Location;
import com.gmail.grigorij.backend.embeddable.Person;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "companies")
@NamedQueries({
		@NamedQuery(
				name="getAllCompanies",
				query="SELECT company FROM Company company ORDER BY company.name ASC"),

		@NamedQuery(
				name="getCompanyById",
				query="SELECT company FROM Company company WHERE company.id = :company_id")
})
public class Company extends EntityPojo {

	@Column(name = "name")
	private String name = "";

	@Column(name = "vat")
	private String vat = "";

	@Embedded
	private Location address = new Location();

	@Embedded
	private Person contactPerson = new Person();

	@ElementCollection
	private List<Location> locations = new ArrayList<>();


	public Company() {}


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
		return address;
	}
	public void setAddress(Location address) {
		this.address = address;
	}

	public List<Location> getLocations() {
		return locations;
	}
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}
	public void addLocation(Location location) {
		this.locations.add(location);
	}

	public Person getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(Person contactPerson) {
		this.contactPerson = contactPerson;
	}
}
