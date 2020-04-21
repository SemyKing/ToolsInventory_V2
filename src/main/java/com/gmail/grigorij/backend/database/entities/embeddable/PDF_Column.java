package com.gmail.grigorij.backend.database.entities.embeddable;

import com.gmail.grigorij.backend.database.enums.tools.ToolParameter;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

@Embeddable
public class PDF_Column {

	// Helper for tracking changes
	@Transient
	private int counter;

	@Enumerated(EnumType.STRING)
	private ToolParameter parameter;

	private Float columnWidth;


	public PDF_Column() {}

	public PDF_Column(ToolParameter parameter) {
		this.parameter = parameter;
		this.columnWidth = parameter.getPrefWidth();
	}

	public PDF_Column(PDF_Column other) {
		this.counter = other.counter;
		this.parameter = other.parameter;
		this.columnWidth = other.columnWidth;
	}


	public ToolParameter getParameter() {
		return parameter;
	}
	public void setParameter(ToolParameter parameter) {
		this.parameter = parameter;
	}

	public Float getColumnWidth() {
		return columnWidth;
	}
	public void setColumnWidth(Float columnWidth) {
		this.columnWidth = columnWidth;
	}


	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}


	public String getParameterString() {
		String p = "";
		if (parameter != null) {
			p = parameter.getName();
		}
		return p;
	}

	public String getColumnWidthString() {
		String w = "";
		if (columnWidth != null) {
			w = String.valueOf(columnWidth);
		}
		return w;
	}

	@Override
	public String toString() {
		return getParameterString() + " " + getColumnWidthString();
	}
}
