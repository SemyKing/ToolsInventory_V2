package com.gmail.grigorij.ui.views.navigation.reporting;

import com.gmail.grigorij.ui.MainLayout;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.layout.size.Horizontal;
import com.gmail.grigorij.ui.layout.size.Vertical;
import com.gmail.grigorij.ui.views.frames.ViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;

@PageTitle("Reporting")
@Route(value = "reporting", layout = MainLayout.class)
public class Reporting extends ViewFrame {

	public Reporting() {
		setId("reporting");
		setViewContent(createContent());
	}

	private Component createContent() {
		FlexBoxLayout content = new FlexBoxLayout(new Span("Reporting View"));
		content.setMargin(Horizontal.AUTO);
		content.setPadding(Horizontal.RESPONSIVE_L, Vertical.L);
		return content;
	}
}