package com.gmail.grigorij.ui.components.dialogs;

import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.html.Span;


public class ConfirmDialog extends CustomDialog {

	private static final String CONFIRM_DIALOG_ROW = "confirm-dialog-row";


	public ConfirmDialog(String message) {
		getHeader().add(UIUtils.createH3Label("Confirm Action"));

		String[] lines = message.split(ProjectConstants.NEW_LINE);

		for (String line : lines) {
			Span row = new Span(line);
			row.addClassName(CONFIRM_DIALOG_ROW);

			getContent().add(row);
		}

		getCancelButton().setText("No");
		getConfirmButton().setText("Yes");

		setCloseOnEsc(false);
		setCloseOnOutsideClick(false);
	}


	public void closeOnCancel() {
		getCancelButton().addClickListener(e -> close());
	}
}
