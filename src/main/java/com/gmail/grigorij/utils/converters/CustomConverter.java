package com.gmail.grigorij.utils.converters;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.access.AccessGroups;
import com.gmail.grigorij.backend.access.EntityStatus;
import com.gmail.grigorij.backend.entities.tool.Tool;
import com.gmail.grigorij.backend.entities.tool.ToolStatus;
import com.gmail.grigorij.backend.entities.user.User;
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


	public static class StatusConverter implements Converter<EntityStatus, Boolean> {
		@Override
		public Result<Boolean> convertToModel(EntityStatus status, ValueContext valueContext) {
			try {
				return Result.ok(status.getBooleanValue());
			} catch (Exception e) {
				return Result.error("Status (enum) convertToModel error");
			}
		}

		@Override
		public EntityStatus convertToPresentation(Boolean aBoolean, ValueContext valueContext) {
			for(EntityStatus status : EnumSet.allOf(EntityStatus.class)) {
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
				return Result.error("Company convertToModel error");
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


	public static class ToolCategoryConverter implements Converter<Tool, Tool> {
		@Override
		public Result<Tool> convertToModel(Tool category, ValueContext valueContext) {
			try {
				if (category == null) {
					return null;
				} else {
					return Result.ok(category);
				}
			} catch (Exception e) {
				return Result.error("Tool category convertToModel error");
			}
		}

		@Override
		public Tool convertToPresentation(Tool toolParent, ValueContext valueContext) {
			return toolParent;
		}
	}


	public static class UserById implements Converter<String, Long> {
		@Override
		public Result<Long> convertToModel(String username, ValueContext valueContext) {
			try {
				if (username == null) {
					return null;
				} else {
					if (username.length() <= 0) {
						return Result.ok(-1L);
					}

					return Result.ok(UserFacade.getInstance().findUserInDatabaseByUsername(username).getId());
				}
			} catch (Exception e) {
				return Result.error("Company convertToModel error");
			}
		}

		@Override
		public String convertToPresentation(Long id, ValueContext valueContext) {
			if (id == null) {
				return "";
			} else {
				if (id < 0) {
					return "";
				}

				return UserFacade.getInstance().getUserById(id).getUsername();
			}
		}
	}
}
