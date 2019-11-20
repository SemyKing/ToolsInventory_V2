package com.gmail.grigorij.ui.views.app.admin;

import com.gmail.grigorij.backend.database.entities.Category;
import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.dialogs.MultipleToolEditDialog;
import com.gmail.grigorij.ui.components.forms.CategoryForm;
import com.gmail.grigorij.ui.components.forms.ToolCopyForm;
import com.gmail.grigorij.ui.components.forms.ToolForm;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.views.app.AdminView;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class AdminInventory extends FlexBoxLayout {

	private static final String CLASS_NAME = "admin-inventory";
	private final ToolForm toolForm = new ToolForm(this);
	private final CategoryForm categoryForm = new CategoryForm();
	private final ToolCopyForm toolCopyForm = new ToolCopyForm();
	private final AdminView admin;

	private Grid<Tool> grid;
	private ListDataProvider<Tool> dataProvider;
	private List<Tool> selectedTools = null;

	private DetailsDrawer detailsDrawer;
	private Button editToolButton;


	public AdminInventory(AdminView admin) {
		this.admin = admin;
		setClassName(CLASS_NAME);

		add(constructHeader());
		add(constructContent());

		constructDetails();
	}


	private Div constructHeader() {
		Div header = new Div();
		header.setClassName(CLASS_NAME + "__header");

		editToolButton = UIUtils.createIconButton(VaadinIcon.EDIT, ButtonVariant.LUMO_PRIMARY);
		editToolButton.addClassName("edit-tool-button");
		editToolButton.addClickListener(e -> editSelectedTools());
		editToolButton.setEnabled(false);
		UIUtils.setTooltip("Edit selected tool(s)", editToolButton);

		header.add(editToolButton);

		TextField searchField = new TextField();
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Tools");
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event -> filterGrid(searchField.getValue()));

		header.add(searchField);

		MenuBar actionsMenuBar = new MenuBar();
		actionsMenuBar.addThemeVariants(MenuBarVariant.LUMO_PRIMARY, MenuBarVariant.LUMO_ICON);

		MenuItem menuItem = actionsMenuBar.addItem(new Icon(VaadinIcon.MENU));

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.ADD, OperationTarget.INVENTORY_TOOL, null)) {

			menuItem.getSubMenu().addItem("New Tool", e -> {
				grid.deselectAll();
				showDetails(null);
			});
			menuItem.getSubMenu().add(new Hr());
		}

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.ADD, OperationTarget.INVENTORY_CATEGORY, null)) {

			menuItem.getSubMenu().addItem("New Category", e -> {
				grid.deselectAll();
				constructCategoryDialog(null);
			});
			menuItem.getSubMenu().add(new Hr());
		}

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.IMPORT, OperationTarget.INVENTORY_TOOL, null)) {

			menuItem.getSubMenu().addItem("Import Inventory", e -> {
				importInventory();
			});
			menuItem.getSubMenu().add(new Hr());
		}

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.EXPORT, OperationTarget.INVENTORY_TOOL, null)) {

			menuItem.getSubMenu().addItem("Export Inventory", e -> {
				exportInventory();
			});
		}

		header.add(actionsMenuBar);

		return header;
	}

	private Div constructContent() {
		Div content = new Div();
		content.setClassName(CLASS_NAME + "__content");

		// GRID
		content.add(constructGrid());

		return content;
	}

	private Grid constructGrid() {
		grid = new Grid<>();
		grid.setClassName("grid-view");
		grid.setSizeFull();

		List<Tool> toolsFromDatabase;

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			toolsFromDatabase = InventoryFacade.getInstance().getAllTools();
			toolsFromDatabase.sort(Comparator.comparing(Tool::getCompanyString).thenComparing(Tool::getName));
		} else {
			toolsFromDatabase = InventoryFacade.getInstance().getAllToolsInCompany(AuthenticationService.getCurrentSessionUser().getCompany().getId());
		}


		dataProvider = DataProvider.ofCollection(toolsFromDatabase);

		grid.setDataProvider(dataProvider);

		grid.addColumn(Tool::getName)
				.setHeader("Tool")
				.setFlexGrow(1)
				.setAutoWidth(true);

		grid.addColumn(Tool::getCategoryString)
				.setHeader("Category")
				.setAutoWidth(true);

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			grid.addColumn(Tool::getCompanyString)
					.setHeader("Company")
					.setAutoWidth(true);
		}

//		grid.addColumn(Tool::getUsageStatusString)
//				.setHeader("Status")
//				.setAutoWidth(true);

		grid.addColumn(new ComponentRenderer<>(tool -> UIUtils.createActiveGridIcon(tool.isDeleted())))
				.setHeader("Active")
				.setFlexGrow(0)
				.setTextAlign(ColumnTextAlign.CENTER)
				.setAutoWidth(true);

		grid.setSelectionMode(Grid.SelectionMode.MULTI);

		grid.addSelectionListener(event -> {
			selectedTools = new ArrayList<>(event.getAllSelectedItems());
			editToolButton.setEnabled((selectedTools.size() > 0));

			if ((selectedTools.size() <= 0)) {
				closeDetails();
			}
		});

		return grid;
	}

	private void constructDetails() {
		detailsDrawer = admin.getDetailsDrawer();

		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Tool Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.COPY, OperationTarget.INVENTORY_TOOL, null)) {

			Button copyToolButton = UIUtils.createButton(VaadinIcon.COPY, ButtonVariant.LUMO_PRIMARY);
			copyToolButton.addClickListener(e -> constructToolCopyDialog());
			UIUtils.setTooltip("Copy This Tool", copyToolButton);

			detailsDrawerHeader.getContent().add(copyToolButton);
			detailsDrawerHeader.getContent().setComponentMargin(copyToolButton, Left.AUTO);
		}

		detailsDrawer.setHeader(detailsDrawerHeader);
		detailsDrawer.setContent(toolForm);

		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().setEnabled(false);

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.EDIT, OperationTarget.INVENTORY_TOOL, null)) {
			detailsDrawerFooter.getSave().addClickListener(e -> {
				saveToolInDatabase(toolForm.getTool(), toolForm.getChanges(), toolForm.isNew());
			});
			detailsDrawerFooter.getSave().setEnabled(true);
		}

		detailsDrawerFooter.getClose().addClickListener(e -> closeDetails());
		detailsDrawer.setFooter(detailsDrawerFooter);
	}


	private void filterGrid(String searchString) {
		dataProvider.clearFilters();
		final String mainSearchString = searchString.trim();

		if (mainSearchString.contains("+")) {
			String[] searchParams = mainSearchString.split("\\+");

			dataProvider.addFilter(
					tool -> {
						boolean res = true;
						for (String sParam : searchParams) {
							res =  matchesFilter(tool, sParam);

							if (!res) {
								break;
							}
						}
						return res;
					}
			);
		} else {
			dataProvider.addFilter(
					tool -> matchesFilter(tool, mainSearchString)
			);
		}
	}

	private boolean matchesFilter(Tool item, String filter) {
		return StringUtils.containsIgnoreCase(item.getName(), filter) ||
				StringUtils.containsIgnoreCase(item.getCurrentUserString(), filter) ||
				StringUtils.containsIgnoreCase((item.getReservedUserString()), filter) ||
				StringUtils.containsIgnoreCase(item.getCategoryString(), filter) ||
				StringUtils.containsIgnoreCase(item.getCompanyString(), filter) ||
				StringUtils.containsIgnoreCase(item.getUsageStatusString(), filter);
	}


	private void showDetails(Tool tool) {
		if (tool != null) {
			if (!AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
				if (!PermissionFacade.getInstance().isUserAllowedTo(Operation.VIEW, OperationTarget.INVENTORY_TOOL, PermissionRange.COMPANY)) {
					UIUtils.showNotification(ProjectConstants.ACTION_NOT_ALLOWED, NotificationVariant.LUMO_PRIMARY);
					grid.deselectAll();
					return;
				}
			}

			detailsDrawer.setDeletedAttribute(tool.isDeleted());
		} else {
			detailsDrawer.setDeletedAttribute(false);
		}

		toolForm.setTool(tool);
		detailsDrawer.show();
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.deselectAll();
	}


	private void editSelectedTools() {
		if (selectedTools != null) {
			if (selectedTools.size() > 0) {

				//IF ONLY ONE TOOL IS SELECTED
				if (selectedTools.size() == 1) {
					showDetails(selectedTools.get(0));

				//IF MORE THAN ONE TOOL IS SELECTED
				} else {
					constructMultipleToolEditDialog(null, -1, selectedTools);

					final UI ui = UI.getCurrent();
					if (ui != null) {
						try {
							ui.access(() -> detailsDrawer.hide());
						} catch (UIDetachedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}


	private boolean saveCategoryInDatabase(Category category, boolean isNew, List<String> changes) {
		if (category == null) {
			return false;
		}

		if (isNew) {
			if (InventoryFacade.getInstance().insert(category)) {
				UIUtils.showNotification("Category created", NotificationVariant.LUMO_SUCCESS, 1000);
			} else {
				UIUtils.showNotification("Category insert failed", NotificationVariant.LUMO_ERROR);
				return false;
			}
		} else {
			if (InventoryFacade.getInstance().update(category)) {
				UIUtils.showNotification("Category updated", NotificationVariant.LUMO_SUCCESS, 1000);
			} else {
				UIUtils.showNotification("Category update failed", NotificationVariant.LUMO_ERROR);
				return false;
			}
		}

		Transaction transaction = new Transaction();
		transaction.setUser(AuthenticationService.getCurrentSessionUser());
		transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
		transaction.setOperation(isNew ? Operation.ADD : Operation.EDIT);
		transaction.setOperationTarget1(OperationTarget.INVENTORY_CATEGORY);
		transaction.setTargetDetails(category.getName());
		transaction.setChanges(changes);
		TransactionFacade.getInstance().insert(transaction);

		dataProvider.refreshAll();
		return true;
	}

	private boolean saveToolInDatabase(Tool tool, List<String> changes, boolean isNew) {
		if (tool == null) {
			return false;
		}

		if (isNew) {
			if (InventoryFacade.getInstance().insert(tool)) {
				dataProvider.getItems().add(tool);

				UIUtils.showNotification("Tool created", NotificationVariant.LUMO_SUCCESS, 1000);
			} else {
				UIUtils.showNotification("Tool insert failed", NotificationVariant.LUMO_ERROR);
				return false;
			}
		} else {
			if (InventoryFacade.getInstance().update(tool)) {
				UIUtils.showNotification("Tool updated", NotificationVariant.LUMO_SUCCESS, 1000);
			} else {
				UIUtils.showNotification("Tool update failed", NotificationVariant.LUMO_ERROR);
				return false;
			}
		}

		Transaction transaction = new Transaction();
		transaction.setUser(AuthenticationService.getCurrentSessionUser());
		transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
		transaction.setOperation(isNew ? Operation.ADD : Operation.EDIT);
		transaction.setOperationTarget1(OperationTarget.INVENTORY_TOOL);
		transaction.setTargetDetails(tool.getName());
		transaction.setChanges(changes);
		TransactionFacade.getInstance().insert(transaction);

		dataProvider.refreshAll();
		return true;
	}


	/**
	 * Dialog for creating copies of selected Tool
	 */
	private void constructToolCopyDialog() {
		if (selectedTools == null || selectedTools.get(0) == null) {
			UIUtils.showNotification("Cannot copy this tool", NotificationVariant.LUMO_PRIMARY);
			return;
		}

		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label("Copy Tool Information"));

		toolCopyForm.setTool(selectedTools.get(0));
		dialog.setContent(toolCopyForm);

		dialog.closeOnCancel();

		dialog.getConfirmButton().setText("Copy");
		dialog.getConfirmButton().addClickListener(e -> {
			Tool toolToCopy = toolCopyForm.getToolCopy();
			if (toolToCopy != null) {
				dialog.close();

//				constructBulkEditDialog(false, toolToCopy, toolCopyForm.getNumberOfCopies());

				constructMultipleToolEditDialog(toolToCopy, toolCopyForm.getNumberOfCopies(), null);

				if (UI.getCurrent() != null) {
					try {
						UI.getCurrent().access(this::closeDetails);
					} catch (UIDetachedException uiDetachException) {
						uiDetachException.printStackTrace();
					}
				}
			}
		});
		dialog.open();
	}


	/**
	 * Dialog for editing multiple Tools at the same time
	 */
	private void constructMultipleToolEditDialog(Tool copyTool, int numberOfCopies, List<Tool> tools) {
		MultipleToolEditDialog multipleToolEditDialog = new MultipleToolEditDialog(this);
		multipleToolEditDialog.setData(copyTool, numberOfCopies, tools);
		multipleToolEditDialog.getConfirmButton().addClickListener(e -> {
			List<Tool> editedTools = multipleToolEditDialog.getTools();

			boolean copyMode = copyTool != null;

			if (editedTools != null) {
				HashMap<Tool, List<String>> toolChangesHashMap = multipleToolEditDialog.getToolChangesHashMap();

				for (Tool tool : editedTools) {
					List<String> changes = new ArrayList<>();

					for (Tool toolFromHashMap : toolChangesHashMap.keySet()) {
						if (tool.getId().equals(toolFromHashMap.getId())) {
							changes = toolChangesHashMap.get(toolFromHashMap);
						}
					}

					saveToolInDatabase(tool, changes, copyMode);
				}

				multipleToolEditDialog.close();
			}
		});

		multipleToolEditDialog.open();
	}


	/**
	 * Dialog for Category
	 */
	public void constructCategoryDialog(Category category) {
		categoryForm.setCategory(category);

		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label("Category Details"));

		dialog.setContent(categoryForm);
		dialog.getCancelButton().addClickListener(e -> dialog.close());

		dialog.getConfirmButton().setText("Save");
		dialog.getConfirmButton().setEnabled(false);

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.EDIT, OperationTarget.INVENTORY_CATEGORY, null)) {

			dialog.getConfirmButton().setEnabled(true);
			dialog.getConfirmButton().addClickListener(e -> {
				Category editedCategory = categoryForm.getCategory();

				if (editedCategory != null) {
					saveCategoryInDatabase(editedCategory, categoryForm.isNew(), categoryForm.getChanges());
					dialog.close();
				}
			});
		}

		dialog.open();
	}


	private void exportInventory() {
		System.out.println("Export Inventory...");
	}

	private void importInventory() {
		System.out.println("Import Inventory...");
	}
}