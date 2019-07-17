package com.gmail.grigorij.ui.utils.forms.admin;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.ToolFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.tool.Tool;
import com.gmail.grigorij.backend.entities.tool.ToolStatus;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.camera.CameraView;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.components.Divider;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.views.navigation.admin.AdminInventory;
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

public class AdminToolForm extends FormLayout {

	private Binder<Tool> binder = new Binder<>(Tool.class);
	private Tool tool;
	private boolean isNew;

	private ComboBox<Tool> categoriesComboBox;
	private ComboBox<User> toolUserComboBox;
	private ComboBox<User> toolReservedComboBox;

	private Button editCategoryButton;
	private TextField QRCodeField, barCodeField;


	public AdminToolForm(AdminInventory adminTools) {

		TextField toolNameField = new TextField("Name");

		Select<String> status = new Select<>(ProjectConstants.ACTIVE, ProjectConstants.INACTIVE);
		status.setWidth("25%");
		status.setLabel("Status");

		FlexBoxLayout toolNameLayout = new FlexBoxLayout();
		toolNameLayout.setFlexDirection(FlexDirection.ROW);
		toolNameLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		toolNameLayout.add(toolNameField, status);
		toolNameLayout.setComponentMargin(status, Left.M);
		toolNameLayout.setFlexGrow("1", toolNameField);


		/*
		QR CODE
		 */
		QRCodeField = new TextField("QR Code");

		Button newQrCodeButton = UIUtils.createIconButton(VaadinIcon.CAMERA, ButtonVariant.LUMO_CONTRAST);
		newQrCodeButton.addClickListener(e -> constructCodeScanDialog(QRCodeField));
		UIUtils.setTooltip("Scan QR Code with camera", newQrCodeButton);

		FlexBoxLayout qrCodeLayout = new FlexBoxLayout();
		qrCodeLayout.setFlexDirection(FlexDirection.ROW);
		qrCodeLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		qrCodeLayout.add(QRCodeField, newQrCodeButton);
		qrCodeLayout.setComponentMargin(newQrCodeButton, Left.M);
		qrCodeLayout.setFlexGrow("1", QRCodeField);
		qrCodeLayout.setAlignItems(FlexComponent.Alignment.BASELINE);


		/*
		BARCODE
		 */
		barCodeField = new TextField("Barcode");

		Button newBarcodeButton = UIUtils.createIconButton(VaadinIcon.CAMERA, ButtonVariant.LUMO_CONTRAST);
		newBarcodeButton.addClickListener(e -> constructCodeScanDialog(barCodeField));
		UIUtils.setTooltip("Scan Barcode with camera", newQrCodeButton);

		FlexBoxLayout barcodeLayout = new FlexBoxLayout();
		barcodeLayout.setFlexDirection(FlexDirection.ROW);
		barcodeLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		barcodeLayout.add(barCodeField, newBarcodeButton);
		barcodeLayout.setComponentMargin(newBarcodeButton, Left.M);
		barcodeLayout.setFlexGrow("1", barCodeField);
		barcodeLayout.setAlignItems(FlexComponent.Alignment.BASELINE);


		TextField toolInfoField = new TextField("Tool Info");
		TextField manufacturerField = new TextField("Manufacturer");
		TextField modelField = new TextField("Model");
		TextField snCodeField = new TextField("SN");


		editCategoryButton = UIUtils.createIconButton(VaadinIcon.EDIT, ButtonVariant.LUMO_CONTRAST);
		editCategoryButton.addClickListener(e -> {
			if (e != null) {
				Tool selectedCategory = categoriesComboBox.getValue();
				if (selectedCategory != null) {
					adminTools.constructToolCategoryDetails(selectedCategory);
				}
			}
		});
		UIUtils.setTooltip("Edit selected category", editCategoryButton);

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
		categoriesComboBox.setItems(ToolFacade.getInstance().getEmptyList());
		categoriesComboBox.setLabel("Category");
		categoriesComboBox.setItemLabelGenerator(Tool::getName);
		categoriesComboBox.setRequired(true);
		categoriesComboBox.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					editCategoryButton.setEnabled(!e.getValue().equals(ToolFacade.getInstance().getRootCategory()));
				}
			}
		});

		FlexBoxLayout categoryLayout = new FlexBoxLayout();
		categoryLayout.setWidth("100%");
		categoryLayout.setFlexDirection(FlexDirection.ROW);
		categoryLayout.add(categoriesComboBox, editCategoryButton);
		categoryLayout.setComponentMargin(editCategoryButton, Left.S);
		categoryLayout.setFlexGrow("1", categoriesComboBox);
		categoryLayout.setAlignItems(FlexComponent.Alignment.BASELINE);


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
				.bind(Tool::getName, Tool::setName);
		binder.forField(status)
				.asRequired("Status is required")
				.withConverter(new CustomConverter.StatusConverter())
				.bind(Tool::isDeleted, Tool::setDeleted);
		binder.forField(QRCodeField)
				.bind(Tool::getQrCode, Tool::setQrCode);
		binder.forField(manufacturerField)
				.bind(Tool::getManufacturer, Tool::setManufacturer);
		binder.forField(modelField)
				.bind(Tool::getModel, Tool::setModel);
		binder.forField(toolInfoField)
				.bind(Tool::getToolInfo, Tool::setToolInfo);
		binder.forField(snCodeField)
				.bind(Tool::getSnCode, Tool::setSnCode);
		binder.forField(barCodeField)
				.bind(Tool::getBarcode, Tool::setBarcode);
		binder.forField(companyComboBox)
				.asRequired("Company is required")
				.bind(Tool::getCompany, Tool::setCompany);
		binder.forField(categoriesComboBox)
				.asRequired("Category is required")
				.withConverter(new CustomConverter.ToolCategoryConverter())
				.bind(Tool::getParentCategory, Tool::setParentCategory);
		binder.forField(toolStatusComboBox)
				.asRequired("Status is required")
				.bind(Tool::getUsageStatus, Tool::setUsageStatus);

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
				}).bind(Tool::getUser, Tool::setUser);

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
				}).bind(Tool::getReservedByUser, Tool::setReservedByUser);

		binder.forField(priceField)
				.withConverter(new StringToDoubleConverter("Price must be a number"))
				.withNullRepresentation(0.00)
				.bind(Tool::getPrice, Tool::setPrice);
		binder.forField(guaranteeField)
				.withConverter(new StringToIntegerConverter("Guarantee must be a number"))
				.withNullRepresentation(0)
				.bind(Tool::getGuarantee_months, Tool::setGuarantee_months);

		binder.forField(boughtDateField)
				.withConverter(new LocalDateToDateConverter())
				.withConverter(new DateToSqlDateConverter())
				.bind(Tool::getDateBought, Tool::setDateBought);

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
				.bind(Tool::getDateNextMaintenance, Tool::setDateNextMaintenance);

		binder.forField(additionalInfo)
				.bind(Tool::getAdditionalInfo, Tool::setAdditionalInfo);
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
			public void onSuccess(String msg) {
				if (UI.getCurrent() != null) {
					UI.getCurrent().access(() -> {
						UIUtils.showNotification("Code scanned successfully", UIUtils.NotificationType.SUCCESS);

						codeField.setValue(msg);
						cameraView.stop();
						cameraActive = false;
					});
					UI.getCurrent().push();
				}
			}

			@Override
			public void onFail(String msg) {
				if (UI.getCurrent() != null) {
					UI.getCurrent().access(() -> {
						UIUtils.showNotification(msg, UIUtils.NotificationType.INFO, 2000);
					});
					UI.getCurrent().push();
				}
			}
		});
	}


	public void setTool(Tool t) {
		tool = t;
		isNew = false;
		binder.removeBean();

		if (tool == null) {
			tool = Tool.getEmptyTool();
			isNew = true;
		}

		try {
			binder.readBean(tool);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateComboBoxData(Company company) {
		if (company.getId() <= 0) {
			System.err.println("Company ID is <= 0, company name: " + company.getName());
			return;
		}
		categoriesComboBox.setItems(ToolFacade.getInstance().getAllCategoriesInCompanyWithRoot(company.getId()));
		if (categoriesComboBox.getValue() != null) {
			editCategoryButton.setEnabled(!categoriesComboBox.getValue().equals(ToolFacade.getInstance().getRootCategory()));
		}

		toolUserComboBox.setItems(UserFacade.getInstance().getUsersByCompanyId(company.getId()));
		toolReservedComboBox.setItems(UserFacade.getInstance().getUsersByCompanyId(company.getId()));
	}

	public Tool getTool() {
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
