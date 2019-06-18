package com.gmail.grigorij.ui.views.navigation.reporting;

import com.gmail.grigorij.ui.utils.components.FlexBoxLayout;
import com.gmail.grigorij.ui.utils.css.size.Horizontal;
import com.gmail.grigorij.ui.utils.css.size.Vertical;
import com.gmail.grigorij.ui.utils.frames.ViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;

@PageTitle("Reporting")
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
