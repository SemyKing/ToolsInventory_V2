package com.gmail.grigorij.utils.converters;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.access.AccessGroups;
import com.gmail.grigorij.backend.access.Status;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.util.EnumSet;
import java.util.List;


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


	public static class StatusConverter implements Converter<Status, Boolean> {
		@Override
		public Result<Boolean> convertToModel(Status status, ValueContext valueContext) {
			try {
				return Result.ok(status.getBooleanValue());
			} catch (Exception e) {
				return Result.error("Status (enum) convert error");
			}
		}

		@Override
		public Status convertToPresentation(Boolean aBoolean, ValueContext valueContext) {
			for(Status status : EnumSet.allOf(Status.class)) {
				if (status.getBooleanValue() == aBoolean) {
					return status;
				}
			}
			return null;
		}
	}


	public static class CompanyConverter implements Converter<Company, Long> {
		@Override
		public Result<Long> convertToModel(Company company, ValueContext valueContext) {
			try {
				return Result.ok(company.getId());
			} catch (Exception e) {
				return Result.error("Status select error");
			}
		}

		@Override
		public Company convertToPresentation(Long longId, ValueContext valueContext) {
			List<Company> companies = CompanyFacade.getInstance().getAllCompanies();

			for (Company company : companies) {
				if (company.getId().equals(longId)) {
					return company;
				}
			}
			return null;
		}
	}
}
