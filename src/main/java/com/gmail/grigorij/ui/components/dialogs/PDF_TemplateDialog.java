package com.gmail.grigorij.ui.components.dialogs;

import com.gmail.grigorij.backend.database.entities.PDF_Template;
import com.gmail.grigorij.backend.database.enums.WeekSelector;
import com.gmail.grigorij.backend.database.enums.tools.ToolParameter;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


public class PDF_TemplateDialog extends CustomDialog {

	private final static String CLASS_NAME = "reporting-template-dialog";

	private FlexBoxLayout content;
	private List<ParameterRowLayout> parameterRows = new ArrayList<>();

	private TextArea signatureTextArea;
	private TextArea contrastTextArea;

	private NumberField normalFontSizeField;
	private NumberField contrastFontSizeField;

	private Checkbox showDateCheckbox;
	private ComboBox<WeekSelector> weekSelectorComboBox;
	private ComboBox<DayOfWeek> dayOfWeekComboBox;


	private PDF_Template originalTemplate;
	private List<PDF_Template.PDF_Column> originalColumns = new ArrayList<>();

	private PDF_Template editedTemplate;

	private List<String> changes = new ArrayList<>();


	public PDF_TemplateDialog(PDF_Template originalTemplate) {
		this.originalTemplate = originalTemplate;

		for (PDF_Template.PDF_Column column : originalTemplate.getPdfColumns()) {
			originalColumns.add(new PDF_Template.PDF_Column(column));
		}

		editedTemplate = new PDF_Template(this.originalTemplate);
		editedTemplate.setId(originalTemplate.getId());

		setCloseOnEsc(false);
		setCloseOnOutsideClick(false);

		getContent().add(constructContent());

		getCancelButton().addClickListener(cancelEditOnClick -> {
			ConfirmDialog confirmDialog = new ConfirmDialog();
			confirmDialog.setMessage("Are you sure you want to cancel?" + ProjectConstants.NEW_LINE + "All changes will be lost");
			confirmDialog.closeOnCancel();
			confirmDialog.getConfirmButton().addClickListener(confirmOnClick -> {
				confirmDialog.close();
				this.close();
			});
			confirmDialog.open();
		});
		getConfirmButton().setText("Save");
	}


	private Div constructContent() {
		Div contentWrapper = new Div();
		contentWrapper.addClassName(CLASS_NAME + "__wrapper");

		content = new FlexBoxLayout();
		content.addClassName(CLASS_NAME + "__content");

		contentWrapper.add(content);

		Button newColumnButton = UIUtils.createButton("ADD COLUMN", ButtonVariant.LUMO_PRIMARY);
		newColumnButton.addClickListener(e -> addRow(constructParameterRow(null)));
		contentWrapper.add(newColumnButton);

		signatureTextArea = new TextArea("Signature Text");
		signatureTextArea.setValue(editedTemplate.getSignatureText());
		contentWrapper.add(signatureTextArea);

		contrastTextArea = new TextArea("Contrast Text");
		contrastTextArea.setValue(editedTemplate.getContrastText());
		contentWrapper.add(contrastTextArea);

		normalFontSizeField = new NumberField("Normal Font Size");
		normalFontSizeField.setStep(1.0);
		normalFontSizeField.setMin(1);
		normalFontSizeField.setMax(50);
		normalFontSizeField.setHasControls(true);
		normalFontSizeField.setValue((double) editedTemplate.getNormalTextFontSize());

		contrastFontSizeField = new NumberField("Contrast Font Size");
		contrastFontSizeField.setStep(1.0);
		contrastFontSizeField.setMin(1);
		contrastFontSizeField.setMax(50);
		contrastFontSizeField.setHasControls(true);
		contrastFontSizeField.setValue((double) editedTemplate.getContrastTextFontSize());

		FlexBoxLayout fontSizeSelectorDiv = new FlexBoxLayout();
		fontSizeSelectorDiv.setFlexDirection(FlexDirection.ROW);
		fontSizeSelectorDiv.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		fontSizeSelectorDiv.add(normalFontSizeField, contrastFontSizeField);
		contentWrapper.add(fontSizeSelectorDiv);

		showDateCheckbox = new Checkbox("Show Date");
		contentWrapper.add(showDateCheckbox);

		weekSelectorComboBox = new ComboBox<>();
		weekSelectorComboBox.addClassName(ProjectConstants.NO_PADDING_TOP);
		weekSelectorComboBox.setLabel("Week Selector");
		weekSelectorComboBox.setItems(EnumSet.allOf(WeekSelector.class));
		weekSelectorComboBox.setItemLabelGenerator(WeekSelector::getName);
		weekSelectorComboBox.setValue(editedTemplate.getWeekSelector());
		weekSelectorComboBox.setRequired(true);
		weekSelectorComboBox.setReadOnly(true);

		dayOfWeekComboBox = new ComboBox<>();
		dayOfWeekComboBox.addClassName(ProjectConstants.NO_PADDING_TOP);
		dayOfWeekComboBox.setLabel("Day of Week");
		dayOfWeekComboBox.setItems(EnumSet.allOf(DayOfWeek.class));
		dayOfWeekComboBox.setValue(editedTemplate.getDayOfWeek());
		dayOfWeekComboBox.setRequired(true);
		dayOfWeekComboBox.setReadOnly(true);

		FlexBoxLayout dateSelectorDiv = new FlexBoxLayout();
		dateSelectorDiv.setFlexDirection(FlexDirection.ROW);
		dateSelectorDiv.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		dateSelectorDiv.add(weekSelectorComboBox, dayOfWeekComboBox);
		contentWrapper.add(dateSelectorDiv);

		showDateCheckbox.addValueChangeListener(e -> {
			weekSelectorComboBox.setReadOnly(!e.getValue());
			dayOfWeekComboBox.setReadOnly(!e.getValue());
		});
		showDateCheckbox.setValue(editedTemplate.isShowDate());

		return contentWrapper;
	}

	private ParameterRowLayout constructParameterRow(PDF_Template.PDF_Column column) {
		ParameterRowLayout parameterRow = new ParameterRowLayout();

		if (column != null) {
			parameterRow.getParameterComboBox().setValue(column.getParameter());
			parameterRow.getColumnWidthField().setValue((double) column.getUserSetWidth());
		}

		parameterRow.getParameterComboBox().addValueChangeListener(e -> {
			if (e.getValue() != null) {
				try {
					parameterRow.getColumnWidthField().setValue((double) e.getValue().getPrefWidth());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		parameterRow.getDeleteButton().addClickListener(e -> {
			if (parameterRow.getParameterComboBox().getValue() != null) {
				changes.add("Removed column: " + parameterRow.getParameterComboBox().getValue().getName());
			}

			parameterRows.remove(parameterRow);
			content.remove(parameterRow);
		});

		return parameterRow;
	}

	private void addRow(ParameterRowLayout row) {
		if (parameterRows.size() > 0) {
			row.getParameterComboBox().setLabel("");
			row.getColumnWidthField().setLabel("");
		}

		content.add(row);
		parameterRows.add(row);
	}


	public void constructView() {
		for (PDF_Template.PDF_Column column : editedTemplate.getPdfColumns()) {
			addRow(constructParameterRow(column));
		}
	}

	public PDF_Template getTemplate() {
		if (validate()) {
			return editedTemplate;
		} else {
			return null;
		}
	}

	public List<String> getChanges() {
		List<PDF_Template.PDF_Column> edited = new ArrayList<>(editedTemplate.getPdfColumns());

		int columnCounter = 1;

		// NEW OR ADDED COLUMNS
		if (edited.size() > originalColumns.size()) {
			if (originalColumns.size() == 0) {
				for (PDF_Template.PDF_Column reportingColumn : edited) {
					String change = compareColumns(columnCounter, null, reportingColumn);

					if (change.length() > 0) {
						changes.add(change);
					}

					columnCounter++;
				}
			} else {
				for (int i = 0; i < originalColumns.size(); i++) {
					String change = compareColumns(columnCounter, originalColumns.get(i), edited.get(i));

					if (change.length() > 0) {
						changes.add(change);
					}

					columnCounter++;
				}

				for (int i = originalColumns.size(); i < edited.size(); i++) {
					if (originalColumns.size() > 0) {
						String change = compareColumns(columnCounter, null, edited.get(i));

						if (change.length() > 0) {
							changes.add(change);
						}
					}

					columnCounter++;
				}
			}

		// REMOVED COLUMNS
		} else if (originalColumns.size() > edited.size()) {
			if (edited.size() == 0) {
				for (PDF_Template.PDF_Column reportingColumn : originalColumns) {
					String change = compareColumns(columnCounter, reportingColumn, null);

					if (change.length() > 0) {
						changes.add(change);
					}

					columnCounter++;
				}
			} else {
				for (int i = 0; i < edited.size(); i++) {
					String change = compareColumns(columnCounter, originalColumns.get(i), edited.get(i));

					if (change.length() > 0) {
						changes.add(change);
					}

					columnCounter++;
				}

				for (int i = edited.size(); i < originalColumns.size(); i++) {
					if (edited.size() > 0) {
						String change = compareColumns(columnCounter, originalColumns.get(i), null);

						if (change.length() > 0) {
							changes.add(change);
						}
					}

					columnCounter++;
				}
			}

		// COLUMNS SIZE NOT CHANGED
		} else {
			for (int i = 0; i < originalColumns.size(); i++) {
				String change = compareColumns(columnCounter, originalColumns.get(i), edited.get(i));

				if (change.length() > 0) {
					changes.add(change);
				}

				columnCounter++;
			}
		}

		if (!originalTemplate.getSignatureText().equals(editedTemplate.getSignatureText())) {
			changes.add("Signature changed from: '" + originalTemplate.getSignatureText() +
					"',  to:  " + editedTemplate.getSignatureText() + "'");
		}
		if (!originalTemplate.getContrastText().equals(editedTemplate.getContrastText())) {
			changes.add("Return text changed from: '" + originalTemplate.getContrastText() +
					"',  to:  " + editedTemplate.getContrastText() + "'");
		}
		if (!originalTemplate.isShowDate() == (editedTemplate.isShowDate())) {
			changes.add("Show date changed from: '" + originalTemplate.isShowDate() +
					"',  to:  " + editedTemplate.isShowDate() + "'");
		}
		if (!originalTemplate.getWeekSelector().equals(editedTemplate.getWeekSelector())) {
			changes.add("Week selector changed from: '" + originalTemplate.getWeekSelector() +
					"',  to:  '" + editedTemplate.getWeekSelector() + "'");
		}
		if (!originalTemplate.getDayOfWeek().equals(editedTemplate.getDayOfWeek())) {
			changes.add("Day of week changed from: '" + originalTemplate.getDayOfWeek() +
					"',  to:  '" + editedTemplate.getDayOfWeek() + "'");
		}

		return changes;
	}


	private boolean validate() {
		List<PDF_Template.PDF_Column> columns = new ArrayList<>();

		for (ParameterRowLayout row : parameterRows) {

			if (row.getParameterComboBox().getValue() == null) {
				row.getParameterComboBox().setInvalid(true);
				return false;
			}

			if (row.getColumnWidthField().getValue() == null) {
				row.getColumnWidthField().setValue(-1.0);
			} else {
				if (row.getColumnWidthField().isInvalid()) {
					return false;
				}
			}

			PDF_Template.PDF_Column column = new PDF_Template.PDF_Column();
			column.setParameter(row.getParameterComboBox().getValue());
			column.setUserSetWidth(row.getColumnWidthField().getValue().floatValue());

			columns.add(column);
		}

		if (normalFontSizeField.getValue() == null || normalFontSizeField.isInvalid()) {
			return false;
		}
		if (contrastFontSizeField.getValue() == null || contrastFontSizeField.isInvalid()) {
			return false;
		}


		if (weekSelectorComboBox.getValue() == null || weekSelectorComboBox.isInvalid()) {
			return false;
		}
		if (dayOfWeekComboBox.getValue() == null || dayOfWeekComboBox.isInvalid()) {
			return false;
		}

		editedTemplate.getPdfColumns().clear();
		editedTemplate.getPdfColumns().addAll(columns);

		editedTemplate.setSignatureText(signatureTextArea.getValue());
		editedTemplate.setContrastText(contrastTextArea.getValue());
		editedTemplate.setNormalTextFontSize(normalFontSizeField.getValue().floatValue());
		editedTemplate.setNormalTextFontSize(contrastFontSizeField.getValue().floatValue());
		editedTemplate.setShowDate(showDateCheckbox.getValue());
		editedTemplate.setWeekSelector(weekSelectorComboBox.getValue());
		editedTemplate.setDayOfWeek(dayOfWeekComboBox.getValue());
		return true;
	}

	private String compareColumns(int columnNumber, PDF_Template.PDF_Column c1, PDF_Template.PDF_Column c2) {
		String change = "";

		if (c1 == null) {
			change = "New Column "+columnNumber+": '" + c2.getParameter().getName() + "', width: " + c2.getUserSetWidth();
		} else {
			if (c2 == null) {
				change = "Removed Column "+columnNumber+": '" + c1.getParameter().getName();
			} else {
				if (!(c1.getParameter().getName().equals(c2.getParameter().getName())) || !(c1.getUserSetWidth().equals(c2.getUserSetWidth()))) {
					change = "Column "+columnNumber+", changed from: '" + c1.getParameter().getName() + "', width: '" + c1.getUserSetWidth()+"',  to:  " +
							c2.getParameter().getName() + "', width: '" + c2.getUserSetWidth();
				}
			}
		}

		return change;
	}


	private static class ParameterRowLayout extends Div {

		private final static String CLASS_NAME = "tool-parameter-row";

		private ComboBox<ToolParameter> parameterComboBox;
		private NumberField columnWidthField;
		private Button deleteButton;


		ParameterRowLayout() {
			addClassName(CLASS_NAME);

			parameterComboBox = new ComboBox<>();
			parameterComboBox.setLabel("Parameter");
			parameterComboBox.setItems(EnumSet.allOf(ToolParameter.class));
			parameterComboBox.setItemLabelGenerator(ToolParameter::getName);
			parameterComboBox.setErrorMessage("Value Required");
			parameterComboBox.setRequired(true);
			parameterComboBox.addValueChangeListener(e -> {
				parameterComboBox.setInvalid(false);
			});

			columnWidthField = new NumberField("Column Width");
			columnWidthField.setStep(1.0);
			columnWidthField.setMin(-1);
			columnWidthField.setMax(10);
			columnWidthField.setHasControls(true);

			deleteButton = UIUtils.createIconButton(VaadinIcon.TRASH, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

			add(parameterComboBox);
			add(columnWidthField);
			add(deleteButton);
		}


		private ComboBox<ToolParameter> getParameterComboBox() {
			return parameterComboBox;
		}

		private NumberField getColumnWidthField() {
			return columnWidthField;
		}

		private Button getDeleteButton() {
			return deleteButton;
		}
	}
}
