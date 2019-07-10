package com.gmail.grigorij.utils.converters;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.ToolFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.access.AccessGroups;
import com.gmail.grigorij.backend.access.EntityStatus;
import com.gmail.grigorij.backend.entities.tool.Tool;
import com.gmail.grigorij.backend.entities.tool.ToolStatus;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.utils.ProjectConstants;
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
			if (longId == null) {
				return null;
			} else {
				return CompanyFacade.getInstance().findCompanyById(longId);
			}
		}
	}

	public static class ToolCategoryConverter implements Converter<Tool, Tool> {
		@Override
		public Result<Tool> convertToModel(Tool category, ValueContext valueContext) {
			try {
				if (category == null || category.equals(ToolFacade.getInstance().getRootCategory())) {
					return Result.ok(null);
				} else {
					return Result.ok(category);
				}
			} catch (Exception e) {
				return Result.error("(ToolCategoryConverter) Tool category convertToModel error");
			}
		}

		@Override
		public Tool convertToPresentation(Tool toolParent, ValueContext valueContext) {
			if (toolParent == null) {
				return ToolFacade.getInstance().getRootCategory();
			} else {
				return toolParent;
			}
		}
	}

//	public static class ToolUser implements Converter<String, User> {
//		@Override
//		public Result<User> convertToModel(String s, ValueContext valueContext) {
//			if (s.length() <= 0) {
//				return null;
//			} else {
//				return Result.ok(UserFacade.getInstance().getUserByUsername(s));
//			}
//		}
//
//		@Override
//		public String convertToPresentation(User user, ValueContext valueContext) {
//			if (user == null) {
//				return "";
//			} else {
//				return user.getUsername();
//			}
//		}
//	}

//	public static class ToolUser implements Converter<User, String> {
//
//		@Override
//		public Result<String> convertToModel(User user, ValueContext valueContext) {
//			if (user == null) {
//				return Result.ok("");
//			} else {
//				return Result.ok(user.getUsername());
//			}
//		}
//
//		@Override
//		public User convertToPresentation(String s, ValueContext valueContext) {
//			if (s.length() <= 0) {
//				return null;
//			} else {
//				return UserFacade.getInstance().getUserByUsername(s);
//			}
//		}
//	}

//	public static class ToolUser implements Converter<User, User> {
//
//	}
}
