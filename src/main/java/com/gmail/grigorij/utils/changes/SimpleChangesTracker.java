package com.gmail.grigorij.utils.changes;

import java.util.LinkedHashMap;

public class SimpleChangesTracker<T> {

	private LinkedHashMap<Integer, Pair<T>> changesHashMap;


	public SimpleChangesTracker() {
		changesHashMap = new LinkedHashMap<>();
	}


	public LinkedHashMap<Integer, Pair<T>> getChangesHashMap() {
		return changesHashMap;
	}
	public void setChangesHashMap(LinkedHashMap<Integer, Pair<T>> changesHashMap) {
		this.changesHashMap = changesHashMap;
	}
}
