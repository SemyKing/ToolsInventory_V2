package com.gmail.grigorij.backend.database.enums.tools;

import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;

/**
 * USER can report tool (if allowed) as LOST, BROKEN & FOUND
 *
 * COMPANY ADMIN and above can set tool status to any
 *
 * FOREMAN can set status FREE (if tool is found by USER -> it must be accepted by at least FOREMAN)
 */
public enum ToolUsageStatus {

	IN_USE("In Use", PermissionLevel.COMPANY_ADMIN),
	RESERVED("Reserved", PermissionLevel.COMPANY_ADMIN),
	IN_USE_AND_RESERVED("In Use & Reserved", PermissionLevel.COMPANY_ADMIN),
	FREE("Free", PermissionLevel.FOREMAN),

	LOST("Lost", PermissionLevel.USER),
	BROKEN("Broken", PermissionLevel.USER),
	FOUND("Found", PermissionLevel.USER);

	private String name;
	private PermissionLevel level;

	ToolUsageStatus(String name, PermissionLevel level) {
		this.name = name;
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public PermissionLevel getLevel() {
		return level;
	}
}
