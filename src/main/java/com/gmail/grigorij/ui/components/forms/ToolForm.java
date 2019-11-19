package com.gmail.grigorij.ui.components.forms;

import com.gmail.grigorij.backend.database.entities.Category;
import com.gmail.grigorij.backend.database.entities.Company;
import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.entities.embeddable.Location;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.backend.database.enums.operations.OperationTarget;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionRange;
import com.gmail.grigorij.backend.database.enums.tools.ToolUsageStatus;
import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.PermissionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.components.dialogs.CameraDialog;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.gmail.grigorij.ui.views.app.admin.AdminInventory;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.gmail.grigorij.utils.DateConverter;
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
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;


public class ToolForm extends FormLayout {

	private final String CLASS_NAME = "form";
	private final AdminInventory adminInventory;

	private Binder<Tool> binder;
	private Tool tool, originalTool;
	private boolean isNew;

	// FORM ITEMS
	private Div entityStatusDiv;
	private Checkbox entityStatusCheckbox;
	private TextField nameField;
	private TextField barcode;
	private FlexBoxLayout barcodeLayout;
	private TextField serialNumber;
	private TextField toolInfo;
	private TextField manufacturer;
	private TextField model;
	private ComboBox<Company> companyComboBox;
	private ComboBox<Category> categoryComboBox;
	private FlexBoxLayout categoryLayout;
	private Div locationAndUsageDiv;
	private ComboBox<Location> locationComboBox;
	private ComboBox<ToolUsageStatus> toolUsageStatusComboBox;
	private Div toolUsersDiv;
	private ComboBox<User> toolCurrentUserComboBox;
	private ComboBox<User> toolReservedByUserComboBox;

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

		setColspan(entityStatusDiv, 2);

//		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().lowerThan(PermissionLevel.SYSTEM_ADMIN)) {
//			if (!PermissionFacade.getInstance().isUserAllowedTo(Operation.DELETE, OperationTarget.USER, PermissionRange.COMPANY)) {
//				entityStatusCheckbox.setReadOnly(true);
//				entityStatusDiv.getElement().setAttribute("hidden", true);
//			}
//		}

		nameField = new TextField("Name");
		nameField.setRequired(true);

		barcode = new TextField("Barcode");
		barcode.setPrefixComponent(VaadinIcon.BARCODE.create());

		Button scanBarcodeButton = UIUtils.createIconButton(VaadinIcon.CAMERA, ButtonVariant.LUMO_PRIMARY);
		scanBarcodeButton.addClickListener(e -> constructCodeScannerDialog(barcode));
		UIUtils.setTooltip("Scan Barcode with camera", scanBarcodeButton);

		barcodeLayout = new FlexBoxLayout();
		barcodeLayout.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		barcodeLayout.add(barcode, scanBarcodeButton);
		barcodeLayout.setFlexGrow("1", barcode);
		barcodeLayout.setComponentMargin(barcode, Right.S);


		serialNumber = new TextField("Serial Number");
		toolInfo = new TextField("Tool Info");
		manufacturer = new TextField("Manufacturer");
		model = new TextField("Model");


		companyComboBox = new ComboBox<>();
		companyComboBox.setItems(CompanyFacade.getInstance().getAllActiveCompanies());
		companyComboBox.setItemLabelGenerator(Company::getName);
		companyComboBox.setLabel("Company");
		companyComboBox.setRequired(true);
		companyComboBox.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				updateComboBoxes(e.getValue());
			}
		});


		categoryComboBox = new ComboBox<>();
		categoryComboBox.setItems();
		categoryComboBox.setLabel("Category");
		categoryComboBox.setItemLabelGenerator(Category::getName);
		categoryComboBox.setRequired(true);

		Button editCategoryButton = UIUtils.createIconButton(VaadinIcon.EDIT, ButtonVariant.LUMO_PRIMARY);
		editCategoryButton.addClickListener(e -> {
			Category selectedCategory = categoryComboBox.getValue();
			if (selectedCategory != null) {
				adminInventory.constructCategoryDialog(selectedCategory);
			}
		});
		UIUtils.setTooltip("Edit category", editCategoryButton);


		categoryLayout = new FlexBoxLayout();
		categoryLayout.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		categoryLayout.add(categoryComboBox, editCategoryButton);
		categoryLayout.setFlexGrow("1", categoryComboBox);
		categoryLayout.setComponentMargin(categoryComboBox, Right.S);


		locationComboBox = new ComboBox<>();
		locationComboBox.setLabel("Current Location");
		locationComboBox.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		locationComboBox.setItems();
		locationComboBox.setItemLabelGenerator(Location::getName);

		toolUsageStatusComboBox = new ComboBox<>();
		toolUsageStatusComboBox.setLabel("Usage Status");
		toolUsageStatusComboBox.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		toolUsageStatusComboBox.setItems(EnumSet.allOf(ToolUsageStatus.class));
		toolUsageStatusComboBox.setItemLabelGenerator(ToolUsageStatus::getName);

		locationAndUsageDiv = new Div();
		locationAndUsageDiv.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		locationAndUsageDiv.add(locationComboBox, toolUsageStatusComboBox);

		setColspan(locationAndUsageDiv, 2);


		toolCurrentUserComboBox = new ComboBox<>();
		toolCurrentUserComboBox.setLabel("Current User");
		toolCurrentUserComboBox.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		toolCurrentUserComboBox.setErrorMessage("User Required");
		toolCurrentUserComboBox.setItems();
		toolCurrentUserComboBox.setItemLabelGenerator(User::getFullName);
		toolCurrentUserComboBox.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				toolCurrentUserComboBox.setInvalid(false);
			}
		});

		toolReservedByUserComboBox = new ComboBox<>();
		toolReservedByUserComboBox.setLabel("Reserved By User");
		toolReservedByUserComboBox.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");
		toolReservedByUserComboBox.setErrorMessage("User Required");
		toolReservedByUserComboBox.setItems();
		toolReservedByUserComboBox.setItemLabelGenerator(User::getFullName);
		toolReservedByUserComboBox.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				toolCurrentUserComboBox.setInvalid(false);
			}
		});

		toolUsersDiv = new Div();
		toolUsersDiv.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		toolUsersDiv.add(toolCurrentUserComboBox, toolReservedByUserComboBox);

		setColspan(toolUsersDiv, 2);


		dateBought = new DatePicker("Bought Date");
		dateBought.setLocale(new Locale("fi"));
		dateBought.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");

		dateNextMaintenance = new DatePicker("Next Maintenance");
		dateNextMaintenance.setLocale(new Locale("fi"));
		dateNextMaintenance.setWidth("calc(50% - (0.5 * var(--vaadin-form-layout-column-spacing)))");

		datesDiv = new Div();
		datesDiv.addClassName(ProjectConstants.CONTAINER_SPACE_BETWEEN);
		datesDiv.add(dateBought, dateNextMaintenance);


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
		priceGuaranteeDiv.add(priceField, guaranteeField);


		additionalInfo = new TextArea("Additional Info");
		additionalInfo.setMaxHeight("200px");

		setColspan(additionalInfo, 2);
	}

	private void constructForm() {
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(entityStatusDiv);
		add(nameField);
		add(barcodeLayout);
		add(serialNumber);
		add(toolInfo);
		add(manufacturer);
		add(model);

		Hr hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		if (AuthenticationService.getCurrentSessionUser().getPermissionLevel().equalsTo(PermissionLevel.SYSTEM_ADMIN)) {
			add(companyComboBox);
		}
		add(categoryLayout);

		add(locationAndUsageDiv);
		add(toolUsersDiv);

		hr = new Hr();
		setColspan(hr, 2);
		add(hr);

		add(datesDiv);
		add(priceGuaranteeDiv);

		add(additionalInfo);
	}

	private void constructBinder() {
		binder = new Binder<>(Tool.class);

		binder.forField(entityStatusCheckbox)
				.bind(Tool::isDeleted, Tool::setDeleted);

		binder.forField(nameField)
				.asRequired("Name is required")
				.bind(Tool::getName, Tool::setName);

		binder.forField(barcode)
				.bind(Tool::getBarcode, Tool::setBarcode);

		binder.forField(serialNumber)
				.bind(Tool::getSerialNumber, Tool::setSerialNumber);

		binder.forField(toolInfo)
				.bind(Tool::getToolInfo, Tool::setToolInfo);

		binder.forField(manufacturer)
				.bind(Tool::getManufacturer, Tool::setManufacturer);

		binder.forField(model)
				.bind(Tool::getModel, Tool::setModel);

		binder.forField(companyComboBox)
				.asRequired("Company is required")
				.bind(Tool::getCompany, Tool::setCompany);

		binder.forField(categoryComboBox)
				.asRequired("Category is required")
				.bind(Tool::getCategory, Tool::setCategory);

		binder.forField(locationComboBox)
				.bind(Tool::getCurrentLocation, Tool::setCurrentLocation);

		binder.forField(toolUsageStatusComboBox)
				.asRequired("Usage Status is required")
				.bind(Tool::getUsageStatus, Tool::setUsageStatus);

		binder.forField(toolCurrentUserComboBox)
				.bind(Tool::getCurrentUser, Tool::setCurrentUser);

		binder.forField(toolReservedByUserComboBox)
				.bind(Tool::getReservedUser, Tool::setReservedUser);

		binder.forField(priceField)
				.withConverter(new StringToDoubleConverter("Price must be a number"))
				.withNullRepresentation(0.00)
				.bind(Tool::getPrice, Tool::setPrice);

		binder.forField(guaranteeField)
				.withConverter(new StringToIntegerConverter("Guarantee must be a number"))
				.withNullRepresentation(0)
				.bind(Tool::getGuarantee_months, Tool::setGuarantee_months);

		binder.forField(dateBought)
				.bind(Tool::getDateBought, Tool::setDateBought);

		binder.forField(dateNextMaintenance)
				.bind(Tool::getDateNextMaintenance, Tool::setDateNextMaintenance);

		binder.forField(additionalInfo)
				.bind(Tool::getAdditionalInfo, Tool::setAdditionalInfo);
	}


	private void initDynamicFormItems() {
		try {
			entityStatusDiv.remove(entityStatusCheckbox);
		} catch (Exception ignored) {}

		if (PermissionFacade.getInstance().isSystemAdminOrAllowedTo(Operation.DELETE, OperationTarget.INVENTORY_TOOL, PermissionRange.COMPANY)) {
			entityStatusDiv.add(entityStatusCheckbox);
		}

		updateComboBoxes(tool.getCompany());
	}

	private void updateComboBoxes(Company company) {
		if (company != null) {

			categoryComboBox.setValue(null);
			toolCurrentUserComboBox.setValue(null);
			toolReservedByUserComboBox.setValue(null);

			categoryComboBox.setItems(InventoryFacade.getInstance().getAllActiveCategoriesInCompany(company.getId()));
			toolCurrentUserComboBox.setItems(UserFacade.getInstance().getAllActiveUsersInCompany(company.getId()));
			toolReservedByUserComboBox.setItems(UserFacade.getInstance().getAllActiveUsersInCompany(company.getId()));
			locationComboBox.setItems(company.getLocations());
		}
	}

	private void constructCodeScannerDialog(TextField codeField) {
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

							UIUtils.showNotification("Code scanned", NotificationVariant.LUMO_SUCCESS, 1000);

							codeField.setValue(code);
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


	public void setTool(Tool tool) {
		isNew = false;

		if (tool == null) {
			this.tool = new Tool();
			isNew = true;
		} else {
			this.tool = tool;
		}

		initDynamicFormItems();

		originalTool = new Tool(this.tool);

		binder.readBean(this.tool);

		if (isNew) {
			companyComboBox.setValue(AuthenticationService.getCurrentSessionUser().getCompany());
		}
	}

	public Tool getTool() {
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

//				if (tool.getParentCategory() != null) {
//					if (tool.getParentCategory().equals(InventoryFacade.getInstance().getRootCategory())) {
//						tool.setParentCategory(null);
//					}
//				}

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

	public List<String> getChanges() {
		List<String> changes = new ArrayList<>();

		if (Boolean.compare(originalTool.isDeleted(), tool.isDeleted()) != 0) {
			changes.add("Status changed from: '" + UIUtils.entityStatusToString(originalTool.isDeleted()) + "', to: '" + UIUtils.entityStatusToString(tool.isDeleted()) + "'");
		}
		if (!originalTool.getName().equals(tool.getName())) {
			changes.add("Name changed from: '" + originalTool.getName() + "', to: '" + tool.getName() + "'");
		}
		if (!originalTool.getBarcode().equals(tool.getBarcode())) {
			changes.add("Barcode changed from: '" + originalTool.getBarcode() + "', to: '" + tool.getBarcode() + "'");
		}
		if (!originalTool.getSerialNumber().equals(tool.getSerialNumber())) {
			changes.add("SN changed from: '" + originalTool.getSerialNumber() + "', to: '" + tool.getSerialNumber() + "'");
		}
		if (!originalTool.getToolInfo().equals(tool.getToolInfo())) {
			changes.add("Tool info changed from: '" + originalTool.getToolInfo() + "', to: '" + tool.getToolInfo() + "'");
		}
		if (!originalTool.getManufacturer().equals(tool.getManufacturer())) {
			changes.add("Manufacturer changed from: '" + originalTool.getManufacturer() + "', to: '" + tool.getManufacturer() + "'");
		}
		if (!originalTool.getModel().equals(tool.getModel())) {
			changes.add("Model changed from: '" + originalTool.getModel() + "', to: '" + tool.getModel() + "'");
		}
		if (!originalTool.getCompanyString().equals(tool.getCompanyString())) {
			changes.add("Tool company changed from: '" + originalTool.getCompanyString() + "', to: '" + tool.getCompanyString() + "'");
		}
		if (!originalTool.getCategoryString().equals(tool.getCategoryString())) {
			changes.add("Tool category changed from: '" +
					originalTool.getCategoryString() + "', to: '" +
					tool.getCategoryString() + "'");
		}
		if (!originalTool.getUsageStatusString().equals(tool.getUsageStatusString())) {
			changes.add("Usage status changed from: '" +
					originalTool.getUsageStatusString() + "', to: '" +
					tool.getUsageStatusString() + "'");
		}
		if (!originalTool.getCurrentUserString().equals(tool.getCurrentUserString())) {
			changes.add("Tool current user changed from: '" +
					originalTool.getCurrentUserString() + "', to: '" +
					tool.getCurrentUserString() + "'");
		}
		if (!originalTool.getReservedUserString().equals(tool.getReservedUserString())) {
			changes.add("Tool reserved user changed from: '" +
					originalTool.getReservedUserString() + "', to: '" +
					tool.getReservedUserString() + "'");
		}
		if (!originalTool.getPrice().equals(tool.getPrice())) {
			changes.add("Price changed from: '" + originalTool.getPrice() + "', to: '" + tool.getPrice() + "'");
		}
		if (!originalTool.getGuarantee_months().equals(tool.getGuarantee_months())) {
			changes.add("Guarantee changed from: '" + originalTool.getGuarantee_months() + "', to: '" + tool.getGuarantee_months() + "'");
		}
		if (originalTool.getDateBought() != null || tool.getDateBought() != null) {
			changes.add("Date bought changed from: '" +
					(originalTool.getDateBought()==null ? "" : DateConverter.localDateToString(originalTool.getDateBought())) +
					"', to: '" +
					(tool.getDateBought()==null ? "" : DateConverter.localDateToString(tool.getDateBought())) + "'");
		}
		if (originalTool.getDateNextMaintenance() != null || tool.getDateNextMaintenance() != null) {
			changes.add("Next maintenance date changed from: '" +
					(originalTool.getDateNextMaintenance()==null ? "" : DateConverter.localDateToString(originalTool.getDateNextMaintenance())) +
					"', to: '" +
					(tool.getDateNextMaintenance()==null ? "" : DateConverter.localDateToString(tool.getDateNextMaintenance())) + "'");
		}
		if (!originalTool.getAdditionalInfo().equals(tool.getAdditionalInfo())) {
			changes.add("Additional Info changed from: '" + originalTool.getAdditionalInfo() + "',  to:  '" + tool.getAdditionalInfo() + "'");
		}

		return changes;
	}
}
