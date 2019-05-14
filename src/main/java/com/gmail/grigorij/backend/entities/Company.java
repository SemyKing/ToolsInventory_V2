package com.gmail.grigorij.backend.entities;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "companies")
public class Company implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;

	@Column(name = "name")
	private String companyName;

	public String getCompanyName() {
		return companyName;
	}
}
