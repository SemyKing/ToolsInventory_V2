package com.gmail.grigorij.ui.authentication;

import com.gmail.grigorij.backend.entities.Company;
import com.gmail.grigorij.backend.entities.User;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;


/**
 * Class for retrieving and setting the User and Company of the current session (without using JAAS).
 * All methods of this class require that a
 * {@link VaadinRequest} is bound to the current thread.
 *
 * @see VaadinService#getCurrentRequest()
 */
public final class CurrentSession {

	private static final String CURRENT_SESSION_ATTRIBUTE_KEY = CurrentSession.class.getCanonicalName();
	private static final String CURRENT_USER_ATTRIBUTE_KEY = CURRENT_SESSION_ATTRIBUTE_KEY + "_USER";
	private static final String CURRENT_COMPANY_ATTRIBUTE_KEY = CURRENT_SESSION_ATTRIBUTE_KEY + "_COMPANY";

	private CurrentSession() {
	}


	/**
	 * Sets the User object of the current user and stores it in the current session.
	 * Using a {@code null} userObj will remove the User from the session.
	 *
	 * @throws IllegalStateException
	 *             if the current session cannot be accessed.
	 */
	public static void setUser(User userObj) {
		if (userObj == null) {
			getCurrentRequest().getWrappedSession().removeAttribute(CURRENT_USER_ATTRIBUTE_KEY);
		} else {
			getCurrentRequest().getWrappedSession().setAttribute(CURRENT_USER_ATTRIBUTE_KEY, userObj);
		}
	}


	/**
	 * Sets the User object of the current user and stores it in the current session.
	 * Using a {@code null} userObj will remove the User from the session.
	 *
	 * @throws IllegalStateException
	 *             if the current session cannot be accessed.
	 */
	public static void setCompany(Company companyObj) {
		if (companyObj == null) {
			getCurrentRequest().getWrappedSession().removeAttribute(CURRENT_COMPANY_ATTRIBUTE_KEY);
		} else {
			getCurrentRequest().getWrappedSession().setAttribute(CURRENT_COMPANY_ATTRIBUTE_KEY, companyObj);
		}
	}


	/**
	 * Returns the User object of the current user stored in the current session, or
	 * null if no User is stored.
	 *
	 * @throws IllegalStateException
	 *             if the current session cannot be accessed.
	 */
	public static User getUser() {
		User currentUser = (User) getCurrentRequest().getWrappedSession().getAttribute(CURRENT_USER_ATTRIBUTE_KEY);
		return currentUser;
	}


	/**
	 * Returns the Company object of the current user stored in the current session, or
	 * null if no Company is stored.
	 *
	 * @throws IllegalStateException
	 *             if the current session cannot be accessed.
	 */
	public static Company getCompany() {
		Company currentCompany = (Company) getCurrentRequest().getWrappedSession().getAttribute(CURRENT_COMPANY_ATTRIBUTE_KEY);
		return currentCompany;
	}


	private static VaadinRequest getCurrentRequest() {
		VaadinRequest request = VaadinService.getCurrentRequest();
		if (request == null) {
			throw new IllegalStateException("No request bound to current thread.");
		}
		return request;
	}
}
