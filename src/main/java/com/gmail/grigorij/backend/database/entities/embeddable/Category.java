package com.gmail.grigorij.backend.database.entities.embeddable;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
@AttributeOverride(name="name", column=@Column(name="category_name"))
public class Category {

	private String name = "";

	public Category() {}

	public Category(Category other) {
		this.name = other.name;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
