package com.gmail.grigorij.backend.entities;

import javax.persistence.*;

@MappedSuperclass
public class EntityPojo {

	@Id
	@GeneratedValue( strategy= GenerationType.AUTO )
	@Column( name = "id", nullable = false, insertable = true, updatable = false )
	protected long id;

	@Column(name = "deleted", nullable = false)
	private boolean deleted = false;


	public long getId() {
		return id;
	}

//	public void setId( long id ) {
//		this.id = id;
//	}


	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted( boolean deleted ) {
		this.deleted = deleted;
	}
}
