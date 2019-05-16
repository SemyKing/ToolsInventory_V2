package com.gmail.grigorij.backend.entities.user;

public enum AccessGroups {

	VIEW_ONLY_USER  ( 1 ),
	NORMAL_USER     ( 2 ),
	FOREMAN         ( 3 ),
	EMPLOYER        ( 4 ),
	ADMIN           ( 5 );


	private int value;

	AccessGroups( int value ) {
		this.value = value;
	}


	public int value() {
		return this.value;
	}
}
