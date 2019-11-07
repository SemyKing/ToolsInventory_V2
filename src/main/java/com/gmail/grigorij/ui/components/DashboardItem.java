package com.gmail.grigorij.ui.components;

import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;


@CssImport(value = "./styles/views/dashboard.css", themeFor = "vaadin-details")
public class DashboardItem extends FlexBoxLayout {

	private final String CLASS_NAME = "dashboard-item";

	private Div content, contentLeft, contentRight, footer;


	public DashboardItem() {
		setClassName(CLASS_NAME);

		content = new Div();
		content.addClassName(CLASS_NAME  + "__content");

		contentLeft = new Div();
		contentLeft.addClassName(CLASS_NAME  + "__content-left");

		contentRight = new Div();
		contentRight.addClassName(CLASS_NAME  + "__content-right");

		content.add(contentLeft, contentRight);

		footer = new Div();
		footer.addClassName(CLASS_NAME  + "__footer");

		add(content, footer);
	}


	public Div getContent() {
		return content;
	}

	public Div getContentLeft() {
		return contentLeft;
	}

	public Div getContentRight() {
		return contentRight;
	}

	public Div getFooter() {
		return footer;
	}
}
