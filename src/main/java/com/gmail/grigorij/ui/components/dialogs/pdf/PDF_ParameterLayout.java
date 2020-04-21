package com.gmail.grigorij.ui.components.dialogs.pdf;

import com.gmail.grigorij.backend.database.entities.embeddable.PDF_Column;
import com.gmail.grigorij.backend.database.enums.tools.ToolParameter;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.NumberField;

import java.util.EnumSet;

public class PDF_ParameterLayout extends Div {

	private final static String CLASS_NAME = "pdf-template-parameter-layout";

	private ComboBox<ToolParameter> parameterComboBox;
	private NumberField columnWidthField;


	PDF_ParameterLayout(PDF_Column column) {
		addClassName(CLASS_NAME);

		parameterComboBox = new ComboBox<>();
		parameterComboBox.setLabel("Parameter");
		parameterComboBox.setItems(EnumSet.allOf(ToolParameter.class));
		parameterComboBox.setItemLabelGenerator(ToolParameter::getName);
		parameterComboBox.setErrorMessage("Value Required");
		parameterComboBox.setRequired(true);
		if (column != null) {
			parameterComboBox.setValue(column.getParameter());
		}
		parameterComboBox.addValueChangeListener(e -> {
			parameterComboBox.setInvalid(false);

			if (e.getValue() != null) {
				columnWidthField.setValue((double)e.getValue().getPrefWidth());
			}
		});

		add(parameterComboBox);

		columnWidthField = new NumberField("Column Width");
		columnWidthField.setStep(1.0);
		columnWidthField.setMin(0);
		columnWidthField.setMax(10);
		columnWidthField.setHasControls(true);
		if (column != null) {
			columnWidthField.setValue(column.getColumnWidth().doubleValue());
		}

		add(columnWidthField);
	}

	private PDF_Column column;

	public PDF_Column getColumn() {
		column = new PDF_Column();

		if (isValid()) {
			return column;
		} else {
			return null;
		}
	}

	private boolean isValid() {
		if (parameterComboBox.getValue() == null || parameterComboBox.isInvalid()) {
			return false;
		} else {
			column.setParameter(parameterComboBox.getValue());
		}

		if (columnWidthField.getValue() == null || columnWidthField.isInvalid()) {
			return false;
		} else {
			column.setColumnWidth(columnWidthField.getValue().floatValue());
		}

		return true;
	}
}
