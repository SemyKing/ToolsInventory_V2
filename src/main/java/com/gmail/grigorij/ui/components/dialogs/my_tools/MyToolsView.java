package com.gmail.grigorij.ui.components.dialogs.my_tools;

import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.enums.tools.ToolUsageStatus;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.views.app.InventoryView;
import com.gmail.grigorij.utils.DataChangeListener;
import com.gmail.grigorij.utils.OperationStatus;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@StyleSheet("context://styles/views/my-tools.css")
public class MyToolsView extends Div {

	private static final String CLASS_NAME = "my-tools";
	private final InventoryView inventoryView;

	private TextField searchField;
	private Div content;

	private List<Tool> allMyTools;
	private Grid<Tool> grid;
	private ListDataProvider<Tool> dataProvider;

	private ComboBox<Location> locationComboBox;
	private Button returnToolButton;

	private final DataChangeListener dataChangeListener;

	public MyToolsView(InventoryView inventoryView, DataChangeListener dataChangeListener) {
		this.inventoryView = inventoryView;
		this.dataChangeListener = dataChangeListener;
		this.setMinWidth("45vw");

		addClassName(CLASS_NAME);

		allMyTools = new ArrayList<>();

		allMyTools.addAll(InventoryFacade.getInstance().getAllToolsByCurrentUserId(AuthenticationService.getCurrentSessionUser().getId()));
		allMyTools.addAll(InventoryFacade.getInstance().getAllToolsByReservedUserId(AuthenticationService.getCurrentSessionUser().getId()));

		if (allMyTools.size() <= 0) {
			constructNoToolsMessage();
			return;
		}

		add(constructHeader());

		add(constructContent());

		add(constructFooter());
	}


	private Div constructHeader() {
		Div header = new Div();
		header.addClassName(CLASS_NAME+"__header");

		searchField = new TextField();
		searchField.setClearButtonVisible(true);
		searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
		searchField.setPlaceholder("Search Tools");
		searchField.setValueChangeMode(ValueChangeMode.LAZY);
		searchField.addValueChangeListener(event -> filterGrid(searchField.getValue()));

		header.add(searchField);

		return header;
	}

	private Div constructContent() {
		content = new Div();
		content.addClassName(CLASS_NAME+"__content");

		// GRID
		content.add(constructGrid());

		return content;
	}

	private Component constructGrid() {
		grid = new Grid<>();
		grid.addClassNames("grid-view", "small-padding-cell");

		dataProvider = new ListDataProvider<>(allMyTools);

		grid.setDataProvider(dataProvider);

		grid.addColumn(Tool::getName)
				.setHeader("Tool")
				.setAutoWidth(true)
				.setFlexGrow(2);

		grid.addColumn(tool -> {
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
				.setAutoWidth(true)
				.setFlexGrow(1);

		grid.addComponentColumn(tool -> {
					if (tool.getReservedUser() != null) {
						if (tool.getReservedUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {
							Button cancelReservationButton = UIUtils.createButton(VaadinIcon.CLOSE_CIRCLE, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_SMALL);
							cancelReservationButton.addClickListener(e -> cancelReservationOnClick(tool));
							UIUtils.setTooltip("Cancel reservation for this tool", cancelReservationButton);
							return cancelReservationButton;
						}
					}

					return new Span("");
				})
				.setFlexGrow(0)
				.setWidth("50px");

		grid.addComponentColumn(tool -> {
					if (tool.getReservedUser() != null && tool.getCurrentUser() == null) {
						if (tool.getReservedUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId())) {
							Button takeToolButton = UIUtils.createButton(VaadinIcon.HAND, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SMALL);
							takeToolButton.addClickListener(e -> takeToolOnClick(tool));
							UIUtils.setTooltip("Take this tool", takeToolButton);
							return takeToolButton;
						}
					}

					return new Span("");
				})
				.setFlexGrow(0)
				.setWidth("50px");

		grid.setSelectionMode(Grid.SelectionMode.MULTI);

		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		return grid;
	}

	private Div constructFooter() {
		Div footer = new Div();
		footer.addClassName(CLASS_NAME + "__footer");

		locationComboBox = new ComboBox<>("Return Location");
		locationComboBox.setRequired(true);
		locationComboBox.setItems(AuthenticationService.getCurrentSessionUser().getCompany().getLocations());
		locationComboBox.setItemLabelGenerator(Location::getName);
		locationComboBox.addValueChangeListener(e -> locationComboBox.setInvalid(false));

		footer.add(locationComboBox);

		returnToolButton = UIUtils.createButton("Return", ButtonVariant.LUMO_PRIMARY);
		returnToolButton.addClickListener(e -> returnToolOnClick());

		footer.add(returnToolButton);

		return footer;
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
				StringUtils.containsIgnoreCase(item.getUsageStatusString(), filter);
	}


	private void cancelReservationOnClick(Tool tool) {
		inventoryView.returnToolOnClick(Collections.singletonList(tool), null);

		dataProvider.getItems().remove(tool);
		dataProvider.refreshAll();

		dataChangeListener.onChange(dataProvider.getItems().size());
	}

	private void takeToolOnClick(Tool tool) {
		inventoryView.takeToolOnClick(tool, true);

		Tool toolFromDB = InventoryFacade.getInstance().getToolById(tool.getId());

		if (toolFromDB == null) {
			System.err.println("Error retrieving Tool from database");
			return;
		}

		inventoryView.copyToolParameters(tool, toolFromDB);
		tool.setUsageStatus(ToolUsageStatus.IN_USE);
		dataProvider.refreshItem(tool);
	}

	private void returnToolOnClick() {
		if (locationComboBox.getValue() == null) {
			locationComboBox.setInvalid(true);
			return;
		}
		List<Tool> toolsToReturn = new ArrayList<>(grid.getSelectedItems());

		toolsToReturn.removeIf(tool -> tool.getReservedUser() != null &&
				tool.getReservedUser().getId().equals(AuthenticationService.getCurrentSessionUser().getId()));

		inventoryView.returnToolOnClick(toolsToReturn, locationComboBox.getValue());

		for (Tool tool : toolsToReturn) {
			dataProvider.getItems().remove(tool);
		}

		dataProvider.refreshAll();

		dataChangeListener.onChange(dataProvider.getItems().size());
	}

	private void constructNoToolsMessage() {
		content = new Div();
		content.addClassName(CLASS_NAME+"__content");

		Span span = new Span("No Tools");
		span.addClassName("no-tools-span");

		content.add(span);

		this.setMinWidth("25vw");
		this.add(content);
	}
}
