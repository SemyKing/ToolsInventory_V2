package com.gmail.grigorij.backend.database.entities.inventory;

import javax.persistence.Embeddable;


@Embeddable
public class Category {

	private String name = "";


	public Category() {}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
