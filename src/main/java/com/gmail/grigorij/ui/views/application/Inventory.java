package com.gmail.grigorij.ui.views.application;

import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.MessageFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.enums.MessageType;
import com.gmail.grigorij.backend.enums.inventory.InventoryHierarchyType;
import com.gmail.grigorij.backend.enums.inventory.ToolStatus;
import com.gmail.grigorij.backend.entities.message.Message;
import com.gmail.grigorij.backend.enums.transactions.TransactionTarget;
import com.gmail.grigorij.backend.enums.transactions.TransactionType;
import com.gmail.grigorij.backend.entities.transaction.Transaction;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.camera.CameraView;
import com.gmail.grigorij.ui.components.dialogs.ConfirmDialog;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.components.layouts.SplitViewFrame;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.*;
import com.gmail.grigorij.ui.components.forms.readonly.ReadOnlyToolForm;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.Broadcaster;
import com.gmail.grigorij.utils.OperationStatus;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.PageTitle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@PageTitle("Inventory")
@CssImport("./styles/views/inventory.css")
public class Inventory extends SplitViewFrame {

	private static final String CLASS_NAME = "inventory";

	private ReadOnlyToolForm toolForm = new ReadOnlyToolForm();

	private TreeGrid<InventoryItem> grid;
	private TreeDataProvider<InventoryItem> dataProvider;
	private TreeData<InventoryItem> treeData = new TreeData<>();

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
		searchField.setPlaceholder("Search Tools (not implemented yet)");

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

	private Component createGrid() {

		if (!refreshAllGridItems()) {
			UIUtils.showNotification("No tools found in your company", UIUtils.NotificationType.INFO);
			return new Span("No tools found in your company");
		} else {

			grid = new TreeGrid<>();
			grid.setSizeFull();

			grid.asSingleSelect().addValueChangeListener(e -> {

				InventoryItem ie = grid.asSingleSelect().getValue();

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


			grid.addHierarchyColumn(InventoryItem::getName)
					.setHeader("Tools")
					.setAutoWidth(true);

			grid.addColumn(ie -> (ie.getInventoryHierarchyType().equals(InventoryHierarchyType.TOOL) ? ie.getUsageStatus().getStringValue() : ""))
					.setHeader("Status")
					.setAutoWidth(true);
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

//			for (InventoryEntity rootItem : dataProvider.getTreeData().getRootItems()) {
//				expandAll(rootItem);
//			}

			return grid;

		}
	}

	private boolean refreshAllGridItems() {
		List<InventoryItem> toolsAndCategories = InventoryFacade.getInstance().getAllInCompany(AuthenticationService.getCurrentSessionUser().getCompany().getId());

		if (toolsAndCategories.size() <= 0) {
			return false;
		}

		treeData.clear();

		//List must be sorted -> Parent must be added before child
		toolsAndCategories.sort(Comparator.comparing(InventoryItem::getLevel));

		toolsAndCategories.forEach(item -> {
			treeData.addItem(item.getParentCategory(), item);
		});

		if (dataProvider == null) {
			dataProvider = new TreeDataProvider<>(treeData);
		} else {
			dataProvider.refreshAll();
		}

		return true;
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
		detailsDrawer.setFooter(createDetailsFooter());

		return detailsDrawer;
	}

	private FlexBoxLayout createDetailsFooter() {
		FlexBoxLayout footer = new FlexBoxLayout();
		footer.setClassName(CLASS_NAME + "__footer");
		footer.setWidthFull();
		footer.setFlexDirection(FlexDirection.ROW);
		footer.setPadding(Horizontal.S, Vertical.XS);
		footer.setBackgroundColor(LumoStyles.Color.Contrast._5);

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
			InventoryItem tool = grid.asSingleSelect().getValue();
			if (tool != null) {
				reserveTool(tool);

				refreshDetails();
			}
		});
		reserveToolButton.setEnabled(false);
		footer.add(reserveToolButton);
		footer.setComponentMargin(reserveToolButton, Left.S);
		footer.setFlexGrow(1, reserveToolButton);


		takeToolButton = UIUtils.createButton("Take", VaadinIcon.HAND, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		takeToolButton.addClickListener(e -> {
			InventoryItem tool = grid.asSingleSelect().getValue();
			if (tool != null) {

				takeTool(tool);
				refreshDetails();
			}
		});
		takeToolButton.setEnabled(false);
		footer.add(takeToolButton);
		footer.setComponentMargin(takeToolButton, Left.S);
		footer.setFlexGrow(1, takeToolButton);


		Button closeDetailsButton = UIUtils.createButton("Close", ButtonVariant.LUMO_PRIMARY);
		closeDetailsButton.addClickListener(e -> {
			closeDetails();
		});
		footer.add(closeDetailsButton);
		footer.setComponentMargin(closeDetailsButton, Left.S);
		footer.setFlexGrow(1, closeDetailsButton);

		return footer;
	}

	private void showDetails(InventoryItem tool) {
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

	private void refreshDetails() {
		dataProvider.refreshAll();

		if (detailsDrawer.isOpen()) {
			detailsDrawer.hide();

			InventoryItem tool = grid.asSingleSelect().getValue();

			if (tool != null) {
				if (tool.getInventoryHierarchyType().equals(InventoryHierarchyType.TOOL)) {
					handleButtonsAvailability(tool);

					showDetails(tool);
				}
			}
		}
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.select(null);
	}


	private List<InventoryItem> mySelectedTools;

	private void constructMyToolsDialog() {

		List<InventoryItem> allMyTools = new ArrayList<>();

		allMyTools.addAll(InventoryFacade.getInstance().getAllToolsInUseByUser(AuthenticationService.getCurrentSessionUser().getId()));
		allMyTools.addAll(InventoryFacade.getInstance().getAllToolsReservedByUser(AuthenticationService.getCurrentSessionUser().getId()));

		if (allMyTools.size() <= 0) {
			UIUtils.showNotification("You don't have any tools", UIUtils.NotificationType.INFO);
			return;
		}

		CustomDialog dialog = new CustomDialog();
		dialog.setHeader(UIUtils.createH3Label("My Tools"));

		Grid<InventoryItem> myToolsGrid = new Grid<>();
		myToolsGrid.setMinWidth("400px");

		myToolsGrid.addColumn(InventoryItem::getName)
				.setHeader("Tool Name")
				.setAutoWidth(true);

		myToolsGrid.addColumn(tool -> {
					if (tool.getInUseByUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {
						return ToolStatus.IN_USE.getStringValue();
					}
					if (tool.getReservedByUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {
						return ToolStatus.RESERVED.getStringValue();
					}
					return "";
				})
				.setHeader("Status")
				.setAutoWidth(true);

		myToolsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
		myToolsGrid.setItems(allMyTools);

		myToolsGrid.addSelectionListener(event -> {
			mySelectedTools = new ArrayList<>(event.getAllSelectedItems());
			dialog.getConfirmButton().setEnabled(false);

			if (event.getAllSelectedItems().size() > 0) {
				dialog.getConfirmButton().setEnabled(true);
			}
		});


		dialog.setContent(myToolsGrid);
		dialog.getContent().removePadding();
		dialog.getContent().setPadding(Horizontal.XS);

		dialog.getCancelButton().addClickListener(closeEvent -> {
			if (mySelectedTools != null) {
				mySelectedTools.clear();
			}
			dialog.close();
		});

		dialog.getConfirmButton().setText("Return / Cancel Reservation");
		dialog.getConfirmButton().addClickListener(returnToolEvent -> {

			boolean errorOccurred = false;

			for (InventoryItem tool : mySelectedTools) {
				if (tool == null) {
					System.err.println("USER: " + AuthenticationService.getCurrentSessionUser().getFullName() + " IS TRYING TO RETURN / CANCEL RESERVATION FOR NULL TOOL");
					return;
				}

				ToolStatus originalToolStatus = tool.getUsageStatus();


				// TOOL IN USE BY USER
				if (tool.getInUseByUser() != null) {
					if (tool.getInUseByUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {

						// TOOL IS RESERVED BY OTHER USER
						if (tool.getReservedByUser() != null) {
							tool.setUsageStatus(ToolStatus.RESERVED);

							Message message = new Message();
							message.setMessageType(MessageType.TOOL_FREE);
							message.setMessageHeader("Tool is free and reserved for you");
							message.setMessageText("Tool: " + tool.getName());
							message.setSender("SYSTEM");
							message.setRecipientId(tool.getReservedByUser().getId());
							message.setToolId(tool.getId());

							MessageFacade.getInstance().insert(message);

							Broadcaster.broadcastToUser(tool.getReservedByUser().getId(), "You have new message");
						} else {
							tool.setUsageStatus(ToolStatus.FREE);
						}

						tool.setInUseByUser(null);
					}
				}

				// TOOL IS RESERVED BY USER
				if (tool.getReservedByUser() != null) {
					if (tool.getReservedByUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {
						tool.setUsageStatus(ToolStatus.IN_USE);
						tool.setReservedByUser(null);
					}
				}


				if (!InventoryFacade.getInstance().update(tool)) {
					errorOccurred = true;
					System.err.println("ERROR UPDATING TOOL, ID: " + tool.getId());
				}

				Transaction tr = new Transaction();
				tr.setTransactionTarget(TransactionTarget.TOOL_STATUS);
				tr.setTransactionOperation(TransactionType.EDIT);
				tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
				tr.setInventoryEntity(tool);

				if (originalToolStatus.equals(ToolStatus.IN_USE_AND_RESERVED) || originalToolStatus.equals(ToolStatus.IN_USE)) {
					tr.setAdditionalInfo("User returned tool, new status:  " + tool.getUsageStatus().getStringValue());
				} else {
					tr.setAdditionalInfo("User cancelled tool reservation, new status:  " + tool.getUsageStatus().getStringValue());
				}

				TransactionFacade.getInstance().insert(tr);
			}
			dialog.close();

			refreshAllGridItems();

			if (!errorOccurred) {
				UIUtils.showNotification("Action successful", UIUtils.NotificationType.SUCCESS, 2000);
			}
		});

		dialog.open();
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
						UIUtils.showNotification("Code scanned, searching for tool...", type, 2000);

						cameraView.stop();
						cameraActive = false;

						UI.getCurrent().push();

						InventoryItem tool = getToolFromDataBaseByCode(msg);
						if (tool == null) {
							UIUtils.showNotification("Tool not found", UIUtils.NotificationType.INFO);
						} else {
							UIUtils.showNotification("Tool found", UIUtils.NotificationType.SUCCESS, 3000);

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


	private void handleButtonsAvailability(InventoryItem tool) {
		takeToolButton.setEnabled(false);
		reserveToolButton.setEnabled(false);

		if (tool == null) {
			return;
		}

		if (tool.getUsageStatus().equals(ToolStatus.IN_USE)) {
			if (!tool.getInUseByUser().equals(AuthenticationService.getCurrentSessionUser())) {
				reserveToolButton.setEnabled(true);
			}
		}

		if (tool.getUsageStatus().equals(ToolStatus.FREE)) {
			takeToolButton.setEnabled(true);
		}
	}


	private void takeTool(InventoryItem toolInGrid) {

		//Get tool with latest information from database
		InventoryItem toolInDB = getToolFromDataBase(toolInGrid, true);

		if (toolInDB == null) {
			return;
		}

		// TAKE TOOL IF IT IS FREE
		if (toolInDB.getUsageStatus().equals(ToolStatus.FREE)) {
			toolInDB.setInUseByUser(AuthenticationService.getCurrentSessionUser());
			toolInDB.setUsageStatus(ToolStatus.IN_USE);

			toolInGrid.setInUseByUser(AuthenticationService.getCurrentSessionUser());
			toolInGrid.setUsageStatus(ToolStatus.IN_USE);

			if (InventoryFacade.getInstance().update(toolInDB)) {

				Transaction tr = new Transaction();
				tr.setTransactionTarget(TransactionTarget.TOOL_STATUS);
				tr.setTransactionOperation(TransactionType.EDIT);
				tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
				tr.setInventoryEntity(toolInDB);
				tr.setAdditionalInfo("User took the tool. Status change from:  " + ToolStatus.FREE.getStringValue() + "  to:  " + ToolStatus.IN_USE.getStringValue());

				TransactionFacade.getInstance().insert(tr);

				UIUtils.showNotification("Tool taken successfully", UIUtils.NotificationType.SUCCESS);
			} else {
				UIUtils.showNotification("Tool take failed", UIUtils.NotificationType.ERROR);
			}

		// TOOL NOT FREE
		} else {
			if (toolInDB.getUsageStatus().equals(ToolStatus.RESERVED) || toolInDB.getUsageStatus().equals(ToolStatus.IN_USE_AND_RESERVED)) {
				UIUtils.showNotification("Tool is reserved by another user", UIUtils.NotificationType.INFO);
				return;
			}

			if (toolInDB.getUsageStatus().equals(ToolStatus.IN_USE)) {

				toolInGrid.setInUseByUser(toolInDB.getInUseByUser()); // VISUAL REFRESH

				ConfirmDialog confirmDialog = new ConfirmDialog("Tool is currently in use. Would you like to reserve it?");
				confirmDialog.closeOnCancel();

				confirmDialog.getConfirmButton().addClickListener(e -> {
					toolInDB.setReservedByUser(AuthenticationService.getCurrentSessionUser());
					toolInDB.setUsageStatus(ToolStatus.IN_USE_AND_RESERVED);

					toolInGrid.setReservedByUser(AuthenticationService.getCurrentSessionUser());
					toolInGrid.setUsageStatus(ToolStatus.IN_USE_AND_RESERVED);

					if (InventoryFacade.getInstance().update(toolInDB)) {

						Transaction tr = new Transaction();
						tr.setTransactionTarget(TransactionTarget.TOOL_STATUS);
						tr.setTransactionOperation(TransactionType.EDIT);
						tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
						tr.setInventoryEntity(toolInDB);
						tr.setAdditionalInfo("User reserved tool.\nStatus change from:  " + ToolStatus.IN_USE.getStringValue() + "  to:  " + ToolStatus.IN_USE_AND_RESERVED.getStringValue());

						TransactionFacade.getInstance().insert(tr);

						UIUtils.showNotification("Tool reserved", UIUtils.NotificationType.SUCCESS);
					} else {
						UIUtils.showNotification("Tool reserve failed", UIUtils.NotificationType.ERROR);
					}

					confirmDialog.close();
					refreshDetails();
				});
				confirmDialog.open();
			}
		}
	}

	private void reserveTool(InventoryItem toolInGrid) {

		//Get tool with latest information from database
		InventoryItem toolInDB = getToolFromDataBase(toolInGrid, true);

		if (toolInDB == null) {
			return;
		}

		if (toolInDB.getUsageStatus().equals(ToolStatus.RESERVED) || toolInDB.getUsageStatus().equals(ToolStatus.IN_USE_AND_RESERVED)) {
			UIUtils.showNotification("Tool is reserved by another user", UIUtils.NotificationType.INFO);
			return;
		}

		// FREE
		if (toolInDB.getUsageStatus().equals(ToolStatus.FREE)) {
			toolInDB.setInUseByUser(AuthenticationService.getCurrentSessionUser());
			toolInDB.setUsageStatus(ToolStatus.IN_USE);

			toolInGrid.setInUseByUser(AuthenticationService.getCurrentSessionUser());
			toolInGrid.setUsageStatus(ToolStatus.IN_USE);

			if (InventoryFacade.getInstance().update(toolInDB)) {

				Transaction tr = new Transaction();
				tr.setTransactionTarget(TransactionTarget.TOOL_STATUS);
				tr.setTransactionOperation(TransactionType.EDIT);
				tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
				tr.setInventoryEntity(toolInDB);
				tr.setAdditionalInfo("User took the tool.\nStatus changed from:  " + ToolStatus.FREE.getStringValue() + "  to:  " + ToolStatus.IN_USE.getStringValue());

				TransactionFacade.getInstance().insert(tr);

				UIUtils.showNotification("Tool taken successfully (it was " + ToolStatus.FREE.getStringValue() + ")", UIUtils.NotificationType.SUCCESS);
			} else {
				UIUtils.showNotification("Tool take failed", UIUtils.NotificationType.ERROR);
			}

			return;
		}

		// IN USE
		if (toolInDB.getUsageStatus().equals(ToolStatus.IN_USE)) {
			toolInDB.setReservedByUser(AuthenticationService.getCurrentSessionUser());
			toolInDB.setUsageStatus(ToolStatus.IN_USE_AND_RESERVED);

			toolInGrid.setReservedByUser(AuthenticationService.getCurrentSessionUser());
			toolInGrid.setUsageStatus(ToolStatus.IN_USE_AND_RESERVED);

			if (InventoryFacade.getInstance().update(toolInDB)) {

				Transaction tr = new Transaction();
				tr.setTransactionTarget(TransactionTarget.TOOL_STATUS);
				tr.setTransactionOperation(TransactionType.EDIT);
				tr.setWhoDid(AuthenticationService.getCurrentSessionUser());
				tr.setInventoryEntity(toolInDB);
				tr.setAdditionalInfo("User reserved the tool. Status changed from:  " + ToolStatus.IN_USE.getStringValue() + "  to:  " + ToolStatus.IN_USE_AND_RESERVED.getStringValue());

				TransactionFacade.getInstance().insert(tr);

				UIUtils.showNotification("Tool reserved successfully", UIUtils.NotificationType.SUCCESS);
			} else {
				UIUtils.showNotification("Tool reserve failed", UIUtils.NotificationType.ERROR);
			}
		}
	}

	private void reportTool(InventoryItem t) {
		System.out.println("Reporting Tool: " + t.getName());

		InventoryItem tool = getToolFromDataBase(t, false);
	}

	private InventoryItem getToolFromDataBase(InventoryItem t, boolean checkStatus) {

		//GET TOOL FROM DATABASE FOR LATEST STATUS
		InventoryItem tool = InventoryFacade.getInstance().getToolById(t.getId());

		if (tool == null) {
			UIUtils.showNotification("Problem occurred retrieving tool from Database", UIUtils.NotificationType.ERROR);
			System.err.println("GOT NULL TOOL ENTITY FROM DATABASE");
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
	private InventoryItem getToolFromDataBaseByCode(String code) {
		return InventoryFacade.getInstance().getToolByCode(code);
	}
}
