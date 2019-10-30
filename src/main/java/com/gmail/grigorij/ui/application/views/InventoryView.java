package com.gmail.grigorij.ui.application.views;

import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.MessageFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.database.entities.Message;
import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.enums.MessageType;
import com.gmail.grigorij.backend.database.enums.inventory.InventoryHierarchyType;
import com.gmail.grigorij.backend.database.enums.inventory.ToolUsageStatus;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.components.dialogs.CameraDialog;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.forms.ReadOnlyToolForm;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.Broadcaster;
import com.gmail.grigorij.utils.DateConverter;
import com.gmail.grigorij.utils.OperationStatus;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@CssImport("./styles/views/inventory.css")
public class InventoryView extends Div {

	private static final String CLASS_NAME = "inventory";
	private final ReadOnlyToolForm toolForm = new ReadOnlyToolForm();

	private Checkbox allToolParametersCheckBox;

	private TreeGrid<InventoryItem> grid;
	private TreeDataProvider<InventoryItem> dataProvider;


	private DetailsDrawer detailsDrawer;


	public InventoryView() {
		addClassName(CLASS_NAME);

		Div contentWrapper = new Div();
		contentWrapper.addClassName(CLASS_NAME + "__content-wrapper");

		contentWrapper.add(constructHeader());
		contentWrapper.add(constructContent());

		add(contentWrapper);
		add(constructDetails());
	}


	private Div constructHeader() {
		Div header = new Div();
		header.addClassName(CLASS_NAME + "__header");

		TextField searchField = new TextField();
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search By Tool Name");
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event -> filterGrid(searchField.getValue()));
		header.add(searchField);

		Div additionalOptionsDiv = new Div();
		additionalOptionsDiv.addClassName(CLASS_NAME + "__additional_options");

		allToolParametersCheckBox = new Checkbox("All Parameters");
		allToolParametersCheckBox.addValueChangeListener(e -> {
			if (allToolParametersCheckBox.getValue()) {
				searchField.setPlaceholder("Search By All Tool Parameters");
			} else {
				searchField.setPlaceholder("Search By Tool Name");
			}

			if (searchField.getValue().length() > 0) {
				filterGrid(searchField.getValue());
			}
		});
		additionalOptionsDiv.add(allToolParametersCheckBox);

		Button myToolsButton = UIUtils.createButton("My Tools", VaadinIcon.TOOLS, ButtonVariant.LUMO_CONTRAST);
		myToolsButton.addClickListener(e -> constructMyToolsDialog());
		additionalOptionsDiv.add(myToolsButton);

		Button scanToolButton = UIUtils.createButton("Scan", VaadinIcon.CAMERA, ButtonVariant.LUMO_CONTRAST);
		scanToolButton.addClickListener(e -> constructCodeScannerDialog());
		additionalOptionsDiv.add(scanToolButton);

		header.add(additionalOptionsDiv);

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
		grid = new TreeGrid<>();
		grid.addClassName("grid-view");
		grid.setSizeFull();

		List<InventoryItem> toolsAndCategories = InventoryFacade.getInstance().getAllInCompany(AuthenticationService.getCurrentSessionUser().getCompany().getId());
		TreeData<InventoryItem> treeData = new TreeData<>();

		//List must be sorted -> Parent must be added before child
		toolsAndCategories.sort(Comparator.comparing(InventoryItem::getLevel));

		toolsAndCategories.forEach(item -> {
			treeData.addItem(item.getParentCategory(), item);
		});

		dataProvider = new TreeDataProvider<>(treeData);

		grid.setDataProvider(dataProvider);

		grid.addHierarchyColumn(InventoryItem::getName)
				.setHeader("Tools")
				.setSortable(false)
				.setAutoWidth(true);

		grid.addColumn(new ComponentRenderer<>(this::getUsageStatusSpan))
				.setHeader("Status")
				.setAutoWidth(true);

		grid.addColumn(InventoryItem::getCurrentLocationName)
				.setHeader("Location")
				.setAutoWidth(true);


//		Menu Button for quick tool actions

//		grid.addComponentColumn(this::getMenuBar)
//				.setHeader("Actions")
//				.setAutoWidth(true)
//				.setTextAlign(ColumnTextAlign.END)
//				.setFlexGrow(0);

		grid.asSingleSelect().addValueChangeListener(e -> {
			InventoryItem inventoryItem = grid.asSingleSelect().getValue();

			if (inventoryItem != null) {
				if (inventoryItem.getInventoryHierarchyType().equals(InventoryHierarchyType.CATEGORY)) {
					if (grid.isExpanded(inventoryItem)) {
						grid.collapse(inventoryItem);
					} else {
						grid.expand(inventoryItem);
					}
					grid.deselectAll();
				} else {
					showDetails(inventoryItem);
				}
			} else {
				detailsDrawer.hide();
				grid.deselectAll();
			}
		});

		grid.addExpandListener(e -> {
			grid.recalculateColumnWidths();
		});
		grid.addCollapseListener(e -> {
			grid.recalculateColumnWidths();
		});

		return grid;
	}

	private Span getUsageStatusSpan(InventoryItem inventoryItem) {
		Span span = new Span("");

		if (inventoryItem.getInventoryHierarchyType().equals(InventoryHierarchyType.CATEGORY)) {
			return span;
		}

		span.getElement().getStyle().set("padding-left", "var(--lumo-space-xs)");
		span.getElement().getStyle().set("padding-right", "var(--lumo-space-xs)");
		span.getElement().getStyle().set("border-radius", "4px");

		span.setText(inventoryItem.getUsageStatus().getName());
		span.getElement().getStyle().set("background-color", inventoryItem.getUsageStatus().getColor());
		span.getElement().getStyle().set("color", "var(--lumo-header-text-color)");

		return span;
	}

//	private MenuBar getMenuBar(InventoryItem inventoryItem) {
//		MenuBar menuBar = new MenuBar();
//
//		if (inventoryItem.getInventoryHierarchyType().equals(InventoryHierarchyType.CATEGORY)) {
//			return menuBar;
//		}
//
//		menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
//		MenuItem menuItem = menuBar.addItem(new Icon(VaadinIcon.MENU));
//
//		boolean take = false;
//		boolean reserve = false;
//
//		if (PermissionFacade.getInstance().isUserAllowedTo(Operation.TAKE, OperationTarget.INVENTORY_TOOL, PermissionRange.COMPANY) ||
//				AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
//
//			menuItem.getSubMenu().addItem("Take", e -> {
//				takeToolOnClick(inventoryItem);
//			});
//			take = true;
//		}
//
//		if (PermissionFacade.getInstance().isUserAllowedTo(Operation.RESERVE, OperationTarget.INVENTORY_TOOL, PermissionRange.COMPANY) ||
//				AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
//
//			if (take) {
//				menuItem.getSubMenu().add(new Hr());
//			}
//			menuItem.getSubMenu().addItem("Reserve", e -> {
//				reserveToolOnClick(inventoryItem);
//			});
//			reserve = true;
//		}
//
//		if (PermissionFacade.getInstance().isUserAllowedTo(Operation.REPORT, OperationTarget.INVENTORY_TOOL, PermissionRange.COMPANY) ||
//				AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
//
//			if (reserve) {
//				menuItem.getSubMenu().add(new Hr());
//			} else {
//				if (take) {
//					menuItem.getSubMenu().add(new Hr());
//				}
//			}
//			menuItem.getSubMenu().addItem("Report", e -> {
//				reportToolOnClick(inventoryItem);
//			});
//		}
//
//		return menuBar;
//	}

	private DetailsDrawer constructDetails() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		detailsDrawer.setContent(toolForm);

		// Header
		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Tool Details");
		detailsDrawerHeader.getClose().addClickListener(e -> closeDetails());

		detailsDrawer.setHeader(detailsDrawerHeader);

		// Footer
		detailsDrawer.setFooter(createDetailsFooter());

		return detailsDrawer;
	}

	private FlexBoxLayout createDetailsFooter() {
		FlexBoxLayout footer = new FlexBoxLayout();
		footer.setClassName(CLASS_NAME + "__details-footer");

		Button closeDetailsButton = UIUtils.createButton("Close", ButtonVariant.LUMO_CONTRAST);
		closeDetailsButton.addClickListener(e -> closeDetails());
		footer.add(closeDetailsButton);


		Button reportToolButton = UIUtils.createButton("Report", VaadinIcon.EXCLAMATION, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
		reportToolButton.addClickListener(e -> {
			InventoryItem tool = grid.asSingleSelect().getValue();
			if (tool != null) {
				reportToolOnClick(tool);
			}
		});
		footer.add(reportToolButton);

		Button reserveToolButton = UIUtils.createButton("Reserve", VaadinIcon.CALENDAR_CLOCK, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
		reserveToolButton.addClickListener(e -> {
			InventoryItem tool = grid.asSingleSelect().getValue();
			if (tool != null) {
				reserveToolOnClick(tool);
			}
		});
		footer.add(reserveToolButton);

		Button takeToolButton = UIUtils.createButton("Take", VaadinIcon.HAND, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		takeToolButton.addClickListener(e -> {
			InventoryItem tool = grid.asSingleSelect().getValue();
			if (tool != null) {
				takeToolOnClick(tool);
			}
		});
		footer.add(takeToolButton);

		return footer;
	}


	private void filterGrid(String searchString) {
		dataProvider.clearFilters();

		if (searchString.length() <= 0) {
			return;
		}

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

	private boolean matchesFilter(InventoryItem item, String filter) {
		if (item.getInventoryHierarchyType().equals(InventoryHierarchyType.CATEGORY)) {
			grid.expand(item);
		}

		if (!allToolParametersCheckBox.getValue()) {
			if (StringUtils.containsIgnoreCase(item.getName(), filter)) {
				return true;
			}
		} else {
			if (StringUtils.containsIgnoreCase(item.getName(), filter) ||
						StringUtils.containsIgnoreCase(item.getBarcode(), filter) ||
						StringUtils.containsIgnoreCase(item.getSerialNumber(), filter) ||
						StringUtils.containsIgnoreCase(item.getToolInfo(), filter) ||
						StringUtils.containsIgnoreCase(item.getManufacturer(), filter) ||
						StringUtils.containsIgnoreCase(item.getModel(), filter) ||
						StringUtils.containsIgnoreCase((item.getUsageStatus() == null) ? "" : item.getUsageStatus().getName(), filter) ||
						StringUtils.containsIgnoreCase((item.getCurrentUser() == null) ? "" : item.getCurrentUser().getFullName(), filter) ||
						StringUtils.containsIgnoreCase((item.getReservedUser() == null) ? "" : item.getReservedUser().getFullName(), filter) ||
						StringUtils.containsIgnoreCase((item.getDateBought() == null) ? "" : DateConverter.localDateToString(item.getDateBought()), filter) ||
						StringUtils.containsIgnoreCase((item.getDateNextMaintenance() == null) ? "" : DateConverter.localDateToString(item.getDateNextMaintenance()), filter) ||
						StringUtils.containsIgnoreCase(String.valueOf(item.getPrice()), filter) ||
						StringUtils.containsIgnoreCase(String.valueOf(item.getGuarantee_months()), filter)) {
				return true;
			}
		}

		return InventoryFacade.getInstance().getAllByParentId(item.getId()).stream().anyMatch(child -> matchesFilter(child, filter));
//		return item.getChildren().stream().anyMatch(child -> matchesFilter(child, filter));
	}


	private void showDetails(InventoryItem tool) {
		if (tool != null) {
			toolForm.setTool(tool);
			detailsDrawer.show();
		}
	}

	private void closeDetails() {
		detailsDrawer.hide();
		grid.deselectAll();
	}


	/**
	 * Dialog with Grid containing all Tools user has in use / reserved with ability to return / cancel reservation
	 */
	private void constructMyToolsDialog() {
		List<InventoryItem> allMyTools = new ArrayList<>();
		List<InventoryItem> selectedTools = new ArrayList<>();

		allMyTools.addAll(InventoryFacade.getInstance().getAllToolsByCurrentUserId(AuthenticationService.getCurrentSessionUser().getId()));
		allMyTools.addAll(InventoryFacade.getInstance().getAllToolsByReservedUserId(AuthenticationService.getCurrentSessionUser().getId()));

		if (allMyTools.size() <= 0) {
			UIUtils.showNotification("You don't have any tools", UIUtils.NotificationType.INFO);
			return;
		}

		ListDataProvider<InventoryItem> myToolsDataProvider = DataProvider.ofCollection(allMyTools);


		CustomDialog dialog = new CustomDialog();
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);
		dialog.setHeader(UIUtils.createH3Label("My Tools"));

		Grid<InventoryItem> myToolsGrid = new Grid<>();
		myToolsGrid.addClassName("grid-view");
		myToolsGrid.addClassName("my-tools-grid");

		myToolsGrid.setDataProvider(myToolsDataProvider);

		myToolsGrid.addColumn(InventoryItem::getName)
				.setHeader("Tool")
				.setFlexGrow(3)
				.setAutoWidth(true);

		myToolsGrid.addColumn(tool -> {
					if (tool.getCurrentUser() != null) {
						if (tool.getCurrentUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {
							return ToolUsageStatus.IN_USE.getName();
						}
					}
					if (tool.getReservedUser() != null) {
						if (tool.getReservedUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {
							return ToolUsageStatus.RESERVED.getName();
						}
					}

					return "";
				})
				.setHeader("Status")
				.setFlexGrow(2)
				.setAutoWidth(true);

		myToolsGrid.addComponentColumn(tool -> {
					if (tool.getReservedUser() != null) {
						if (tool.getReservedUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {
							Button toolActionButton = new Button();
							toolActionButton.setText("Cancel");
							toolActionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_SMALL);
							toolActionButton.addClickListener(e -> {
								selectedTools.clear();
								selectedTools.add(tool);

								returnToolOnClick(selectedTools, null);

								if (myToolsDataProvider.getItems().size() > 1) {
									myToolsDataProvider.getItems().removeIf(item -> item.getId().equals(tool.getId()));
									myToolsDataProvider.refreshAll();
								} else {
									dialog.close();
								}


							});

							return toolActionButton;
						}
					}

					return new Span("");
				})
				.setHeader("Reservation")
				.setFlexGrow(1)
				.setAutoWidth(true);

		myToolsGrid.setSelectionMode(Grid.SelectionMode.MULTI);

		myToolsGrid.addSelectionListener(event -> {
			selectedTools.clear();
			selectedTools.addAll(event.getAllSelectedItems());
		});


		ComboBox<Location> locationComboBox = new ComboBox<>();
		locationComboBox.setLabel("Return Location");
		locationComboBox.setItems(AuthenticationService.getCurrentSessionUser().getCompany().getLocations());
		locationComboBox.setItemLabelGenerator(Location::getName);

		Button returnButton = UIUtils.createButton("Return", ButtonVariant.LUMO_PRIMARY);
		returnButton.addClickListener(e -> {
			if (locationComboBox.getValue() == null) {
				UIUtils.showNotification("Select Location where to return", UIUtils.NotificationType.INFO);
			} else {

				if (selectedTools.size() <= 0) {
					UIUtils.showNotification("Select Tools to return", UIUtils.NotificationType.INFO);
				} else {

					// IF TOOL IS RESERVED
					selectedTools.removeIf(tool -> tool.getReservedUser() != null && tool.getReservedUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId()));

					returnToolOnClick(selectedTools, locationComboBox.getValue());

					if (selectedTools.size() < myToolsDataProvider.getItems().size()) {
						for (InventoryItem tool : selectedTools) {
							myToolsDataProvider.getItems().removeIf(item -> item.getId().equals(tool.getId()));
						}
						myToolsDataProvider.refreshAll();
					} else {
						dialog.close();
					}
				}
			}
		});

		Div locationDiv = new Div();
		locationDiv.addClassName("location-selection-layout");
		locationDiv.add(locationComboBox, returnButton);

		dialog.getContent().removePadding();
		dialog.getContent().setPadding(Horizontal.XS);

		dialog.getContent().add(myToolsGrid);
		dialog.getContent().add(locationDiv);

		dialog.closeOnCancel();
		dialog.getCancelButton().setText("Close");

		dialog.setConfirmButton(null);

		dialog.open();
	}


	/**
	 * Dialog with camera controls for scanning codes
	 */
	private void constructCodeScannerDialog() {
		CameraDialog cameraDialog = new CameraDialog();
		cameraDialog.getCameraView().onFinished(new OperationStatus() {
			@Override
			public void onSuccess(String code) {
				if (UI.getCurrent() != null) {
					UI.getCurrent().access(() -> {
						try {
							UIUtils.showNotification("Code scanned, searching for tool...", UIUtils.NotificationType.INFO);
							cameraDialog.stopCamera();
							UI.getCurrent().push();

							InventoryItem tool = InventoryFacade.getInstance().getToolByCode(code);
							if (tool == null) {
								UIUtils.showNotification("Tool not found", UIUtils.NotificationType.INFO);
							} else {
								UIUtils.showNotification("Tool found", UIUtils.NotificationType.SUCCESS, 3000);
								showDetails(tool);
							}

							cameraDialog.close();
							UI.getCurrent().push();
						} catch (Exception e) {
							cameraDialog.getCameraView().stop();
							cameraDialog.close();

							UIUtils.showNotification("We are sorry, but an internal error occurred", UIUtils.NotificationType.ERROR);
							e.printStackTrace();
						}
					});
				}
			}

			@Override
			public void onFail() {
				if (UI.getCurrent() != null) {
					UI.getCurrent().access(() -> {
						try {
							UIUtils.showNotification("Code not found in image", UIUtils.NotificationType.INFO, 2000);
							UI.getCurrent().push();
						} catch (Exception e) {
							cameraDialog.getCameraView().stop();
							cameraDialog.close();

							UIUtils.showNotification("We are sorry, but an internal error occurred", UIUtils.NotificationType.ERROR);
							e.printStackTrace();
						}
					});
				}
			}
		});

		cameraDialog.open();
		cameraDialog.getCameraView().showPreview();
	}


	/**
	 * TAKE TOOL
	 */
	private void takeToolOnClick(InventoryItem tool) {
		if (tool == null) {
			return;
		}

		InventoryItem toolFromDB = InventoryFacade.getInstance().getById(tool.getId());

		if (toolFromDB == null) {
			UIUtils.showNotification("Error retrieving tool from database", UIUtils.NotificationType.ERROR);
			return;
		}

		copyToolParameters(tool, toolFromDB);

		if (tool.getReservedUser() != null) {

			// RESERVED BY OTHER USER
			if (!tool.getReservedUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {
				UIUtils.showNotification("Tool is currently reserved", UIUtils.NotificationType.INFO);
				dataProvider.refreshItem(tool);
				showDetails(tool);
				return;
			}

			// RESERVED BY CURRENT USER
			tool.setReservedUser(null);
		}

		if (tool.getCurrentUser() == null) {

			tool.setCurrentUser(AuthenticationService.getCurrentSessionUser());
			tool.setUsageStatus(ToolUsageStatus.IN_USE);

			if (InventoryFacade.getInstance().update(tool)) {

				Transaction transaction = new Transaction();
				transaction.setUser(AuthenticationService.getCurrentSessionUser());
				transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
				transaction.setOperation(Operation.TAKE);
				transaction.setOperationTarget1(OperationTarget.INVENTORY_TOOL);
				transaction.setTargetDetails(tool.getName());
				TransactionFacade.getInstance().insert(transaction);

				dataProvider.refreshItem(tool);
				UIUtils.showNotification("Tool taken", UIUtils.NotificationType.SUCCESS, 2000);

				closeDetails();
			} else {
				UIUtils.showNotification("Tool take failed", UIUtils.NotificationType.ERROR);
			}
		} else {
			UIUtils.showNotification("Tool is currently in use", UIUtils.NotificationType.INFO);
			dataProvider.refreshItem(tool);
			showDetails(tool);
		}
	}


	/**
	 * RESERVE TOOL
	 */
	private void reserveToolOnClick(InventoryItem tool) {
		if (tool == null) {
			return;
		}

		InventoryItem toolFromDB = InventoryFacade.getInstance().getById(tool.getId());

		if (toolFromDB == null) {
			UIUtils.showNotification("Error retrieving tool from database", UIUtils.NotificationType.ERROR);
			return;
		}

		copyToolParameters(tool, toolFromDB);

		if (toolFromDB.getReservedUser() == null) {

			if (toolFromDB.getCurrentUser() != null) {
				if (tool.getCurrentUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {
					UIUtils.showNotification("You cannot reserve the tool you already have", UIUtils.NotificationType.INFO);
					dataProvider.refreshItem(tool);
					return;
				}

				tool.setUsageStatus(ToolUsageStatus.IN_USE_AND_RESERVED);
			} else {
				tool.setUsageStatus(ToolUsageStatus.RESERVED);
			}

			tool.setReservedUser(AuthenticationService.getCurrentSessionUser());


			if (InventoryFacade.getInstance().update(tool)) {

				Transaction transaction = new Transaction();
				transaction.setUser(AuthenticationService.getCurrentSessionUser());
				transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
				transaction.setOperation(Operation.RESERVE);
				transaction.setOperationTarget1(OperationTarget.INVENTORY_TOOL);
				transaction.setTargetDetails(tool.getName());
				TransactionFacade.getInstance().insert(transaction);

				dataProvider.refreshItem(tool);
				UIUtils.showNotification("Tool reserved", UIUtils.NotificationType.SUCCESS, 2000);

				closeDetails();
			} else {
				UIUtils.showNotification("Tool reserve failed", UIUtils.NotificationType.ERROR);
			}
		} else {
			UIUtils.showNotification("Tool is currently reserved", UIUtils.NotificationType.INFO);
			dataProvider.refreshItem(tool);
			showDetails(tool);
		}
	}


	/**
	 * REPORT TOOL
	 */
	private void reportToolOnClick(InventoryItem tool) {
		if (tool == null) {
			return;
		}

		InventoryItem toolFromDB = InventoryFacade.getInstance().getById(tool.getId());

		//TODO: DIALOG WITH REPORTS FOR tool
	}


	/**
	 * RETURN TOOL
	 */
	private void returnToolOnClick(List<InventoryItem> userSelectedTools, Location location) {

		if (userSelectedTools == null) {
			System.out.println("userSelectedTools == null");
			return;
		}

		if (userSelectedTools.size() <= 0) {
			System.out.println("userSelectedTools.size() <= 0");
			return;
		}

		List<InventoryItem> tools = new ArrayList<>();

		// RECURSIVELY FIND TOOL FROM DATA PROVIDER TREE DATA
		for (InventoryItem userSelectedItem : userSelectedTools) {
			for (InventoryItem rootItem : dataProvider.getTreeData().getRootItems()) {
				InventoryItem item = getItemFromDataProviderRecursively(rootItem, userSelectedItem.getId());

				if (item != null) {
					tools.add(item);
					break;
				}
			}
		}

		for (InventoryItem tool : tools) {

			copyToolParameters(tool, InventoryFacade.getInstance().getById(tool.getId()));

			if (tool.getCurrentUser() != null) {

				// USER RETURNING TOOL
				if (tool.getCurrentUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {

					// TOOL IS RESERVED BY OTHER USER
					if (tool.getReservedUser() != null) {
						tool.setUsageStatus(ToolUsageStatus.RESERVED);
					} else {
						tool.setUsageStatus(ToolUsageStatus.FREE);
					}
					tool.setCurrentUser(null);

					if (location != null) {
						tool.setCurrentLocation(location);
					}

					if (InventoryFacade.getInstance().update(tool)) {

						Transaction transaction = new Transaction();
						transaction.setUser(AuthenticationService.getCurrentSessionUser());
						transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
						transaction.setOperation(Operation.RETURN);
						transaction.setOperationTarget1(OperationTarget.INVENTORY_TOOL);
						if (location == null) {
							transaction.setTargetDetails(tool.getName());
						} else {
							transaction.setTargetDetails(tool.getName() +" to " +location.getName());
						}
						TransactionFacade.getInstance().insert(transaction);

						// TOOL IS RESERVED BY OTHER USER
						if (tool.getReservedUser() != null) {
							Message message = new Message();
							message.setMessageType(MessageType.TOOL_FREE);
							message.setSubject("Tool is available");
							if (location == null) {
								message.setText("Tool you have reserved is now available");
							} else {
								message.setText("Tool you have reserved is now available \nat " + location.getName());
							}
							message.setToolId(tool.getId());
							message.setRecipientId(tool.getReservedUser().getId());
							message.setSenderString("SYSTEM");
							MessageFacade.getInstance().insert(message);

							Broadcaster.broadcastToUser(tool.getReservedUser().getId(), "You have new message");
						}
					}
				}
			}


			if (tool.getReservedUser() != null) {

				// USER CANCELLING TOOL RESERVATION
				if (tool.getReservedUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {

					if (tool.getCurrentUser() != null) {
						tool.setUsageStatus(ToolUsageStatus.IN_USE);
					} else {
						tool.setUsageStatus(ToolUsageStatus.FREE);
					}
					tool.setReservedUser(null);

					if (InventoryFacade.getInstance().update(tool)) {

						Transaction transaction = new Transaction();
						transaction.setUser(AuthenticationService.getCurrentSessionUser());
						transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
						transaction.setOperation(Operation.CANCEL_RESERVATION);
						transaction.setTargetDetails(tool.getName());
						TransactionFacade.getInstance().insert(transaction);
					}
				}
			}

			dataProvider.refreshItem(tool);
		}
	}


	private void copyToolParameters(InventoryItem destinationTool, InventoryItem sourceTool) {
		destinationTool.setParentCategory(sourceTool.getParentCategory());
		destinationTool.setInventoryHierarchyType(sourceTool.getInventoryHierarchyType());
		destinationTool.setName(sourceTool.getName());
		destinationTool.setSerialNumber(sourceTool.getSerialNumber());
		destinationTool.setSerialNumber(sourceTool.getSerialNumber());
		destinationTool.setRF_Code(sourceTool.getRF_Code());
		destinationTool.setBarcode(sourceTool.getBarcode());
		destinationTool.setManufacturer(sourceTool.getManufacturer());
		destinationTool.setModel(sourceTool.getModel());
		destinationTool.setToolInfo(sourceTool.getToolInfo());
		destinationTool.setPersonal(sourceTool.isPersonal());
		destinationTool.setUsageStatus(sourceTool.getUsageStatus());
		destinationTool.setCurrentUser(sourceTool.getCurrentUser());
		destinationTool.setReservedUser(sourceTool.getReservedUser());
		destinationTool.setCompany(sourceTool.getCompany());
		destinationTool.setDateBought(sourceTool.getDateBought());
		destinationTool.setDateNextMaintenance(sourceTool.getDateNextMaintenance());
		destinationTool.setPrice(sourceTool.getPrice());
		destinationTool.setGuarantee_months(sourceTool.getGuarantee_months());
		destinationTool.setCurrentLocation(sourceTool.getCurrentLocation());
	}


	/**
	 * Recursive search method
	 * @param item return item from memory data provider if id's match
	 * @param id item's id to search for
	 * @return null if item is null else item from memory
	 */
	private InventoryItem getItemFromDataProviderRecursively(InventoryItem item, long id) {
		if (item == null) {
			return null;
		}

		if (item.getId().equals(id)) {
			return item;
		}

		if (dataProvider.getTreeData().getChildren(item).size() > 0) {
			for (InventoryItem child : dataProvider.getTreeData().getChildren(item)) {
				InventoryItem temp = getItemFromDataProviderRecursively(child, id);

				if (temp != null) {
					return temp;
				}
			}
		}

		return null;
	}
}
