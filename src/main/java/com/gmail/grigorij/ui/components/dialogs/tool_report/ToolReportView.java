package com.gmail.grigorij.ui.components.dialogs.tool_report;

import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.enums.tools.ToolUsageStatus;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.views.app.InventoryView;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * View for reporting tool as broken, stolen, found...
 */

@StyleSheet("context://styles/views/tool-report.css")
public class ToolReportView extends Div {

	private static final String CLASS_NAME = "tool-report";

	private Tool tool;

	private TextField toolName;
	private ComboBox<ToolUsageStatus> toolStatusComboBox;
	private Button reportButton;


	public ToolReportView(Tool tool) {
		this.tool = tool;

		addClassName(CLASS_NAME);

		add(constructContent());
	}


	private Div constructContent() {
		Div content = new Div();
		content.addClassName(CLASS_NAME+"__content");

		toolName = new TextField();
		toolName.setReadOnly(true);

		if (tool != null) {
			toolName.setValue(tool.getName());
		}

		content.add(toolName);


		toolStatusComboBox = new ComboBox<>("Tool Status");
		toolStatusComboBox.setRequired(true);

		List<ToolUsageStatus> statuses = new ArrayList<>(EnumSet.allOf(ToolUsageStatus.class));
		statuses.removeIf(status -> status.getLevel().lowerThan(AuthenticationService.getCurrentSessionUser().getPermissionLevel()));

		toolStatusComboBox.setItems(statuses);
		toolStatusComboBox.setItemLabelGenerator(ToolUsageStatus::getName);

		if (tool != null) {
			toolStatusComboBox.setValue(tool.getUsageStatus());
		}

		reportButton = UIUtils.createButton("Report", ButtonVariant.LUMO_PRIMARY);

		content.add(reportButton);

		return content;
	}

	public ToolUsageStatus getToolStatus() {
		return toolStatusComboBox.getValue();
	}

	public boolean isValid() {

		if (toolStatusComboBox.getValue() == null || toolStatusComboBox.isInvalid()) {
			toolStatusComboBox.setInvalid(true);
			return false;
		}

		return true;
	}

	public Button getReportButton() {
		return reportButton;
	}
}
