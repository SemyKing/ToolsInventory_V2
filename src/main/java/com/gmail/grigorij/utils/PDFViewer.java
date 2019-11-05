package com.gmail.grigorij.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.server.StreamResource;


//@Tag("pdf-browser-viewer")
@Tag("object")
public class PDFViewer extends Component implements HasSize {


	public PDFViewer(StreamResource resource) {
		this();
		getElement().setAttribute("data", resource);
	}

	public PDFViewer(String url) {
		this();
		getElement().setAttribute("data", url);
	}

	private PDFViewer() {
		getElement().setAttribute("type", "application/pdf");
		setSizeFull();
	}
}
