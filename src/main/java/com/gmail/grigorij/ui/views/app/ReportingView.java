package com.gmail.grigorij.ui.views.app;

import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.backend.database.facades.PDF_Facade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.ui.components.PDF_Component;
import com.gmail.grigorij.ui.components.dialogs.PDF_Dialog;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.authentication.AuthenticationService;
import com.gmail.grigorij.utils.pdf.PDF_ReportConstructor;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;


@CssImport("./styles/views/reporting.css")
public class ReportingView extends Div {

	private static final String CLASS_NAME = "reporting";

	private Grid<User> grid;
//	private List<User> selectedUsers = new ArrayList<>();


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

		grid.setItems(UserFacade.getInstance().getAllActiveUsersInCompany(AuthenticationService.getCurrentSessionUser().getCompany().getId()));

		grid.addColumn(User::getFullName)
				.setHeader("User")
				.setFlexGrow(1);

		grid.addColumn(user -> InventoryFacade.getInstance().getAllToolsByCurrentUserId(user.getId()).size())
				.setHeader("Tools In Use")
				.setFlexGrow(1);

		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		grid.setSelectionMode(Grid.SelectionMode.MULTI);


//		grid.addSelectionListener(event -> selectedUsers = new ArrayList<>(event.getAllSelectedItems()));

		return grid;
	}


	private void constructReport() {
		if (grid.getSelectedItems().size() <= 0) {
			UIUtils.showNotification("Select users for report", NotificationVariant.LUMO_PRIMARY);
			return;
		}

		PDF_ReportConstructor reportConstructor = new PDF_ReportConstructor(new ArrayList<>(grid.getSelectedItems()));

		if (reportConstructor.generateReport(
				PDF_Facade.getInstance().getPDF_TemplateByCompany(AuthenticationService.getCurrentSessionUser().getCompany().getId()))) {

			StreamResource streamResource = new StreamResource("Report", () -> new ByteArrayInputStream(reportConstructor.getPDF_ByteArray()));
			streamResource.setContentType("application/pdf");
			streamResource.setCacheTime(0);

			PDF_Dialog dialog = new PDF_Dialog();
			dialog.setContent(new PDF_Component(streamResource));
			dialog.open();
		}

//		PDF_Report pdf_report = new PDF_Report();
//		pdf_report.setBytes(pdfConstructor.getPDF_ByteArray());
//		PDF_Facade.getInstance().insert(pdf_report);

//		String stringURL = "report/" + pdf_report.getName();
//		UI.getCurrent().getPage().executeJs("window.open('"+ stringURL +"', '_blank')");
	}
}
