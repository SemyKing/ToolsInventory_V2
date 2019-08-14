package com.gmail.grigorij.utils.converters;

import com.gmail.grigorij.backend.entities.access.AccessGroups;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.util.EnumSet;

public class CustomConverter {

	public static class AccessGroupsConverter implements Converter<AccessGroups, Integer> {
		@Override
		public Result<Integer> convertToModel(AccessGroups accessGroups, ValueContext valueContext) {
			try {
				return Result.ok(accessGroups.getIntValue());
			} catch (Exception e) {
				return Result.error("AccessGroups (enum) convert error");
			}
		}

		@Override
		public AccessGroups convertToPresentation(Integer integer, ValueContext valueContext) {
			for(AccessGroups accessGroup : EnumSet.allOf(AccessGroups.class)) {
				if (accessGroup.getIntValue() == integer) {
					return accessGroup;
				}
			}
			return null;
		}
	}

	public static class StatusConverter implements Converter<String, Boolean> {
		@Override
		public Result<Boolean> convertToModel(String status, ValueContext valueContext) {
			try {
				return Result.ok(status.equals(ProjectConstants.INACTIVE));
			} catch (Exception e) {
				return Result.error("Status (enum) convertToModel error");
			}
		}

		@Override
		public String convertToPresentation(Boolean aBoolean, ValueContext valueContext) {
			return aBoolean ? ProjectConstants.INACTIVE : ProjectConstants.ACTIVE;
		}
	}
}
