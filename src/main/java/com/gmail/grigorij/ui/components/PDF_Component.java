package com.gmail.grigorij.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.server.StreamResource;

@Tag("object")
public class PDF_Component extends Component implements HasSize {


	public PDF_Component(StreamResource resource) {
		this();
		getElement().setAttribute("data", resource);
	}

	public PDF_Component(String url) {
		this();
		getElement().setAttribute("data", url);
	}

	private PDF_Component() {
		getElement().setAttribute("type", "application/pdf");
		setSizeFull();
	}
}
