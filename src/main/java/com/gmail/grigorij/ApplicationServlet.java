package com.gmail.grigorij;

import com.vaadin.flow.server.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@WebServlet(value = "/*", asyncSupported = true)
public class ApplicationServlet extends VaadinServlet implements SessionInitListener, SessionDestroyListener {

	@Override
	protected void servletInitialized() throws ServletException {
		super.servletInitialized();
		getService().addSessionInitListener(this);
		getService().addSessionDestroyListener(this);
	}

	@Override
	public void sessionInit(SessionInitEvent event) throws ServiceException {
		System.out.println("-----ApplicationServlet session initialization");
	}

	@Override
	public void sessionDestroy(SessionDestroyEvent event) {
		System.out.println("-----ApplicationServlet session destruction");
	}
}
