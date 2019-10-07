package com.gmail.grigorij.ui.components;

import com.gmail.grigorij.ui.components.layouts.FlexBoxLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;

@CssImport(value = "./styles/views/dashboard.css", themeFor = "vaadin-details")
public class DashboardItem extends FlexBoxLayout {

	private final String CLASS_NAME = "dashboard-item";


	private FlexBoxLayout header, content, footer;

	public DashboardItem() {
		setClassName(CLASS_NAME);

		header = new FlexBoxLayout();
		header.addClassName(CLASS_NAME  + "__header");

		content = new FlexBoxLayout();
		content.addClassName(CLASS_NAME  + "__content");

		footer = new FlexBoxLayout();
		footer.addClassName(CLASS_NAME  + "__footer");

		add(header, content, footer);
	}

	public FlexBoxLayout getHeader() {
		return header;
	}

	public void setHeader(Component... components) {
		header.removeAll();
		header.add(components);
	}


	public FlexBoxLayout getContent() {
		return content;
	}

	public void setContent(Component... components) {
		content.removeAll();
		content.add(components);
	}


	public FlexBoxLayout getFooter() {
		return footer;
	}

	public void setFooter(Component... components) {
		footer.removeAll();
		footer.add(components);
	}
}
