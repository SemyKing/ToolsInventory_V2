package com.gmail.grigorij.ui.components.dialogs.pdf;

import com.gmail.grigorij.backend.database.entities.PDF_Template;
import com.gmail.grigorij.backend.database.entities.embeddable.PDF_Column;
import com.gmail.grigorij.backend.database.enums.WeekSelector;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.changes.Pair;
import com.gmail.grigorij.utils.changes.SimpleChangesTracker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@StyleSheet("context://styles/views/pdf-template.css")
public class PDF_TemplateView extends Div {

	private final static String CLASS_NAME = "pdf-template-view";

	private SimpleChangesTracker<PDF_Column> permissionChangesTracker;

	private PDF_Template template;

	private ListDataProvider<PDF_Column> dataProvider;
	private List<String> changes = new ArrayList<>();
	private static int counter;

	private TextArea normalTextArea;
	private String normalTextAreaOldValue;

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


	public PDF_TemplateView(PDF_Template template) {
		addClassName(CLASS_NAME);

		this.template = template;

		if (this.template == null) {
			this.template = new PDF_Template();
		}

		normalTextAreaOldValue = this.template.getNormalText();
		contrastTextAreaOldValue = this.template.getContrastText();
		normalFontSizeFieldOldValue = this.template.getNormalTextFontSize();
		contrastFontSizeFieldOldValue = this.template.getContrastTextFontSize();
		showDateCheckboxOldValue = this.template.isShowDate();
		weekSelectorComboBoxOldValue = this.template.getWeekSelector();
		dayOfWeekComboBoxOldValue = this.template.getDayOfWeek();


		add(constructContent());
	}


	private Div constructContent() {
		Div content = new Div();
		content.addClassName(CLASS_NAME + "__content");

		Button newColumnButton = UIUtils.createButton("Add Column", ButtonVariant.LUMO_PRIMARY);
		newColumnButton.addClickListener(e -> {
			addColumnOnClick();
		});

		content.add(newColumnButton);

		content.add(constructGrid());


		Div contentFooter = new Div();
		contentFooter.addClassName(CLASS_NAME + "__content-footer");

		normalTextArea = new TextArea("Normal Text");
		normalTextArea.setValue(template.getNormalText());
		contentFooter.add(normalTextArea);

		contrastTextArea = new TextArea("Contrast Text");
		contrastTextArea.setValue(template.getContrastText());
		contentFooter.add(contrastTextArea);

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

		Div fontSizeSelectorDiv = new Div();
		fontSizeSelectorDiv.addClassName(CLASS_NAME + "__font-selectors");
		fontSizeSelectorDiv.add(normalFontSizeField, contrastFontSizeField);
		contentFooter.add(fontSizeSelectorDiv);

		showDateCheckbox = new Checkbox("Show Date");
		contentFooter.add(showDateCheckbox);

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

		Div dateSelectorDiv = new Div();
		dateSelectorDiv.addClassName(CLASS_NAME + "__date-selectors");
		dateSelectorDiv.add(weekSelectorComboBox, dayOfWeekComboBox);
		contentFooter.add(dateSelectorDiv);

		showDateCheckbox.addValueChangeListener(e -> {
			weekSelectorComboBox.setReadOnly(!e.getValue());
			dayOfWeekComboBox.setReadOnly(!e.getValue());
		});
		showDateCheckbox.setValue(template.isShowDate());

		content.add(contentFooter);

		return content;
	}

	private Grid constructGrid() {
		Grid<PDF_Column> grid = new Grid<>();
		grid.addClassNames("grid-view", "pdf-columns-grid");

		dataProvider = new ListDataProvider<>(template.getPdfColumns());

		counter = 0;
		permissionChangesTracker = new SimpleChangesTracker<>();

		for (PDF_Column column : dataProvider.getItems()) {
			column.setCounter(counter);

			permissionChangesTracker.getChangesHashMap().put(counter, new Pair<>(new PDF_Column(column), new PDF_Column(column)));
			counter++;
		}

		grid.setDataProvider(dataProvider);

		grid.addColumn(PDF_Column::getParameterString)
				.setHeader("Parameter")
				.setAutoWidth(true)
				.setFlexGrow(1);

		grid.addColumn(PDF_Column::getColumnWidthString)
				.setHeader("Column Width")
				.setAutoWidth(true)
				.setFlexGrow(1);

		grid.addComponentColumn(column -> {
			Button editColumnButton = UIUtils.createIconButton(VaadinIcon.EDIT, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
			editColumnButton.addClickListener(editEvent -> {
				editColumnOnClick(column);
			});

			return editColumnButton;
		})
				.setTextAlign(ColumnTextAlign.CENTER)
				.setWidth("50px")
				.setFlexGrow(0);

		grid.addComponentColumn(column -> {
					Button removeColumnButton = UIUtils.createIconButton(VaadinIcon.TRASH, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
					removeColumnButton.addClickListener(removeEvent -> {
						removeColumnOnClick(column);
					});

					return removeColumnButton;
				})
				.setTextAlign(ColumnTextAlign.CENTER)
				.setWidth("50px")
				.setFlexGrow(0);

		return grid;
	}


	private void addColumnOnClick() {
		CustomDialog dialog = new CustomDialog();
		dialog.closeOnCancel();
		dialog.setHeader(UIUtils.createH3Label("Column Details"));

		PDF_ParameterLayout layout = new PDF_ParameterLayout(null);
		dialog.setContent(layout);

		dialog.getConfirmButton().setText("Save");
		dialog.getConfirmButton().addClickListener(e -> {
			PDF_Column column = layout.getColumn();
			if (column != null) {
				dataProvider.getItems().add(column);
				dataProvider.refreshAll();

				permissionChangesTracker.getChangesHashMap().put(counter, new Pair<>(null, new PDF_Column(column)));
				counter++;
				dialog.close();
			}
		});
		dialog.open();
	}

	private void editColumnOnClick(PDF_Column column) {
		CustomDialog dialog = new CustomDialog();
		dialog.closeOnCancel();
		dialog.setHeader(UIUtils.createH3Label("Column Details"));

		PDF_ParameterLayout layout = new PDF_ParameterLayout(column);
		dialog.setContent(layout);

		dialog.getConfirmButton().setText("Save");
		dialog.getConfirmButton().addClickListener(e -> {
			PDF_Column c = layout.getColumn();
			if (c != null) {
				column.setParameter(c.getParameter());
				column.setColumnWidth(c.getColumnWidth());
				dataProvider.refreshItem(column);

				permissionChangesTracker.getChangesHashMap().get(column.getCounter()).setObj2(c);
				dialog.close();
			}
		});
		dialog.open();
	}

	private void removeColumnOnClick(PDF_Column column) {
		permissionChangesTracker.getChangesHashMap().get(column.getCounter()).setObj2(null);

		dataProvider.getItems().remove(column);
		dataProvider.refreshAll();
	}

	private boolean isValid() {

		if (normalFontSizeField.isInvalid() || normalFontSizeField.isEmpty()) {
			normalFontSizeField.setInvalid(true);
			return false;
		}

		if (contrastFontSizeField.isInvalid() || contrastFontSizeField.isEmpty()) {
			contrastFontSizeField.setInvalid(true);
			return false;
		}

		if (showDateCheckbox.getValue()) {
			if (weekSelectorComboBox.getValue() == null || weekSelectorComboBox.isInvalid()) {
				weekSelectorComboBox.setInvalid(true);
				return false;
			}
			if (dayOfWeekComboBox.getValue() == null || dayOfWeekComboBox.isInvalid()) {
				dayOfWeekComboBox.setInvalid(true);
				return false;
			}
		}

		template.setNormalText(normalTextArea.getValue());
		template.setNormalTextFontSize(normalFontSizeField.getValue().floatValue());
		template.setContrastText(contrastTextArea.getValue());
		template.setContrastTextFontSize(contrastFontSizeField.getValue().floatValue());
		template.setShowDate(showDateCheckbox.getValue());
		template.setWeekSelector(weekSelectorComboBox.getValue());
		template.setDayOfWeek(dayOfWeekComboBox.getValue());
		return true;
	}


	public PDF_Template getTemplate() {
		if (isValid()) {
			return template;
		} else {
			return null;
		}
	}

	public List<String> getChanges() {
		if (changes == null) {
			return null;
		}

		for (PDF_Column column : template.getPdfColumns()) {
			permissionChangesTracker.getChangesHashMap().get(column.getCounter()).setObj2(column);
		}

		for (Integer i : permissionChangesTracker.getChangesHashMap().keySet()) {

			PDF_Column c1 = permissionChangesTracker.getChangesHashMap().get(i).getObj1();
			PDF_Column c2 = permissionChangesTracker.getChangesHashMap().get(i).getObj2();

			if (c1 == null) {
				changes.add("Added Column: " + c2.toString());
				continue;
			}

			if (c2 == null) {
				changes.add("Removed Column: " + c1.toString());
				continue;
			}

			String c1s = c1.toString();
			String c2s = c2.toString();

			if (!c1s.equals(c2s)) {
				changes.add("Column changed from:  '" + c1s + "'  to:  '" + c2s + "'");
			}
		}

		if (!normalTextAreaOldValue.equals(template.getNormalText())) {
			changes.add("Signature Text changed from:  '" + normalTextAreaOldValue + "'  to:  '" + template.getNormalText() + "'");
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

	public void setChanges(List<String> changes) {
		this.changes = changes;
	}
}
