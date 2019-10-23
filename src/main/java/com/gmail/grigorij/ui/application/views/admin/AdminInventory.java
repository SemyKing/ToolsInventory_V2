package com.gmail.grigorij.ui.application.views.admin;

import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.backend.enums.inventory.InventoryHierarchyType;
import com.gmail.grigorij.backend.enums.operations.Operation;
import com.gmail.grigorij.backend.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.enums.permissions.PermissionLevel;
import com.gmail.grigorij.ui.application.views.Admin;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.components.dialogs.ConfirmDialog;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.forms.CategoryForm;
import com.gmail.grigorij.ui.components.forms.ToolForm;
import com.gmail.grigorij.ui.components.forms.ToolCopyForm;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.gmail.grigorij.ui.utils.css.size.Top;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class AdminInventory extends FlexBoxLayout {

	private static final String CLASS_NAME = "admin-inventory";
	private final ToolForm toolForm = new ToolForm(this);
	private final ToolForm bulkToolForm = new ToolForm(this);
	private final CategoryForm categoryForm = new CategoryForm();
	private final ToolCopyForm toolCopyForm = new ToolCopyForm();
	private final Admin admin;

	private Grid<InventoryItem> grid;
	private ListDataProvider<InventoryItem> dataProvider;
	private List<InventoryItem> selectedTools = null;

	private DetailsDrawer detailsDrawer;
	private Button copyToolButton;
	private Button editToolButton;


	public AdminInventory(Admin admin) {
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
		editToolButton.setMinWidth("52px"); //looks better
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
		actionsMenuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY, MenuBarVariant.LUMO_CONTRAST);

		MenuItem menuItem = actionsMenuBar.addItem(new Icon(VaadinIcon.MENU));

		menuItem.getSubMenu().addItem("New Tool", e -> {
			grid.deselectAll();
			showToolDetails(null);
		});
		menuItem.getSubMenu().add(new Hr());
		menuItem.getSubMenu().addItem("New Category", e -> {
			grid.deselectAll();
			constructCategoryDialog(null);
		});
		menuItem.getSubMenu().add(new Hr());
		menuItem.getSubMenu().addItem("Import", e -> {
			importTools();
		});
		menuItem.getSubMenu().add(new Hr());
		menuItem.getSubMenu().addItem("Export", e -> {
			exportTools();
		});

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

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			dataProvider = DataProvider.ofCollection(InventoryFacade.getInstance().getAllByHierarchyType(InventoryHierarchyType.TOOL));
		} else {
			dataProvider = DataProvider.ofCollection(InventoryFacade.getInstance().getAllInCompanyByType(AuthenticationService.getCurrentSessionUser().getCompany().getId(), InventoryHierarchyType.TOOL));
		}

		grid.setDataProvider(dataProvider);
		grid.addColumn(InventoryItem::getName).setHeader("Tool")
				.setFlexGrow(1)
				.setAutoWidth(true);

		grid.addColumn(tool -> (tool.getParentCategory() == null) ? InventoryFacade.getInstance().getRootCategory().getName() : tool.getParentCategory().getName())
				.setHeader("Category")
				.setAutoWidth(true);

		grid.addColumn(tool -> (tool.getCompany() == null) ? "" : tool.getCompany().getName())
				.setHeader("Company")
				.setAutoWidth(true);

		grid.addColumn(tool -> (tool.getUsageStatus() == null) ? "" : tool.getUsageStatus().getName())
				.setHeader("Usage Status")
				.setAutoWidth(true);

		grid.addColumn(tool -> {
					if (tool.getCurrentUser() != null && tool.getReservedUser() != null) {
						return tool.getCurrentUser().getFullName() + ", " + tool.getReservedUser().getFullName();
					}
					if (tool.getCurrentUser() != null) {
						return tool.getCurrentUser().getFullName();
					}
					if (tool.getReservedUser() != null) {
						return tool.getReservedUser().getFullName();
					}
					return "";
				})
				.setHeader("In Use / Reserved By")
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

		copyToolButton = UIUtils.createButton(VaadinIcon.COPY, ButtonVariant.LUMO_PRIMARY);
		copyToolButton.addClickListener(e -> constructToolCopyDialog());
		UIUtils.setTooltip("Copy This Tool", copyToolButton);

		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Tool Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());
		detailsDrawerHeader.getContent().add(copyToolButton);
		detailsDrawerHeader.getContent().setComponentMargin(copyToolButton, Left.AUTO);
		detailsDrawer.setHeader(detailsDrawerHeader);

		detailsDrawer.setContent(toolForm);

		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().addClickListener(e -> toolSaveOnClick());
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
									StringUtils.containsIgnoreCase((tool.getParentCategory() == null) ? "" : tool.getParentCategory().getName(), sParam) ||
									StringUtils.containsIgnoreCase((tool.getCompany() == null) ? "" : tool.getCompany().getName(), sParam) ||
									StringUtils.containsIgnoreCase((tool.getUsageStatus() == null) ? "" : tool.getUsageStatus().getName(), sParam);

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
							StringUtils.containsIgnoreCase((tool.getParentCategory() == null) ? "" : tool.getParentCategory().getName(), mainSearchString) ||
							StringUtils.containsIgnoreCase((tool.getCompany() == null) ? "" : tool.getCompany().getName(), mainSearchString) ||
							StringUtils.containsIgnoreCase((tool.getUsageStatus() == null) ? "" : tool.getUsageStatus().getName(), mainSearchString)
			);
		}
	}

	private void showToolDetails(InventoryItem tool) {
		copyToolButton.setEnabled( tool != null );

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
					constructBulkEditDialog(true, null, selectedTools.size());

					if (UI.getCurrent() != null) {
						try {
							UI.getCurrent().access(this::closeDetails);
						} catch (UIDetachedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}


	private void categorySaveOnClick(InventoryItem editedCategory) {
		if (editedCategory == null) {
			return;
		}

		if (editedCategory.getParentCategory() != null) {
			if (editedCategory.getParentCategory().equals(InventoryFacade.getInstance().getRootCategory())) {
				editedCategory.setParentCategory(null);
			}
		}

		if (categoryForm.isNew()) {
			if (InventoryFacade.getInstance().insert(editedCategory)) {
				UIUtils.showNotification("Category created", UIUtils.NotificationType.SUCCESS);
			} else {
				UIUtils.showNotification("Category insert failed", UIUtils.NotificationType.ERROR);
				return;
			}
		} else {
			if (InventoryFacade.getInstance().update(editedCategory)) {
				UIUtils.showNotification("Category updated", UIUtils.NotificationType.SUCCESS);
			} else {
				UIUtils.showNotification("Category updated failed", UIUtils.NotificationType.ERROR);
				return;
			}
		}

		Transaction transaction = new Transaction();
		transaction.setUser(AuthenticationService.getCurrentSessionUser());
		transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());

		if (categoryForm.isNew()) {
			transaction.setOperation(Operation.ADD);
		} else {
			transaction.setOperation(Operation.EDIT);
		}
		transaction.setOperationTarget1(OperationTarget.INVENTORY_CATEGORY);
		transaction.setTargetDetails(editedCategory.getName());
		if (!categoryForm.isNew()) {
			transaction.setChanges(categoryForm.getChanges());
		}
		TransactionFacade.getInstance().insert(transaction);

		dataProvider.refreshAll();
	}

	private void toolSaveOnClick() {
		InventoryItem editedTool = toolForm.getTool();

		if (editedTool == null) {
			return;
		}

		if (toolForm.isNew()) {
			if (InventoryFacade.getInstance().insert(editedTool)) {
				dataProvider.getItems().add(editedTool);

				UIUtils.showNotification("Tool created successfully", UIUtils.NotificationType.SUCCESS);
			} else {
				UIUtils.showNotification("Tool insert failed", UIUtils.NotificationType.ERROR);
				return;
			}
		} else {
			if (InventoryFacade.getInstance().update(editedTool)) {
				UIUtils.showNotification("Tool updated successfully", UIUtils.NotificationType.SUCCESS);
			} else {
				UIUtils.showNotification("Tool update failed", UIUtils.NotificationType.ERROR);
				return;
			}
		}

		Transaction transaction = new Transaction();
		transaction.setUser(AuthenticationService.getCurrentSessionUser());
		transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());

		if (toolForm.isNew()) {
			transaction.setOperation(Operation.ADD);
		} else {
			transaction.setOperation(Operation.EDIT);
		}
		transaction.setOperationTarget1(OperationTarget.INVENTORY_TOOL);
		transaction.setTargetDetails(editedTool.getName());
		if (!toolForm.isNew()) {
			transaction.setChanges(toolForm.getChanges());
		}
		TransactionFacade.getInstance().insert(transaction);

		dataProvider.refreshAll();
	}

	private void exportTools() {
		System.out.println("Export Tools...");
	}

	private void importTools() {
		System.out.println("Import Tools...");
	}


	private void constructToolCopyDialog() {
		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label("Copy Tool Information"));

		toolCopyForm.setTool(selectedTools.get(0));
		dialog.setContent(toolCopyForm);

		dialog.closeOnCancel();

		dialog.getConfirmButton().setText("Copy");
		dialog.getConfirmButton().addClickListener(e -> {
			InventoryItem toolToCopy = toolCopyForm.getToolCopy();
			if (toolToCopy != null) {
				dialog.close();

				constructBulkEditDialog(false, toolToCopy, toolCopyForm.getNumberOfCopies());

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

	public void constructCategoryDialog(InventoryItem category) {
		categoryForm.setCategory(category);

		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label("Category Details"));

		dialog.setContent(categoryForm);
		dialog.getCancelButton().addClickListener(e -> dialog.close());

		dialog.getConfirmButton().setText("Save");
		dialog.getConfirmButton().addClickListener(e -> {
			InventoryItem editedCategory = categoryForm.getCategory();

			if (editedCategory != null) {
				categorySaveOnClick(editedCategory);
				dialog.close();
			}
		});

		dialog.open();
	}

	/*
	 ******BULK OPERATIONS******
	 */
	private CustomDialog bulkEditDialog;
	private List<InventoryItem> bulkTools;
	private int currentBulkEditToolIndex = 0;

	private void constructBulkEditDialog(boolean editMode, InventoryItem toolToCopy, int numberOfTools) {

		currentBulkEditToolIndex = 0;

		if (editMode) {
			bulkTools = new ArrayList<>(selectedTools);
		} else {
			bulkTools = new ArrayList<>();

			for (int i = 0; i < numberOfTools; i++) {
				bulkTools.add(new InventoryItem(toolToCopy));
			}
		}

		bulkEditDialog = new CustomDialog();
		bulkEditDialog.setCloseOnOutsideClick(false);
		bulkEditDialog.setCloseOnEsc(false);

		bulkEditDialog.setHeader(constructBulkDialogHeader(editMode));

		bulkEditDialog.getContent().removePadding();

		setToolBulkEditDialogContent(bulkTools.get(0));

		bulkEditDialog.getCancelButton().addClickListener(closeEvent -> {
			ConfirmDialog dialog = new ConfirmDialog();
			dialog.setMessage("Are you sure you want to close Tool dialog?" + ProjectConstants.NEW_LINE + "All changes will be lost");
			dialog.closeOnCancel();
			dialog.getConfirmButton().addClickListener(event -> {
				bulkEditDialog.close();
				dialog.close();
			});
			dialog.open();
		});

		bulkEditDialog.getConfirmButton().setText("Save");
		bulkEditDialog.getConfirmButton().addClickListener(confirmEvent -> {

			boolean error = false;

			InventoryItem editedTool = bulkToolForm.getTool();
			if (editedTool != null) {
				bulkTools.set(currentBulkEditToolIndex, editedTool);

				if (editMode) { //EDIT TOOLS
					for (InventoryItem tool : bulkTools) {
						if (InventoryFacade.getInstance().update(tool)) {

							Transaction transaction = new Transaction();
							transaction.setUser(AuthenticationService.getCurrentSessionUser());
							transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
							transaction.setOperation(Operation.EDIT);
							transaction.setOperationTarget1(OperationTarget.INVENTORY_TOOL);
							transaction.setTargetDetails(tool.getName());
//							transaction.setChanges(bulkToolForm.getChanges()); // CANNOT GET CHANGES BECAUSE ONLY ONE INSTANCE OF bulkToolForm
							TransactionFacade.getInstance().insert(transaction);
						} else {
							error = true;
						}
					}

					if (error) {
						UIUtils.showNotification("Tools Update Error", UIUtils.NotificationType.ERROR);
					} else {
						UIUtils.showNotification("Tools Updated successful", UIUtils.NotificationType.SUCCESS);
					}
				} else {    // COPY TOOLS
					for (InventoryItem tool : bulkTools) {
						if (InventoryFacade.getInstance().insert(tool)) {
							dataProvider.getItems().add(tool);

							Transaction transaction = new Transaction();
							transaction.setUser(AuthenticationService.getCurrentSessionUser());
							transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
							transaction.setOperation(Operation.ADD);
							transaction.setOperationTarget1(OperationTarget.INVENTORY_TOOL);
							transaction.setTargetDetails(tool.getName());
							TransactionFacade.getInstance().insert(transaction);
						} else {
							error = true;
						}
					}

					if (error) {
						UIUtils.showNotification("Tools Insert Error", UIUtils.NotificationType.ERROR);
					} else {
						UIUtils.showNotification("Tools Inserted Successful", UIUtils.NotificationType.SUCCESS);
					}
				}

				dataProvider.refreshAll();
				bulkEditDialog.close();
			}
		});

		bulkEditDialog.open();
	}

	private FlexBoxLayout constructBulkDialogHeader(boolean editMode) {

		FlexBoxLayout bulkDialogHeader = new FlexBoxLayout();
		bulkDialogHeader.setWidthFull();
		bulkDialogHeader.setFlexDirection(FlexDirection.ROW);
		bulkDialogHeader.setAlignItems(FlexComponent.Alignment.CENTER);
		bulkDialogHeader.add(UIUtils.createH3Label("Tool Details"));

		Label toolIndexLabel = UIUtils.createH4Label("1");
		bulkDialogHeader.add(toolIndexLabel);
		bulkDialogHeader.setComponentMargin(toolIndexLabel, Left.M, Top.NONE);

		Label slash = UIUtils.createH4Label("/");
		bulkDialogHeader.add(slash);
		bulkDialogHeader.setComponentMargin(slash, Horizontal.S, Top.NONE);

		Label toolsSizeLabel = UIUtils.createH4Label(""+bulkTools.size());
		bulkDialogHeader.add(toolsSizeLabel);
		bulkDialogHeader.setComponentMargin(toolsSizeLabel, Top.NONE);


		/*
			***HEADER BUTTONS & EVENTS***
		 */
		// ACTIVE WHEN COPYING TOOLS -> ALLOWS TO ADD ONE MORE COPY OF CURRENT TOOL
		Button addOneCopyButton = UIUtils.createIconButton(VaadinIcon.PLUS, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE);
		UIUtils.setTooltip("Create new copy of this Tool", addOneCopyButton);
		addOneCopyButton.addClickListener(e -> {
			InventoryItem newToolCopy = new InventoryItem(bulkTools.get(currentBulkEditToolIndex));
			bulkTools.add(newToolCopy);
			toolsSizeLabel.setText(""+(bulkTools.size()));
		});

		bulkDialogHeader.add(addOneCopyButton);
		bulkDialogHeader.setComponentMargin(addOneCopyButton, Left.AUTO);

		addOneCopyButton.setEnabled(!editMode);

		Button toolsToLeftButton = UIUtils.createIconButton(VaadinIcon.ANGLE_LEFT, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE);
		Button toolsToRightButton = UIUtils.createIconButton(VaadinIcon.ANGLE_RIGHT, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE);

		// ACTIVE WHEN COPYING TOOLS -> ALLOWS TO REMOVE CURRENT TOOL FROM LIST
		Button removeCurrentTool = UIUtils.createIconButton(VaadinIcon.MINUS, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
		UIUtils.setTooltip("Remove selected Tool", removeCurrentTool);
		removeCurrentTool.addClickListener(e -> {
			// If more than one item
			if (bulkTools.size() > 1) {

				int itemPositionToRemove = -1;

				// Current position is first
				if (currentBulkEditToolIndex == 0) {
					toolsToRightButton.click();
					itemPositionToRemove = 0;

					--currentBulkEditToolIndex;
				}

				// Current position is last
				if (currentBulkEditToolIndex == ((bulkTools.size())-1)) {
					toolsToLeftButton.click();
					itemPositionToRemove = ((bulkTools.size())-1);
				}

				// Current position is middle, move Left
				if (itemPositionToRemove == -1) {
					toolsToLeftButton.click();
					itemPositionToRemove = currentBulkEditToolIndex;
				}


				bulkTools.remove(itemPositionToRemove);
				toolsSizeLabel.setText(""+(bulkTools.size()));
				toolIndexLabel.setText(""+(currentBulkEditToolIndex+1));
			}
		});
		bulkDialogHeader.add(removeCurrentTool);
		bulkDialogHeader.setComponentMargin(removeCurrentTool, Right.L);

		removeCurrentTool.setEnabled(!editMode);


		//CLICK LEFT
		toolsToLeftButton.addClickShortcut(Key.ARROW_LEFT);
		UIUtils.setTooltip("Arrow Left", toolsToLeftButton);
		toolsToLeftButton.addClickListener(e -> {
			if (currentBulkEditToolIndex > 0) {

				InventoryItem editedTool = bulkToolForm.getTool();
				if (editedTool != null) {
					bulkTools.set(currentBulkEditToolIndex, editedTool);

					--currentBulkEditToolIndex;
					setToolBulkEditDialogContent(bulkTools.get(currentBulkEditToolIndex));

					toolIndexLabel.setText(""+(currentBulkEditToolIndex+1));
				}
			}
		});
		bulkDialogHeader.add(toolsToLeftButton);

		//CLICK RIGHT
		toolsToRightButton.addClickShortcut(Key.ARROW_RIGHT);
		UIUtils.setTooltip("Arrow Right", toolsToRightButton);
		toolsToRightButton.addClickListener(e -> {
			if (currentBulkEditToolIndex < (bulkTools.size()-1)) {

				InventoryItem editedTool = bulkToolForm.getTool();
				if (editedTool != null) {
					bulkTools.set(currentBulkEditToolIndex, editedTool);

					++currentBulkEditToolIndex;
					setToolBulkEditDialogContent(bulkTools.get(currentBulkEditToolIndex));

					toolIndexLabel.setText(""+(currentBulkEditToolIndex+1));
				}
			}
		});
		bulkDialogHeader.add(toolsToRightButton);

		return bulkDialogHeader;
	}

	private void setToolBulkEditDialogContent(InventoryItem tool) {
		bulkToolForm.setTool(tool);
		bulkEditDialog.setContent(bulkToolForm);
	}
}