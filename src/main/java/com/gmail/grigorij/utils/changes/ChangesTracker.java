package com.gmail.grigorij.utils.changes;

import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import java.util.LinkedHashMap;

public class ChangesTracker<T> extends CustomDialog {

	private LinkedHashMap<Integer, Pair<T>> changesHashMap = new LinkedHashMap<>();


	public ChangesTracker() {}


	public LinkedHashMap<Integer, Pair<T>> getChangesHashMap() {
		return changesHashMap;
	}
	public void setChangesHashMap(LinkedHashMap<Integer, Pair<T>> changesHashMap) {
		this.changesHashMap = changesHashMap;
	}
}
