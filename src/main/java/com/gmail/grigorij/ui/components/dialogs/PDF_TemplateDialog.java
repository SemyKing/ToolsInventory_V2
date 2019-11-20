package com.gmail.grigorij.ui.components.dialogs;

import com.gmail.grigorij.backend.database.entities.PDF_Template;
import com.gmail.grigorij.backend.database.entities.embeddable.PDF_Column;
import com.gmail.grigorij.backend.database.entities.embeddable.Permission;
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
import java.util.LinkedHashMap;
import java.util.List;


public class PDF_TemplateDialog extends CustomDialog {

	private final static String CLASS_NAME = "pdf_template-dialog";

	private FlexBoxLayout content;
	private List<ParameterRowLayout> parameterRows = new ArrayList<>();

	private TextArea signatureTextArea;
	private String signatureTextAreaOldValue;

	private TextArea contrastTextArea;
	private String contrastTextAreaOldValue;

	private NumberField normalFontSizeField;
	private Float normalFontSizeFieldOldValue;

	private NumberField contrastFontSizeField;
	private Float contrastFontSizeFieldOldValue;

	private Checkbox showDateCheckbox;
	private boolean showDateCheckboxOldValue;

	private ComboBox<WeekSelector> weekSelectorComboBox;
	private WeekSelector weekSelectorComboBoxOldValue;

	private ComboBox<DayOfWeek> dayOfWeekComboBox;
	private DayOfWeek dayOfWeekComboBoxOldValue;

	private PDF_Template template;

	private List<String> changes = new ArrayList<>();
	private LinkedHashMap<Integer, ColumnPair> templateChangesHashMap = new LinkedHashMap<>();

	private boolean isNew;


	public PDF_TemplateDialog(PDF_Template template) {
		isNew = false;

		if (template == null) {
			this.template = new PDF_Template();
			isNew = true;
		} else {
			this.template = new PDF_Template(template);
		}

		ParameterRowLayout.instanceCounter = 0;

		signatureTextAreaOldValue = this.template.getSignatureText();
		contrastTextAreaOldValue = this.template.getContrastText();
		normalFontSizeFieldOldValue = this.template.getNormalTextFontSize();
		contrastFontSizeFieldOldValue = this.template.getContrastTextFontSize();
		showDateCheckboxOldValue = this.template.isShowDate();
		weekSelectorComboBoxOldValue = this.template.getWeekSelector();
		dayOfWeekComboBoxOldValue = this.template.getDayOfWeek();

		setCloseOnEsc(false);
		setCloseOnOutsideClick(false);

		getContent().add(constructContent());

		getCancelButton().addClickListener(cancelEditOnClick -> {
			ConfirmDialog confirmDialog = new ConfirmDialog();
			confirmDialog.setMessage("Are you sure you want to cancel?" + ProjectConstants.NEW_LINE + "All changes will be lost");
			confirmDialog.closeOnCancel();
			confirmDialog.getConfirmButton().addClickListener(confirmOnClick -> {
				changes = null;
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
		newColumnButton.addClickListener(e -> {
			addRow(constructParameterRow(null));
		});


		contentWrapper.add(newColumnButton);

		signatureTextArea = new TextArea("Signature Text");
		signatureTextArea.setValue(template.getSignatureText());
		contentWrapper.add(signatureTextArea);

		contrastTextArea = new TextArea("Contrast Text");
		contrastTextArea.setValue(template.getContrastText());
		contentWrapper.add(contrastTextArea);

		normalFontSizeField = new NumberField("Normal Font Size");
		normalFontSizeField.setStep(1.0);
		normalFontSizeField.setMin(1);
		normalFontSizeField.setMax(50);
		normalFontSizeField.setHasControls(true);
		normalFontSizeField.setValue((double) template.getNormalTextFontSize());

		contrastFontSizeField = new NumberField("Contrast Font Size");
		contrastFontSizeField.setStep(1.0);
		contrastFontSizeField.setMin(1);
		contrastFontSizeField.setMax(50);
		contrastFontSizeField.setHasControls(true);
		contrastFontSizeField.setValue((double) template.getContrastTextFontSize());

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
		weekSelectorComboBox.setValue(template.getWeekSelector());
		weekSelectorComboBox.setRequired(true);
		weekSelectorComboBox.setReadOnly(true);

		dayOfWeekComboBox = new ComboBox<>();
		dayOfWeekComboBox.addClassName(ProjectConstants.NO_PADDING_TOP);
		dayOfWeekComboBox.setLabel("Day of Week");
		dayOfWeekComboBox.setItems(EnumSet.allOf(DayOfWeek.class));
		dayOfWeekComboBox.setValue(template.getDayOfWeek());
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
		showDateCheckbox.setValue(template.isShowDate());

		return contentWrapper;
	}

	private ParameterRowLayout constructParameterRow(PDF_Column column) {
		ParameterRowLayout parameterRow = new ParameterRowLayout();

		if (column != null) {
			parameterRow.getParameterComboBox().setValue(column.getParameter());
			parameterRow.getColumnWidthField().setValue((double) column.getUserSetWidth());

			templateChangesHashMap.put(ParameterRowLayout.instanceCounter,
					new ColumnPair(new PDF_Column(column), new PDF_Column(column)));
		} else {
			templateChangesHashMap.put(ParameterRowLayout.instanceCounter, new PDF_TemplateDialog.ColumnPair(null, null));
		}
		ParameterRowLayout.instanceCounter++;

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
			templateChangesHashMap.get(parameterRow.getCounter()).setC2(null);

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
		for (PDF_Column column : template.getPdfColumns()) {
			addRow(constructParameterRow(column));
		}
	}

	public PDF_Template getTemplate() {
		if (validate()) {
			return template;
		} else {
			return null;
		}
	}

	public List<String> getChanges() {
		if (changes == null) {
			return null;
		}

		for (Integer i : templateChangesHashMap.keySet()) {

			PDF_Column c1 = templateChangesHashMap.get(i).getC1();
			PDF_Column c2 = templateChangesHashMap.get(i).getC2();

			if (c1 == null) {
				changes.add("Added Column: " + getColumnString(c2));
				continue;
			}

			if (c2 == null) {
				changes.add("Removed Column: " + getColumnString(c1));
				continue;
			}

			String c1s = getColumnString(c1);
			String c2s = getColumnString(c2);

			if (!c1s.equals(c2s)) {
				changes.add("Column changed from:  '" + c1s + "'  to:  '" + c2s + "'");
			}
		}

		if (!signatureTextAreaOldValue.equals(template.getSignatureText())) {
			changes.add("Signature Text changed from:  '" + signatureTextAreaOldValue + "'  to:  '" + template.getSignatureText() + "'");
		}
		if (!contrastTextAreaOldValue.equals(template.getContrastText())) {
			changes.add("Contrast Text changed from:  '" + contrastTextAreaOldValue + "'  to:  '" + template.getContrastText() + "'");
		}
		if (!normalFontSizeFieldOldValue.equals(template.getNormalTextFontSize())) {
			changes.add("Normal Font Size changed from:  '" + normalFontSizeFieldOldValue + "'  to:  '" + template.getNormalTextFontSize() + "'");
		}
		if (!contrastFontSizeFieldOldValue.equals(template.getContrastTextFontSize())) {
			changes.add("Contrast Font Size changed from:  '" + contrastFontSizeFieldOldValue + "'  to:  '" + template.getContrastTextFontSize() + "'");
		}
		if (!showDateCheckboxOldValue == (template.isShowDate())) {
			changes.add("Show Date changed from:  '" + showDateCheckboxOldValue + "'  to:  '" + template.isShowDate() + "'");
		}
		if (!weekSelectorComboBoxOldValue.equals(template.getWeekSelector())) {
			changes.add("Week Selector changed from:  '" + weekSelectorComboBoxOldValue + "'  to:  '" + template.getWeekSelector() + "'");
		}
		if (!dayOfWeekComboBoxOldValue.equals(template.getDayOfWeek())) {
			changes.add("Day of Week changed from:  '" + dayOfWeekComboBoxOldValue + "'  to:  '" + template.getDayOfWeek() + "'");
		}

		return changes;
	}

	public boolean isNew() {
		return isNew;
	}


	private boolean validate() {
		List<PDF_Column> columns = new ArrayList<>();

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

			PDF_Column column = new PDF_Column();
			column.setParameter(row.getParameterComboBox().getValue());
			column.setUserSetWidth(row.getColumnWidthField().getValue().floatValue());

			columns.add(column);

			templateChangesHashMap.get(row.getCounter()).setC2(column);
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

		template.getPdfColumns().clear();
		template.getPdfColumns().addAll(columns);

		template.setSignatureText(signatureTextArea.getValue());
		template.setContrastText(contrastTextArea.getValue());
		template.setNormalTextFontSize(normalFontSizeField.getValue().floatValue());
		template.setContrastTextFontSize(contrastFontSizeField.getValue().floatValue());
		template.setShowDate(showDateCheckbox.getValue());
		template.setWeekSelector(weekSelectorComboBox.getValue());
		template.setDayOfWeek(dayOfWeekComboBox.getValue());
		return true;
	}

	private String getColumnString(PDF_Column c) {
		String cs = "";

		if (c != null) {
			cs = c.getParameter().getName() + ", " + c.getUserSetWidth();
		}

		return cs;
	}


	private static class ParameterRowLayout extends Div {

		private final static String CLASS_NAME = "tool-parameter-row";

		private ComboBox<ToolParameter> parameterComboBox;
		private NumberField columnWidthField;
		private Button deleteButton;

		private static int instanceCounter = 0;
		private int counter;


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

			counter = instanceCounter;
//			instanceCounter++;
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

		public int getCounter() {
			return counter;
		}
	}

	private static class ColumnPair {

		private PDF_Column c1;
		private PDF_Column c2;

		ColumnPair(PDF_Column p1, PDF_Column p2) {
			this.c1 = p1;
			this.c2 = p2;
		}

		private PDF_Column getC1() {
			return c1;
		}
		private void setC1(PDF_Column c1) {
			this.c1 = c1;
		}

		private PDF_Column getC2() {
			return c2;
		}
		private void setC2(PDF_Column c2) {
			this.c2 = c2;
		}

		private boolean isNull() {
			return this.c1 == null && this.c2 == null;
		}
	}
}
