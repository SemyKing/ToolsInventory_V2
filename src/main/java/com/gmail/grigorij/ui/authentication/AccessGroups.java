package com.gmail.grigorij.ui.authentication;

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


	public static AccessGroups fromValue( int i ) {
		for ( AccessGroups type : AccessGroups.values()) {
			if ( type.value == i ) {
				return type;
			}
		}
		throw new IllegalArgumentException( Integer.toString( i ));
	}


	public static AccessGroups fromValue( String str ) {
		return valueOf( str );
	}
}
