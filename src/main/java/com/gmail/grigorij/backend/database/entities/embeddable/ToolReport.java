package com.gmail.grigorij.backend.database.entities.embeddable;

import com.gmail.grigorij.backend.database.enums.tools.ToolUsageStatus;

import javax.persistence.*;
import java.util.Date;


@Embeddable
@AttributeOverride(name="name", column=@Column(name="tool_report_name"))
public class ToolReport {

	private String name = "";

	@Enumerated(EnumType.STRING)
	private ToolUsageStatus toolStatus;

	@Embedded
	private Location lastKnownLocation;

	private String lastKnownLocationOptional = "";
	private String additionalInfo = "";

	private Date date;


	public ToolReport() {
		date = new Date();
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public ToolUsageStatus getToolStatus() {
		return toolStatus;
	}
	public void setToolStatus(ToolUsageStatus toolStatus) {
		this.toolStatus = toolStatus;
	}

	public Location getLastKnownLocation() {
		return lastKnownLocation;
	}
	public void setLastKnownLocation(Location lastKnownLocation) {
		this.lastKnownLocation = lastKnownLocation;
	}

	public String getLastKnownLocationOptional() {
		return lastKnownLocationOptional;
	}
	public void setLastKnownLocationOptional(String lastKnownLocationOptional) {
		this.lastKnownLocationOptional = lastKnownLocationOptional;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
