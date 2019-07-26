package com.gmail.grigorij;

import com.vaadin.flow.server.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = {"/*"}, name = "MySessionServlet", asyncSupported = true, initParams = {
		@WebInitParam(name = Constants.I18N_PROVIDER, value = "com.gmail.grigorij.utils.TranslationProvider") })
//@VaadinServletConfiguration(productionMode = true)
public class SessionServlet extends VaadinServlet {


	@Override
	protected void servletInitialized() throws ServletException {
		super.servletInitialized();
		getService().addSessionInitListener(new VaadinSessionListener.VaadinSessionInitListener());
		getService().addSessionDestroyListener(new VaadinSessionListener.VaadinSessionDestroyListener());
	}
}