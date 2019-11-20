package com.gmail.grigorij.backend.database.entities.embeddable;

import com.gmail.grigorij.backend.database.enums.tools.ToolParameter;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class PDF_Column {

	@Enumerated(EnumType.STRING)
	private ToolParameter parameter;

	private Float userSetWidth;


	public PDF_Column() {}

	public PDF_Column(ToolParameter parameter) {
		this.parameter = parameter;
		this.userSetWidth = parameter.getPrefWidth();
	}

	public PDF_Column(PDF_Column other) {
		this.parameter = other.parameter;
		this.userSetWidth = other.userSetWidth;
	}

	public ToolParameter getParameter() {
		return parameter;
	}
	public void setParameter(ToolParameter parameter) {
		this.parameter = parameter;
	}

	public Float getUserSetWidth() {
		return userSetWidth;
	}
	public void setUserSetWidth(Float userSetWidth) {
		this.userSetWidth = userSetWidth;
	}
}
