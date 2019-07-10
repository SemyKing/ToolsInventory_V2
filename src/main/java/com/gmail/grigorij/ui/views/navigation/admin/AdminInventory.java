package com.gmail.grigorij.ui.views.navigation.admin;

import com.gmail.grigorij.backend.database.facades.ToolFacade;
import com.gmail.grigorij.backend.entities.tool.HierarchyType;
import com.gmail.grigorij.backend.entities.tool.Tool;
import com.gmail.grigorij.backend.entities.tool.ToolStatus;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.*;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.utils.css.Display;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.*;
import com.gmail.grigorij.ui.utils.forms.ToolCopyForm;
import com.gmail.grigorij.ui.utils.forms.admin.AdminToolCategoryForm;
import com.gmail.grigorij.ui.utils.forms.admin.AdminToolForm;
import com.gmail.grigorij.ui.views.navigation.admin.AdminInventoryBulkEditor.OperationType;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
	final static String TAB_NAME = ProjectConstants.INVENTORY;

	private AdminMain adminMain;
	private AdminToolForm adminToolForm;
	private AdminInventoryBulkEditor bulkEditor = null;
	private AdminToolCategoryForm adminCategoryForm = new AdminToolCategoryForm();
	private ToolCopyForm toolCopyForm; //used for checkboxes(tool parameters) and numbers of copy

	private Grid<Tool> grid;
	private ListDataProvider<Tool> dataProvider;
	private List<Tool> selectedTools = null;

	private DetailsDrawer detailsDrawer;
	private DetailsDrawerHeader detailsDrawerHeader;
	private Button copyToolButton;
	private Button deleteToolButton;
	private Button editToolButton;


	AdminInventory(AdminMain adminMain) {
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

		dataProvider = DataProvider.ofCollection(ToolFacade.getInstance().getAllToolsOnly());
		grid.setDataProvider(dataProvider);

		grid.addColumn(Tool::getId).setHeader("ID")
				.setWidth(UIUtils.COLUMN_WIDTH_XS)
				.setFlexGrow(0);

//		ComponentRenderer<Span, Tool> toolCompanyRenderer = new ComponentRenderer<>(
//				tool -> {
//					Span companyName = new Span("");
//					companyName.setWidth("100%");
//					if (tool.getCompany() != null) {
//						companyName.setText(tool.getCompany().getName());
//					}
//					return companyName;
//				});
//		grid.addColumn(toolCompanyRenderer)
//				.setHeader("Company")
//				.setWidth(UIUtils.COLUMN_WIDTH_M);

		grid.addColumn(tool -> (tool.getCompany() == null) ? "" : tool.getCompany().getName())
				.setHeader("Company")
				.setWidth(UIUtils.COLUMN_WIDTH_M);

//		ComponentRenderer<Span, Tool> toolCategoryRenderer = new ComponentRenderer<>(
//				tool -> {
//					Span categoryName = new Span("");
//					categoryName.setWidth("100%");
//					if (tool.getParentCategory() != null) {
//						categoryName.setText(tool.getParentCategory().getName());
//					}
//					return categoryName;
//				});
//		grid.addColumn(toolCategoryRenderer)
//				.setHeader("Category")
//				.setWidth(UIUtils.COLUMN_WIDTH_M);

		grid.addColumn(tool -> (tool.getParentCategory() == null) ? "" : tool.getParentCategory().getName())
				.setHeader("Category")
				.setWidth(UIUtils.COLUMN_WIDTH_M);

		grid.addColumn(Tool::getName).setHeader("Tool")
				.setWidth(UIUtils.COLUMN_WIDTH_XL);

//		ComponentRenderer<FlexBoxLayout, Tool> toolStatusRenderer = new ComponentRenderer<>(
//				tool -> {
//					FlexBoxLayout layout = new FlexBoxLayout();
//					ToolStatus status = tool.getUsageStatus();
//					if (status != null) {
//						layout = new CustomBadge(status.getStringValue(), status.getColor(), status.getIcon());
//					}
//					return layout;
//				});
//		grid.addColumn(toolStatusRenderer)
//				.setHeader("Status")
//				.setWidth(UIUtils.COLUMN_WIDTH_S)
//				.setFlexGrow(0);

		grid.addColumn(tool -> (tool.getUsageStatus() == null) ? "" : tool.getUsageStatus().getStringValue())
				.setHeader("Status")
				.setWidth(UIUtils.COLUMN_WIDTH_S)
				.setFlexGrow(0);

//		ComponentRenderer<Component, Tool> toolUserRenderer = new ComponentRenderer<>(
//				tool -> {
//					FlexBoxLayout layout  = new FlexBoxLayout();
//					layout.setWidth("100%");
//
//					if (tool.getHierarchyType().equals(HierarchyType.TOOL)) {
//						ToolStatus status = tool.getUsageStatus();
//
//						if (status == null) {
//							System.err.println("Tools status is NULL");
//							System.out.println("Tool: " + tool.toString());
//						} else {
//							if (status.equals(ToolStatus.IN_USE)) {
//								Span username = new Span("");
//								username.setWidth("100%");
//
//								if (tool.getUser() == null) {
//									System.err.println("Tools User is NULL with status: " + status.getStringValue());
//								} else {
//									username.setText(tool.getUser().getUsername());
//									layout.add(username);
//								}
//							} else if (status.equals(ToolStatus.RESERVED)) {
//								if (tool.getUser() == null) {
//									System.err.println("Tools User is NULL with status: " + status.getStringValue());
//								}
//								if (tool.getReservedByUser() == null) {
//									System.err.println("Tools User (reserved by) is NULL with status: " + status.getStringValue());
//								}
//
//								if (tool.getUser() != null && tool.getReservedByUser() != null) {
//									ListItem item = new ListItem(tool.getUser().getUsername(), tool.getReservedByUser().getUsername());
//									item.setHorizontalPadding(false);
//									layout.add(item);
//								}
//							}
//						}
//					}
//					return layout;
//				});
//		grid.addColumn(toolUserRenderer)
//				.setHeader("User")
//				.setWidth(UIUtils.COLUMN_WIDTH_M);

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

//		System.out.println("Main Search String: " + mainSearchString);
//
//		final String space = " ";
//
//		List<String> finalParams = new ArrayList<>();
//
//		//Add search parameters with double quotes: "param".
//		if ((StringUtils.countMatches(mainSearchString, "\"") % 2) == 0) {
//			String[] valuesInQuotes = StringUtils.substringsBetween(mainSearchString, "\"", "\"");
//
//			if (valuesInQuotes != null) {
//				if (valuesInQuotes.length > 0) {
//					finalParams.addAll(Arrays.asList(valuesInQuotes));
//				}
//			}
//		}
//
//		String searchParamCopy = mainSearchString.replaceAll("\"", "");
//
//		for (String s : finalParams) {
//			if (searchParamCopy.contains(s)) {
//				searchParamCopy = searchParamCopy.replace(s, "");
//			}
//		}
//
////		searchParamCopy = searchParamCopy.trim();
//
//		if (searchParamCopy.contains(space)) {
//			String[] searchParams = searchParamCopy.split(space);
//
//			for (String s : searchParams) {
//				s = s.trim();
//				if (s.length() > 0) {
//					finalParams.add(s);
//				}
//			}
//		}
//
//		System.out.println();
//		System.out.println("Final parameters");
//		for (String s : finalParams) {
//			System.out.println("param: '" + s + "'");
//		}
//
//
//		dataProvider.addFilter(
//			tool -> {
//				boolean res = true;
//				for (String sParam : finalParams) {
//					res = StringUtils.containsIgnoreCase(tool.getName(), sParam) ||
//							StringUtils.containsIgnoreCase(tool.getToolInfo(), sParam) ||
//							StringUtils.containsIgnoreCase(tool.getManufacturer(), sParam) ||
//							StringUtils.containsIgnoreCase(tool.getModel(), sParam) ||
//							StringUtils.containsIgnoreCase(tool.getSnCode(), sParam) ||
//							StringUtils.containsIgnoreCase(tool.getBarcode(), sParam) ||
//
//							(tool.getCompany() != null) ? StringUtils.containsIgnoreCase(tool.getCompany().getName(), sParam) : false ||
//							(tool.getParentCategory() != null) ? StringUtils.containsIgnoreCase(tool.getParentCategory().getName(), sParam) : false ||
//
//							StringUtils.containsIgnoreCase(tool.getUsageStatus().getStringValue(), sParam);
//
//
//					//(res) -> shows All items based on searchParams
//					//(!res) -> shows ONE item based on searchParams
//					if (!res)
//						break;
//				}
//				return res;
//			}
//		);

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

		// Original Header
		detailsDrawerHeader = new DetailsDrawerHeader("");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());

		copyToolButton = UIUtils.createIconButton(VaadinIcon.COPY, ButtonVariant.LUMO_CONTRAST);
		copyToolButton.addClickListener(e -> constructToolCopyDialog());
		UIUtils.setTooltip("Create a copy of this tool", copyToolButton);

		deleteToolButton = UIUtils.createIconButton(VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR);
		deleteToolButton.addClickListener(e -> confirmDelete());
		UIUtils.setTooltip("Delete this tool from Database", deleteToolButton);

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

	private void showToolDetails(Tool tool, String title) {
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


	public void constructToolCategoryDetails(Tool category) {

		boolean bNewCategory = (category == null);

		String headerTitle = (bNewCategory) ? "New Category" : "Category Details";
		String confirmButtonText = (bNewCategory) ? "Add" : "Edit";


		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH4Label(headerTitle));
		dialog.getCancelButton().addClickListener(e -> dialog.close());
		dialog.setConfirmButton(UIUtils.createButton(confirmButtonText, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));

		/*
		Allow and handle category delete
		 */
		if (!bNewCategory) {
			dialog.setDeleteButtonVisible(true);
			dialog.getDeleteButton().addClickListener(deleteEvent -> {

				ConfirmDialog confirmDialog = new ConfirmDialog(ConfirmDialog.Type.DELETE, "category", category.getName());
				confirmDialog.closeOnCancel();
				confirmDialog.getConfirmButton().addClickListener(confirmDeleteEvent -> {
					try {
						if (ToolFacade.getInstance().remove(category)) {
							UIUtils.showNotification("Category deleted successfully", UIUtils.NotificationType.SUCCESS);
						} else {
							UIUtils.showNotification("Category delete failed", UIUtils.NotificationType.ERROR);
						}
						confirmDialog.close();
						dialog.close();

					} catch (Exception ex) {
						UIUtils.showNotification("Category delete failed", UIUtils.NotificationType.ERROR);
						ex.printStackTrace();
					}
				});
				confirmDialog.open();
			});
		}

		adminCategoryForm.setCategory(category);
		dialog.setContent(adminCategoryForm);
		dialog.getConfirmButton().addClickListener(e -> {
			Tool editedCategory = adminCategoryForm.getCategory();
			if (editedCategory != null) {
				updateCategory(editedCategory);
				dialog.close();
			}
		});
		dialog.open();
	}


	private void updateCategory(Tool editedCategory) {
		System.out.println("\nupdateCategory()");

		if (editedCategory != null) {

			System.out.println("editedCategory.getParentCategory(): " + editedCategory.getParentCategory());

			if (editedCategory.getParentCategory() != null) {
				if (editedCategory.getParentCategory().equals(ToolFacade.getInstance().getRootCategory())) {
					editedCategory.setParentCategory(null);
				}
			}

			if (adminCategoryForm.isNew()) {
				if (ToolFacade.getInstance().insert(editedCategory)) {
					UIUtils.showNotification("Category created successfully", UIUtils.NotificationType.SUCCESS);
				} else {
					UIUtils.showNotification("Category insert failed", UIUtils.NotificationType.ERROR);
				}
			} else {
				if (ToolFacade.getInstance().update(editedCategory)) {
					UIUtils.showNotification("Category updated successfully", UIUtils.NotificationType.SUCCESS);
				} else {
					UIUtils.showNotification("Category updated failed", UIUtils.NotificationType.ERROR);
				}
			}
		}
	}

	private void updateTool() {
		System.out.println("updateTool()");

		Tool editedTool = adminToolForm.getTool();

		if (editedTool != null) {

			System.out.println("editedTool.getParentCategory(): " + editedTool.getParentCategory());

			if (editedTool.getParentCategory() != null) {
				if (editedTool.getParentCategory().equals(ToolFacade.getInstance().getRootCategory())) {
					editedTool.setParentCategory(null);
				}
			}

			if (adminToolForm.isNew()) {
				if (ToolFacade.getInstance().insert(editedTool)) {
					UIUtils.showNotification("Tool created successfully", UIUtils.NotificationType.SUCCESS);
					grid.select(editedTool);
				} else {
					UIUtils.showNotification("Tool insert failed", UIUtils.NotificationType.ERROR);
				}
			} else {
				if (ToolFacade.getInstance().update(editedTool)) {
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

			final Tool selectedTool = selectedTools.get(0);

			if (selectedTool != null) {

				CustomDialog dialog = new CustomDialog();
				dialog.setHeader(UIUtils.createH4Label("Confirm delete"));

				dialog.getCancelButton().addClickListener(e -> dialog.close());
				dialog.setConfirmButton(UIUtils.createButton("Delete", VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY));
				dialog.getConfirmButton().setEnabled(false);

				TextField confirmInputField = new TextField("Input tool name to confirm action");
				confirmInputField.setRequired(true);
				confirmInputField.setValueChangeMode(ValueChangeMode.EAGER);
				confirmInputField.addValueChangeListener(e -> {
					dialog.getConfirmButton().setEnabled(false);

					if (e.getValue() != null) {
						if (e.getValue().length() > 0) {
							if (e.getValue().equals(selectedTool.getName())) {
								dialog.getConfirmButton().setEnabled(true);
							}
						}
					}
				});

				dialog.setContent(
						new Span("Are you sure you want to delete this tool?"),
						new Span("This will completely remove selected tool from Database."),
						new HorizontalLayout(new Span("Deleting tool: "), UIUtils.createBoldText(selectedTool.getName())),
						confirmInputField
				);

				dialog.getConfirmButton().addClickListener(e -> {
					if (ToolFacade.getInstance().remove(selectedTool)) {
						dataProvider.getItems().remove(selectedTool);
						dataProvider.refreshAll();
						closeDetails();
						UIUtils.showNotification("Tool deleted successfully", UIUtils.NotificationType.SUCCESS);
					} else {
						UIUtils.showNotification("Tool delete failed", UIUtils.NotificationType.ERROR);
					}
					dialog.close();
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
		dialog.setHeader(UIUtils.createH4Label("Copy Tool Parameters"));
		dialog.getCancelButton().addClickListener(e -> dialog.close());
		dialog.setConfirmButton(UIUtils.createButton("Copy", ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));

		toolCopyForm = new ToolCopyForm();
		if (!toolCopyForm.setOriginalTool(selectedTools.get(0))) {
			System.out.println("ORIGINAL TOOL NULL");
			return;
		}
		dialog.setContent(toolCopyForm);
		dialog.getConfirmButton().addClickListener(e -> {
			Tool toolCopy = toolCopyForm.getToolCopy();
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

	ListDataProvider<Tool> getDataProvider() {
		return this.dataProvider;
	}
}