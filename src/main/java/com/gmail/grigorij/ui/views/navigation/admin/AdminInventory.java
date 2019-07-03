package com.gmail.grigorij.ui.views.navigation.admin;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.ToolFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
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
import com.gmail.grigorij.ui.utils.forms.admin.AdminToolBulkForm;
import com.gmail.grigorij.ui.utils.forms.admin.AdminToolCategoryForm;
import com.gmail.grigorij.ui.utils.forms.admin.AdminToolForm;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.ArrayList;
import java.util.List;


public class AdminInventory extends FlexBoxLayout {

	private static final String CLASS_NAME = "admin-inventory";
	final static String TAB_NAME = "Inventory";

	private AdminMain adminMain;
	private AdminToolForm adminToolForm;
	private AdminToolBulkForm bulkAdminToolForm = null;
	private AdminToolCategoryForm adminCategoryForm = new AdminToolCategoryForm();
	private ToolCopyForm toolCopyForm; //used for checkboxes(tool parameters) and numbers of copy

	private Grid<Tool> grid;
	private ListDataProvider<Tool> dataProvider;

	private DetailsDrawer detailsDrawer;
	private DetailsDrawerHeader detailsDrawerHeader;
	private FlexBoxLayout bulkEditHeader = null;
	private DetailsDrawerFooter detailsDrawerFooter;


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

		TextField searchField = new TextField();
		searchField.setWidth("100%");
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Tools");

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
		grid.asSingleSelect().addValueChangeListener(e -> {
			if (grid.asSingleSelect().getValue() != null) {
				showToolDetails(grid.asSingleSelect().getValue(), "Tool Details");
			} else {
				detailsDrawer.hide();
			}
		});

		dataProvider = DataProvider.ofCollection(ToolFacade.getInstance().getAllToolsOnly());
		grid.setDataProvider(dataProvider);

		grid.addColumn(Tool::getId).setHeader("ID")
				.setWidth(UIUtils.COLUMN_WIDTH_XS)
				.setFlexGrow(0);

		ComponentRenderer<Span, Tool> toolCompanyRenderer = new ComponentRenderer<>(
				tool -> {
					Span companyName = new Span("");
					companyName.setWidth("100%");
					if (tool.getCompanyId() >= 0) {
						companyName.setText(CompanyFacade.getInstance().findCompanyById(tool.getCompanyId()).getName());
					}
					return companyName;
				});
		grid.addColumn(toolCompanyRenderer)
				.setHeader("Company")
				.setWidth(UIUtils.COLUMN_WIDTH_M);

		ComponentRenderer<Span, Tool> toolCategoryRenderer = new ComponentRenderer<>(
				tool -> {
					Span categoryName = new Span("");
					categoryName.setWidth("100%");
					if (tool.getParentCategory() != null) {
						categoryName.setText(tool.getParentCategory().getName());
					}
					return categoryName;
				});
		grid.addColumn(toolCategoryRenderer)
				.setHeader("Category")
				.setWidth(UIUtils.COLUMN_WIDTH_M);

		grid.addColumn(Tool::getName).setHeader("Tool")
				.setWidth(UIUtils.COLUMN_WIDTH_L);

		ComponentRenderer<Badge, Tool> toolStatusRenderer = new ComponentRenderer<>(
				tool -> {
					ToolStatus status = tool.getUsageStatus();
					Badge badge;
					if (status == null) {
						badge = new Badge("", null, null);
					} else {
						badge = new Badge(status.getStringValue(), status.getIcon(), status.getColor());
					}
					badge.setWidth("100%");
					return badge;
				});
		grid.addColumn(toolStatusRenderer)
				.setHeader("Status")
				.setWidth(UIUtils.COLUMN_WIDTH_S)
				.setFlexGrow(0);

		ComponentRenderer<Span, Tool> toolUserRenderer = new ComponentRenderer<>(
				tool -> {
					ToolStatus status = tool.getUsageStatus();
					Span username = new Span("");
					username.setWidth("100%");

					if (status != null) {
						if (status.equals(ToolStatus.IN_USE)) {
							if (tool.getInUseByUserId() <= 0) {
								//TODO: ADD TO LOG
//								System.out.println("User id is <= 0 for tool with 'IN USE' status");
//								System.out.println("Tool id: " + tool.getId() + ", name: " + tool.getName());
							} else {
								username.setText(UserFacade.getInstance().getUserById(tool.getInUseByUserId()).getUsername());
							}
						}
					}
					return username;
				});
		grid.addColumn(toolUserRenderer)
				.setHeader("User")
				.setWidth(UIUtils.COLUMN_WIDTH_M)
				.setFlexGrow(0);

		grid.addColumn(new ComponentRenderer<>(tool -> UIUtils.createActiveGridIcon(tool.isDeleted()))).setHeader("Visible")
				.setWidth(UIUtils.COLUMN_WIDTH_XS)
				.setFlexGrow(0);

		add(grid);
	}

	private Button copyToolButton;

	private void createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		detailsDrawer.setContent(adminToolForm);
		detailsDrawer.getElement().setAttribute(ProjectConstants.FORM_LAYOUT_LARGE_ATTR, true);

		// Original Header
		detailsDrawerHeader = new DetailsDrawerHeader("");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());

		copyToolButton = UIUtils.createTertiaryInlineButton(VaadinIcon.COPY);
		copyToolButton.addClickListener(e -> constructToolCopyDialog());
		detailsDrawerHeader.getContainer().add(copyToolButton);
		detailsDrawerHeader.getContainer().setComponentMargin(copyToolButton, Left.AUTO);
		UIUtils.setTooltip("Create a copy of this tool", copyToolButton);

		detailsDrawer.setHeader(detailsDrawerHeader);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.getSave().addClickListener(e -> updateTool());
		detailsDrawerFooter.getCancel().addClickListener(e -> closeDetails());
		detailsDrawerFooter.getDelete().addClickListener(e -> confirmDelete());
		detailsDrawer.setFooter(detailsDrawerFooter);

		adminMain.setDetailsDrawer(detailsDrawer);
	}

	private void showToolDetails(Tool tool, String title) {
		detailsDrawerHeader.setTitle(title);

		detailsDrawerFooter.getDelete().setEnabled( tool != null );
		copyToolButton.setEnabled( tool != null );

		adminToolForm.setTool(tool);
		detailsDrawer.show();

		UIUtils.updateFormSize(adminToolForm);
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.select(null);
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

			if (ToolFacade.getInstance().update(editedCategory)) {
				if (adminCategoryForm.isNew()) {
					dataProvider.getItems().add(editedCategory);
					UIUtils.showNotification("Category created successfully", UIUtils.NotificationType.SUCCESS);
				} else {
					UIUtils.showNotification("Category updated successfully", UIUtils.NotificationType.SUCCESS);
				}
				dataProvider.refreshAll();
			} else {
				UIUtils.showNotification("Category insert/updated failed", UIUtils.NotificationType.ERROR);
			}
		}
	}

	private void updateTool() {
		System.out.println("updateTool()");

		Tool editedTool = adminToolForm.getTool();

		if (editedTool != null) {
			if (editedTool.getParentCategory() != null) {
				if (editedTool.getParentCategory().equals(ToolFacade.getInstance().getRootCategory())) {
					editedTool.setParentCategory(null);
				}
			}

			if (ToolFacade.getInstance().update(editedTool)) {
				if (adminToolForm.isNew()) {
					dataProvider.getItems().add(editedTool);
					dataProvider.refreshAll();
					UIUtils.showNotification("Tool created successfully", UIUtils.NotificationType.SUCCESS);
				} else {
					dataProvider.refreshItem(grid.asSingleSelect().getValue());
					UIUtils.showNotification("Tool updated successfully", UIUtils.NotificationType.SUCCESS);
				}

				grid.select(editedTool);
			} else {
				UIUtils.showNotification("Tool insert/updated failed", UIUtils.NotificationType.ERROR);
			}
		}
	}

	private void confirmDelete() {
		if (detailsDrawer.isOpen()) {

			System.out.println("selectedCompany: " + grid.asSingleSelect().getValue());
			final Tool selectedTool = grid.asSingleSelect().getValue();

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

	private void exportTools() {
		System.out.println("Export Tools...");
	}

	private void importTools() {
		System.out.println("Import Tools...");
	}




	/*
	Tools Bulk Operations
	 */
	private void constructBulkEditHeader() {
		// Header for tools bulk edit operation
		bulkEditHeader = new FlexBoxLayout();
		bulkEditHeader.setFlexDirection(FlexDirection.ROW);
		bulkEditHeader.setAlignItems(Alignment.CENTER);
		bulkEditHeader.add(UIUtils.createH4Label("Tool"));

		nOfCopyLabel = UIUtils.createH4Label("1");
		bulkEditHeader.add(nOfCopyLabel);
		bulkEditHeader.setComponentMargin(nOfCopyLabel, Left.M);

		Label slash = UIUtils.createH4Label("/");
		bulkEditHeader.add(slash);
		bulkEditHeader.setComponentMargin(slash, Horizontal.S);

		nOfCopiesLabel = UIUtils.createH4Label("100");
		bulkEditHeader.add(nOfCopiesLabel);

		Button toolsToLeftButton = UIUtils.createButton(VaadinIcon.ANGLE_LEFT);
		toolsToLeftButton.addClickListener(e -> {
			if (currentBulkEditToolIndex > 0) {
				Tool editedTool = bulkAdminToolForm.getTool();
				if (editedTool != null) {
					bulkEditTools.set(currentBulkEditToolIndex, editedTool);

					--currentBulkEditToolIndex;
					setToolBulkEditDialogContent(bulkEditTools.get(currentBulkEditToolIndex));

					nOfCopyLabel.setText(""+(currentBulkEditToolIndex+1));
				}
			}
		});
		bulkEditHeader.add(toolsToLeftButton);

		Button toolsToRightButton = UIUtils.createButton(VaadinIcon.ANGLE_RIGHT);
		toolsToRightButton.addClickListener(e -> {
			if (currentBulkEditToolIndex < (maxNumberOfBulkEditTools-1)) {
				Tool editedTool = bulkAdminToolForm.getTool();
				if (editedTool != null) {
					bulkEditTools.set(currentBulkEditToolIndex, editedTool);

					++currentBulkEditToolIndex;
					setToolBulkEditDialogContent(bulkEditTools.get(currentBulkEditToolIndex));

					nOfCopyLabel.setText(""+(currentBulkEditToolIndex+1));
				}
			}
		});
		bulkEditHeader.add(toolsToRightButton);
		bulkEditHeader.setComponentMargin(toolsToLeftButton, Left.AUTO);
	}

	private void constructToolCopyDialog() {
		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH4Label("Copy tool parameters"));
		dialog.getCancelButton().addClickListener(e -> dialog.close());
		dialog.setConfirmButton(UIUtils.createButton("Copy", ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));

		toolCopyForm = new ToolCopyForm();
		if (!toolCopyForm.setOriginalTool(grid.asSingleSelect().getValue())) {
			System.out.println("ORIGINAL TOOL NULL");
			return;
		}
		dialog.setContent(toolCopyForm);
		dialog.getConfirmButton().addClickListener(e -> {
			Tool toolCopy = toolCopyForm.getToolCopy();
			if (toolCopy != null) {
				dialog.close();
				initToolBulkEdit(toolCopy, toolCopyForm.getNumberOfCopies());
			}
		});
		dialog.open();
	}

	private Label nOfCopyLabel;
	private Label nOfCopiesLabel;
	private CustomDialog toolBulkEditDialog;
	private List<Tool> bulkEditTools = null;
	private int currentBulkEditToolIndex = -1;
	private int maxNumberOfBulkEditTools = -1;

	private void initToolBulkEdit(Tool tool, int numberOfCopies) {
		if (UI.getCurrent() != null) {
			try {
				UI.getCurrent().access(this::closeDetails);
			} catch (UIDetachedException e) {
				e.printStackTrace();
			}
		}

		if (bulkEditHeader == null) {
			constructBulkEditHeader();
		}

		bulkEditTools = new ArrayList<>();
		for (int i = 0; i < numberOfCopies; i++) {
			Tool newTool = new Tool(tool);
			bulkEditTools.add(newTool);
		}

		currentBulkEditToolIndex = 0;
		maxNumberOfBulkEditTools = numberOfCopies;

		nOfCopiesLabel.setText(""+numberOfCopies);

		toolBulkEditDialog = new CustomDialog();
		toolBulkEditDialog.setCloseOnEsc(false);
		toolBulkEditDialog.setCloseOnOutsideClick(false);

		toolBulkEditDialog.setHeader(bulkEditHeader);

		setToolBulkEditDialogContent(bulkEditTools.get(currentBulkEditToolIndex));

		toolBulkEditDialog.getCancelButton().setText("Cancel");
		toolBulkEditDialog.getCancelButton().addClickListener(e -> {
			confirmBulkEditClose();
		});

		toolBulkEditDialog.setConfirmButton(UIUtils.createButton("Save All", ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));
		toolBulkEditDialog.getConfirmButton().addClickListener(e -> {

			Tool editedTool = bulkAdminToolForm.getTool();
			if (editedTool != null) {
				bulkEditTools.set(currentBulkEditToolIndex, editedTool);

				boolean errorOccur = false;

				for (Tool t : bulkEditTools) {

					if (t.getParentCategory() != null) {
						t.setLevel((t.getParentCategory().getLevel()+1));
					}

					if (!ToolFacade.getInstance().insert(t)) {
						errorOccur = true;
					}
					dataProvider.getItems().add(t);
				}

				if (!errorOccur) {
					UIUtils.showNotification("Tools inserted successfully", UIUtils.NotificationType.SUCCESS);
				} else {
					UIUtils.showNotification("Error(s) inserting tool(s)", UIUtils.NotificationType.ERROR);
				}

				toolBulkEditDialog.close();
				dataProvider.refreshAll();
			}
		});
		toolBulkEditDialog.open();
	}

	private void setToolBulkEditDialogContent(Tool tool) {
		if (bulkAdminToolForm == null) {
			this.bulkAdminToolForm = new AdminToolBulkForm(this);
		}
		bulkAdminToolForm.setTool(tool);
		toolBulkEditDialog.setContent(bulkAdminToolForm);
	}

	private void confirmBulkEditClose() {
		CustomDialog dialog = new CustomDialog();
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);

		dialog.setHeader(UIUtils.createH4Label("Cancel tool copies edit?"));

		Paragraph content = new Paragraph();
		content.add(new Span("This will discard all copies and made changes. Are you sure you want to cancel?"));

		dialog.setContent(content);

		dialog.getCancelButton().setText("No");
		dialog.getCancelButton().addClickListener(e -> dialog.close());
		dialog.setConfirmButton(UIUtils.createButton("Yes", ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));
		dialog.getConfirmButton().addClickListener(e -> {
			dialog.close();
			toolBulkEditDialog.close();
		});
		dialog.open();
	}

	/**
	 * Method used from {@link AdminToolBulkForm} setAllButton action
	 * @param companyId
	 * @param category
	 */
	public void setBulkCompaniesAndCategories(long companyId, Tool category) {
		try {
			for (Tool t : bulkEditTools) {
				t.setCompanyId(companyId);
				t.setParentCategory(category);
			}
			UIUtils.showNotification("Companies and categories set", UIUtils.NotificationType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			UIUtils.showNotification("Error setting companyId and/or parent category", UIUtils.NotificationType.ERROR);
		}
	}
}