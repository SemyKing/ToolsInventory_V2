package com.gmail.grigorij.ui.views.navigation.admin.inventory;

import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.backend.entities.inventory.ToolStatus;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.components.ConfirmDialog;
import com.gmail.grigorij.ui.utils.components.CustomDialog;
import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.FlexDirection;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Left;
import com.gmail.grigorij.ui.utils.css.size.Right;
import com.gmail.grigorij.ui.utils.forms.admin.AdminToolBulkForm;
import com.gmail.grigorij.utils.OperationStatus;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

import java.util.ArrayList;
import java.util.List;

public class AdminInventoryBulkEditor {

	public enum OperationType {
		EDIT,
		COPY
	}
	private AdminToolBulkForm bulkAdminToolForm;

	private FlexBoxLayout headerLayout = null;
	private Label nOfCopyLabel;
	private Label nOfCopiesLabel;
	private CustomDialog toolBulkEditDialog;
	private List<InventoryEntity> bulkEditTools;
	private InventoryEntity bulkEditTool = null;
	private int currentBulkEditToolIndex = -1;

	private OperationType operationType;
	private int numberOfTools;

	private final AdminInventory adminInventory;

	AdminInventoryBulkEditor(AdminInventory adminInventory, OperationType operationType, int numberOfTools) {
		this.adminInventory = adminInventory;
		this.operationType = operationType;
		this.numberOfTools = numberOfTools;

		bulkAdminToolForm = new AdminToolBulkForm(this);

		bulkEditTools = new ArrayList<>();

		constructHeader();
	}

	private void constructHeader() {
		// Header for tools bulk edit operation
		headerLayout = new FlexBoxLayout();
		headerLayout.setSizeFull();
		headerLayout.setFlexDirection(FlexDirection.ROW);
		headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

		String headerText = operationType.equals(OperationType.EDIT) ? "Edit Tool" : "Edit Tool Copy";
		headerLayout.add(UIUtils.createH4Label(headerText));

		nOfCopyLabel = UIUtils.createH4Label("1");
		headerLayout.add(nOfCopyLabel);
		headerLayout.setComponentMargin(nOfCopyLabel, Left.M);

		Label slash = UIUtils.createH4Label("/");
		headerLayout.add(slash);
		headerLayout.setComponentMargin(slash, Horizontal.S);

		nOfCopiesLabel = UIUtils.createH4Label("100");
		headerLayout.add(nOfCopiesLabel);

		if (operationType.equals(OperationType.COPY)) {
			Button addOneCopyButton = UIUtils.createIconButton(VaadinIcon.PLUS, ButtonVariant.LUMO_CONTRAST);
			UIUtils.setTooltip("Create new copy of this inventory", addOneCopyButton);
			addOneCopyButton.addClickListener(e -> {
				InventoryEntity newToolCopy = new InventoryEntity(bulkEditTools.get(currentBulkEditToolIndex));
				bulkEditTools.add(newToolCopy);
				++numberOfTools;
				nOfCopiesLabel.setText(""+(numberOfTools));
			});

			headerLayout.add(addOneCopyButton);
			headerLayout.setComponentMargin(addOneCopyButton, Left.AUTO);
		}


		Button toolsToLeftButton = UIUtils.createIconButton(VaadinIcon.ANGLE_LEFT, ButtonVariant.LUMO_CONTRAST);
		Button toolsToRightButton = UIUtils.createIconButton(VaadinIcon.ANGLE_RIGHT, ButtonVariant.LUMO_CONTRAST);

		Button deleteButton;

		if (operationType.equals(OperationType.EDIT)) {
			deleteButton = UIUtils.createButton("Delete All", VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR);
			UIUtils.setTooltip("Remove all selected tools from Database", deleteButton);
			deleteButton.addClickListener(e -> {

				ConfirmDialog confirmDialog = new ConfirmDialog(ConfirmDialog.Type.DELETE, "selected tools", "Delete " + numberOfTools + " Selected Tools");
				confirmDialog.closeOnCancel();
				confirmDialog.getConfirmButton().addClickListener(confirmDeleteEvent -> {
					try {
						final boolean[] deleteError = {false};

						for (InventoryEntity tool : bulkEditTools) {
							InventoryFacade.getInstance().remove(tool, new OperationStatus() {
								@Override
								public void onSuccess(String msg, UIUtils.NotificationType type) {

								}
								@Override
								public void onFail(String msg, UIUtils.NotificationType type) {
									UIUtils.showNotification(msg, type);
									deleteError[0] = true;
								}

							});
						}

						if (!deleteError[0]) {
							UIUtils.showNotification("Tools deleted successfully", UIUtils.NotificationType.SUCCESS);
						}

						adminInventory.getDataProvider().refreshAll();
						adminInventory.clearSelection();

						confirmDialog.close();
						toolBulkEditDialog.close();
					} catch (Exception ex) {
						UIUtils.showNotification("Tool delete failed", UIUtils.NotificationType.ERROR);
						ex.printStackTrace();
					}
				});
				confirmDialog.open();
			});
		} else {
			deleteButton = UIUtils.createIconButton(VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR);
			UIUtils.setTooltip("Remove this tool", deleteButton);
			deleteButton.addClickListener(e -> {

				// If more that one item
				if (numberOfTools > 1) {

					int itemPositionToRemove = -1;

					// Current position is first
					if (currentBulkEditToolIndex == 0) {
						toolsToRightButton.click();
						itemPositionToRemove = 0;
					}

					// Current position is last
					if (currentBulkEditToolIndex == (numberOfTools-1)) {
						toolsToLeftButton.click();
						itemPositionToRemove = (numberOfTools-1);
					}

					// Current position is middle, move Left
					if (itemPositionToRemove == -1) {
						itemPositionToRemove = currentBulkEditToolIndex;
						toolsToLeftButton.click();
					}

					bulkEditTools.remove(itemPositionToRemove);
					--numberOfTools;
					nOfCopiesLabel.setText(""+(numberOfTools));
				}
			});
		}


		headerLayout.add(deleteButton);
		headerLayout.setComponentMargin(deleteButton, Right.L);

		if (operationType.equals(OperationType.EDIT)) {
			headerLayout.setComponentMargin(deleteButton, Left.AUTO);
		}


//		Button toolsToLeftButton = UIUtils.createButton(VaadinIcon.ANGLE_LEFT, ButtonVariant.LUMO_CONTRAST);
		toolsToLeftButton.addClickShortcut(Key.ARROW_LEFT, KeyModifier.CONTROL);
		UIUtils.setTooltip("Ctrl + Arrow Left", toolsToLeftButton);
		toolsToLeftButton.addClickListener(e -> {
			if (currentBulkEditToolIndex > 0) {
				InventoryEntity editedTool = bulkAdminToolForm.getTool();
				if (editedTool != null) {
					bulkEditTools.set(currentBulkEditToolIndex, editedTool);

					--currentBulkEditToolIndex;
					setToolBulkEditDialogContent(bulkEditTools.get(currentBulkEditToolIndex));

					nOfCopyLabel.setText(""+(currentBulkEditToolIndex+1));
				}
			}
		});
		headerLayout.add(toolsToLeftButton);


//		Button toolsToRightButton = UIUtils.createButton(VaadinIcon.ANGLE_RIGHT, ButtonVariant.LUMO_CONTRAST);
		toolsToRightButton.addClickShortcut(Key.ARROW_RIGHT, KeyModifier.CONTROL);
		UIUtils.setTooltip("Ctrl + Arrow Right", toolsToRightButton);
		toolsToRightButton.addClickListener(e -> {
			if (currentBulkEditToolIndex < (numberOfTools-1)) {
				InventoryEntity editedTool = bulkAdminToolForm.getTool();
				if (editedTool != null) {
					bulkEditTools.set(currentBulkEditToolIndex, editedTool);

					++currentBulkEditToolIndex;
					setToolBulkEditDialogContent(bulkEditTools.get(currentBulkEditToolIndex));

					nOfCopyLabel.setText(""+(currentBulkEditToolIndex+1));
				}
			}
		});
		headerLayout.add(toolsToRightButton);
	}


	public void initToolBulkEdit() {
		nOfCopyLabel.setText("1");

		currentBulkEditToolIndex = 0;
		nOfCopiesLabel.setText(""+numberOfTools);

		if (operationType.equals(OperationType.COPY)) {
			for (int i = 0; i < numberOfTools; i++) {
				InventoryEntity newTool = new InventoryEntity(bulkEditTool);
				bulkEditTools.add(newTool);
			}
		}

		toolBulkEditDialog = new CustomDialog();
		toolBulkEditDialog.setCloseOnEsc(false);
		toolBulkEditDialog.setCloseOnOutsideClick(false);

		toolBulkEditDialog.setHeader(headerLayout);

		setToolBulkEditDialogContent(bulkEditTools.get(currentBulkEditToolIndex));

		toolBulkEditDialog.getCancelButton().setText("Cancel");
		toolBulkEditDialog.getCancelButton().addClickListener(e -> {
			confirmBulkEditClose();
		});

		toolBulkEditDialog.setConfirmButton(UIUtils.createButton("Save All", ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));
		toolBulkEditDialog.getConfirmButton().addClickListener(e -> {

			InventoryEntity editedTool = bulkAdminToolForm.getTool();
			if (editedTool != null) {
				bulkEditTools.set(currentBulkEditToolIndex, editedTool);

				boolean errorOccur = false;

				for (InventoryEntity t : bulkEditTools) {
					if (t.getParentCategory() != null) {
						t.setLevel((t.getParentCategory().getLevel()+1));
					}

					if (operationType.equals(OperationType.EDIT)) {
						if (!InventoryFacade.getInstance().update(t)) {
							errorOccur = true;
						}
					} else if (operationType.equals(OperationType.COPY)) {
						if (!InventoryFacade.getInstance().insert(t)) {
							errorOccur = true;
						}
						adminInventory.getDataProvider().getItems().add(t);
					}
				}

				if (!errorOccur) {
					if (operationType.equals(OperationType.EDIT)) {
						UIUtils.showNotification("Tools(s) updated successfully", UIUtils.NotificationType.SUCCESS);
					} else if (operationType.equals(OperationType.COPY)) {
						UIUtils.showNotification("Tools(s) inserted successfully", UIUtils.NotificationType.SUCCESS);
					}
				} else  {
					if (operationType.equals(OperationType.EDIT)) {
						UIUtils.showNotification("Tools(s) update failed", UIUtils.NotificationType.ERROR);
					} else if (operationType.equals(OperationType.COPY)) {
						UIUtils.showNotification("Tools(s) insert failed", UIUtils.NotificationType.ERROR);
					}
				}

				toolBulkEditDialog.close();
				adminInventory.getDataProvider().refreshAll();
			}
		});
		toolBulkEditDialog.open();
	}


	private void setToolBulkEditDialogContent(InventoryEntity tool) {
		if (bulkAdminToolForm == null) {
			this.bulkAdminToolForm = new AdminToolBulkForm(this);
		}
		bulkAdminToolForm.setTool(tool);
		toolBulkEditDialog.setContent(bulkAdminToolForm);
	}

	private void confirmBulkEditClose() {
		CustomDialog dialog = new CustomDialog();
		dialog.setCloseOnEsc(false);
		dialog.setCloseOnOutsideClick(false);

		dialog.setHeader(UIUtils.createH4Label("Cancel tools edit?"));

		Paragraph content = new Paragraph();
		content.add(new Span("This will discard all made changes. Are you sure you want to cancel?"));

		dialog.setContent(content);

		dialog.getCancelButton().setText("No");
		dialog.getCancelButton().addClickListener(e -> dialog.close());
		dialog.setConfirmButton(UIUtils.createButton("Yes", ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY));
		dialog.getConfirmButton().addClickListener(e -> {
			dialog.close();
			toolBulkEditDialog.close();
		});
		dialog.open();
	}

	public void setBulkCompanies(Company company) {
		try {
			for (InventoryEntity t : bulkEditTools) {
				t.setCompany(company);
			}
			UIUtils.showNotification("Companies set successfully", UIUtils.NotificationType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			UIUtils.showNotification("Companies set failed", UIUtils.NotificationType.ERROR);
		}
	}

	public void setBulkCategories(InventoryEntity category) {
		try {
			for (InventoryEntity t : bulkEditTools) {
				if (category != null) {
					if (category.equals(InventoryFacade.getInstance().getRootCategory())) {
						category = null; //Must be set manually
					}
				}
				t.setParentCategory(category);
			}
			UIUtils.showNotification("Categories set successfully", UIUtils.NotificationType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			UIUtils.showNotification("Categories set failed", UIUtils.NotificationType.ERROR);
		}
	}

	public void setBulkUsageStatus(ToolStatus usageStatus) {
		try {
			for (InventoryEntity t : bulkEditTools) {
				t.setUsageStatus(usageStatus);
			}
			UIUtils.showNotification("Usage Status set successfully", UIUtils.NotificationType.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			UIUtils.showNotification("Usage Status set failed", UIUtils.NotificationType.ERROR);
		}
	}


	public void setBulkEditTools(List<InventoryEntity> bulkEditTools) {
		this.bulkEditTools = bulkEditTools;
	}

	public void setBulkEditTool(InventoryEntity bulkEditTool) {
		this.bulkEditTool = bulkEditTool;
	}
}
