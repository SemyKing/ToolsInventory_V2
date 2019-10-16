package com.gmail.grigorij.ui.components.forms.editable;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.inventory.ToolUsageStatus;
import com.gmail.grigorij.ui.application.views.admin.AdminInventory;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.camera.CameraView;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.gmail.grigorij.utils.OperationStatus;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;


public class ToolForm extends FormLayout {

	private final String CLASS_NAME = "form";

	private final AdminInventory adminInventory;

	private Binder<InventoryItem> binder;
	private InventoryItem tool;
	private boolean isNew;

	private boolean cameraActive = false;

	// FORM ITEMS
	private Div entityStatusDiv;
	private Checkbox entityStatusCheckbox;
	private TextField nameField;
	private TextField barcode;
	private FlexBoxLayout barcodeLayout;
	private TextField snCode;
	private TextField toolInfo;
	private TextField manufacturer;
	private TextField model;
	private ComboBox<Company> companyComboBox;
	private ComboBox<InventoryItem> categoryComboBox;
	private FlexBoxLayout categoryLayout;
	private ComboBox<ToolUsageStatus> toolUsageStatusComboBox;
	private ComboBox<User> toolCurrentUserComboBox;
	private ComboBox<User> toolReservedByUserComboBox;
	private Div toolUsageDiv;
	private TextField priceField;
	private TextField guaranteeField;
	private Div priceGuaranteeDiv;
	private DatePicker dateBought;
	private DatePicker dateNextMaintenance;
	private Div datesDiv;
	private TextArea additionalInfo;


	public ToolForm(AdminInventory adminInventory) {
		this.adminInventory = adminInventory;

		addClassName(CLASS_NAME);

		constructFormItems();

		constructForm();

		constructBinder();
	}


	private void constructFormItems() {
		entityStatusCheckbox = new Checkbox("Deleted");

		entityStatusDiv = new Div();
		entityStatusDiv.addClassName(ProjectConstants.CONTAINER_ALIGN_CENTER);
		entityStatusDiv.add(entityStatusCheckbox);

		setColspan(entityStatusDiv, 2);

		nameField = new TextField("Name");
		nameField.setRequired(true);

		barcode = new TextField("Barcode");
		barcode.setPrefixComponent(VaadinIcon.BARCODE.create());

		Button scanBarcodeButton = UIUtils.createIconButton(VaadinIcon.CAMERA, ButtonVariant.LUMO_CONTRAST);
		scanBarcodeButton.addClickListener(e -> constructCodeScanDialog(barcode));
		UIUtils.setTooltip("Scan Barcode with camera", scanBarcodeButton);

		barcodeLayout = new FlexBoxLayout();
		barcodeLayout.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		barcodeLayout.add(barcode, scanBarcodeButton);
		barcodeLayout.setFlexGrow("1", barcode);
		barcodeLayout.setComponentMargin(barcode, Right.S);


		snCode = new TextField("SN");
		toolInfo = new TextField("Tool Info");
		manufacturer = new TextField("Manufacturer");
		model = new TextField("Model");


		companyComboBox = new ComboBox<>();
		companyComboBox.setItems(CompanyFacade.getInstance().getAllCompanies());
		companyComboBox.setItemLabelGenerator(Company::getName);
		companyComboBox.setLabel("Company (Owner)");
		companyComboBox.setRequired(true);
		companyComboBox.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					updateComboBoxes(e.getValue());
				}
			}
		});


		categoryComboBox = new ComboBox<>();
		categoryComboBox.setItems();
		categoryComboBox.setLabel("Parent Category");
		categoryComboBox.setItemLabelGenerator(InventoryItem::getName);
		categoryComboBox.setRequired(true);

		Button editCategoryButton = UIUtils.createIconButton(VaadinIcon.EDIT, ButtonVariant.LUMO_CONTRAST);
		editCategoryButton.addClickListener(e -> {
			InventoryItem selectedCategory = categoryComboBox.getValue();
			if (selectedCategory != null) {
				if (!selectedCategory.equals(InventoryFacade.getInstance().getRootCategory())) {
					adminInventory.constructCategoryDialog(selectedCategory);
				}
			}
		});
		UIUtils.setTooltip("Edit selected category", editCategoryButton);


		categoryLayout = new FlexBoxLayout();
		categoryLayout.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		categoryLayout.add(categoryComboBox, editCategoryButton);
		categoryLayout.setFlexGrow("1", categoryComboBox);
		categoryLayout.setComponentMargin(categoryComboBox, Right.S);


		toolUsageStatusComboBox = new ComboBox<>();
		toolUsageStatusComboBox.setLabel("Usage Status");
		toolUsageStatusComboBox.setWidth("calc(33% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		toolUsageStatusComboBox.setItems(EnumSet.allOf(ToolUsageStatus.class));
		toolUsageStatusComboBox.setItemLabelGenerator(ToolUsageStatus::getStringValue);


		toolCurrentUserComboBox = new ComboBox<>();
		toolCurrentUserComboBox.setLabel("Current User");
		toolCurrentUserComboBox.setWidth("calc(33% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		toolCurrentUserComboBox.setErrorMessage("User Required");
		toolCurrentUserComboBox.setItems();
		toolCurrentUserComboBox.setItemLabelGenerator(User::getUsername);
		toolCurrentUserComboBox.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				toolCurrentUserComboBox.setInvalid(false);
			}
		});

		toolReservedByUserComboBox = new ComboBox<>();
		toolReservedByUserComboBox.setLabel("Reserved By User");
		toolReservedByUserComboBox.setWidth("calc(33% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		toolReservedByUserComboBox.setErrorMessage("User Required");
		toolReservedByUserComboBox.setItems();
		toolReservedByUserComboBox.setItemLabelGenerator(User::getUsername);
		toolReservedByUserComboBox.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				toolCurrentUserComboBox.setInvalid(false);
			}
		});

		toolUsageDiv = new Div();
		toolUsageDiv.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		toolUsageDiv.add(toolUsageStatusComboBox, toolCurrentUserComboBox, toolReservedByUserComboBox);

		setColspan(toolUsageDiv, 2);

		dateBought = new DatePicker("Bought Date");
		dateBought.setLocale(new Locale("fi"));
		dateBought.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");

		dateNextMaintenance = new DatePicker("Next Maintenance");
		dateNextMaintenance.setLocale(new Locale("fi"));
		dateNextMaintenance.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");

		datesDiv = new Div();
		datesDiv.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		datesDiv.add(dateBought);
		datesDiv.add(dateNextMaintenance);


		priceField = new TextField("Price");
		priceField.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		priceField.setSuffixComponent(new Span("€"));
		priceField.getElement().setAttribute("type", "number");

		guaranteeField = new TextField("Guarantee");
		guaranteeField.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		guaranteeField.setSuffixComponent(new Span("Months"));
		guaranteeField.getElement().setAttribute("type", "number");

		priceGuaranteeDiv = new Div();
		priceGuaranteeDiv.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		priceGuaranteeDiv.add(priceField);
		priceGuaranteeDiv.add(guaranteeField);


		additionalInfo = new TextArea("Additional Info");
		additionalInfo.setMaxHeight("200px");

		setColspan(additionalInfo, 2);
	}

	private void constructForm() {
//		addClassNames(LumoStyles.Padding.Vertical.S, LumoStyles.Padding.Left.M, LumoStyles.Padding.Right.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));


		add(entityStatusDiv);
		add(nameField);
		add(barcodeLayout);
		add(snCode);
		add(toolInfo);
		add(manufacturer);
		add(model);

		Hr hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		add(companyComboBox);
		add(categoryLayout);
		add(toolUsageDiv);

		hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		add(datesDiv);
		add(priceGuaranteeDiv);

		add(additionalInfo);
	}

	private void constructBinder() {
		binder = new Binder<>(InventoryItem.class);

		binder.forField(entityStatusCheckbox)
				.bind(InventoryItem::isDeleted, InventoryItem::setDeleted);

		binder.forField(nameField)
				.asRequired("Name is required")
				.bind(InventoryItem::getName, InventoryItem::setName);

		binder.forField(barcode)
				.bind(InventoryItem::getBarcode, InventoryItem::setBarcode);

		binder.forField(snCode)
				.bind(InventoryItem::getSnCode, InventoryItem::setSnCode);

		binder.forField(toolInfo)
				.bind(InventoryItem::getToolInfo, InventoryItem::setToolInfo);

		binder.forField(manufacturer)
				.bind(InventoryItem::getManufacturer, InventoryItem::setManufacturer);

		binder.forField(model)
				.bind(InventoryItem::getModel, InventoryItem::setModel);

		binder.forField(companyComboBox)
				.asRequired("Company is required")
				.bind(InventoryItem::getCompany, InventoryItem::setCompany);

		binder.forField(categoryComboBox)
				.asRequired("Category is required")
				.bind(InventoryItem::getParentCategory, InventoryItem::setParentCategory);

		binder.forField(toolUsageStatusComboBox)
				.asRequired("Usage Status is required")
				.bind(InventoryItem::getToolUsageStatus, InventoryItem::setToolUsageStatus);

		binder.forField(toolCurrentUserComboBox)
				.bind(InventoryItem::getInUseByUser, InventoryItem::setInUseByUser);

		binder.forField(toolReservedByUserComboBox)
				.bind(InventoryItem::getReservedByUser, InventoryItem::setReservedByUser);

		binder.forField(priceField)
				.withConverter(new StringToDoubleConverter("Price must be a number"))
				.withNullRepresentation(0.00)
				.bind(InventoryItem::getPrice, InventoryItem::setPrice);

		binder.forField(guaranteeField)
				.withConverter(new StringToIntegerConverter("Guarantee must be a number"))
				.withNullRepresentation(0)
				.bind(InventoryItem::getGuarantee_months, InventoryItem::setGuarantee_months);

		binder.forField(dateBought)
				.bind(InventoryItem::getDateBought, InventoryItem::setDateBought);

		binder.forField(dateNextMaintenance)
				.bind(InventoryItem::getDateNextMaintenance, InventoryItem::setDateNextMaintenance);

		binder.forField(additionalInfo)
				.bind(InventoryItem::getAdditionalInfo, InventoryItem::setAdditionalInfo);
	}


	private void initDynamicFormItems() {
		updateComboBoxes(tool.getCompany());
	}

	private void updateComboBoxes(Company company) {
		if (company != null) {

			categoryComboBox.setValue(null);
			toolCurrentUserComboBox.setValue(null);
			toolReservedByUserComboBox.setValue(null);

			List<InventoryItem> categories = InventoryFacade.getInstance().getAllCategoriesInCompany(company.getId());
			categories.add(0, InventoryFacade.getInstance().getRootCategory());

			categoryComboBox.setItems(categories);

			toolCurrentUserComboBox.setItems(UserFacade.getInstance().getUsersInCompany(company.getId()));
			toolReservedByUserComboBox.setItems(UserFacade.getInstance().getUsersInCompany(company.getId()));
		}
	}

	private void constructCodeScanDialog(TextField codeField) {

		CameraView cameraView = new CameraView();
		cameraView.addClickListener(imageClickEvent -> {
			if (cameraActive) {
				cameraView.takePicture();
			} else {
				cameraView.showPreview();
				cameraActive = true;
			}
		});


		CustomDialog dialog = new CustomDialog();
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);

		dialog.setHeader(UIUtils.createH3Label("Code Scanner"));

		dialog.setContent(cameraView);

		dialog.getCancelButton().addClickListener(e -> {
			cameraView.stop();
			dialog.close();
		});

		dialog.getFooter().remove(dialog.getConfirmButton());

		dialog.open();


		UIUtils.showNotification("Click on Image to take a picture", UIUtils.NotificationType.INFO);

		cameraView.showPreview();
		cameraActive = true;


		cameraView.onFinished(new OperationStatus() {
			@Override
			public void onSuccess(String msg, UIUtils.NotificationType type) {
				if (UI.getCurrent() != null) {
					UI.getCurrent().access(() -> {
						UIUtils.showNotification("Code scanned successfully", type, 2000);

						codeField.setValue(msg);
						cameraView.stop();
						cameraActive = false;

						UI.getCurrent().push();
					});

				}
			}

			@Override
			public void onFail(String msg, UIUtils.NotificationType type) {
				if (UI.getCurrent() != null) {
					UI.getCurrent().access(() -> {
						UIUtils.showNotification("Code not found", UIUtils.NotificationType.INFO, 2000);
						UI.getCurrent().push();
					});
				}
			}
		});
	}


	public void setTool(InventoryItem t) {
		isNew = false;

		if (t == null) {
			tool = new InventoryItem();
			isNew = true;
		} else {
			tool = t;
		}

		initDynamicFormItems();

		binder.readBean(tool);
	}

	public InventoryItem getTool() {
		try {
			binder.validate();

			if (binder.isValid()) {

				if (toolUsageStatusComboBox.getValue().equals(ToolUsageStatus.IN_USE)) {
					if (toolCurrentUserComboBox.getValue() == null) {
						toolCurrentUserComboBox.setInvalid(true);
						return null;
					}
				}

				if (toolUsageStatusComboBox.getValue().equals(ToolUsageStatus.RESERVED)) {
					if (toolReservedByUserComboBox.getValue() == null) {
						toolReservedByUserComboBox.setInvalid(true);
						return null;
					}
				}

				if (toolUsageStatusComboBox.getValue().equals(ToolUsageStatus.IN_USE_AND_RESERVED)) {
					if (toolCurrentUserComboBox.getValue() == null) {
						toolCurrentUserComboBox.setInvalid(true);
						return null;
					}
					if (toolReservedByUserComboBox.getValue() == null) {
						toolReservedByUserComboBox.setInvalid(true);
						return null;
					}
				}

				if (dateNextMaintenance.getValue() != null) {
					if (dateBought.getValue() != null) {
						if (dateNextMaintenance.getValue().isBefore(dateBought.getValue())) {
							dateNextMaintenance.setInvalid(true);
							dateNextMaintenance.setErrorMessage("Date cannot be before Bought Date");
							return null;
						}
					}
				}

				binder.writeBean(tool);

				return tool;
			}
		} catch (ValidationException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public boolean isNew() {
		return isNew;
	}
}
