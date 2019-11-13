package com.gmail.grigorij.ui.views.routes;

import com.gmail.grigorij.backend.database.entities.PDF_Report;
import com.gmail.grigorij.backend.database.facades.PDF_Facade;
import com.gmail.grigorij.ui.components.PDF_Component;
import com.gmail.grigorij.utils.AuthenticationService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;


@Route(value = "report")
public class PDF_PageView extends Div implements HasUrlParameter<String> {

	private Div content;
	private String pdfName;

	public PDF_PageView() {
		setSizeFull();

		content = new Div();
		content.setSizeFull();

		add(content);
	}

	@Override
	public void setParameter(BeforeEvent event, String pdfName) {

		//AUTHENTICATION
		if (!AuthenticationService.isAuthenticated()) {
			event.rerouteTo("");
		}

		this.pdfName = pdfName.replaceAll("[^a-zA-Z0-9_]", "");

		findPDF(event);
	}

	private void findPDF(BeforeEvent event) {
		PDF_Report pdf_entity = PDF_Facade.getInstance().getPDF_ReportByName(pdfName);

		if (pdf_entity == null) {
			event.rerouteTo("");
		} else {
			constructPDF_Report(pdf_entity.getBytes());
		}
	}

	private void constructPDF_Report(byte[] bytes) {
		StreamResource streamResource = new StreamResource("Report", () -> new ByteArrayInputStream(bytes));
		streamResource.setContentType("application/pdf");
		streamResource.setCacheTime(0);

		PDF_Component pdfViewer = new PDF_Component(streamResource);

		content.removeAll();
		content.add(pdfViewer);
	}
}
