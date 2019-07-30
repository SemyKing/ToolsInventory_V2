package com.gmail.grigorij.ui.views.navigation.inventory;

import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.inventory.InventoryHierarchyType;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.backend.entities.inventory.ToolStatus;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.ConfirmDialog;
import com.gmail.grigorij.ui.utils.components.CustomBadge;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerFooter;
import com.gmail.grigorij.ui.utils.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.utils.components.frames.SplitViewFrame;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.gmail.grigorij.ui.utils.css.size.Top;
import com.gmail.grigorij.ui.utils.forms.readonly.ReadOnlyToolForm;
import com.gmail.grigorij.ui.views.MenuLayout;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;

import java.util.ArrayList;
import java.util.List;


@PageTitle("Inventory")
public class Inventory extends SplitViewFrame {

	private static final String CLASS_NAME = "inventory";

	private ReadOnlyToolForm toolForm = new ReadOnlyToolForm();

	private TreeGrid<InventoryEntity> grid;
	private TreeDataProvider<InventoryEntity> dataProvider;

	private DetailsDrawer detailsDrawer;

	private final MenuLayout menuLayout;

	private Button detailsToolButton;
	private Button takeToolButton;
	private Button reserveToolButton;
	private Button reportToolButton;

	public Inventory(MenuLayout menuLayout) {
		this.menuLayout = menuLayout;

		setViewContent(createContent());
		setViewDetails(createDetailsDrawer());
	}

	private Component createContent() {
		FlexBoxLayout wrapper = new FlexBoxLayout();
		wrapper.setClassName(CLASS_NAME + "__wrapper");
		wrapper.setFlexDirection(FlexDirection.COLUMN);

		//HEADER
		FlexBoxLayout header = new FlexBoxLayout();
		header.setClassName(CLASS_NAME + "__header");
		header.setWidthFull();
		header.setFlexDirection(FlexDirection.ROW);
		header.setMargin(Top.S);

		TextField searchField = new TextField();
		searchField.setWidth("100%");
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Tools");

		header.add(searchField);
		header.setComponentMargin(searchField, Right.S);

		FlexBoxLayout buttons = new FlexBoxLayout();
		buttons.setClassName(CLASS_NAME + "__header-buttons");

		Button showMyToolsButton = UIUtils.createButton("My Tools", VaadinIcon.TOOLS, ButtonVariant.LUMO_CONTRAST);
		showMyToolsButton.addClickListener(e -> constructMyToolsDialog());

		buttons.add(showMyToolsButton);

		detailsToolButton = UIUtils.createButton("Details", VaadinIcon.INFO_CIRCLE, ButtonVariant.LUMO_CONTRAST);
		detailsToolButton.setEnabled(false);
		detailsToolButton.addClickListener(e -> {
			if (e != null) {
				InventoryEntity tool = grid.asSingleSelect().getValue();
				if (tool != null) {
					showDetails(tool);
				}
			}
		});

		buttons.add(detailsToolButton);
		buttons.setComponentMargin(detailsToolButton, Left.S);

		takeToolButton = UIUtils.createButton("Take", VaadinIcon.HAND, ButtonVariant.LUMO_CONTRAST);
		takeToolButton.addClickListener(e -> {
			if (e != null) {
				InventoryEntity tool = grid.asSingleSelect().getValue();
				if (tool != null) {
					if (tool.getUser() == null) {
						takeTool(tool);
					} else {
						if (tool.getUser().equals(AuthenticationService.getCurrentSessionUser())) {
							UIUtils.showNotification("You already have this inventory", UIUtils.NotificationType.INFO);
						} else {
							takeTool(tool);
						}
					}
				}
			}
		});
		takeToolButton.setEnabled(false);
		buttons.add(takeToolButton);
		buttons.setComponentMargin(takeToolButton, Left.S);

		reserveToolButton = UIUtils.createButton("Reserve", VaadinIcon.CALENDAR_CLOCK, ButtonVariant.LUMO_CONTRAST);
		reserveToolButton.addClickListener(e -> {
			if (e != null) {
				InventoryEntity tool = grid.asSingleSelect().getValue();
				if (tool != null) {

					if (tool.getReservedByUser() == null) {
						reserveTool(tool);
					} else {
						if (tool.getReservedByUser().equals(AuthenticationService.getCurrentSessionUser())) {
							UIUtils.showNotification("You have already reserved this inventory", UIUtils.NotificationType.INFO);
						} else {
							reserveTool(tool);
						}
					}
				}
			}
		});
		reserveToolButton.setEnabled(false);
		buttons.add(reserveToolButton);
		buttons.setComponentMargin(reserveToolButton, Left.S);

		reportToolButton = UIUtils.createButton("Report", VaadinIcon.EXCLAMATION, ButtonVariant.LUMO_ERROR);
		reportToolButton.addClickListener(e -> {
			if (e != null) {
				if (grid.asSingleSelect().getValue() != null) {
					reportTool(grid.asSingleSelect().getValue());
				}
			}
		});
		reportToolButton.setEnabled(false);
		buttons.add(reportToolButton);
		buttons.setComponentMargin(reportToolButton, Left.S);

		header.add(buttons);

		wrapper.add(header);



		FlexBoxLayout content = new FlexBoxLayout();
		content.setClassName(CLASS_NAME + "__content");
		content.setSizeFull();
		content.setAlignItems(FlexComponent.Alignment.CENTER);
		content.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

		content.add(createGrid());

		wrapper.add(content);
		return wrapper;
	}

	private Component createGrid() {
		User currentUser = AuthenticationService.getCurrentSessionUser();

		if (currentUser == null) {
			UIUtils.showNotification("Current user is NULL", UIUtils.NotificationType.ERROR);
			AuthenticationService.signOut();
		} else {
			dataProvider = new TreeDataProvider<>(InventoryFacade.getInstance().getTreeDataInCompany(currentUser.getCompany().getId()));
		}

		if (dataProvider == null) {
			UIUtils.showNotification("No tools found in your company", UIUtils.NotificationType.INFO);
			return new Span("No tools found in your company");
		} else {
			if (dataProvider.getTreeData().getRootItems().size() <= 0) {
				UIUtils.showNotification("No tools found in your company", UIUtils.NotificationType.INFO);
				return new Span("No tools found in your company");
			}

			grid = new TreeGrid<>();
			grid.setSizeFull();

			grid.asSingleSelect().addValueChangeListener(e -> {

				InventoryEntity ie = grid.asSingleSelect().getValue();

				if (ie != null) {
					if (ie.getInventoryHierarchyType().equals(InventoryHierarchyType.CATEGORY)) {
						if (grid.isExpanded(ie)) {
							grid.collapse(ie);
						} else {
							grid.expand(ie);
						}
						grid.select(null);
					} else {
//						showDetails(ie);
						handleButtonsAvailability(ie);
					}
				} else {
					detailsDrawer.hide();
					handleButtonsAvailability(null);
				}
			});

			grid.setDataProvider(dataProvider);

			ComponentRenderer<FlexBoxLayout, InventoryEntity> toolStatusRenderer = new ComponentRenderer<>(
					tool -> {
						FlexBoxLayout layout = new FlexBoxLayout();
						ToolStatus status = tool.getUsageStatus();
						if (status != null) {
							layout = new CustomBadge(status.getStringValue(), status.getColor(), status.getIcon());
						}
						return layout;
					});
			grid.addColumn(toolStatusRenderer)
					.setHeader("Status")
					.setWidth(UIUtils.COLUMN_WIDTH_S)
					.setFlexGrow(0);

			grid.addHierarchyColumn(InventoryEntity::getName).setHeader("Tools")
					.setWidth(UIUtils.COLUMN_WIDTH_XXL);

//			for (InventoryEntity rootItem : dataProvider.getTreeData().getRootItems()) {
//				expandAll(rootItem);
//			}

			return grid;

		}
	}




//	private int counter = 0;
//	/*
//	RECURSIVE METHOD!
//	 */
//	private void expandAll(InventoryEntity gridItem) {
//
////		System.out.println("expandAll " + counter);
//		counter++;
//
//		if (gridItem == null) {
//			return;
//		}
//
//		if (counter >= 10000) {
//			return;
//		}
//
//		grid.expand(gridItem);
//
//		if (gridItem.getChildren().size() > 0) {
//			for (InventoryEntity item : gridItem.getChildren()) {
//				expandAll(item);
//			}
//		}
//	}

	private List<InventoryEntity> mySelectedTools;

	private void constructMyToolsDialog() {

		List<InventoryEntity> myTools = new ArrayList<>();
		myTools.addAll(AuthenticationService.getCurrentSessionUser().getToolsInUse());
		myTools.addAll(AuthenticationService.getCurrentSessionUser().getToolsReserved());

		if (myTools.size() <= 0) {
			UIUtils.showNotification("You have not taken / reserved any tools", UIUtils.NotificationType.INFO);
			return;
		}

		Grid<InventoryEntity> myToolsGrid = new Grid<>();
		myToolsGrid.setMinWidth("400px");

		ListDataProvider<InventoryEntity> myToolsDataProvider = DataProvider.ofCollection(myTools);
		myToolsGrid.setDataProvider(myToolsDataProvider);

		myToolsGrid.addColumn(InventoryEntity::getName)
				.setHeader("Tool Name")
				.setAutoWidth(true);

		myToolsGrid.addColumn(tool -> (tool.getUsageStatus() == null) ? "" : tool.getUsageStatus().getStringValue())
				.setHeader("Status")
				.setAutoWidth(true);

		myToolsGrid.setSelectionMode(Grid.SelectionMode.MULTI);

		myToolsGrid.addSelectionListener(event -> {
			mySelectedTools = new ArrayList<>(event.getAllSelectedItems());
		});


		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label("My Tools"));

		dialog.setContent(myToolsGrid);
		dialog.getContent().removePadding();
		dialog.getContent().setPadding(Horizontal.XS);

		dialog.getCancelButton().addClickListener(closeEvent -> {
			if (mySelectedTools != null) {
				mySelectedTools.clear();
			}
			dialog.close();
		});

		dialog.getConfirmButton().setText("Return");
		dialog.getConfirmButton().addClickListener(returnToolEvent -> {
			System.out.println("SELECTED: " + mySelectedTools.size() + " TOOLS");

			if (mySelectedTools.size() <= 0) {
				UIUtils.showNotification("Select Tools to return", UIUtils.NotificationType.INFO);
			} else {
				if (mySelectedTools.size() == 1) {
					System.out.println("1 tool to return");


				} else {
					System.out.println("multiple tool to return");


				}
			}
		});

		dialog.open();

	}





	private DetailsDrawer createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		detailsDrawer.getElement().setAttribute(ProjectConstants.FORM_LAYOUT_LARGE_ATTR, true);
		detailsDrawer.setContent(toolForm);
		detailsDrawer.setContentPadding(Left.M, Right.S);

		// Header
		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Tool Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());

		detailsDrawer.setHeader(detailsDrawerHeader);
		detailsDrawer.getHeader().setFlexDirection(FlexDirection.COLUMN);

		// Footer
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.removeButton(detailsDrawerFooter.getSave());
		detailsDrawerFooter.getCancel().addClickListener(e -> closeDetails());
		detailsDrawer.setFooter(detailsDrawerFooter);

		return detailsDrawer;
	}

	private void showDetails(InventoryEntity tool) {
		if (tool != null) {
			if (tool.getInventoryHierarchyType() != null) {
				if (tool.getInventoryHierarchyType().equals(InventoryHierarchyType.TOOL)) {

					toolForm.setTool(tool);
					detailsDrawer.show();

					UIUtils.updateFormSize(toolForm);
				}
			}
		}
	}

	private void refreshDetails(InventoryEntity tool) {
		if (detailsDrawer.isOpen()) {
			detailsDrawer.hide();
			showDetails(tool);
		}
		handleButtonsAvailability(tool);
		dataProvider.refreshAll();
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.select(null);
	}


	private void handleButtonsAvailability(InventoryEntity tool) {

		takeToolButton.setEnabled(false);
		reserveToolButton.setEnabled(false);
		reportToolButton.setEnabled(false);
		detailsToolButton.setEnabled(false);

		if (tool == null) {
			return;
		}
		if (tool.getInventoryHierarchyType().equals(InventoryHierarchyType.CATEGORY)) {
			return;
		}

		reportToolButton.setEnabled(true);
		detailsToolButton.setEnabled(true);

		if (tool.getUsageStatus().equals(ToolStatus.LOST)) {
			return;
		}
		if (tool.getUsageStatus().equals(ToolStatus.BROKEN)) {
			return;
		}
		if (tool.getUsageStatus().equals(ToolStatus.RESERVED)) {
			return;
		}

		if (tool.getUsageStatus().equals(ToolStatus.IN_USE)) {
			if (!tool.getUser().equals(AuthenticationService.getCurrentSessionUser())) {
				reserveToolButton.setEnabled(true);
			}
		}

		if (tool.getUsageStatus().equals(ToolStatus.FREE)) {
			takeToolButton.setEnabled(true);
		}
	}

	private void takeTool(InventoryEntity t) {
		System.out.println("takeTool: " + t.getName());

		InventoryEntity tool = getToolFromDB(t, true);

		if (tool == null) {
			return;
		}

		// TAKE TOOL IF IT IS FREE
		if (tool.getUsageStatus().equals(ToolStatus.FREE)) {
			tool.setUser(AuthenticationService.getCurrentSessionUser());
			tool.setUsageStatus(ToolStatus.IN_USE);

			if (InventoryFacade.getInstance().update(tool)) {

				AuthenticationService.getCurrentSessionUser().addToolInUse(tool);

				if (!UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser())) {
					System.err.println("User UPDATE Error");
				}

				UIUtils.showNotification("Tool taken successfully", UIUtils.NotificationType.SUCCESS);
			} else {
				UIUtils.showNotification("Tool take failed", UIUtils.NotificationType.ERROR);
			}

		// TOOL NOT FREE
		} else {
			if (tool.getUsageStatus().equals(ToolStatus.RESERVED)) {
				UIUtils.showNotification("Tool is currently in use and was reserved by another user", UIUtils.NotificationType.INFO);
				refreshDetails(tool);
				return;
			}
			if (tool.getUsageStatus().equals(ToolStatus.IN_USE)) {

				ConfirmDialog confirmDialog = new ConfirmDialog("Tool was taken by another user. Would you like to reserve it?");
				confirmDialog.closeOnCancel();
				confirmDialog.getConfirmButton().addClickListener(e -> {

					if (tool.getUsageStatus().equals(ToolStatus.RESERVED)) {
						UIUtils.showNotification("Tool was reserved by another user", UIUtils.NotificationType.INFO);
					} else {
						tool.setReservedByUser(AuthenticationService.getCurrentSessionUser());
						tool.setUsageStatus(ToolStatus.RESERVED);

						if (InventoryFacade.getInstance().update(tool)) {

							AuthenticationService.getCurrentSessionUser().addToolReserved(tool);

							if (!UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser())) {
								System.err.println("User UPDATE Error");
							}

							UIUtils.showNotification("Tool reserved successfully", UIUtils.NotificationType.SUCCESS);
						} else {
							UIUtils.showNotification("Tool reserve failed", UIUtils.NotificationType.ERROR);
						}
					}
					refreshDetails(tool);
					confirmDialog.close();
				});
				confirmDialog.open();
			}
		}

		refreshDetails(tool);
	}

	private void reserveTool(InventoryEntity t) {
		System.out.println("reserveTool: " + t.getName());

		InventoryEntity tool = getToolFromDB(t, true);

		if (tool == null) {
			return;
		}

		// ALREADY RESERVED
		if (tool.getUsageStatus().equals(ToolStatus.RESERVED)) {
			UIUtils.showNotification("Tool was reserved by another user", UIUtils.NotificationType.INFO);
			refreshDetails(tool);
			return;

		// FREE OR IN USE
		} else {
			if (tool.getUsageStatus().equals(ToolStatus.FREE)) {
				tool.setUser(AuthenticationService.getCurrentSessionUser());
				tool.setUsageStatus(ToolStatus.IN_USE);

				if (InventoryFacade.getInstance().update(tool)) {

					AuthenticationService.getCurrentSessionUser().addToolInUse(tool);

					if (!UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser())) {
						System.err.println("User UPDATE Error");
					}

					UIUtils.showNotification("Tool taken successfully (it was " + ToolStatus.FREE.getStringValue() +")", UIUtils.NotificationType.SUCCESS);
				} else {
					UIUtils.showNotification("Tool take failed", UIUtils.NotificationType.ERROR);
				}
				return;
			}

			if (tool.getUsageStatus().equals(ToolStatus.IN_USE)) {
				tool.setReservedByUser(AuthenticationService.getCurrentSessionUser());
				tool.setUsageStatus(ToolStatus.RESERVED);

				if (InventoryFacade.getInstance().update(tool)) {

					AuthenticationService.getCurrentSessionUser().addToolReserved(tool);

					if (!UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser())) {
						System.err.println("User UPDATE Error");
					}

					UIUtils.showNotification("Tool reserved successfully", UIUtils.NotificationType.SUCCESS);
				} else {
					UIUtils.showNotification("Tool reserve failed", UIUtils.NotificationType.ERROR);
				}
			}
		}

		refreshDetails(tool);
	}

	private void reportTool(InventoryEntity t) {
		System.out.println("Reporting Tool: " + t.getName());

		InventoryEntity tool = getToolFromDB(t, false);


	}


	private InventoryEntity getToolFromDB(InventoryEntity t, boolean checkStatus) {

		//GET TOOL FROM DATABASE FOR LATEST STATUS
		InventoryEntity tool = InventoryFacade.getInstance().getToolById(t.getId());

		if (tool == null) {
			UIUtils.showNotification("Problem occurred retrieving tool from Database", UIUtils.NotificationType.ERROR);
			return null;
		}

		if (checkStatus) {
			if (tool.getUsageStatus().equals(ToolStatus.BROKEN)) {
				UIUtils.showNotification("Tool was reported " + ToolStatus.BROKEN.getStringValue() + ", operation cancelled", UIUtils.NotificationType.INFO);
				return null;
			}

			if (tool.getUsageStatus().equals(ToolStatus.LOST)) {
				UIUtils.showNotification("Tool was reported " + ToolStatus.LOST.getStringValue() + ", operation cancelled", UIUtils.NotificationType.INFO);
				return null;
			}
		}

		return tool;
	}
}
