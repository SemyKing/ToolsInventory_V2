package com.gmail.grigorij.ui.views.navigation.inventory;

import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.MessageFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.backend.entities.inventory.InventoryHierarchyType;
import com.gmail.grigorij.backend.entities.inventory.ToolStatus;
import com.gmail.grigorij.backend.entities.message.Message;
import com.gmail.grigorij.backend.entities.transaction.OperationTarget;
import com.gmail.grigorij.backend.entities.transaction.OperationType;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.camera.CameraView;
import com.gmail.grigorij.ui.utils.components.ConfirmDialog;
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
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.Broadcaster;
import com.gmail.grigorij.utils.OperationStatus;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
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

	private Button takeToolButton;
	private Button reserveToolButton;
	private Button reportToolButton;

	public Inventory() {
		setViewContent(createContent());
		setViewDetails(createDetailsDrawer());
	}

	private FlexBoxLayout createContent() {
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
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Tools");

		header.add(searchField);
		header.setComponentMargin(searchField, Right.S);
		header.setFlexGrow(1, searchField);

		FlexBoxLayout buttons = new FlexBoxLayout();
		buttons.setClassName(CLASS_NAME + "__header-buttons");

		Button showMyToolsButton = UIUtils.createButton("My Tools", VaadinIcon.TOOLS, ButtonVariant.LUMO_CONTRAST);
		showMyToolsButton.addClickListener(e -> constructMyToolsDialog());

		header.add(showMyToolsButton);

		Button scanToolButton = UIUtils.createButton("Scan", VaadinIcon.QRCODE, ButtonVariant.LUMO_CONTRAST);
		scanToolButton.addClickListener(e -> constructScanToolDialog());

		header.add(scanToolButton);
		header.setComponentMargin(scanToolButton, Left.S);

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

	private boolean cameraActive;

	private void constructScanToolDialog() {
		CustomDialog dialog = new CustomDialog();
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);

		dialog.setHeader(UIUtils.createH3Label("Scan Code"));

		CameraView cameraView = new CameraView();
		cameraView.addClickListener(imageClickEvent -> {
			if (cameraActive) {
				cameraView.takePicture();
			} else {
				cameraView.showPreview();
				cameraActive = true;
			}
		});

		dialog.setContent(cameraView);

		dialog.setCancelButton(null);

		dialog.getConfirmButton().setText("Close");
		dialog.getConfirmButton().addClickListener(e -> {
			cameraView.stop();
			dialog.close();
		});

		dialog.open();

		UIUtils.showNotification("Click on Image to take a picture", UIUtils.NotificationType.INFO);

		cameraView.showPreview();
		cameraActive = true;


		cameraView.onFinished(new OperationStatus() {
			@Override
			public void onSuccess(String msg, UIUtils.NotificationType type) {
				if (UI.getCurrent() != null) {
					UI.getCurrent().access(() -> {
						UIUtils.showNotification("Code scanned, searching for tool...", type);

						cameraView.stop();
						cameraActive = false;

						UI.getCurrent().push();

						InventoryEntity tool = getToolFromDataBaseByCode(msg);
						if (tool == null) {
							UIUtils.showNotification("Tool not found", UIUtils.NotificationType.INFO);
						} else {
							UIUtils.showNotification("Tool found", UIUtils.NotificationType.SUCCESS);

							handleButtonsAvailability(tool);
							showDetails(tool);
						}

						dialog.close();
						UI.getCurrent().push();
					});
				}
			}

			@Override
			public void onFail(String msg, UIUtils.NotificationType type) {
				if (UI.getCurrent() != null) {
					UI.getCurrent().access(() -> {
						UIUtils.showNotification(msg, UIUtils.NotificationType.INFO, 2000);
						UI.getCurrent().push();
					});
				}
			}
		});
	}


	private Component createGrid() {
		dataProvider = new TreeDataProvider<>(InventoryFacade.getInstance().getTreeDataInCompany(AuthenticationService.getCurrentSessionUser().getCompany().getId()));

		if (dataProvider.getTreeData().getRootItems().size() <= 0) {
			UIUtils.showNotification("No tools found in your company", UIUtils.NotificationType.INFO);
			return new Span("No tools found in your company");
		} else {

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
						showDetails(ie);
						handleButtonsAvailability(ie);
					}
				} else {
					detailsDrawer.hide();
					handleButtonsAvailability(null);
				}
			});

			grid.setDataProvider(dataProvider);

			grid.addColumn(ie -> (ie.getInventoryHierarchyType().equals(InventoryHierarchyType.TOOL) ? ie.getUsageStatus().getStringValue() : ""))
					.setHeader("Status")
					.setWidth(UIUtils.COLUMN_WIDTH_S)
					.setFlexGrow(0);
//			ComponentRenderer<FlexBoxLayout, InventoryEntity> toolStatusRenderer = new ComponentRenderer<>(
//					tool -> {
//						FlexBoxLayout layout = new FlexBoxLayout();
//						ToolStatus status = tool.getUsageStatus();
//						if (status != null) {
//							layout = new CustomBadge(status.getStringValue(), status.getColor(), status.getIcon());
//						}
//						return layout;
//					});
//			grid.addColumn(toolStatusRenderer)
//					.setHeader("Status")
//					.setWidth(UIUtils.COLUMN_WIDTH_S)
//					.setFlexGrow(0);

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

		dialog.getConfirmButton().setText("Return Selected");
		dialog.getConfirmButton().addClickListener(returnToolEvent -> {

			if (mySelectedTools == null) {
				UIUtils.showNotification("Select Tools to return", UIUtils.NotificationType.INFO);
				return;
			}

			if (mySelectedTools.size() <= 0) {
				UIUtils.showNotification("Select Tools to return", UIUtils.NotificationType.INFO);
			} else {

				boolean toolError = false;

				for (InventoryEntity t : mySelectedTools) {

					InventoryEntity tool = getToolFromDataBase(t, false);

					if (tool == null) {
						return;
					}

					if (tool.getReservedByUser() != null) {
						tool.setUsageStatus(ToolStatus.RESERVED);


						Message message = new Message();
						message.setMessageText("The tool you have reserved is " + ToolStatus.FREE.getStringValue());
						message.setRecipient(tool.getReservedByUser());
						message.setTool(tool);

						MessageFacade.getInstance().insert(message);

						Broadcaster.broadcastToUser(tool.getReservedByUser().getId(), "The tool you have reserved is " + ToolStatus.FREE.getStringValue() +", check your Messages");
					} else {
						tool.setUsageStatus(ToolStatus.FREE);
					}

					if (!InventoryFacade.getInstance().update(tool)) {
						toolError = true;
					} else {
						AuthenticationService.getCurrentSessionUser().removeToolInUse(tool);

						UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser());


						Transaction tr = new Transaction();
						tr.setTransactionTarget(OperationTarget.TOOL_STATUS);
						tr.setTransactionOperation(OperationType.CHANGE);
						tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
						tr.setInventoryEntity(tool);
						tr.setAdditionalInfo("User returned tool, new status:" + tool.getUsageStatus().getStringValue());

						TransactionFacade.getInstance().insert(tr);
					}
				}

				if (mySelectedTools.size() == 1) {
					if (!toolError) {
						UIUtils.showNotification("Tool returned", UIUtils.NotificationType.SUCCESS);
					}

				} else {
					if (!toolError) {
						UIUtils.showNotification("Tools returned", UIUtils.NotificationType.SUCCESS);
					}
				}

				dialog.close();
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
		detailsDrawerFooter.removeButton(detailsDrawerFooter.getCancel());

		detailsDrawerFooter.getCancel().addClickListener(e -> closeDetails());
		detailsDrawerFooter.getContent().add(createDetailsFooter());
		detailsDrawer.setFooter(detailsDrawerFooter);

		return detailsDrawer;
	}

	private FlexBoxLayout createDetailsFooter() {
		FlexBoxLayout footer = new FlexBoxLayout();
		footer.setClassName(CLASS_NAME + "__footer");
		footer.setWidthFull();
		footer.setFlexDirection(FlexDirection.ROW);
		footer.setMargin(Horizontal.S);


		reportToolButton = UIUtils.createButton("Report", VaadinIcon.EXCLAMATION, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
		reportToolButton.addClickListener(e -> {
			if (grid.asSingleSelect().getValue() != null) {
				reportTool(grid.asSingleSelect().getValue());
			}
		});
		reportToolButton.setEnabled(false);
		footer.add(reportToolButton);
		footer.setFlexGrow(1, reportToolButton);


		reserveToolButton = UIUtils.createButton("Reserve", VaadinIcon.CALENDAR_CLOCK, ButtonVariant.LUMO_CONTRAST);
		reserveToolButton.addClickListener(e -> {
			InventoryEntity tool = grid.asSingleSelect().getValue();
			if (tool != null) {
				reserveTool(tool);
			}
		});
		reserveToolButton.setEnabled(false);
		footer.add(reserveToolButton);
		footer.setComponentMargin(reserveToolButton, Left.S);
		footer.setFlexGrow(1, reserveToolButton);


		takeToolButton = UIUtils.createButton("Take", VaadinIcon.HAND, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		takeToolButton.addClickListener(e -> {
			InventoryEntity tool = grid.asSingleSelect().getValue();
			if (tool != null) {
				takeTool(tool);
			}
		});
		takeToolButton.setEnabled(false);
		footer.add(takeToolButton);
		footer.setComponentMargin(takeToolButton, Left.S);
		footer.setFlexGrow(1, takeToolButton);


		Button closeDetailsButton = UIUtils.createButton("Close",  ButtonVariant.LUMO_PRIMARY);
		closeDetailsButton.addClickListener(e -> {
			closeDetails();
		});
		footer.add(closeDetailsButton);
		footer.setComponentMargin(closeDetailsButton, Left.S);
		footer.setFlexGrow(1, closeDetailsButton);

		return footer;
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
			handleButtonsAvailability(tool);
		}

		dataProvider.refreshAll();
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.select(null);
	}


	private void handleButtonsAvailability(InventoryEntity tool) {
		takeToolButton.setEnabled(false);
		reserveToolButton.setEnabled(false);

		if (tool == null) {
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

		//'tool' is same object as 't', only with latest information from database
		InventoryEntity tool = getToolFromDataBase(t, true);

		if (tool == null) {
			return;
		}

		// TAKE TOOL IF IT IS FREE
		if (tool.getUsageStatus().equals(ToolStatus.FREE)) {
			t.setUser(AuthenticationService.getCurrentSessionUser());
			t.setUsageStatus(ToolStatus.IN_USE);

			if (InventoryFacade.getInstance().update(t)) {

				AuthenticationService.getCurrentSessionUser().addToolInUse(t);

				UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser());

				Transaction tr = new Transaction();
				tr.setTransactionTarget(OperationTarget.TOOL_STATUS);
				tr.setTransactionOperation(OperationType.CHANGE);
				tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
				tr.setInventoryEntity(t);
				tr.setAdditionalInfo("User took the tool.\nStatus change from: " + ToolStatus.FREE.getStringValue() + " to: " + ToolStatus.IN_USE.getStringValue());

				TransactionFacade.getInstance().insert(tr);

				UIUtils.showNotification("Tool taken successfully", UIUtils.NotificationType.SUCCESS);
			} else {
				UIUtils.showNotification("Tool take failed", UIUtils.NotificationType.ERROR);
			}

		// TOOL NOT FREE
		} else {
			if (tool.getUsageStatus().equals(ToolStatus.RESERVED)) {
				UIUtils.showNotification("Tool is reserved", UIUtils.NotificationType.INFO);
				refreshDetails(t);
				return;
			}
			if (tool.getUsageStatus().equals(ToolStatus.IN_USE_AND_RESERVED)) {
				UIUtils.showNotification("Tool is currently in use and was reserved", UIUtils.NotificationType.INFO);
				refreshDetails(t);
				return;
			}


			if (tool.getUsageStatus().equals(ToolStatus.IN_USE)) {

				ConfirmDialog confirmDialog = new ConfirmDialog("Tool is currently in use. Would you like to reserve it?");
				confirmDialog.closeOnCancel();
				confirmDialog.getConfirmButton().addClickListener(e -> {
					t.setReservedByUser(AuthenticationService.getCurrentSessionUser());
					t.setUsageStatus(ToolStatus.IN_USE_AND_RESERVED);

					if (InventoryFacade.getInstance().update(t)) {

						AuthenticationService.getCurrentSessionUser().addToolReserved(t);

						UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser());

						Transaction tr = new Transaction();
						tr.setTransactionTarget(OperationTarget.TOOL_STATUS);
						tr.setTransactionOperation(OperationType.CHANGE);
						tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
						tr.setInventoryEntity(t);
						tr.setAdditionalInfo("Status change from: " + ToolStatus.IN_USE.getStringValue() + " to: " + ToolStatus.IN_USE_AND_RESERVED.getStringValue());

						TransactionFacade.getInstance().insert(tr);

						UIUtils.showNotification("Tool reserved successfully", UIUtils.NotificationType.SUCCESS);
					} else {
						UIUtils.showNotification("Tool reserve failed", UIUtils.NotificationType.ERROR);
					}
					refreshDetails(t);
					confirmDialog.close();
				});
				confirmDialog.open();
			}
		}

		refreshDetails(t);
	}

	private void reserveTool(InventoryEntity t) {
		System.out.println("reserveTool: " + t.getName());

		InventoryEntity tool = getToolFromDataBase(t, true);

		if (tool == null) {
			return;
		}

		if (tool.getUsageStatus().equals(ToolStatus.RESERVED)) {
			UIUtils.showNotification("Tool is reserved by another user", UIUtils.NotificationType.INFO);
			refreshDetails(t);
			return;
		}
		if (tool.getUsageStatus().equals(ToolStatus.IN_USE_AND_RESERVED)) {
			UIUtils.showNotification("Tool is currently in use and is reserved", UIUtils.NotificationType.INFO);
			refreshDetails(t);
			return;
		}

		// FREE OR IN USE
		if (tool.getUsageStatus().equals(ToolStatus.FREE)) {
			t.setUser(AuthenticationService.getCurrentSessionUser());
			t.setUsageStatus(ToolStatus.IN_USE);

			if (InventoryFacade.getInstance().update(t)) {

				AuthenticationService.getCurrentSessionUser().addToolInUse(t);

				UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser());

				Transaction tr = new Transaction();
				tr.setTransactionTarget(OperationTarget.TOOL_STATUS);
				tr.setTransactionOperation(OperationType.CHANGE);
				tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
				tr.setInventoryEntity(t);
				tr.setAdditionalInfo("User took the tool.\nStatus change from: " + ToolStatus.FREE.getStringValue() + " to: " + ToolStatus.IN_USE.getStringValue());

				TransactionFacade.getInstance().insert(tr);

				UIUtils.showNotification("Tool taken successfully (it was " + ToolStatus.FREE.getStringValue() +")", UIUtils.NotificationType.SUCCESS);
			} else {
				UIUtils.showNotification("Tool take failed", UIUtils.NotificationType.ERROR);
			}
			return;
		}

		if (tool.getUsageStatus().equals(ToolStatus.IN_USE)) {
			t.setReservedByUser(AuthenticationService.getCurrentSessionUser());
			t.setUsageStatus(ToolStatus.IN_USE_AND_RESERVED);

			if (InventoryFacade.getInstance().update(t)) {

				AuthenticationService.getCurrentSessionUser().addToolReserved(t);

				UserFacade.getInstance().update(AuthenticationService.getCurrentSessionUser());

				Transaction tr = new Transaction();
				tr.setTransactionTarget(OperationTarget.TOOL_STATUS);
				tr.setTransactionOperation(OperationType.CHANGE);
				tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
				tr.setInventoryEntity(t);
				tr.setAdditionalInfo("User reserved the tool.\nStatus change from: " + ToolStatus.IN_USE.getStringValue() + " to: " + ToolStatus.IN_USE_AND_RESERVED.getStringValue());

				TransactionFacade.getInstance().insert(tr);

				UIUtils.showNotification("Tool reserved successfully", UIUtils.NotificationType.SUCCESS);
			} else {
				UIUtils.showNotification("Tool reserve failed", UIUtils.NotificationType.ERROR);
			}
		}

		refreshDetails(t);
	}

	private void reportTool(InventoryEntity t) {
		System.out.println("Reporting Tool: " + t.getName());

		InventoryEntity tool = getToolFromDataBase(t, false);


	}

	private InventoryEntity getToolFromDataBase(InventoryEntity t, boolean checkStatus) {

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


	//TODO: LIMIT SEARCH IN USERS COMPANY
	private InventoryEntity getToolFromDataBaseByCode(String code) {
		return InventoryFacade.getInstance().getToolByCode(code);
	}
}
