package com.gmail.grigorij.ui.views.routes;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;

import javax.servlet.http.HttpServletResponse;


@Tag(Tag.DIV)
public class InvalidRouteReRouter extends Component implements HasErrorParameter<NotFoundException> {

	@Override
	public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<NotFoundException> errorParameter) {
		beforeEnterEvent.rerouteTo("");
		return HttpServletResponse.SC_NOT_FOUND;
	}
}
