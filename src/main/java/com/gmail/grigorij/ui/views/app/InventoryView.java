package com.gmail.grigorij.ui.views.app;

import com.gmail.grigorij.backend.database.entities.Category;
import com.gmail.grigorij.backend.database.entities.Message;
import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.tools.ToolUsageStatus;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.MessageFacade;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawer;
import com.gmail.grigorij.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.gmail.grigorij.ui.components.dialogs.CameraDialog;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.forms.ReadOnlyToolForm;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.utils.*;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


@CssImport("./styles/views/inventory.css")
public class InventoryView extends Div {

	private static final String CLASS_NAME = "inventory";
	private final ReadOnlyToolForm toolForm = new ReadOnlyToolForm();

	private TextField searchField;
	private Div filtersDiv;
	private ComboBox<Category> categoryComboBox;
	private ComboBox<ToolUsageStatus> usageStatusComboBox;
	private ComboBox<Location> currentLocationComboBox;

	private boolean filtersVisible = false;


	private Grid<Tool> grid;
	private ListDataProvider<Tool> dataProvider;

	private DetailsDrawer detailsDrawer;


	public InventoryView() {
		addClassName(CLASS_NAME);

		Div contentWrapper = new Div();
		contentWrapper.addClassName(CLASS_NAME + "__content-wrapper");

		contentWrapper.add(constructHeader());
		contentWrapper.add(constructContent());

		add(contentWrapper);
		add(constructDetails());

		toggleFilters();
	}


	private Div constructHeader() {
		Div header = new Div();
		header.addClassName(CLASS_NAME + "__header");

		Div headerTopDiv = new Div();
		headerTopDiv.addClassName(CLASS_NAME + "__header-top");
		header.add(headerTopDiv);

		Button toggleFiltersButton = UIUtils.createButton("Filters", VaadinIcon.FILTER, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ICON);
		toggleFiltersButton.addClassName("dynamic-label-button");
		toggleFiltersButton.addClickListener(e -> toggleFilters());
		headerTopDiv.add(toggleFiltersButton);


		searchField = new TextField();
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Tools");
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event -> applyFilters());
		headerTopDiv.add(searchField);

		Button myToolsButton = UIUtils.createButton("My Tools", VaadinIcon.TOOLS, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ICON);
		myToolsButton.addClassName("dynamic-label-button");
		myToolsButton.addClickListener(e -> constructMyToolsDialog());
		headerTopDiv.add(myToolsButton);

		Button scanToolButton = UIUtils.createButton("Scan Code", VaadinIcon.CAMERA, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ICON);
		scanToolButton.addClassName("dynamic-label-button");
		scanToolButton.addClickListener(e -> constructCodeScannerDialog());
		headerTopDiv.add(scanToolButton);

		Div headerBottomDiv = new Div();
		headerBottomDiv.addClassName(CLASS_NAME + "__header-bottom");
		headerBottomDiv.add(constructToolsFilterLayout());
		header.add(headerBottomDiv);

		return header;
	}

	private Div constructToolsFilterLayout() {
		filtersDiv = new Div();
		filtersDiv.addClassName(CLASS_NAME + "__filters");

		categoryComboBox = new ComboBox<>();
		categoryComboBox.addClassName(ProjectConstants.NO_PADDING_TOP);
		categoryComboBox.setLabel("Category");
		categoryComboBox.setClearButtonVisible(true);
		categoryComboBox.setItems(InventoryFacade.getInstance().getAllActiveCategoriesInCompany(AuthenticationService.getCurrentSessionUser().getCompany().getId()));
		categoryComboBox.setItemLabelGenerator(Category::getName);
		categoryComboBox.addValueChangeListener(e -> applyFilters());

		filtersDiv.add(categoryComboBox);

		usageStatusComboBox = new ComboBox<>();
		usageStatusComboBox.addClassName(ProjectConstants.NO_PADDING_TOP);
		usageStatusComboBox.setLabel("Status");
		usageStatusComboBox.setClearButtonVisible(true);
		usageStatusComboBox.setItems(EnumSet.allOf(ToolUsageStatus.class));
		usageStatusComboBox.setItemLabelGenerator(ToolUsageStatus::getName);
		usageStatusComboBox.addValueChangeListener(e -> applyFilters());

		filtersDiv.add(usageStatusComboBox);

		currentLocationComboBox = new ComboBox<>();
		currentLocationComboBox.addClassName(ProjectConstants.NO_PADDING_TOP);
		currentLocationComboBox.setLabel("Current Location");
		currentLocationComboBox.setClearButtonVisible(true);
		currentLocationComboBox.setItems(AuthenticationService.getCurrentSessionUser().getCompany().getLocations());
		currentLocationComboBox.setItemLabelGenerator(Location::getName);
		currentLocationComboBox.addValueChangeListener(e -> applyFilters());

		filtersDiv.add(currentLocationComboBox);
		return filtersDiv;
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
		grid.addClassName("grid-view");
		grid.setSizeFull();

		dataProvider = new ListDataProvider<>(
				InventoryFacade.getInstance().getAllActiveToolsInCompany(AuthenticationService.getCurrentSessionUser().getCompany().getId()));

		grid.setDataProvider(dataProvider);

		grid.addColumn(Tool::getName)
				.setHeader("Tool")
				.setAutoWidth(true);

		grid.addColumn(Tool::getUsageStatusString)
				.setHeader("Status")
				.setAutoWidth(true);

		grid.addColumn(Tool::getCurrentLocationString)
				.setHeader("Current Location")
				.setAutoWidth(true);


//		Menu Button for quick tool actions

//		grid.addComponentColumn(this::getMenuBar)
//				.setHeader("Actions")
//				.setAutoWidth(true)
//				.setTextAlign(ColumnTextAlign.END)
//				.setFlexGrow(0);

		grid.asSingleSelect().addValueChangeListener(e -> {
			Tool tool = grid.asSingleSelect().getValue();

			if (tool != null) {
				showDetails(tool);
			} else {
				detailsDrawer.hide();
				grid.deselectAll();
			}
		});

		return grid;
	}

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

		Button closeDetailsButton = UIUtils.createButton("Close", ButtonVariant.LUMO_PRIMARY);
		closeDetailsButton.addClickListener(e -> closeDetails());
		footer.add(closeDetailsButton);


		Button reportToolButton = UIUtils.createButton("Report", VaadinIcon.EXCLAMATION, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
		reportToolButton.addClickListener(e -> {
			Tool tool = grid.asSingleSelect().getValue();
			if (tool != null) {
				reportToolOnClick(tool);
			}
		});
		footer.add(reportToolButton);

		Button reserveToolButton = UIUtils.createButton("Reserve", VaadinIcon.CALENDAR_CLOCK, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
		reserveToolButton.addClickListener(e -> {
			Tool tool = grid.asSingleSelect().getValue();
			if (tool != null) {
				reserveToolOnClick(tool);
			}
		});
		footer.add(reserveToolButton);

		Button takeToolButton = UIUtils.createButton("Take", VaadinIcon.HAND, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		takeToolButton.addClickListener(e -> {
			Tool tool = grid.asSingleSelect().getValue();
			if (tool != null) {
				takeToolOnClick(tool);
			}
		});
		footer.add(takeToolButton);

		return footer;
	}


	private void toggleFilters() {
		filtersDiv.getElement().setAttribute("hidden", !filtersVisible);
		filtersVisible = !filtersVisible;
	}

	private void applyFilters() {
		dataProvider.clearFilters();

		if (categoryComboBox.getValue() != null) {
			dataProvider.addFilter(tool -> tool.getCategoryString().equals(categoryComboBox.getValue().getName()));
		}

		if (usageStatusComboBox.getValue() != null) {
			dataProvider.addFilter(tool -> tool.getUsageStatusString().equals(usageStatusComboBox.getValue().getName()));
		}

		if (currentLocationComboBox.getValue() != null) {
			dataProvider.addFilter(tool -> tool.getCurrentLocationString().equals(currentLocationComboBox.getValue().getName()));
		}

		filterGrid(searchField.getValue());
	}

	private void filterGrid(String searchString) {

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

	private boolean matchesFilter(Tool item, String filter) {
		return StringUtils.containsIgnoreCase(item.getName(), filter) ||
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
				StringUtils.containsIgnoreCase(String.valueOf(item.getGuarantee_months()), filter);
	}


	private void showDetails(Tool tool) {
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
		List<Tool> allMyTools = new ArrayList<>();
		List<Tool> selectedTools = new ArrayList<>();

		allMyTools.addAll(InventoryFacade.getInstance().getAllToolsByCurrentUserId(AuthenticationService.getCurrentSessionUser().getId()));
		allMyTools.addAll(InventoryFacade.getInstance().getAllToolsByReservedUserId(AuthenticationService.getCurrentSessionUser().getId()));

		if (allMyTools.size() <= 0) {
			UIUtils.showNotification("You don't have any tools", NotificationVariant.LUMO_PRIMARY);
			return;
		}

		ListDataProvider<Tool> myToolsDataProvider = DataProvider.ofCollection(allMyTools);


		CustomDialog dialog = new CustomDialog();
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);
		dialog.setHeader(UIUtils.createH3Label("My Tools"));

		Grid<Tool> myToolsGrid = new Grid<>();
		myToolsGrid.addClassName("grid-view");
		myToolsGrid.addClassName("my-tools-grid");

		myToolsGrid.setDataProvider(myToolsDataProvider);

		myToolsGrid.addColumn(Tool::getName)
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
				UIUtils.showNotification("Select Location where to return", NotificationVariant.LUMO_PRIMARY);
			} else {

				if (selectedTools.size() <= 0) {
					UIUtils.showNotification("Select Tools to return", NotificationVariant.LUMO_PRIMARY);
				} else {

					// IF TOOL IS RESERVED
					selectedTools.removeIf(tool -> tool.getReservedUser() != null && tool.getReservedUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId()));

					returnToolOnClick(selectedTools, locationComboBox.getValue());

					if (selectedTools.size() < myToolsDataProvider.getItems().size()) {
						for (Tool tool : selectedTools) {
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
				final UI ui = UI.getCurrent();

				if (ui != null) {
					ui.access(() -> {
						try {
//							cameraDialog.getCameraView().stop();
							cameraDialog.stop();
							cameraDialog.close();

							Tool tool = InventoryFacade.getInstance().getToolByCode(code);
							if (tool == null) {
								UIUtils.showNotification("Tool not found", NotificationVariant.LUMO_PRIMARY, 2000);
							} else {
								UIUtils.showNotification("Tool found", NotificationVariant.LUMO_SUCCESS, 1000);
								showDetails(tool);
							}
						} catch (Exception e) {
//							cameraDialog.getCameraView().stop();
							cameraDialog.stop();
							cameraDialog.close();

							UIUtils.showNotification("We are sorry, but an internal error occurred", NotificationVariant.LUMO_ERROR);
							e.printStackTrace();
						}
						ui.push();
					});
				}
			}

			@Override
			public void onFail() {
				final UI ui = UI.getCurrent();

				if (ui != null) {
					ui.access(() -> {
						try {
							cameraDialog.getCameraView().takePicture();
						} catch (Exception e) {
//							cameraDialog.getCameraView().stop();
							cameraDialog.stop();
							cameraDialog.close();

							UIUtils.showNotification("We are sorry, but an internal error occurred", NotificationVariant.LUMO_ERROR);
							e.printStackTrace();
						}
						ui.push();
					});
				}
			}
		});

		cameraDialog.open();
		cameraDialog.initCamera();
	}


	/**
	 * TAKE TOOL
	 */
	private void takeToolOnClick(Tool tool) {
		if (tool == null) {
			return;
		}

		Tool toolFromDB = InventoryFacade.getInstance().getToolById(tool.getId());

		if (toolFromDB == null) {
			UIUtils.showNotification("Error retrieving tool from database", NotificationVariant.LUMO_ERROR);
			return;
		}

		copyToolParameters(tool, toolFromDB);

		if (tool.getReservedUser() != null) {

			// RESERVED BY OTHER USER
			if (!tool.getReservedUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {
				UIUtils.showNotification("Tool is currently reserved", NotificationVariant.LUMO_PRIMARY);
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
				UIUtils.showNotification("Tool taken", NotificationVariant.LUMO_SUCCESS, 2000);

				closeDetails();
			} else {
				UIUtils.showNotification("Tool take failed", NotificationVariant.LUMO_ERROR);
			}
		} else {
			UIUtils.showNotification("Tool is currently in use", NotificationVariant.LUMO_PRIMARY);
			dataProvider.refreshItem(tool);
			showDetails(tool);
		}
	}


	/**
	 * RESERVE TOOL
	 */
	private void reserveToolOnClick(Tool tool) {
		if (tool == null) {
			return;
		}

		Tool toolFromDB = InventoryFacade.getInstance().getToolById(tool.getId());

		if (toolFromDB == null) {
			UIUtils.showNotification("Error retrieving tool from database", NotificationVariant.LUMO_ERROR);
			return;
		}

		copyToolParameters(tool, toolFromDB);

		if (toolFromDB.getReservedUser() == null) {

			if (toolFromDB.getCurrentUser() != null) {
				if (tool.getCurrentUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {
					UIUtils.showNotification("You cannot reserve the tool you already have", NotificationVariant.LUMO_PRIMARY);
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
				UIUtils.showNotification("Tool reserved", NotificationVariant.LUMO_SUCCESS, 2000);

				closeDetails();
			} else {
				UIUtils.showNotification("Tool reserve failed", NotificationVariant.LUMO_ERROR);
			}
		} else {
			UIUtils.showNotification("Tool is currently reserved", NotificationVariant.LUMO_PRIMARY);
			dataProvider.refreshItem(tool);
			showDetails(tool);
		}
	}


	/**
	 * REPORT TOOL
	 */
	private void reportToolOnClick(Tool tool) {
		if (tool == null) {
			return;
		}

		Tool toolFromDB = InventoryFacade.getInstance().getToolById(tool.getId());

		//TODO: DIALOG WITH REPORTS FOR tool
	}


	/**
	 * RETURN TOOL
	 */
	private void returnToolOnClick(List<Tool> userSelectedTools, Location location) {

		if (userSelectedTools == null) {
			System.out.println("userSelectedTools == null");
			return;
		}

		if (userSelectedTools.size() <= 0) {
			System.out.println("userSelectedTools.size() <= 0");
			return;
		}

		List<Tool> tools = new ArrayList<>();

		// RECURSIVELY FIND TOOL FROM DATA PROVIDER
		for (Tool userSelectedItem : userSelectedTools) {

			for (Tool tool : dataProvider.getItems()) {
				if (tool.getId().equals(userSelectedItem.getId())) {
					tools.add(tool);
					break;
				}
			}
		}

		for (Tool tool : tools) {

			copyToolParameters(tool, InventoryFacade.getInstance().getToolById(tool.getId()));

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
						transaction.setOperation(Operation.RETURN_T);
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
//							message.setMessageType(MessageType.TOOL_FREE);
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
						transaction.setOperation(Operation.CANCEL_RESERVATION_T);
						transaction.setTargetDetails(tool.getName());
						TransactionFacade.getInstance().insert(transaction);
					}
				}
			}

			dataProvider.refreshItem(tool);
		}
	}


	/**
	 * UPDATE TOOL PARAMETERS FROM DATABASE TO LOCAL TOOLS IN DATA PROVIDER
	 */
	private void copyToolParameters(Tool destinationTool, Tool sourceTool) {
		destinationTool.setName(sourceTool.getName());
		destinationTool.setSerialNumber(sourceTool.getSerialNumber());
		destinationTool.setSerialNumber(sourceTool.getSerialNumber());
		destinationTool.setRF_Code(sourceTool.getRF_Code());
		destinationTool.setBarcode(sourceTool.getBarcode());
		destinationTool.setManufacturer(sourceTool.getManufacturer());
		destinationTool.setModel(sourceTool.getModel());
		destinationTool.setToolInfo(sourceTool.getToolInfo());
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
}
