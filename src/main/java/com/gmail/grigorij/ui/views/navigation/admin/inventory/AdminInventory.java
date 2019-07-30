package com.gmail.grigorij.ui.views.navigation.admin.inventory;

import com.github.appreciated.papermenubutton.PaperMenuButton;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.backend.entities.inventory.InventoryHierarchyType;
import com.gmail.grigorij.backend.entities.inventory.ToolStatus;
import com.gmail.grigorij.backend.entities.transaction.OperationType;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.*;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.*;
import com.gmail.grigorij.ui.utils.forms.editable.ToolCopyForm;
import com.gmail.grigorij.ui.utils.forms.editable.EditableCategoryForm;
import com.gmail.grigorij.ui.utils.forms.editable.EditableToolForm;
import com.gmail.grigorij.ui.views.navigation.admin.AdminMain;
import com.gmail.grigorij.utils.OperationStatus;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
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

	private final AdminMain adminMain;

	private EditableToolForm editableToolForm = new EditableToolForm(this);
	private EditableToolForm bulkEditableToolForm = new EditableToolForm(this);
	private EditableCategoryForm editableCategoryForm = new EditableCategoryForm();
	private ToolCopyForm toolCopyForm = new ToolCopyForm();

//	private AdminInventoryBulkEditor bulkEditor = null;

	private Grid<InventoryEntity> grid;
	private ListDataProvider<InventoryEntity> dataProvider;
	private List<InventoryEntity> selectedTools = null;

	private DetailsDrawer detailsDrawer;
	private DetailsDrawerHeader detailsDrawerHeader;
	private Button copyToolButton;
	private Button deleteToolButton;
	private Button editToolButton;


	public AdminInventory(AdminMain adminMain) {
		this.adminMain = adminMain;

		setClassName(CLASS_NAME);
		setSizeFull();
		setDisplay(Display.FLEX);
		setFlexDirection(FlexDirection.COLUMN);

		createHeader();
		createGrid();
		createDetailsDrawer();
	}

	private void createHeader() {
		FlexBoxLayout header = new FlexBoxLayout();
		header.setClassName(CLASS_NAME + "__header");
		header.setMargin(Top.S);
		header.setAlignItems(Alignment.BASELINE);
		header.setWidthFull();

		editToolButton = UIUtils.createIconButton(VaadinIcon.EDIT, ButtonVariant.LUMO_CONTRAST);
		editToolButton.setMinWidth("52px"); //looks better
		editToolButton.addClickListener(e -> editSelectedTools());
		editToolButton.setEnabled(false);

		header.add(editToolButton);

		TextField searchField = new TextField();
		searchField.setWidth("100%");
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Tools");
//		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event -> filterGrid(searchField.getValue()));

		header.add(searchField);
		header.setComponentMargin(searchField, Horizontal.S);


		Button actionsButton = UIUtils.createIconButton("Options", VaadinIcon.MENU, ButtonVariant.LUMO_CONTRAST);
		actionsButton.addClassName("hiding-text-button");

		FlexBoxLayout popupWrapper = new FlexBoxLayout();

		PaperMenuButton inventoryPaperMenuButton = new PaperMenuButton(actionsButton, popupWrapper);
		inventoryPaperMenuButton.setVerticalOffset(40);
		inventoryPaperMenuButton.setHorizontalOffset(-130);

		//POPUP VIEW
		popupWrapper.setFlexDirection(FlexDirection.COLUMN);
		popupWrapper.setDisplay(Display.FLEX);
		popupWrapper.setPadding(Horizontal.S);
		popupWrapper.setBackgroundColor("var(--lumo-base-color)");


		Button newToolButton = UIUtils.createIconButton("New Tool", VaadinIcon.TOOLS, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
		newToolButton.addClassName("button-align-left");
		newToolButton.addClickListener(e -> {
			inventoryPaperMenuButton.close();
			grid.select(null);
			showToolDetails(null, "New Tool");
		});

		popupWrapper.add(newToolButton);
		popupWrapper.setComponentMargin(newToolButton, Vertical.NONE);

		popupWrapper.add(new Divider(1, Vertical.XS));

		Button newCategoryButton = UIUtils.createIconButton("New Category", VaadinIcon.FILE_TREE, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
		newCategoryButton.addClassName("button-align-left");
		newCategoryButton.addClickListener(e -> {
			inventoryPaperMenuButton.close();
			grid.select(null);
			constructCategoryDialog(null);
		});

		popupWrapper.add(newCategoryButton);
		popupWrapper.setComponentMargin(newCategoryButton, Vertical.NONE);

		popupWrapper.add(new Divider(1, Vertical.XS));

		Button changeThemeButton = UIUtils.createIconButton("Import", VaadinIcon.SIGN_IN, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
		changeThemeButton.addClassName("button-align-left");
		changeThemeButton.addClickListener(e -> {
			inventoryPaperMenuButton.close();
			importTools();
		});

		popupWrapper.add(changeThemeButton);
		popupWrapper.setComponentMargin(changeThemeButton, Vertical.NONE);

		popupWrapper.add(new Divider(1, Vertical.XS));

		Button logOutButton = UIUtils.createIconButton("Export", VaadinIcon.SIGN_OUT, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
		logOutButton.addClassName("button-align-left");
		logOutButton.addClickListener(e -> {
			inventoryPaperMenuButton.close();
			exportTools();
		});

		popupWrapper.add(logOutButton);
		popupWrapper.setComponentMargin(logOutButton, Vertical.NONE);

		header.add(inventoryPaperMenuButton);
		header.setComponentPadding(inventoryPaperMenuButton, Horizontal.NONE);
		header.setComponentPadding(inventoryPaperMenuButton, Vertical.NONE);

		add(header);
	}

	private void createGrid() {
		grid = new Grid<>();
		grid.setId("tools-grid");
		grid.setClassName("grid-view");
		grid.setSizeFull();


		dataProvider = DataProvider.ofCollection(InventoryFacade.getInstance().getAllByHierarchyType(InventoryHierarchyType.TOOL));
		grid.setDataProvider(dataProvider);


		grid.addColumn(InventoryEntity::getName).setHeader("Tool")
				.setWidth(UIUtils.COLUMN_WIDTH_XL);

		grid.addColumn(tool -> (tool.getParentCategory() == null) ? "" : tool.getParentCategory().getName())
				.setHeader("Category")
				.setWidth(UIUtils.COLUMN_WIDTH_M);

		grid.addColumn(tool -> (tool.getCompany() == null) ? "" : tool.getCompany().getName())
				.setHeader("Company")
				.setWidth(UIUtils.COLUMN_WIDTH_M);

		grid.addColumn(tool -> (tool.getUsageStatus() == null) ? "" : tool.getUsageStatus().getStringValue())
				.setHeader("Status")
				.setWidth(UIUtils.COLUMN_WIDTH_XS)
				.setFlexGrow(0);

		grid.addColumn(tool -> (tool.getUser() == null) ? "" : tool.getUser().getUsername())
				.setHeader("User")
				.setWidth(UIUtils.COLUMN_WIDTH_M);

		grid.addColumn(new ComponentRenderer<>(tool -> UIUtils.createActiveGridIcon(tool.isDeleted()))).setHeader("Visible")
				.setWidth(UIUtils.COLUMN_WIDTH_XS)
				.setFlexGrow(0);

		grid.setSelectionMode(Grid.SelectionMode.MULTI);


		grid.addSelectionListener(event -> {
			selectedTools = new ArrayList<>(event.getAllSelectedItems());
			editToolButton.setEnabled((selectedTools.size() > 0));

			if ((selectedTools.size() <= 0)) {
				closeDetails();
			}
		});

		add(grid);
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
									StringUtils.containsIgnoreCase((tool.getUser() == null) ? "" : tool.getUser().getUsername(), sParam) ||
									StringUtils.containsIgnoreCase((tool.getReservedByUser() == null) ? "" : tool.getReservedByUser().getUsername(), sParam) ||
									StringUtils.containsIgnoreCase((tool.getParentCategory() == null) ? "" : tool.getParentCategory().getName(), sParam) ||
									StringUtils.containsIgnoreCase((tool.getCompany() == null) ? "" : tool.getCompany().getName(), sParam) ||
									StringUtils.containsIgnoreCase((tool.getUsageStatus() == null) ? "" : tool.getUsageStatus().getStringValue(), sParam);

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
							StringUtils.containsIgnoreCase((tool.getUser() == null) ? "" : tool.getUser().getUsername(), mainSearchString) ||
							StringUtils.containsIgnoreCase((tool.getReservedByUser() == null) ? "" : tool.getReservedByUser().getUsername(), mainSearchString) ||
							StringUtils.containsIgnoreCase((tool.getParentCategory() == null) ? "" : tool.getParentCategory().getName(), mainSearchString) ||
							StringUtils.containsIgnoreCase((tool.getCompany() == null) ? "" : tool.getCompany().getName(), mainSearchString) ||
							StringUtils.containsIgnoreCase((tool.getUsageStatus() == null) ? "" : tool.getUsageStatus().getStringValue(), mainSearchString)
			);
		}
	}

	private void createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		detailsDrawer.setContent(editableToolForm);
		detailsDrawer.setContentPadding(Left.M, Right.S);
		detailsDrawer.getElement().setAttribute(ProjectConstants.FORM_LAYOUT_LARGE_ATTR, true);

		detailsDrawerHeader = new DetailsDrawerHeader("");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());

		copyToolButton = UIUtils.createIconButton(VaadinIcon.COPY, ButtonVariant.LUMO_CONTRAST);
		copyToolButton.addClickListener(e -> constructToolCopyDialog());
		UIUtils.setTooltip("Create a copy of this inventory", copyToolButton);

		deleteToolButton = UIUtils.createIconButton(VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR);
		deleteToolButton.addClickListener(e -> confirmDelete());
		UIUtils.setTooltip("Delete this inventory from Database", deleteToolButton);

		detailsDrawerHeader.getContainer().add(copyToolButton, deleteToolButton);
		detailsDrawerHeader.getContainer().setComponentMargin(copyToolButton, Left.AUTO);
		detailsDrawerHeader.getContainer().setComponentMargin(deleteToolButton, Left.M);


		detailsDrawer.setHeader(detailsDrawerHeader);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().addClickListener(e -> updateTool());
		detailsDrawerFooter.getCancel().addClickListener(e -> closeDetails());
		detailsDrawer.setFooter(detailsDrawerFooter);

		adminMain.setDetailsDrawer(detailsDrawer);
	}

	private void showToolDetails(InventoryEntity tool, String title) {
		detailsDrawerHeader.setTitle(title);

		deleteToolButton.setEnabled( tool != null );
		copyToolButton.setEnabled( tool != null );

		editableToolForm.setBulkEditMode(false);
		editableToolForm.setTool(tool);
		detailsDrawer.show();

		UIUtils.updateFormSize(editableToolForm);
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.select(null);
	}

	private void editSelectedTools() {
		if (selectedTools != null) {
			if (selectedTools.size() > 0) {

				//IF ONLY ONE TOOL IS SELECTED
				if (selectedTools.size() == 1) {
					showToolDetails(selectedTools.get(0), "Tool Details");

				//IF MORE THAN ONE TOOL IS SELECTED
				} else {
					constructBulkEditDialog(OperationType.EDIT, null, selectedTools.size());

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


	public void constructCategoryDialog(InventoryEntity category) {

		boolean bNewCategory = (category == null);

		String headerTitle = (bNewCategory) ? "New Category" : "Category Details";
		String confirmButtonText = (bNewCategory) ? "Add" : "Edit";


		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label(headerTitle));

		editableCategoryForm.setCategory(category);
		dialog.setContent(editableCategoryForm);

		dialog.getCancelButton().addClickListener(e -> dialog.close());

		dialog.getConfirmButton().setText(confirmButtonText);
		dialog.getConfirmButton().addClickListener(e -> {
			InventoryEntity editedCategory = editableCategoryForm.getCategory();
			if (editedCategory != null) {
				updateCategory(editedCategory);
				dialog.close();
			}
		});

		/*
		Allow and handle category delete
		 */
		if (!bNewCategory) {
			dialog.setDeleteButtonVisible(true);
			dialog.getDeleteButton().addClickListener(deleteEvent -> {

				ConfirmDialog confirmCategoryDeleteDialog = new ConfirmDialog(ConfirmDialog.Type.DELETE, "category", category.getName());
				confirmCategoryDeleteDialog.closeOnCancel();
				confirmCategoryDeleteDialog.getConfirmButton().addClickListener(confirmDeleteEvent -> {
					try {
						InventoryFacade.getInstance().remove(category, new OperationStatus() {
							@Override
							public void onSuccess(String msg, UIUtils.NotificationType type) {
								UIUtils.showNotification("Category deleted successfully", type);

								confirmCategoryDeleteDialog.close();
								dialog.close();
							}

							@Override
							public void onFail(String msg, UIUtils.NotificationType type) {
								UIUtils.showNotification(msg, type);
							}
						});
					} catch (Exception ex) {
						UIUtils.showNotification("Category delete failed", UIUtils.NotificationType.ERROR);
						ex.printStackTrace();
					}
				});
				confirmCategoryDeleteDialog.open();
			});
		}

		dialog.open();
	}

	private void constructToolCopyDialog() {
		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label("Copy Tool Information"));

		if (!toolCopyForm.setOriginalTool(selectedTools.get(0))) {
			System.out.println("ORIGINAL TOOL NULL");
			return;
		}
		dialog.setContent(toolCopyForm);

		dialog.getCancelButton().addClickListener(e -> dialog.close());

		dialog.getConfirmButton().setText("Copy");
		dialog.getConfirmButton().addClickListener(e -> {
			InventoryEntity toolToCopy = toolCopyForm.getToolCopy();
			if (toolToCopy != null) {
				dialog.close();

				constructBulkEditDialog(OperationType.COPY, toolToCopy, toolCopyForm.getNumberOfCopies());

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


	private void updateCategory(InventoryEntity editedCategory) {
		System.out.println("\nupdateCategory()");

		if (editedCategory != null) {

			if (editedCategory.getParentCategory() != null) {
				if (editedCategory.getParentCategory().equals(InventoryFacade.getInstance().getRootCategory())) {
					editedCategory.setParentCategory(null);
				}
			}

			if (editableCategoryForm.isNew()) {
				if (InventoryFacade.getInstance().insert(editedCategory)) {
					UIUtils.showNotification("Category created successfully", UIUtils.NotificationType.SUCCESS);
				} else {
					UIUtils.showNotification("Category insert failed", UIUtils.NotificationType.ERROR);
				}
			} else {
				if (InventoryFacade.getInstance().update(editedCategory)) {
					UIUtils.showNotification("Category updated successfully", UIUtils.NotificationType.SUCCESS);
				} else {
					UIUtils.showNotification("Category updated failed", UIUtils.NotificationType.ERROR);
				}
			}
		}
	}

	private void updateTool() {
		System.out.println("updateTool()");

		InventoryEntity editedTool = editableToolForm.getTool();

		if (editedTool != null) {

			if (editedTool.getParentCategory() != null) {
				if (editedTool.getParentCategory().equals(InventoryFacade.getInstance().getRootCategory())) {
					editedTool.setParentCategory(null);
				}
			}

			if (editableToolForm.isNew()) {
				if (InventoryFacade.getInstance().insert(editedTool)) {
					dataProvider.getItems().add(editedTool);

					UIUtils.showNotification("Tool created successfully", UIUtils.NotificationType.SUCCESS);
				} else {
					UIUtils.showNotification("Tool insert failed", UIUtils.NotificationType.ERROR);
				}
			} else {
				if (InventoryFacade.getInstance().update(editedTool)) {
					UIUtils.showNotification("Tool updated successfully", UIUtils.NotificationType.SUCCESS);
				} else {
					UIUtils.showNotification("Tool update failed", UIUtils.NotificationType.ERROR);
				}
			}
			dataProvider.refreshAll();
		}
	}

	private void confirmDelete() {
		if (detailsDrawer.isOpen()) {

			final InventoryEntity selectedTool = selectedTools.get(0);

			if (selectedTool != null) {

				ConfirmDialog dialog = new ConfirmDialog(ConfirmDialog.Type.DELETE, "selected tool", selectedTool.getName());
				dialog.closeOnCancel();
				dialog.getConfirmButton().addClickListener(e -> {

					InventoryFacade.getInstance().remove(selectedTool, new OperationStatus() {
						@Override
						public void onSuccess(String msg, UIUtils.NotificationType type) {
							selectedTools.clear();
							grid.select(null);

							dataProvider.getItems().remove(selectedTool);
							dataProvider.refreshAll();

							closeDetails();
							UIUtils.showNotification(msg, type);

							dialog.close();
						}

						@Override
						public void onFail(String msg, UIUtils.NotificationType type) {
							UIUtils.showNotification(msg, type);

							dialog.close();
						}
					});
				});
				dialog.open();
			}
		}
	}



	private void exportTools() {
		System.out.println("Export Tools...");
	}

	private void importTools() {
		System.out.println("Import Tools...");
	}



	/*
	 ******BULK OPERATIONS******
	 */
	private CustomDialog bulkEditDialog;
	private List<InventoryEntity> bulkTools;
	private int currentBulkEditToolIndex = 0;

	private void constructBulkEditDialog(OperationType operationType, InventoryEntity toolToCopy, int numberOfTools) {

		if (operationType.equals(OperationType.EDIT)) {
			bulkTools = new ArrayList<>(selectedTools);

		} else if (operationType.equals(OperationType.COPY)) {
			bulkTools = new ArrayList<>();

			for (int i = 0; i < numberOfTools; i++) {
				InventoryEntity newTool = new InventoryEntity(toolToCopy);
				bulkTools.add(newTool);
			}

		} else {
			System.out.println("constructBulkEditDialog() -> Unknown OperationType: " + operationType.getStringValue());
			return;
		}

		bulkEditableToolForm.setBulkEditMode(true);

		bulkEditDialog = new CustomDialog();
		bulkEditDialog.setCloseOnOutsideClick(false);
		bulkEditDialog.setCloseOnEsc(false);

		bulkEditDialog.setHeader(constructBulkDialogHeader(operationType, numberOfTools));

		setToolBulkEditDialogContent(bulkTools.get(0));

		bulkEditDialog.getCancelButton().addClickListener(closeEvent -> {
			confirmBulkDialogClose();
		});

		bulkEditDialog.getConfirmButton().setText("Save");
		bulkEditDialog.getConfirmButton().addClickListener(confirmEvent -> {

			boolean error = false;

			if (operationType.equals(OperationType.COPY)) {
				for (InventoryEntity tool : bulkTools) {
					if (InventoryFacade.getInstance().insert(tool)) {
						dataProvider.getItems().add(tool);
					} else {
						error = true;
					}
				}

				if (error) {
					UIUtils.showNotification("Tools Insert Error", UIUtils.NotificationType.ERROR);
				} else {
					UIUtils.showNotification("Tools Inserted Successful", UIUtils.NotificationType.SUCCESS);


				}
			} else {
				for (InventoryEntity tool : bulkTools) {
					if (!InventoryFacade.getInstance().update(tool)) {
						error = true;
					}
				}

				if (error) {
					UIUtils.showNotification("Tools Update Error", UIUtils.NotificationType.ERROR);
				} else {
					UIUtils.showNotification("Tools Updated successful", UIUtils.NotificationType.SUCCESS);
				}
			}

			dataProvider.refreshAll();
			bulkEditDialog.close();
		});

		bulkEditDialog.open();
	}

	private FlexBoxLayout constructBulkDialogHeader(OperationType operationType, int numberOfTools) {

		FlexBoxLayout bulkDialogHeader = new FlexBoxLayout();
		bulkDialogHeader.setSizeFull();
		bulkDialogHeader.setFlexDirection(FlexDirection.ROW);
		bulkDialogHeader.setAlignItems(FlexComponent.Alignment.CENTER);
		bulkDialogHeader.add(UIUtils.createH3Label("Tool Details"));

		Label toolIndexLabel = UIUtils.createH4Label("1");
		bulkDialogHeader.add(toolIndexLabel);
		bulkDialogHeader.setComponentMargin(toolIndexLabel, Left.M);

		Label slash = UIUtils.createH4Label("/");
		bulkDialogHeader.add(slash);
		bulkDialogHeader.setComponentMargin(slash, Horizontal.S);

		Label toolsSizeLabel = UIUtils.createH4Label(""+numberOfTools);
		bulkDialogHeader.add(toolsSizeLabel);


		/*
			***HEADER BUTTONS & EVENTS***
		 */

		// ACTIVE WHEN OperationType.COPY
		Button addOneCopyButton = UIUtils.createIconButton(VaadinIcon.PLUS, ButtonVariant.LUMO_CONTRAST);
		UIUtils.setTooltip("Create new copy of this Tool", addOneCopyButton);
		addOneCopyButton.addClickListener(e -> {
			InventoryEntity newToolCopy = new InventoryEntity(bulkTools.get(currentBulkEditToolIndex));
			bulkTools.add(newToolCopy);
			toolsSizeLabel.setText(""+(bulkTools.size()));
		});

		bulkDialogHeader.add(addOneCopyButton);
		bulkDialogHeader.setComponentMargin(addOneCopyButton, Left.AUTO);

		addOneCopyButton.setEnabled((operationType.equals(OperationType.COPY)));

		Button toolsToLeftButton = UIUtils.createIconButton(VaadinIcon.ANGLE_LEFT, ButtonVariant.LUMO_CONTRAST);
		Button toolsToRightButton = UIUtils.createIconButton(VaadinIcon.ANGLE_RIGHT, ButtonVariant.LUMO_CONTRAST);

		// ACTIVE WHEN OperationType.COPY
		Button deleteCurrentTool = UIUtils.createIconButton(VaadinIcon.MINUS, ButtonVariant.LUMO_ERROR);
		UIUtils.setTooltip("Remove selected Tool", deleteCurrentTool);
		deleteCurrentTool.addClickListener(e -> {

			// If more that one item
			if (bulkTools.size() > 1) {

				int itemPositionToRemove = -1;

				// Current position is first
				if (currentBulkEditToolIndex == 0) {
					toolsToRightButton.click();
					itemPositionToRemove = 0;
				}

				// Current position is last
				if (currentBulkEditToolIndex == ((bulkTools.size())-1)) {
					toolsToLeftButton.click();
					itemPositionToRemove = ((bulkTools.size())-1);
				}

				// Current position is middle, move Left
				if (itemPositionToRemove == -1) {
					itemPositionToRemove = currentBulkEditToolIndex;
					toolsToLeftButton.click();
				}

				bulkTools.remove(itemPositionToRemove);
				toolIndexLabel.setText(""+(bulkTools.size()));
			}
		});
		bulkDialogHeader.add(deleteCurrentTool);
		bulkDialogHeader.setComponentMargin(deleteCurrentTool, Right.L);

		deleteCurrentTool.setEnabled((operationType.equals(OperationType.COPY)));


		//DELETE ALL TOOL FROM DB
		Button deleteAllToolsButton = UIUtils.createIconButton(VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR);
		UIUtils.setTooltip("Remove all selected tools from Database", deleteAllToolsButton);
		deleteAllToolsButton.addClickListener(e -> {

			ConfirmDialog confirmDialog = new ConfirmDialog(ConfirmDialog.Type.DELETE, "selected tools", "Delete " + numberOfTools + " Selected Tools");
			confirmDialog.closeOnCancel();
			confirmDialog.getConfirmButton().addClickListener(confirmDeleteEvent -> {
				try {
					final boolean[] deleteError = {false};

					for (InventoryEntity tool : bulkTools) {
						InventoryFacade.getInstance().remove(tool, new OperationStatus() {
							@Override
							public void onSuccess(String msg, UIUtils.NotificationType type) {}
							@Override
							public void onFail(String msg, UIUtils.NotificationType type) {
								UIUtils.showNotification(msg, type);
								deleteError[0] = true;
							}
						});
					}

					if (!deleteError[0]) {
						UIUtils.showNotification("Tools deleted successfully", UIUtils.NotificationType.SUCCESS);
					}

					for (InventoryEntity tool : selectedTools) {
						dataProvider.getItems().remove(tool);
					}
					dataProvider.refreshAll();
					selectedTools.clear();
					grid.select(null);

					confirmDialog.close();
					bulkEditDialog.close();
				} catch (Exception ex) {
					UIUtils.showNotification("Tool delete failed", UIUtils.NotificationType.ERROR);
					ex.printStackTrace();
				}
			});
			confirmDialog.open();
		});

		bulkDialogHeader.add(deleteAllToolsButton);
		bulkDialogHeader.setComponentMargin(deleteAllToolsButton, Right.S);


		deleteAllToolsButton.setEnabled((operationType.equals(OperationType.EDIT)));


		//CLICK LEFT
		toolsToLeftButton.addClickShortcut(Key.ARROW_LEFT);
		UIUtils.setTooltip("Arrow Left", toolsToLeftButton);
		toolsToLeftButton.addClickListener(e -> {
			if (currentBulkEditToolIndex > 0) {

				InventoryEntity editedTool = bulkEditableToolForm.getTool();
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
			if (currentBulkEditToolIndex < (numberOfTools-1)) {

				InventoryEntity editedTool = bulkEditableToolForm.getTool();
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

	private void setToolBulkEditDialogContent(InventoryEntity tool) {
		bulkEditableToolForm.setTool(tool);
		bulkEditDialog.setContent(bulkEditableToolForm);
	}


	private void confirmBulkDialogClose() {
		ConfirmDialog dialog = new ConfirmDialog("This action will revert all made changes. Proceed?");
		dialog.closeOnCancel();
		dialog.getConfirmButton().addClickListener(event -> {
			bulkTools.clear();
			bulkEditDialog.close();
			dialog.close();
		});
		dialog.open();
	}

	public void setBulkStatus(String status) {
		try {
			for (InventoryEntity t : bulkTools) {
				t.setDeleted( (status.equals(ProjectConstants.INACTIVE)) );
			}
			UIUtils.showNotification("Status set successfully", UIUtils.NotificationType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			UIUtils.showNotification("Status set failed", UIUtils.NotificationType.ERROR);
		}
	}

	public void setBulkCompanies(Company company) {
		try {
			for (InventoryEntity t : bulkTools) {
				t.setCompany(company);
			}
			UIUtils.showNotification("Categories set successfully", UIUtils.NotificationType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			UIUtils.showNotification("Categories set failed", UIUtils.NotificationType.ERROR);
		}
	}

	public void setBulkCategories(InventoryEntity category) {
		try {
			for (InventoryEntity t : bulkTools) {
				t.setParentCategory(category);
			}
			UIUtils.showNotification("Categories set successfully", UIUtils.NotificationType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			UIUtils.showNotification("Categories set failed", UIUtils.NotificationType.ERROR);
		}
	}

	public void setBulkUsageStatus(ToolStatus usageStatus) {
		try {
			for (InventoryEntity t : bulkTools) {
				t.setUsageStatus(usageStatus);
			}
			UIUtils.showNotification("Usage Status set successfully", UIUtils.NotificationType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			UIUtils.showNotification("Usage Status set failed", UIUtils.NotificationType.ERROR);
		}
	}
}