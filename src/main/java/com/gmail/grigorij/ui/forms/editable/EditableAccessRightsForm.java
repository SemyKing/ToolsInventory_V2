package com.gmail.grigorij.ui.forms.editable;

import com.gmail.grigorij.backend.database.facades.AccessRightFacade;
import com.gmail.grigorij.backend.embeddable.AccessRight;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.enums.permissions.Permission;
import com.gmail.grigorij.backend.enums.permissions.PermissionOperation;
import com.gmail.grigorij.backend.enums.transactions.TransactionTarget;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.components.Divider;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.select.Select;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class EditableAccessRightsForm extends FormLayout {

	private static final String CLASS_NAME = "access-rights-form";

	private User targetUser;
	private User currentUser;

	private List<AccessRight> accessRights = new ArrayList<>();

	private boolean isNew;

	public EditableAccessRightsForm() {
		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S, CLASS_NAME);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
	}

	private void constructFormLayout() {
		add(UIUtils.createH3Label((isNew) ? "New User" : targetUser.getFullName()));

		if (!isNew) {
			add(UIUtils.createH4Label(targetUser.getCompany().getName()));
		}

		add(new Span());

		//USER VIEWING SOMEONE ELSE'S (NEW USER'S) ACCESS RIGHTS -> MUST HAVE COMPANY OR SYSTEM RIGHTS
		if (isNew) {
			if (!AccessRightFacade.getInstance().isUserAllowedTo(PermissionOperation.VIEW_USER_ACCESS_RIGHTS, PermissionLevel.COMPANY, PermissionLevel.SYSTEM)) {
				System.out.println("NOT ALLOWED TO VIEW OTHER'S ACCESS RIGHTS");
				return;
			}
		} else {
			//TARGET USER EXISTS IN DATABASE

			//USER EDITING SOMEONE ELSE'S ACCESS RIGHTS
			if (!targetUser.getId().equals(currentUser.getId())) {

				//MUST HAVE COMPANY OR SYSTEM RIGHTS
				if (!AccessRightFacade.getInstance().isUserAllowedTo(PermissionOperation.VIEW_USER_ACCESS_RIGHTS, PermissionLevel.COMPANY, PermissionLevel.SYSTEM)) {
					System.out.println("NOT ALLOWED TO VIEW OTHER'S ACCESS RIGHTS");
					return;
				}

				//DON'T ALLOW TO VIEW IF TARGET USER'S PERMISSION_LEVEL IS HIGHER THAN CURRENT USER'S LEVEL
				if (targetUser.getAccessGroup().getPermissionLevel().higherThan(currentUser.getAccessGroup().getPermissionLevel())) {
					System.out.println("TARGET USER'S PERMISSION LEVEL IS HIGHER");
					return;
				}
			}
		}

		FlexBoxLayout selectorsHeaderLayout = new FlexBoxLayout();
		selectorsHeaderLayout.setFlexDirection(FlexDirection.ROW);
		selectorsHeaderLayout.setAlignItems(FlexComponent.Alignment.CENTER);

		add(selectorsHeaderLayout);


		List<Select<Permission>> ownPermissionSelectors = new ArrayList<>();
		Select<Permission> ownHeaderSelector = constructPermissionSelector(null);
		boolean ownHeaderSet = false;

		List<Select<Permission>> companyPermissionSelectors = new ArrayList<>();
		Select<Permission> companyHeaderSelector = constructPermissionSelector(null);
		boolean companyHeaderSet = false;

		List<Select<Permission>> systemPermissionSelectors = new ArrayList<>();
		Select<Permission> systemHeaderSelector = constructPermissionSelector(null);
		boolean systemHeaderSet = false;

		List<Select<Permission>> visibilityPermissionSelectors = new ArrayList<>();
		Select<Permission> visibilityHeaderSelector = constructPermissionSelector(null);
		boolean visibilityHeaderSet = false;

		TransactionTarget previousTarget = null;


		for (AccessRight accessRight : accessRights) {

			// IF USER IS NOT SYSTEM ADMIN DON'T SHOW HIDDEN RIGHTS
			if (currentUser.getAccessGroup().getPermissionLevel().lowerThan(PermissionLevel.SYSTEM)) {
				if (!AccessRightFacade.getInstance().isUserAllowedToSeeAccessRight(accessRight)) {
					System.out.println("ROW SET TO INVISIBLE");
					continue;
				}
			}

			if (previousTarget != null) {
				if (!accessRight.getPermissionOperation().getTarget().equals(previousTarget)) {
					add(new Divider(2, Vertical.XS));
				}
			}
			previousTarget = accessRight.getPermissionOperation().getTarget();


			add(new Span(accessRight.getPermissionOperation().getStringValue()));


			FlexBoxLayout selectorsLayout = new FlexBoxLayout();
			selectorsLayout.setFlexDirection(FlexDirection.ROW);
			selectorsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

			PermissionLevel[] levels;

			// USER EDITING OWN RIGHTS
			if (currentUser.getId().equals(targetUser.getId())) {
				levels = new PermissionLevel[]{PermissionLevel.OWN};
			} else {
				levels = new PermissionLevel[]{PermissionLevel.COMPANY, PermissionLevel.SYSTEM};
			}

			Select<Permission> ownSelector = constructPermissionSelector(accessRight.getPermissionOwn());
			selectorsLayout.add(ownSelector);
			ownPermissionSelectors.add(ownSelector);

			boolean isUserAllowedTo = (!AccessRightFacade.getInstance().isUserAllowedTo(PermissionOperation.EDIT_USER_ACCESS_RIGHTS, levels));

			if (isUserAllowedTo) {
				ownSelector.setEnabled(false);
			} else {
				ownSelector.addValueChangeListener(event -> {
					accessRight.setPermissionOwn(ownSelector.getValue());
				});
			}

			if (!ownHeaderSet) {
				handleSelectorHeader(selectorsHeaderLayout, ownHeaderSelector, "Own", ownPermissionSelectors, PermissionLevel.OWN);
				ownHeaderSet = true;
			}


			if (currentUser.getAccessGroup().getPermissionLevel().higherOrEqualsTo(PermissionLevel.COMPANY)) {
				Select<Permission> companySelector = constructPermissionSelector(accessRight.getPermissionCompany());
				selectorsLayout.add(companySelector);
				companyPermissionSelectors.add(companySelector);

				if (isUserAllowedTo) {
					companySelector.setEnabled(false);
				} else {
					companySelector.addValueChangeListener(event -> {
						accessRight.setPermissionCompany(companySelector.getValue());
					});
				}

				if (!companyHeaderSet) {
					handleSelectorHeader(selectorsHeaderLayout, companyHeaderSelector, "Company", companyPermissionSelectors, PermissionLevel.COMPANY);
					companyHeaderSet = true;
				}
			}

			if (currentUser.getAccessGroup().getPermissionLevel().higherOrEqualsTo(PermissionLevel.SYSTEM)) {
				Select<Permission> systemSelector = constructPermissionSelector(accessRight.getPermissionSystem());
				selectorsLayout.add(systemSelector);
				systemPermissionSelectors.add(systemSelector);

				if (isUserAllowedTo) {
					systemSelector.setEnabled(false);
				} else {
					systemSelector.addValueChangeListener(event -> {
						accessRight.setPermissionSystem(systemSelector.getValue());
					});
				}

				if (!systemHeaderSet) {
					handleSelectorHeader(selectorsHeaderLayout, systemHeaderSelector, "System", systemPermissionSelectors, PermissionLevel.SYSTEM);
					systemHeaderSet = true;
				}


				Select<Permission> visibilitySelector = constructPermissionSelector(accessRight.getVisibleToUser());
				selectorsLayout.add(visibilitySelector);
				visibilityPermissionSelectors.add(visibilitySelector);

				visibilitySelector.addValueChangeListener(event -> {
					accessRight.setVisibleToUser(visibilitySelector.getValue());
				});

				if (!visibilityHeaderSet) {
					handleSelectorHeader(selectorsHeaderLayout, visibilityHeaderSelector, "Visible", visibilityPermissionSelectors, PermissionLevel.SYSTEM);
					visibilityHeaderSet = true;
				}
			}

			add(selectorsLayout);
		}
	}

	private Select<Permission> constructPermissionSelector(Permission type) {
		Select<Permission> permissionTypeSelect = new Select<>();
		permissionTypeSelect.setItems(EnumSet.allOf(Permission.class));
		permissionTypeSelect.setItemLabelGenerator(Permission::getStringValue);
		permissionTypeSelect.setMinWidth("0");
		permissionTypeSelect.setWidthFull();
		if (type == null) {
			permissionTypeSelect.setPlaceholder("SET ALL");
		} else {
			permissionTypeSelect.setValue(type);
		}
		return permissionTypeSelect;
	}

	private void handleSelectorHeader(FlexBoxLayout selectorsHeaderLayout, Select<Permission> headerSelector, String name, List<Select<Permission>> headerSelectorList, PermissionLevel level) {
		selectorsHeaderLayout.add(headerSelector);
		headerSelector.setLabel(name);

		if (!AccessRightFacade.getInstance().isUserAllowedTo(PermissionOperation.EDIT_USER_ACCESS_RIGHTS, level)) {
			headerSelector.setEnabled(false);
		} else {
			headerSelector.addValueChangeListener(event -> {
				for (Select<Permission> select : headerSelectorList) {
					select.setValue(headerSelector.getValue());
				}
			});
		}
	}


	public void setTargetUser(User user) {
		targetUser = user;
		isNew = false;
		if (targetUser == null) {
			targetUser = new User();
			isNew = true;
		}
		currentUser = AuthenticationService.getCurrentSessionUser();

		this.removeAll();

		accessRights = new ArrayList<>();
		accessRights.addAll(targetUser.getAccessRights());
		constructFormLayout();
	}

	public List<AccessRight> getAccessRights() {
		return accessRights;
	}
}
