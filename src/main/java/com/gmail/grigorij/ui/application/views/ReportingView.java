package com.gmail.grigorij.ui.application.views;

import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.ui.components.dialogs.PDF_Dialog;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.pdf.PDF_ReportGenerator;
import com.gmail.grigorij.utils.AuthenticationService;
import com.gmail.grigorij.utils.PDFViewer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;


@CssImport("./styles/views/reporting.css")
public class ReportingView extends Div {

	private static final String CLASS_NAME = "reporting";

	private Grid<User> grid;
	private List<User> selectedUsers = new ArrayList<>();


	public ReportingView() {
		addClassName(CLASS_NAME);

		Div contentWrapper = new Div();
		contentWrapper.addClassName(CLASS_NAME + "__content-wrapper");

		contentWrapper.add(constructHeader());
		contentWrapper.add(constructContent());

		add(contentWrapper);
	}


	private Div constructHeader() {
		Div header = new Div();
		header.addClassName(CLASS_NAME + "__header");

		Button constructReportButton = UIUtils.createButton("Generate Report", VaadinIcon.CLIPBOARD_TEXT, ButtonVariant.LUMO_PRIMARY);
		constructReportButton.addClickListener(e -> constructReport());

		header.add(constructReportButton);

		return header;
	}

	private Div constructContent() {
		Div content = new Div();
		content.addClassName(CLASS_NAME + "__content");

		content.add(constructGrid());

		return content;
	}

	private Grid constructGrid() {
		grid = new Grid<>();
		grid.setClassName("grid-view");
		grid.setSizeFull();

		grid.setItems(UserFacade.getInstance().getUsersInCompany(AuthenticationService.getCurrentSessionUser().getCompany().getId()));

		grid.addColumn(User::getFullName)
				.setHeader("User")
				.setFlexGrow(1);

		grid.addColumn(user -> InventoryFacade.getInstance().getAllToolsByCurrentUserId(user.getId()).size())
				.setHeader("Tools In Use")
				.setFlexGrow(1);

		grid.setSelectionMode(Grid.SelectionMode.MULTI);

		grid.addSelectionListener(event -> selectedUsers = new ArrayList<>(event.getAllSelectedItems()));

		return grid;
	}

	private void constructReport() {
		if (selectedUsers.size() <= 0) {
			UIUtils.showNotification("Select users for report", NotificationVariant.LUMO_PRIMARY);
			return;
		}

		PDF_ReportGenerator pdfReportGenerator = new PDF_ReportGenerator(selectedUsers);

		if (pdfReportGenerator.hasErrors()) {
			System.err.println("PDF report generation error");
			return;
		}

		StreamResource streamResource = new StreamResource("Report", () -> new ByteArrayInputStream(pdfReportGenerator.getPDFByteArray()));
		streamResource.setContentType("application/pdf");
		streamResource.setCacheTime(0);


		PDF_Dialog dialog = new PDF_Dialog();
		dialog.setContent(new PDFViewer(streamResource));
		dialog.open();

		dialog.addDetachListener(e -> {
			if (!pdfReportGenerator.getPDF_File().delete()) {
				System.err.println("PDF file: " + pdfReportGenerator.getPDF_File().getName() +  " was not deleted");
			}
		});
	}
}
