package com.gmail.grigorij.ui.components.forms.editable;

import com.gmail.grigorij.backend.database.facades.CompanyFacade;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.inventory.ToolStatus;
import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.gmail.grigorij.ui.components.dialogs.CustomDialog;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.camera.CameraView;
import com.gmail.grigorij.ui.utils.css.LumoStyles;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.application.views.admin.AdminInventory;
import com.gmail.grigorij.utils.OperationStatus;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.converter.*;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;


public class EditableToolForm extends FormLayout {

	private Binder<InventoryItem> binder = new Binder<>(InventoryItem.class);
	private InventoryItem tool;
	private boolean isNew;

	private TextField qrCodeField, barCodeField;

	private ComboBox<InventoryItem> categoriesComboBox;
	private ComboBox<User> inUseByComboBox;
	private ComboBox<User> reservedByComboBox;

	private Button setAllCompaniesButton;
	private Button setAllStatusButton;
	private Button editCategoryButton;
	private Button setAllCategoriesButton;
	private Button setAllUsageStatusButton;

	//DEFAULT
	private boolean bulkEditMode;


	public EditableToolForm(AdminInventory adminInventory) {

		TextField nameField = new TextField("Name");
		nameField.setRequired(true);

		Select<String> status = new Select<>(ProjectConstants.ACTIVE, ProjectConstants.INACTIVE);
		status.setWidth("25%");
		status.setLabel("Status");

		setAllStatusButton = UIUtils.createButton("Set all", ButtonVariant.LUMO_CONTRAST);
		setAllStatusButton.addClickListener(e -> {
			if (e != null) {
				if (status.getValue() != null) {
					adminInventory.setBulkStatus(status.getValue());
				}
			}
		});
		UIUtils.setTooltip("Set selected Status to all tools", setAllStatusButton);

		//NAME & STATUS
		FlexBoxLayout nameStatusLayout = UIUtils.getFormRowLayout(nameField, status, false);
		nameStatusLayout.add(setAllStatusButton);
		nameStatusLayout.setComponentMargin(setAllStatusButton, Left.M);

		/*
		QR CODE
		 */
		qrCodeField = new TextField("QR Code");
		qrCodeField.setPrefixComponent(VaadinIcon.QRCODE.create());

		Button scanQrCodeButton = UIUtils.createIconButton(VaadinIcon.CAMERA, ButtonVariant.LUMO_CONTRAST);
		scanQrCodeButton.addClickListener(e -> constructCodeScanDialog(qrCodeField));
		UIUtils.setTooltip("Scan QR Code with camera", scanQrCodeButton);

		//QR CODE & SCAN QR CODE BUTTON
		FlexBoxLayout qrCodeLayout = UIUtils.getFormRowLayout(qrCodeField, scanQrCodeButton, false);


		/*
		BARCODE
		 */
		barCodeField = new TextField("Barcode");
		barCodeField.setPrefixComponent(VaadinIcon.BARCODE.create());

		Button scanBarcodeButton = UIUtils.createIconButton(VaadinIcon.CAMERA, ButtonVariant.LUMO_CONTRAST);
		scanBarcodeButton.addClickListener(e -> constructCodeScanDialog(barCodeField));
		UIUtils.setTooltip("Scan Barcode with camera", scanQrCodeButton);

		//BARCODE & SCAN BARCODE BUTTON
		FlexBoxLayout barcodeLayout = UIUtils.getFormRowLayout(barCodeField, scanBarcodeButton, false);


		TextField snCodeField = new TextField("SN");
		TextField toolInfoField = new TextField("Tool Info");
		TextField manufacturerField = new TextField("Manufacturer");
		TextField modelField = new TextField("Model");


		ComboBox<Company> companiesComboBox = new ComboBox<>();
		companiesComboBox.setItems(CompanyFacade.getInstance().getAllCompanies());
		companiesComboBox.setItemLabelGenerator(Company::getName);
		companiesComboBox.setLabel("Company (Owner)");
		companiesComboBox.setRequired(true);
		companiesComboBox.addValueChangeListener(e -> {
			if (e != null) {
				if (e.getValue() != null) {
					updateComboBoxData(e.getValue());
				}
			}
		});

		setAllCompaniesButton = UIUtils.createButton("Set all", ButtonVariant.LUMO_CONTRAST);
		setAllCompaniesButton.addClickListener(e -> {
			if (e != null) {
				if (companiesComboBox.getValue() != null) {
					adminInventory.setBulkCompanies(companiesComboBox.getValue());
				}
			}
		});
		UIUtils.setTooltip("Set selected company to all tools", setAllCompaniesButton);

		//COMPANY COMBO BOX & SET ALL BUTTON*
		FlexBoxLayout companyLayout = UIUtils.getFormRowLayout(companiesComboBox, setAllCompaniesButton, false);

		categoriesComboBox = new ComboBox<>();
		categoriesComboBox.setItems();
		categoriesComboBox.setLabel("Parent Category");
		categoriesComboBox.setItemLabelGenerator(InventoryItem::getName);
		categoriesComboBox.setRequired(true);

		editCategoryButton = UIUtils.createIconButton(VaadinIcon.EDIT, ButtonVariant.LUMO_CONTRAST);
		editCategoryButton.addClickListener(e -> {
			if (e != null) {
				InventoryItem selectedCategory = categoriesComboBox.getValue();
				if (selectedCategory != null) {
					if (!selectedCategory.equals(InventoryFacade.getInstance().getRootCategory())) {
						adminInventory.constructCategoryDialog(selectedCategory);
					}
				}
			}
		});
		UIUtils.setTooltip("Edit selected category", editCategoryButton);

		setAllCategoriesButton = UIUtils.createButton("Set all", ButtonVariant.LUMO_CONTRAST);
		setAllCategoriesButton.addClickListener(e -> {
				if (e != null) {
					InventoryItem parentCategory = categoriesComboBox.getValue();
					if (parentCategory != null) {
						if (!parentCategory.equals(InventoryFacade.getInstance().getRootCategory())) {
							adminInventory.setBulkCategories(parentCategory);
						}
					}
				}
			});
		UIUtils.setTooltip("Set selected category to all tools", setAllCategoriesButton);

		//CATEGORY COMBO BOX & EDIT CATEGORY / SET ALL BUTTON*
		FlexBoxLayout categoryLayout = UIUtils.getFormRowLayout(categoriesComboBox, editCategoryButton, false);
		categoryLayout.add(setAllCategoriesButton);
		categoryLayout.setComponentMargin(setAllCategoriesButton, Left.M);


		ComboBox<ToolStatus> usageStatusComboBox = new ComboBox<>();
		usageStatusComboBox.setLabel("Usage Status");
		usageStatusComboBox.setItems(EnumSet.allOf(ToolStatus.class));
		usageStatusComboBox.setItemLabelGenerator(ToolStatus::getStringValue);


		setAllUsageStatusButton = UIUtils.createButton("Set all", ButtonVariant.LUMO_CONTRAST);
		setAllUsageStatusButton.addClickListener(e -> {
			if (e != null) {
				if (usageStatusComboBox.getValue() != null) {
					adminInventory.setBulkUsageStatus(usageStatusComboBox.getValue());
				}
			}
		});
		UIUtils.setTooltip("Set selected usage status to all tools", setAllCompaniesButton);

		//USAGE STATUS COMBO BOX & SET ALL BUTTON*
		FlexBoxLayout usageStatusLayout = UIUtils.getFormRowLayout(usageStatusComboBox, setAllUsageStatusButton, false);

		setBulkEditMode(false);

		inUseByComboBox = new ComboBox<>();
		inUseByComboBox.setItems();
		inUseByComboBox.setLabel("Current User");
		inUseByComboBox.setItemLabelGenerator(User::getUsername);

		reservedByComboBox = new ComboBox<>();
		reservedByComboBox.setItems();
		reservedByComboBox.setLabel("Reserved By User");
		reservedByComboBox.setItemLabelGenerator(User::getUsername);

		DatePicker dateBoughtPicker = new DatePicker("Bought");
		dateBoughtPicker.setWidth(ProjectConstants.FORM_HALF_WIDTH);
		dateBoughtPicker.setWeekNumbersVisible(true);

		DatePicker dateNextMaintenancePicker = new DatePicker("Next Maintenance");
		dateNextMaintenancePicker.setWidth(ProjectConstants.FORM_HALF_WIDTH);
		dateNextMaintenancePicker.setWeekNumbersVisible(true);

		//BOUGHT & NEXT MAINTENANCE
		FlexBoxLayout datesLayout = UIUtils.getFormRowLayout(dateBoughtPicker, dateNextMaintenancePicker, true);


		TextField priceField = new TextField("Price");
		priceField.setWidth(ProjectConstants.FORM_HALF_WIDTH);
		priceField.setSuffixComponent(new Span("€"));
		priceField.getElement().setAttribute("type", "number");

		TextField guaranteeField = new TextField("Guarantee");
		guaranteeField.setWidth(ProjectConstants.FORM_HALF_WIDTH);
		guaranteeField.setSuffixComponent(new Span("Months"));
		guaranteeField.getElement().setAttribute("type", "number");

		//PRICE & GUARANTEE
		FlexBoxLayout priceGuaranteeLayout = UIUtils.getFormRowLayout(priceField, guaranteeField, true);


		TextArea additionalInfo = new TextArea("Additional Info");
		additionalInfo.setMaxHeight("200px");


		UIUtils.setColSpan(2, nameStatusLayout, qrCodeLayout, barcodeLayout, companyLayout, categoryLayout, usageStatusLayout, additionalInfo, datesLayout,
				priceGuaranteeLayout);

		addClassNames(LumoStyles.Padding.Bottom.S, LumoStyles.Padding.Top.S);
		setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
				new FormLayout.ResponsiveStep(ProjectConstants.COL_2_MIN_WIDTH, 2, FormLayout.ResponsiveStep.LabelsPosition.TOP));
		add(nameStatusLayout);
		add(qrCodeLayout);
		add(barcodeLayout);
		add(snCodeField);
		add(toolInfoField);
		add(manufacturerField);
		add(modelField);

		add(new Hr());
		add(companyLayout);
		add(categoryLayout);
		add(usageStatusLayout);
		add(inUseByComboBox);
		add(reservedByComboBox);

		add(new Hr());
		add(datesLayout);
		add(priceGuaranteeLayout);
		add(additionalInfo);


		binder.forField(nameField)
				.asRequired("Name is required")
				.bind(InventoryItem::getName, InventoryItem::setName);
		binder.forField(status)
				.asRequired("Status is required")
				.withConverter(new StringToBooleanConverter("Error", ProjectConstants.INACTIVE, ProjectConstants.ACTIVE))
				.bind(InventoryItem::isDeleted, InventoryItem::setDeleted);
		binder.forField(qrCodeField)
				.bind(InventoryItem::getQrCode, InventoryItem::setQrCode);
		binder.forField(barCodeField)
				.bind(InventoryItem::getBarcode, InventoryItem::setBarcode);
		binder.forField(snCodeField)
				.bind(InventoryItem::getSnCode, InventoryItem::setSnCode);
		binder.forField(toolInfoField)
				.bind(InventoryItem::getToolInfo, InventoryItem::setToolInfo);
		binder.forField(manufacturerField)
				.bind(InventoryItem::getManufacturer, InventoryItem::setManufacturer);
		binder.forField(modelField)
				.bind(InventoryItem::getModel, InventoryItem::setModel);
		binder.forField(companiesComboBox)
				.asRequired("Company is required")
				.bind(InventoryItem::getCompany, InventoryItem::setCompany);
		binder.forField(categoriesComboBox)
				.asRequired("Parent Category is required")
				.bind(InventoryItem::getParentCategory, InventoryItem::setParentCategory);
		binder.forField(usageStatusComboBox)
				.asRequired("Usage Status is required")
				.bind(InventoryItem::getUsageStatus, InventoryItem::setUsageStatus);

		binder.forField(inUseByComboBox)
				.withValidator((s, valueContext) -> {
					if (usageStatusComboBox.getValue() != null) {
						if(usageStatusComboBox.getValue().equals(ToolStatus.IN_USE) || usageStatusComboBox.getValue().equals(ToolStatus.RESERVED)) {
							if (inUseByComboBox.getValue() == null) {
								return ValidationResult.error("User required for status: " + usageStatusComboBox.getValue().getStringValue());
							}
						}
					}
					return ValidationResult.ok();
				}).bind(InventoryItem::getInUseByUser, InventoryItem::setInUseByUser);

		binder.forField(reservedByComboBox)
				.withValidator((s, valueContext) -> {
					if (usageStatusComboBox.getValue() != null) {
						if(usageStatusComboBox.getValue().equals(ToolStatus.RESERVED)) {
							if (reservedByComboBox.getValue() == null) {
								return ValidationResult.error("User required for status: " + usageStatusComboBox.getValue().getStringValue());
							}
						}
					}
					return ValidationResult.ok();
				}).bind(InventoryItem::getReservedByUser, InventoryItem::setReservedByUser);

		binder.forField(priceField)
				.withConverter(new StringToDoubleConverter("Price must be a number"))
				.withNullRepresentation(0.00)
				.bind(InventoryItem::getPrice, InventoryItem::setPrice);
		binder.forField(guaranteeField)
				.withConverter(new StringToIntegerConverter("Guarantee must be a number"))
				.withNullRepresentation(0)
				.bind(InventoryItem::getGuarantee_months, InventoryItem::setGuarantee_months);

		binder.forField(dateBoughtPicker)
				.withConverter(new LocalDateToDateConverter())
				.withConverter(new DateToSqlDateConverter())
				.bind(InventoryItem::getDateBought, InventoryItem::setDateBought);

		binder.forField(dateNextMaintenancePicker)
				.withValidator((Validator<LocalDate>) (nextMaintenanceDate, valueContext) -> {
					if (dateBoughtPicker.getValue() == null) {
						return ValidationResult.ok();
					}
					if (nextMaintenanceDate == null) {
						return ValidationResult.ok();
					} else {
						if (nextMaintenanceDate.isBefore(dateBoughtPicker.getValue())) {
							return ValidationResult.error("Next maintenance date cannot be before purchase date");
						} else {
							return ValidationResult.ok();
						}
					}
				})
				.withConverter(new LocalDateToDateConverter())
				.withConverter(new DateToSqlDateConverter())
				.bind(InventoryItem::getDateNextMaintenance, InventoryItem::setDateNextMaintenance);

		binder.forField(additionalInfo)
				.bind(InventoryItem::getAdditionalInfo, InventoryItem::setAdditionalInfo);
	}


	private boolean cameraActive = false;

	private void constructCodeScanDialog(TextField formCodeField) {
		CustomDialog dialog = new CustomDialog();
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);

		dialog.setHeader(UIUtils.createH3Label("Scan Code"));


		TextField codeField = new TextField("Code");
		codeField.setReadOnly(true);

		CameraView cameraView = new CameraView();
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
						UIUtils.showNotification(msg, UIUtils.NotificationType.INFO, 2000);
						UI.getCurrent().push();
					});
				}
			}
		});
	}

	private void updateComboBoxData(Company company) {
		if (company != null) {
			List<InventoryItem> categories = InventoryFacade.getInstance().getAllCategoriesInCompany(company.getId());
			categories.add(0, InventoryFacade.getInstance().getRootCategory());

			categoriesComboBox.setItems(categories);

			inUseByComboBox.setItems(UserFacade.getInstance().getUsersInCompany(company.getId()));
			reservedByComboBox.setItems(UserFacade.getInstance().getUsersInCompany(company.getId()));
		}
	}


	public void setTool(InventoryItem t) {
		tool = t;
		isNew = false;
		binder.removeBean();

		if (tool == null) {
			tool = new InventoryItem();
			isNew = true;
		}

		try {
			binder.readBean(tool);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public InventoryItem getTool() {
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

	public boolean isBulkEditMode() {
		return bulkEditMode;
	}
	public void setBulkEditMode(boolean bulkEditMode) {
		this.bulkEditMode = bulkEditMode;

		setAllStatusButton.setEnabled(bulkEditMode);

		setAllCompaniesButton.setEnabled(bulkEditMode);

		editCategoryButton.setEnabled(!bulkEditMode);
		setAllCategoriesButton.setEnabled(bulkEditMode);

		setAllUsageStatusButton.setEnabled(bulkEditMode);
	}
}