package com.gmail.grigorij.ui.views.navigation.messages;

import com.gmail.grigorij.ui.MainLayout;
import com.gmail.grigorij.ui.components.FlexBoxLayout;
import com.gmail.grigorij.ui.layout.size.Horizontal;
import com.gmail.grigorij.ui.layout.size.Vertical;
import com.gmail.grigorij.ui.views.frames.ViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(layout = MainLayout.class)
@PageTitle("Messages")
public class Messages extends ViewFrame {

	public Messages() {
		setId("messages");
		setViewContent(createContent());
	}

	private Component createContent() {
		FlexBoxLayout content = new FlexBoxLayout(new Span("Messages View"));
		content.setMargin(Horizontal.AUTO);
		content.setPadding(Horizontal.RESPONSIVE_L, Vertical.L);
		return content;
	}
}
