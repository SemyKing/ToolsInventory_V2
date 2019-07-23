package com.gmail.grigorij.ui.views.navigation.admin.inventory;

import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.backend.entities.inventory.InventoryHierarchyType;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.*;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.*;
import com.gmail.grigorij.ui.utils.forms.ToolCopyForm;
import com.gmail.grigorij.ui.utils.forms.admin.ToolCategoryForm;
import com.gmail.grigorij.ui.utils.forms.admin.AdminToolForm;
import com.gmail.grigorij.ui.views.navigation.admin.AdminMain;
import com.gmail.grigorij.ui.views.navigation.admin.inventory.AdminInventoryBulkEditor.OperationType;
import com.gmail.grigorij.utils.OperationStatus;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
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
	private AdminToolForm adminToolForm;
	private AdminInventoryBulkEditor bulkEditor = null;
	private ToolCategoryForm adminCategoryForm = new ToolCategoryForm();
	private ToolCopyForm toolCopyForm; //used for checkboxes(inventory parameters) and numbers of copy

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

		this.adminToolForm = new AdminToolForm(this);

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
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
//		searchField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
		searchField.addValueChangeListener(event -> filterGrid(searchField.getValue()));

		header.setComponentMargin(searchField, Left.S);
		header.add(searchField);

		FlexBoxLayout optionsContextMenuButton = adminMain.constructOptionsButton();
		header.add(optionsContextMenuButton);

		ContextMenu contextMenu = new ContextMenu(optionsContextMenuButton);
		contextMenu.setOpenOnClick(true);

		contextMenu.add(new Divider(Bottom.XS));
		contextMenu.addItem(UIUtils.createTextIcon(VaadinIcon.TOOLS, UIUtils.createText("Add Tool")), e -> {
			grid.select(null);
			showToolDetails(null, "New Tool");
		});
		contextMenu.add(new Divider(Vertical.XS));
		contextMenu.addItem(UIUtils.createTextIcon(VaadinIcon.FILE_TREE, UIUtils.createText("Add Category")), e -> {
			grid.select(null);
			constructToolCategoryDetails(null);
		});
		contextMenu.add(new Divider(Vertical.XS));
		contextMenu.addItem(UIUtils.createTextIcon(VaadinIcon.INSERT, UIUtils.createText("Import")), e -> importTools());
		contextMenu.add(new Divider(Vertical.XS));
		contextMenu.addItem(UIUtils.createTextIcon(VaadinIcon.EXTERNAL_LINK, UIUtils.createText("Export")), e -> exportTools());
		contextMenu.add(new Divider(Top.XS));

		add(header);
	}

	private void createGrid() {
		grid = new Grid<>();
		grid.setId("tools-grid");
		grid.setClassName("grid-view");
		grid.setSizeFull();

		dataProvider = DataProvider.ofCollection(InventoryFacade.getInstance().getAllByHierarchyType(InventoryHierarchyType.TOOL));
		grid.setDataProvider(dataProvider);

//		grid.addColumn(InventoryEntity::getId).setHeader("ID")
//				.setWidth(UIUtils.COLUMN_WIDTH_XS)
//				.setFlexGrow(0);

		grid.addColumn(tool -> (tool.getCompany() == null) ? "" : tool.getCompany().getName())
				.setHeader("Company")
				.setWidth(UIUtils.COLUMN_WIDTH_M);

		grid.addColumn(tool -> (tool.getParentCategory() == null) ? "" : tool.getParentCategory().getName())
				.setHeader("Category")
				.setWidth(UIUtils.COLUMN_WIDTH_M);

		grid.addColumn(InventoryEntity::getName).setHeader("Tool")
				.setWidth(UIUtils.COLUMN_WIDTH_XL);

		grid.addColumn(tool -> (tool.getUsageStatus() == null) ? "" : tool.getUsageStatus().getStringValue())
				.setHeader("Status")
				.setWidth(UIUtils.COLUMN_WIDTH_S)
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
		detailsDrawer.setContent(adminToolForm);
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

		adminToolForm.setTool(tool);
		detailsDrawer.show();

		UIUtils.updateFormSize(adminToolForm);
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.select(null);
	}


	public void constructToolCategoryDetails(InventoryEntity category) {

		boolean bNewCategory = (category == null);

		String headerTitle = (bNewCategory) ? "New Category" : "Category Details";
		String confirmButtonText = (bNewCategory) ? "Add" : "Edit";


		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH4Label(headerTitle));


		adminCategoryForm.setCategory(category);
		dialog.setContent(adminCategoryForm);

		dialog.getCancelButton().addClickListener(e -> dialog.close());

		dialog.setConfirmButton(UIUtils.createButton(confirmButtonText, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));
		dialog.getConfirmButton().addClickListener(e -> {
			InventoryEntity editedCategory = adminCategoryForm.getCategory();
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


	private void updateCategory(InventoryEntity editedCategory) {
		System.out.println("\nupdateCategory()");

		if (editedCategory != null) {

			if (editedCategory.getParentCategory() != null) {
				if (editedCategory.getParentCategory().equals(InventoryFacade.getInstance().getRootCategory())) {
					editedCategory.setParentCategory(null);
				}
			}

			if (adminCategoryForm.isNew()) {
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

		InventoryEntity editedTool = adminToolForm.getTool();

		if (editedTool != null) {

			if (editedTool.getParentCategory() != null) {
				if (editedTool.getParentCategory().equals(InventoryFacade.getInstance().getRootCategory())) {
					editedTool.setParentCategory(null);
				}
			}

			if (adminToolForm.isNew()) {
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
							clearSelection();

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


	private void editSelectedTools() {
		if (selectedTools != null) {
			if (selectedTools.size() > 0) {
				if (selectedTools.size() == 1) {
					showToolDetails(selectedTools.get(0), "Tool Details");
				} else {
					bulkEditor = new AdminInventoryBulkEditor(this, OperationType.EDIT, selectedTools.size());
					bulkEditor.setBulkEditTools(selectedTools);
					bulkEditor.initToolBulkEdit();

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

	private void constructToolCopyDialog() {
		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH4Label("Copy Tool Information"));

		toolCopyForm = new ToolCopyForm();
		if (!toolCopyForm.setOriginalTool(selectedTools.get(0))) {
			System.out.println("ORIGINAL TOOL NULL");
			return;
		}
		dialog.setContent(toolCopyForm);

		dialog.getCancelButton().addClickListener(e -> dialog.close());

		dialog.setConfirmButton(UIUtils.createButton("Copy", ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));
		dialog.getConfirmButton().addClickListener(e -> {
			InventoryEntity toolCopy = toolCopyForm.getToolCopy();
			if (toolCopy != null) {
				dialog.close();
				bulkEditor = new AdminInventoryBulkEditor(this, OperationType.COPY, toolCopyForm.getNumberOfCopies());
				bulkEditor.setBulkEditTool(toolCopy);
				bulkEditor.initToolBulkEdit();

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

	ListDataProvider<InventoryEntity> getDataProvider() {
		return this.dataProvider;
	}

	void clearSelection() {
		selectedTools.clear();
		grid.select(null);
	}

}