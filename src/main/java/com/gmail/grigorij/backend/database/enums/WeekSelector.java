package com.gmail.grigorij.backend.database.enums;

public enum WeekSelector {
	THIS_WEEK("This week"),
	NEXT_WEEK("Next week"),
	AFTER_TWO_WEEKS("After 2 weeks"),
	AFTER_THREE_WEEKS("After 3 weeks"),
	AFTER_FOUR_WEEKS("After 4 weeks");

	private String name;

	WeekSelector(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
