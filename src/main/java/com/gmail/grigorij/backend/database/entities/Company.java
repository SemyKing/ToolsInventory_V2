package com.gmail.grigorij.backend.database.entities;

import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.entities.embeddable.Person;
import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "companies")
@NamedQueries({
		@NamedQuery(name=Company.QUERY_ALL,
				query="SELECT company FROM Company company ORDER BY company.name ASC"),

		@NamedQuery(name=Company.QUERY_BY_ID,
				query="SELECT company FROM Company company WHERE company.id = :" + ProjectConstants.ID_VAR)
})
public class Company extends EntityPojo {

	public static final String QUERY_ALL = "get_all_companies";
	public static final String QUERY_BY_ID = "get_company_by_id";

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

	@Column(columnDefinition = "text")
	private String announcement = "";

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "pdf_template_id")
	private PDF_Template pdf_template;


	public Company() {}

	public Company(Company other) {
		this.name = other.name;
		this.vat = other.vat;
		this.address = other.address;
		this.contactPerson = other.contactPerson;

		for (Location location : other.locations) {
			this.locations.add(new Location(location));
		}
		this.announcement = other.announcement;
		this.pdf_template =  other.pdf_template;

		this.setAdditionalInfo(other.getAdditionalInfo());
	}


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

	public String getAnnouncement() {
		return announcement;
	}
	public void setAnnouncement(String announcement) {
		this.announcement = announcement;
	}

	public PDF_Template getPdf_template() {
		return pdf_template;
	}
	public void setPdf_template(PDF_Template pdf_template) {
		this.pdf_template = pdf_template;
	}


	public String getContactPersonString() {
		String cp = "";
		if (contactPerson != null) {
			cp = contactPerson.getFullName();
		}
		return cp;
	}

	public String getAddressString() {
		String a = "";
		if (address != null) {
			a = address.toString();
		}
		return a;
	}
}
