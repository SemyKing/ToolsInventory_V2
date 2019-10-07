package com.gmail.grigorij.utils;

import com.vaadin.flow.server.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = {"/*"}, name = "MySessionServlet", asyncSupported = true, initParams = {
		@WebInitParam(name = Constants.I18N_PROVIDER, value = "com.gmail.grigorij.utils.TranslationProvider") })
//@VaadinServletConfiguration(productionMode = true)
public class SessionServlet extends VaadinServlet implements SessionInitListener, SessionDestroyListener{


	@Override
	protected void servletInitialized() throws ServletException {
		super.servletInitialized();
		getService().addSessionInitListener(this);
		getService().addSessionDestroyListener(this);
	}

	@Override
	public void sessionInit(SessionInitEvent event) {
		if (event.getSession() != null) {
			System.out.println("-----New Session Started-----");
		}
	}

	@Override
	public void sessionDestroy(SessionDestroyEvent event) {
		if (event.getSession() != null) {
			System.out.println("-----Session Has Ended-----");
		}
	}
}