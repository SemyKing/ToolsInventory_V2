package com.gmail.grigorij.ui.utils.forms.admin;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.backend.entities.inventory.ToolStatus;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.camera.CameraView;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.views.navigation.admin.inventory.AdminInventory;
import com.gmail.grigorij.utils.OperationStatus;
import com.gmail.grigorij.utils.ProjectConstants;
import com.gmail.grigorij.utils.converters.CustomConverter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.converter.DateToSqlDateConverter;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;


public class AdminToolForm extends FormLayout {

	private Binder<InventoryEntity> binder = new Binder<>(InventoryEntity.class);
	private InventoryEntity tool;
	private boolean isNew;

	private ComboBox<InventoryEntity> categoriesComboBox;
	private ComboBox<User> toolUserComboBox;
	private ComboBox<User> toolReservedComboBox;

	private TextField QRCodeField, barCodeField;


	public AdminToolForm(AdminInventory adminTools) {

		TextField toolNameField = new TextField("Name");

		Select<String> status = new Select<>(ProjectConstants.ACTIVE, ProjectConstants.INACTIVE);
		status.setWidth("25%");
		status.setLabel("Status");

		FlexBoxLayout toolNameLayout = UIUtils.getFormRowLayout(toolNameField, status);

		/*
		QR CODE
		 */
		QRCodeField = new TextField("QR Code");

		Button newQrCodeButton = UIUtils.createIconButton(VaadinIcon.CAMERA, ButtonVariant.LUMO_CONTRAST);
		newQrCodeButton.addClickListener(e -> constructCodeScanDialog(QRCodeField));
		UIUtils.setTooltip("Scan QR Code with camera", newQrCodeButton);

		FlexBoxLayout qrCodeLayout = UIUtils.getFormRowLayout(QRCodeField, newQrCodeButton);

		/*
		BARCODE
		 */
		barCodeField = new TextField("Barcode");

		Button newBarcodeButton = UIUtils.createIconButton(VaadinIcon.CAMERA, ButtonVariant.LUMO_CONTRAST);
		newBarcodeButton.addClickListener(e -> constructCodeScanDialog(barCodeField));
		UIUtils.setTooltip("Scan Barcode with camera", newQrCodeButton);

		FlexBoxLayout barcodeLayout = UIUtils.getFormRowLayout(barCodeField, newBarcodeButton);


		TextField toolInfoField = new TextField("Tool Info");
		TextField manufacturerField = new TextField("Manufacturer");
		TextField modelField = new TextField("Model");
		TextField snCodeField = new TextField("SN");



		ComboBox<Company> companyComboBox = new ComboBox<>();
		companyComboBox.setItems(CompanyFacade.getInstance().getAllCompanies());
		companyComboBox.setItemLabelGenerator(Company::getName);
		companyComboBox.setLabel("Company (Owner)");
		companyComboBox.setRequired(true);
		companyComboBox.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					updateComboBoxData(e.getValue());
				}
			}
		});

		categoriesComboBox = new ComboBox<>();
		categoriesComboBox.setItems();
		categoriesComboBox.setLabel("Category");
		categoriesComboBox.setItemLabelGenerator(InventoryEntity::getName);
		categoriesComboBox.setRequired(true);

		Button editCategoryButton = UIUtils.createIconButton(VaadinIcon.EDIT, ButtonVariant.LUMO_CONTRAST);
		editCategoryButton.addClickListener(e -> {
			if (e != null) {
				InventoryEntity selectedCategory = categoriesComboBox.getValue();
				if (selectedCategory != null) {
					if (!selectedCategory.equals(InventoryFacade.getInstance().getRootCategory())) {
						adminTools.constructToolCategoryDetails(selectedCategory);
					}
				}
			}
		});
		UIUtils.setTooltip("Edit selected category", editCategoryButton);

		FlexBoxLayout categoryLayout = UIUtils.getFormRowLayout(categoriesComboBox, editCategoryButton);


		ComboBox<ToolStatus> toolStatusComboBox = new ComboBox<>();
		toolStatusComboBox.setLabel("Status (Usage)");
		toolStatusComboBox.setItems(EnumSet.allOf(ToolStatus.class));
		toolStatusComboBox.setItemLabelGenerator(ToolStatus::getStringValue);

		toolUserComboBox = new ComboBox<>();
		toolUserComboBox.setItems(UserFacade.getInstance().getEmptyList());
		toolUserComboBox.setLabel("Current User");
		toolUserComboBox.setItemLabelGenerator(User::getUsername);

		toolReservedComboBox = new ComboBox<>();
		toolReservedComboBox.setItems(UserFacade.getInstance().getEmptyList());
		toolReservedComboBox.setLabel("Reserved By User");
		toolReservedComboBox.setItemLabelGenerator(User::getUsername);

		DatePicker boughtDateField = new DatePicker("Bought");
		boughtDateField.setWidth("calc(48% - 0rem)");
		boughtDateField.setWeekNumbersVisible(true);

		DatePicker nextMaintenanceDateField = new DatePicker("Next Maintenance");
		nextMaintenanceDateField.setWidth("calc(48% - 0rem)");
		nextMaintenanceDateField.setWeekNumbersVisible(true);

		FlexBoxLayout datesLayout = new FlexBoxLayout();
		datesLayout.setFlexDirection(FlexDirection.ROW);
		datesLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		datesLayout.add(boughtDateField, nextMaintenanceDateField);

		TextField priceField = new TextField("Price");
		priceField.setWidth("calc(48% - 0rem)");
		priceField.setSuffixComponent(new Span("€"));
		priceField.getElement().setAttribute("type", "number");

		TextField guaranteeField = new TextField("Guarantee");
		guaranteeField.setWidth("calc(48% - 0rem)");
		guaranteeField.setSuffixComponent(new Span("Months"));

		FlexBoxLayout priceAndGuaranteeLayout = new FlexBoxLayout();
		priceAndGuaranteeLayout.setFlexDirection(FlexDirection.ROW);
		priceAndGuaranteeLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		priceAndGuaranteeLayout.add(priceField, guaranteeField);

		TextArea additionalInfo = new TextArea("Additional Info");
		additionalInfo.setMaxHeight("200px");

		Divider divider = new Divider(Horizontal.NONE, Vertical.S);
		Divider divider2 = new Divider(Horizontal.NONE, Vertical.S);


		UIUtils.setColSpan(2, toolNameLayout, qrCodeLayout, barcodeLayout, toolInfoField,
				companyComboBox, categoryLayout, additionalInfo, divider, divider2, datesLayout,
				priceAndGuaranteeLayout);

//      Form layout
		addClassNames(LumoStyles.Padding.Bottom.L, LumoStyles.Padding.Horizontal.M, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(toolNameLayout);
		add(qrCodeLayout);
		add(barcodeLayout);
		add(toolInfoField);
		add(manufacturerField);
		add(modelField);
		add(snCodeField);
		add(divider);
		add(companyComboBox);
		add(categoryLayout);
		add(toolStatusComboBox);
		add(toolUserComboBox);
		add(toolReservedComboBox);
		add(divider2);
		add(datesLayout);
		add(priceAndGuaranteeLayout);
		add(additionalInfo);


		binder.forField(toolNameField)
				.asRequired("Name is required")
				.bind(InventoryEntity::getName, InventoryEntity::setName);
		binder.forField(status)
				.asRequired("Status is required")
				.withConverter(new CustomConverter.StatusConverter())
				.bind(InventoryEntity::isDeleted, InventoryEntity::setDeleted);
		binder.forField(QRCodeField)
				.bind(InventoryEntity::getQrCode, InventoryEntity::setQrCode);
		binder.forField(manufacturerField)
				.bind(InventoryEntity::getManufacturer, InventoryEntity::setManufacturer);
		binder.forField(modelField)
				.bind(InventoryEntity::getModel, InventoryEntity::setModel);
		binder.forField(toolInfoField)
				.bind(InventoryEntity::getToolInfo, InventoryEntity::setToolInfo);
		binder.forField(snCodeField)
				.bind(InventoryEntity::getSnCode, InventoryEntity::setSnCode);
		binder.forField(barCodeField)
				.bind(InventoryEntity::getBarcode, InventoryEntity::setBarcode);
		binder.forField(companyComboBox)
				.asRequired("Company is required")
				.bind(InventoryEntity::getCompany, InventoryEntity::setCompany);
		binder.forField(categoriesComboBox)
				.asRequired("Category is required")
//				.withConverter(new CustomConverter.ToolCategoryConverter())
				.bind(InventoryEntity::getParentCategory, InventoryEntity::setParentCategory);
		binder.forField(toolStatusComboBox)
				.asRequired("Status is required")
				.bind(InventoryEntity::getUsageStatus, InventoryEntity::setUsageStatus);

		binder.forField(toolUserComboBox)
				.withValidator((s, valueContext) -> {
					if (toolStatusComboBox.getValue() != null) {
						if(toolStatusComboBox.getValue().equals(ToolStatus.IN_USE) || toolStatusComboBox.getValue().equals(ToolStatus.RESERVED)) {
							if (toolUserComboBox.getValue() == null) {
								return ValidationResult.error("User required for status: " + toolStatusComboBox.getValue().getStringValue());
							}
						}
					}
					return ValidationResult.ok();
				}).bind(InventoryEntity::getUser, InventoryEntity::setUser);

		binder.forField(toolReservedComboBox)
				.withValidator((s, valueContext) -> {
					if (toolStatusComboBox.getValue() != null) {
						if(toolStatusComboBox.getValue().equals(ToolStatus.RESERVED)) {
							if (toolReservedComboBox.getValue() == null) {
								return ValidationResult.error("User required for status: " + toolStatusComboBox.getValue().getStringValue());
							}
						}
					}
					return ValidationResult.ok();
				}).bind(InventoryEntity::getReservedByUser, InventoryEntity::setReservedByUser);

		binder.forField(priceField)
				.withConverter(new StringToDoubleConverter("Price must be a number"))
				.withNullRepresentation(0.00)
				.bind(InventoryEntity::getPrice, InventoryEntity::setPrice);
		binder.forField(guaranteeField)
				.withConverter(new StringToIntegerConverter("Guarantee must be a number"))
				.withNullRepresentation(0)
				.bind(InventoryEntity::getGuarantee_months, InventoryEntity::setGuarantee_months);

		binder.forField(boughtDateField)
				.withConverter(new LocalDateToDateConverter())
				.withConverter(new DateToSqlDateConverter())
				.bind(InventoryEntity::getDateBought, InventoryEntity::setDateBought);

		binder.forField(nextMaintenanceDateField)
				.withValidator((Validator<LocalDate>) (nextMaintenanceDate, valueContext) -> {
					if (boughtDateField.getValue() == null) {
						return ValidationResult.ok();
					}
					if (nextMaintenanceDate == null) {
						return ValidationResult.ok();
					} else {
						if (nextMaintenanceDate.isBefore(boughtDateField.getValue())) {
							return ValidationResult.error("Next maintenance date cannot be before purchase date");
						} else {
							return ValidationResult.ok();
						}
					}
				})
				.withConverter(new LocalDateToDateConverter())
				.withConverter(new DateToSqlDateConverter())
				.bind(InventoryEntity::getDateNextMaintenance, InventoryEntity::setDateNextMaintenance);

		binder.forField(additionalInfo)
				.bind(InventoryEntity::getAdditionalInfo, InventoryEntity::setAdditionalInfo);
	}


	private boolean cameraActive = false;

	private void constructCodeScanDialog(TextField formCodeField) {
		CustomDialog dialog = new CustomDialog();
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);

		dialog.setHeader(UIUtils.createH2Label("Scan Code"));

		CameraView cameraView = new CameraView();

		TextField codeField = new TextField("Code");
		codeField.setReadOnly(true);

		cameraView.addClickListener(imageClickEvent -> {
			if (cameraActive) {
				cameraView.takePicture();
			} else {
				cameraView.showPreview();
				cameraActive = true;
			}
		});

		dialog.setContent(codeField, cameraView);

		dialog.getCancelButton().addClickListener(e -> {
			cameraView.stop();
			dialog.close();
		});

		dialog.getConfirmButton().setText("Add");
		dialog.getConfirmButton().addClickListener(e -> {

			if (cameraActive) {
				cameraView.takePicture();
			}

			formCodeField.setValue(codeField.getValue());
			cameraView.stop();
			dialog.close();
		});
		dialog.open();

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
					});
					UI.getCurrent().push();
				}
			}

			@Override
			public void onFail(String msg, UIUtils.NotificationType type) {
				if (UI.getCurrent() != null) {
					UI.getCurrent().access(() -> {
						UIUtils.showNotification(msg, UIUtils.NotificationType.INFO, 2000);
					});
					UI.getCurrent().push();
				}
			}
		});
	}


	public void setTool(InventoryEntity t) {
		tool = t;
		isNew = false;
		binder.removeBean();

		if (tool == null) {
			tool = InventoryEntity.getEmptyTool();
			isNew = true;
		}

		try {
			binder.readBean(tool);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateComboBoxData(Company company) {
		if (company != null) {
			List<InventoryEntity> categories = InventoryFacade.getInstance().getAllCategoriesInCompany(company.getId());
			categories.add(0, InventoryFacade.getInstance().getRootCategory());

			categoriesComboBox.setItems(categories);

			toolUserComboBox.setItems(UserFacade.getInstance().getUsersByCompanyId(company.getId()));
			toolReservedComboBox.setItems(UserFacade.getInstance().getUsersByCompanyId(company.getId()));
		}
	}

	public InventoryEntity getTool() {
		try {
			binder.validate();

			if (binder.isValid()) {
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
