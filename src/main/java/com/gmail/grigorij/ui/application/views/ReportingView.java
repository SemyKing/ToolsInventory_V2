package com.gmail.grigorij.ui.application.views;

import com.gmail.grigorij.ui.utils.UIUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;


@CssImport("./styles/views/reporting.css")
public class ReportingView extends Div {

	private static final String CLASS_NAME = "reporting";


	public ReportingView() {
		addClassName(CLASS_NAME);

		Div contentWrapper = new Div();
		contentWrapper.addClassName(CLASS_NAME + "__content-wrapper");

		contentWrapper.add(constructHeader());
		contentWrapper.add(constructContent());

		add(contentWrapper);
//		add(constructDetails());
	}


	private Div constructHeader() {
		Div header = new Div();
		header.addClassName(CLASS_NAME + "__header");

		return header;
	}

	private Div constructContent() {
		Div content = new Div();
		content.addClassName(CLASS_NAME + "__content");

		content.add(constructControls());

		return content;
	}

	private Div constructControls() {
		Div controlsDiv = new Div();
		controlsDiv.addClassName(CLASS_NAME + "__content-controls");


		return controlsDiv;
	}


}
