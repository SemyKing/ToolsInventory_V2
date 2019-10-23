package com.gmail.grigorij.backend.database.entities;

import javax.persistence.*;

@MappedSuperclass
public class EntityPojo {

	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	@Column(name = "id", nullable = false, updatable = false)
	protected long id;

	@Column(name = "deleted", nullable = false)
	private boolean deleted = false;

	@Column(name = "additional_info")
	private String additionalInfo = "";


	protected EntityPojo() {}


	public Long getId() {
		return id;
	}
	public void setId( long id ) {
		this.id = id;
	}

	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted( boolean deleted ) {
		this.deleted = deleted;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
}
