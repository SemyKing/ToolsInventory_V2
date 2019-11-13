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
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
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

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
				PermissionFacade.getInstance().isUserAllowedTo(Operation.ADD, OperationTarget.INVENTORY_TOOL, null)) {

			menuItem.getSubMenu().addItem("New Tool", e -> {
				grid.deselectAll();
				showToolDetails(null);
			});
			menuItem.getSubMenu().add(new Hr());
		}

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
				PermissionFacade.getInstance().isUserAllowedTo(Operation.ADD, OperationTarget.INVENTORY_CATEGORY, null)) {

			menuItem.getSubMenu().addItem("New Category", e -> {
				grid.deselectAll();
				constructCategoryDialog(null);
			});
			menuItem.getSubMenu().add(new Hr());
		}

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
				PermissionFacade.getInstance().isUserAllowedTo(Operation.IMPORT, OperationTarget.INVENTORY_TOOL, null)) {

			menuItem.getSubMenu().addItem("Import", e -> {
				importTools();
			});
			menuItem.getSubMenu().add(new Hr());
		}

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
				PermissionFacade.getInstance().isUserAllowedTo(Operation.EXPORT, OperationTarget.INVENTORY_TOOL, null)) {

			menuItem.getSubMenu().addItem("Export", e -> {
				exportTools();
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

		grid.addColumn(Tool::getUsageStatusString)
				.setHeader("Status")
				.setAutoWidth(true);

		grid.addColumn(new ComponentRenderer<>(tool -> UIUtils.createActiveGridIcon(tool.isDeleted())))
				.setHeader("Active")
				.setFlexGrow(0)
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

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
				PermissionFacade.getInstance().isUserAllowedTo(Operation.COPY, OperationTarget.INVENTORY_TOOL, null)) {

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

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
				PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.INVENTORY_TOOL, null)) {
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
							res =  StringUtils.containsIgnoreCase(tool.getName(), sParam) ||
									StringUtils.containsIgnoreCase((tool.getCurrentUser() == null) ? "" : tool.getCurrentUser().getUsername(), sParam) ||
									StringUtils.containsIgnoreCase((tool.getReservedUser() == null) ? "" : tool.getReservedUser().getUsername(), sParam) ||
									StringUtils.containsIgnoreCase(tool.getCategoryString(), sParam) ||
									StringUtils.containsIgnoreCase(tool.getCompanyString(), sParam) ||
									StringUtils.containsIgnoreCase(tool.getUsageStatusString(), sParam);

							//(res) -> shows All items based on searchParams (multiple rows)
							//(!res) -> shows ONE item based on searchParams
							if (!res)
								break;
						}
						return res;
					}
			);
		} else {
			dataProvider.addFilter(
					tool -> StringUtils.containsIgnoreCase(tool.getName(), mainSearchString)  ||
							StringUtils.containsIgnoreCase((tool.getCurrentUser() == null) ? "" : tool.getCurrentUser().getUsername(), mainSearchString) ||
							StringUtils.containsIgnoreCase((tool.getReservedUser() == null) ? "" : tool.getReservedUser().getUsername(), mainSearchString) ||
							StringUtils.containsIgnoreCase(tool.getCategoryString(), mainSearchString) ||
							StringUtils.containsIgnoreCase(tool.getCompanyString(), mainSearchString) ||
							StringUtils.containsIgnoreCase(tool.getUsageStatusString(), mainSearchString)
			);
		}
	}

	private void showToolDetails(Tool tool) {
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
					showToolDetails(selectedTools.get(0));

				//IF MORE THAN ONE TOOL IS SELECTED
				} else {
//					constructBulkEditDialog(true, null, selectedTools.size());

					constructMultipleToolEditDialog(null, -1, selectedTools);

					if (UI.getCurrent() != null) {
						try {
//							UI.getCurrent().access(this::closeDetails);
							UI.getCurrent().access(() -> detailsDrawer.hide());
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


	private void exportTools() {
		System.out.println("Export Tools...");
	}

	private void importTools() {
		System.out.println("Import Tools...");
	}


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

	public void constructCategoryDialog(Category category) {
		categoryForm.setCategory(category);

		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label("Category Details"));

		dialog.setContent(categoryForm);
		dialog.getCancelButton().addClickListener(e -> dialog.close());

		dialog.getConfirmButton().setText("Save");
		dialog.getConfirmButton().setEnabled(false);

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
				PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.INVENTORY_CATEGORY, null)) {

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







//	/*
//	 ******BULK OPERATIONS******
//	 */
//	private CustomDialog bulkEditDialog;
//	private List<Tool> bulkTools;
//	private int currentBulkEditToolIndex = 0;
//
//	private void constructBulkEditDialog(boolean editMode, Tool toolToCopy, int numberOfTools) {
//
//		currentBulkEditToolIndex = 0;
//
//		if (editMode) {
//			bulkTools = new ArrayList<>(selectedTools);
//		} else {
//			bulkTools = new ArrayList<>();
//
//			for (int i = 0; i < numberOfTools; i++) {
//				bulkTools.add(new Tool(toolToCopy));
//			}
//		}
//
//		bulkEditDialog = new CustomDialog();
//		bulkEditDialog.setCloseOnOutsideClick(false);
//		bulkEditDialog.setCloseOnEsc(false);
//
//		bulkEditDialog.setHeader(constructBulkDialogHeader(editMode));
//
//		bulkEditDialog.getContent().removePadding();
//
//		setToolBulkEditDialogContent(bulkTools.get(0));
//
//		bulkEditDialog.getCancelButton().addClickListener(closeEvent -> {
//			ConfirmDialog dialog = new ConfirmDialog();
//			dialog.setMessage("Are you sure you want to close Tool dialog?" + ProjectConstants.NEW_LINE + "All changes will be lost");
//			dialog.closeOnCancel();
//			dialog.getConfirmButton().addClickListener(event -> {
//				bulkEditDialog.close();
//				dialog.close();
//			});
//			dialog.open();
//		});
//
//		bulkEditDialog.getConfirmButton().setText("Save");
//		bulkEditDialog.getConfirmButton().setEnabled(false);
//
//		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN) ||
//				PermissionFacade.getInstance().isUserAllowedTo(Operation.EDIT, OperationTarget.INVENTORY_TOOL, null)) {
//
//			bulkEditDialog.getConfirmButton().setEnabled(true);
//			bulkEditDialog.getConfirmButton().addClickListener(confirmEvent -> {
//
//				boolean error = false;
//
//				Tool editedTool = bulkToolForm.getTool();
//				if (editedTool != null) {
//					bulkTools.set(currentBulkEditToolIndex, editedTool);
//
//					if (editMode) { //EDIT TOOLS
//						for (Tool tool : bulkTools) {
//							if (InventoryFacade.getInstance().update(tool)) {
//
//								Transaction transaction = new Transaction();
//								transaction.setUser(AuthenticationService.getCurrentSessionUser());
//								transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
//								transaction.setOperation(Operation.EDIT);
//								transaction.setOperationTarget1(OperationTarget.INVENTORY_TOOL);
//								transaction.setTargetDetails(tool.getName());
////							transaction.setChanges(bulkToolForm.getChanges()); // CANNOT GET CHANGES BECAUSE ONLY ONE INSTANCE OF bulkToolForm
//								TransactionFacade.getInstance().insert(transaction);
//							} else {
//								error = true;
//							}
//						}
//
//						if (error) {
//							UIUtils.showNotification("Tools Update Error", NotificationVariant.LUMO_ERROR);
//						} else {
//							UIUtils.showNotification("Tools Updated", NotificationVariant.LUMO_SUCCESS);
//						}
//					} else {    // COPY TOOLS
//						for (Tool tool : bulkTools) {
//							if (InventoryFacade.getInstance().insert(tool)) {
//								dataProvider.getItems().add(tool);
//
//								Transaction transaction = new Transaction();
//								transaction.setUser(AuthenticationService.getCurrentSessionUser());
//								transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
//								transaction.setOperation(Operation.ADD);
//								transaction.setOperationTarget1(OperationTarget.INVENTORY_TOOL);
//								transaction.setTargetDetails(tool.getName());
//								TransactionFacade.getInstance().insert(transaction);
//							} else {
//								error = true;
//							}
//						}
//
//						if (error) {
//							UIUtils.showNotification("Tools Insert Error", NotificationVariant.LUMO_ERROR);
//						} else {
//							UIUtils.showNotification("Tools Created", NotificationVariant.LUMO_SUCCESS);
//						}
//					}
//
//					dataProvider.refreshAll();
//					bulkEditDialog.close();
//				}
//			});
//		}
//
//		bulkEditDialog.open();
//	}
//
//	private FlexBoxLayout constructBulkDialogHeader(boolean editMode) {
//		FlexBoxLayout bulkDialogHeader = new FlexBoxLayout();
//		bulkDialogHeader.setWidthFull();
//		bulkDialogHeader.setFlexDirection(FlexDirection.ROW);
//		bulkDialogHeader.setAlignItems(FlexComponent.Alignment.CENTER);
//		bulkDialogHeader.add(UIUtils.createH3Label("Tool Details"));
//
//		Label toolIndexLabel = UIUtils.createH4Label("1");
//		bulkDialogHeader.add(toolIndexLabel);
//		bulkDialogHeader.setComponentMargin(toolIndexLabel, Left.M, Top.NONE);
//
//		Label slash = UIUtils.createH4Label("/");
//		bulkDialogHeader.add(slash);
//		bulkDialogHeader.setComponentMargin(slash, Horizontal.S, Top.NONE);
//
//		Label toolsSizeLabel = UIUtils.createH4Label(""+bulkTools.size());
//		bulkDialogHeader.add(toolsSizeLabel);
//		bulkDialogHeader.setComponentMargin(toolsSizeLabel, Top.NONE);
//
//
//		/*
//			***HEADER BUTTONS & EVENTS***
//		 */
//		// ACTIVE WHEN COPYING TOOLS -> ALLOWS TO ADD ONE MORE COPY OF CURRENT TOOL
//		Button addOneCopyButton = UIUtils.createIconButton(VaadinIcon.PLUS, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE);
//		UIUtils.setTooltip("Create new copy of this Tool", addOneCopyButton);
//		addOneCopyButton.addClickListener(e -> {
//			Tool newToolCopy = new Tool(bulkTools.get(currentBulkEditToolIndex));
//			bulkTools.add(newToolCopy);
//			toolsSizeLabel.setText(""+(bulkTools.size()));
//		});
//
//		bulkDialogHeader.add(addOneCopyButton);
//		bulkDialogHeader.setComponentMargin(addOneCopyButton, Left.AUTO);
//
//		addOneCopyButton.setEnabled(!editMode);
//
//		Button toolsToLeftButton = UIUtils.createIconButton(VaadinIcon.ANGLE_LEFT, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE);
//		Button toolsToRightButton = UIUtils.createIconButton(VaadinIcon.ANGLE_RIGHT, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE);
//
//		// ACTIVE WHEN COPYING TOOLS -> ALLOWS TO REMOVE CURRENT TOOL FROM LIST
//		Button removeCurrentTool = UIUtils.createIconButton(VaadinIcon.MINUS, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
//		UIUtils.setTooltip("Remove selected Tool", removeCurrentTool);
//		removeCurrentTool.addClickListener(e -> {
//			// If more than one item
//			if (bulkTools.size() > 1) {
//
//				int itemPositionToRemove = -1;
//
//				// Current position is first
//				if (currentBulkEditToolIndex == 0) {
//					toolsToRightButton.click();
//					itemPositionToRemove = 0;
//
//					--currentBulkEditToolIndex;
//				}
//
//				// Current position is last
//				if (currentBulkEditToolIndex == ((bulkTools.size())-1)) {
//					toolsToLeftButton.click();
//					itemPositionToRemove = ((bulkTools.size())-1);
//				}
//
//				// Current position is middle, move Left
//				if (itemPositionToRemove == -1) {
//					toolsToLeftButton.click();
//					itemPositionToRemove = currentBulkEditToolIndex;
//				}
//
//
//				bulkTools.remove(itemPositionToRemove);
//				toolsSizeLabel.setText(""+(bulkTools.size()));
//				toolIndexLabel.setText(""+(currentBulkEditToolIndex+1));
//			}
//		});
//		bulkDialogHeader.add(removeCurrentTool);
//		bulkDialogHeader.setComponentMargin(removeCurrentTool, Right.L);
//
//		removeCurrentTool.setEnabled(!editMode);
//
//
//		//CLICK LEFT
//		toolsToLeftButton.addClickShortcut(Key.ARROW_LEFT);
//		UIUtils.setTooltip("Arrow Left", toolsToLeftButton);
//		toolsToLeftButton.addClickListener(e -> {
//			if (currentBulkEditToolIndex > 0) {
//
//				Tool editedTool = bulkToolForm.getTool();
//				if (editedTool != null) {
//					bulkTools.set(currentBulkEditToolIndex, editedTool);
//
//					--currentBulkEditToolIndex;
//					setToolBulkEditDialogContent(bulkTools.get(currentBulkEditToolIndex));
//
//					toolIndexLabel.setText(""+(currentBulkEditToolIndex+1));
//				}
//			}
//		});
//		bulkDialogHeader.add(toolsToLeftButton);
//
//		//CLICK RIGHT
//		toolsToRightButton.addClickShortcut(Key.ARROW_RIGHT);
//		UIUtils.setTooltip("Arrow Right", toolsToRightButton);
//		toolsToRightButton.addClickListener(e -> {
//			if (currentBulkEditToolIndex < (bulkTools.size()-1)) {
//
//				Tool editedTool = bulkToolForm.getTool();
//				if (editedTool != null) {
//					bulkTools.set(currentBulkEditToolIndex, editedTool);
//
//					++currentBulkEditToolIndex;
//					setToolBulkEditDialogContent(bulkTools.get(currentBulkEditToolIndex));
//
//					toolIndexLabel.setText(""+(currentBulkEditToolIndex+1));
//				}
//			}
//		});
//		bulkDialogHeader.add(toolsToRightButton);
//
//		return bulkDialogHeader;
//	}
//
//	private void setToolBulkEditDialogContent(Tool tool) {
//		bulkToolForm.setTool(tool);
//		bulkEditDialog.setContent(bulkToolForm);
//	}
}