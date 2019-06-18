package com.gmail.grigorij.backend.entities.tool;

import com.gmail.grigorij.backend.entities.EntityPojo;

import javax.persistence.Column;

public class Tool extends EntityPojo {

	@Column(name = "name")
	private String name;

	@Column(name = "manufacturer")
	private String manufacturer;

	@Column(name = "model")
	private String model;

	@Column(name = "tool_info")
	private String toolInfo;

	@Column(name = "bPersonal")
	private boolean bPersonal;

	@Column(name = "owner")
	private String owner;



}
