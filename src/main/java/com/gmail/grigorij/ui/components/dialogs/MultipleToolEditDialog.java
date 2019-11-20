package com.gmail.grigorij.ui.components.dialogs;

import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.ui.components.forms.ToolForm;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.views.app.admin.AdminInventory;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MultipleToolEditDialog extends CustomDialog {

	private final static String CLASS_NAME = "multiple-tool-edit-dialog";
	private final AdminInventory adminInventory;
	private final ToolForm toolForm;

	private HashMap<Tool, List<String>> toolChangesHashMap = new HashMap<>();

	private Tool copyTool;
	private int numberOfCopies;
	private List<Tool> tools;

	private Label currentToolNumberLabel;
	private int currentToolNumber;
	private Label totalNumberOfToolsLabel;
	private int totalNumberOfTools;

	private boolean copyMode;
	private int currentToolIndex;


	public MultipleToolEditDialog(AdminInventory adminInventory) {
		this.adminInventory = adminInventory;
		toolForm = new ToolForm(this.adminInventory);

		addClassName(CLASS_NAME);

		setCloseOnEsc(false);
		setCloseOnOutsideClick(false);

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
		getConfirmButton().setText("Save All");
	}


	public void setData(Tool copyTool, int numberOfCopies, List<Tool> tools) {
		copyMode = false;

		this.copyTool = copyTool;
		this.numberOfCopies = numberOfCopies;

		if (tools == null) {
			tools = new ArrayList<>();
			copyMode = true;
		}
		this.tools = tools;

		if (tools.size() <= 0) {
			if (copyTool != null) {
				for (int i = 0; i < numberOfCopies; i++) {
					this.tools.add(new Tool(copyTool));
				}
			}
		}

		for (Tool tool : tools) {
			toolChangesHashMap.put(tool, new ArrayList<>());
		}

		constructHeader();

		currentToolIndex = 0;

		toolForm.setTool(tools.get(currentToolIndex));
		setContent(toolForm);
	}


	private void constructHeader() {
		Div multipleToolHeader = new Div();
		multipleToolHeader.addClassName(CLASS_NAME + "__header");

		currentToolNumber = 1;
		totalNumberOfTools = tools.size();

		multipleToolHeader.add(UIUtils.createH3Label("Tool Details"));

		currentToolNumberLabel = UIUtils.createH4Label(String.valueOf(currentToolNumber));
		multipleToolHeader.add(currentToolNumberLabel);

		multipleToolHeader.add(UIUtils.createH4Label("/"));

		totalNumberOfToolsLabel = UIUtils.createH4Label(String.valueOf(totalNumberOfTools));
		multipleToolHeader.add(totalNumberOfToolsLabel);

		Div headerButtonsDiv = new Div();
		headerButtonsDiv.addClassName(CLASS_NAME + "__header-buttons");

		if (copyMode) {
			Button addToolCopyButton = UIUtils.createButton(VaadinIcon.PLUS_CIRCLE, ButtonVariant.LUMO_PRIMARY);
			addToolCopyButton.addClickListener(addToolEvent -> addToolCopy());
			headerButtonsDiv.add(addToolCopyButton);


			Button removeToolCopyButton = UIUtils.createButton(VaadinIcon.TRASH, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
			removeToolCopyButton.addClickListener(removeToolEvent -> removeToolCopy());
			headerButtonsDiv.add(removeToolCopyButton);
		}

		Button moveLeftButton = UIUtils.createButton(VaadinIcon.ANGLE_LEFT, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
		moveLeftButton.addClassName("move-left-button");
		moveLeftButton.addClickListener(addToolEvent -> moveLeft());
		headerButtonsDiv.add(moveLeftButton);

		Button moveRightButton = UIUtils.createButton(VaadinIcon.ANGLE_RIGHT, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
		moveRightButton.addClickListener(addToolEvent -> moveRight());
		headerButtonsDiv.add(moveRightButton);

		multipleToolHeader.add(headerButtonsDiv);

		setHeader(multipleToolHeader);
	}


	private void addToolCopy() {
		Tool newToolCopy = new Tool(tools.get(currentToolIndex));

		tools.add(newToolCopy);
		toolChangesHashMap.put(newToolCopy, new ArrayList<>());

		totalNumberOfTools++;
		totalNumberOfToolsLabel.setText(String.valueOf(totalNumberOfTools));
	}

	private void removeToolCopy() {
		if (tools.size() > 1) {
			int toolIndexToRemove = currentToolIndex;

			toolChangesHashMap.remove(tools.get(currentToolIndex));

			if (currentToolNumber == tools.size()) {
				currentToolNumber--;
				currentToolIndex--;
			}

			tools.remove(toolIndexToRemove);

			totalNumberOfTools--;

			currentToolNumberLabel.setText(String.valueOf(currentToolNumber));
			totalNumberOfToolsLabel.setText(String.valueOf(totalNumberOfTools));

			toolForm.setTool(tools.get(currentToolIndex));
		}
	}

	private void moveLeft() {
		if (toolForm.getTool() != null) {
			if (currentToolIndex > 0) {

				if (!copyMode) {
					toolChangesHashMap.get(tools.get(currentToolIndex)).addAll(toolForm.getChanges());
				}

				tools.set(currentToolIndex, toolForm.getTool());

				currentToolIndex--;
				currentToolNumber--;

				currentToolNumberLabel.setText(String.valueOf(currentToolNumber));

				toolForm.setTool(tools.get(currentToolIndex));
			}
		}
	}

	private void moveRight() {
		if (toolForm.getTool() != null) {
			if (currentToolIndex < (tools.size() - 1)) {

				if (!copyMode) {
					toolChangesHashMap.get(tools.get(currentToolIndex)).addAll(toolForm.getChanges());
				}

				tools.set(currentToolIndex, toolForm.getTool());

				currentToolIndex++;
				currentToolNumber++;

				currentToolNumberLabel.setText(String.valueOf(currentToolNumber));

				toolForm.setTool(tools.get(currentToolIndex));
			}
		}
	}


	public List<Tool> getTools() {
		if (toolForm.getTool() != null) {
			if (!copyMode) {
				toolChangesHashMap.get(tools.get(currentToolIndex)).addAll(toolForm.getChanges());
			}

			tools.set(currentToolIndex, toolForm.getTool());

			return tools;
		}

		return null;
	}

	public HashMap<Tool, List<String>> getToolChangesHashMap() {
		return toolChangesHashMap;
	}
}
