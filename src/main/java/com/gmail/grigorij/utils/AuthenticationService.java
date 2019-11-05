package com.gmail.grigorij.utils;

import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.facades.TransactionFacade;
import com.gmail.grigorij.backend.database.facades.UserFacade;
import com.gmail.grigorij.backend.database.entities.Transaction;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.enums.operations.Operation;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

import javax.servlet.http.Cookie;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Handle User authentication
 */
public class AuthenticationService {

	private static final String COOKIE_NAME = "remember_me_cookie";
	private static final String SESSION_DATA = "session_data";

	private static SecureRandom random = new SecureRandom();
	private static Map<String, String> rememberedUsers = new HashMap<>();


	public static boolean isAuthenticated() {
		return getCurrentRequest().getWrappedSession().getAttribute(SESSION_DATA) != null || loginRememberedUser();
	}

	public static User getCurrentSessionUser() {
		return UserFacade.getInstance().getUserById((Long) getCurrentRequest().getWrappedSession().getAttribute(SESSION_DATA));
	}

	private static void setCurrentSessionUser(Long userId) {
		if (userId == null) {
			return;
		}
		getCurrentRequest().getWrappedSession().removeAttribute(SESSION_DATA);
		getCurrentRequest().getWrappedSession().setAttribute(SESSION_DATA, userId);
	}

	public static boolean signIn(String username, String password, boolean rememberMe) {
		if (username == null || username.isEmpty())
			return false;

		if (password == null || password.isEmpty())
			return false;

		User user = UserFacade.getInstance().getUserByUsernameAndPassword(username, password);

		if (user != null) {
			if (!constructSessionData(user, null)) {
				return false;
			}
			if (rememberMe) {
				rememberUser(username);
			}
			return true;
		} else {
			return false;
		}
	}

	public static void signOut() {
		Optional<Cookie> cookie = getRememberMeCookie();
		if (cookie.isPresent()) {
			String id = cookie.get().getValue();
			rememberedUsers.remove(id);
			deleteRememberMeCookie();
		}


		Broadcaster.removeBroadcasterForUser(getCurrentSessionUser().getId());

		Transaction transaction = new Transaction();
		transaction.setUser(AuthenticationService.getCurrentSessionUser());
		transaction.setCompany(AuthenticationService.getCurrentSessionUser().getCompany());
		transaction.setOperation(Operation.LOG_OUT);
		TransactionFacade.getInstance().insert(transaction);

		getCurrentRequest().getWrappedSession().removeAttribute(SESSION_DATA);
		UI.getCurrent().getSession().close();
		UI.getCurrent().getPage().reload();
	}


	private static boolean constructSessionData(User user, String username) {
		if (user == null) {
			user = UserFacade.getInstance().getUserByUsername(username);
		}

		if (user == null) {
			System.out.println("\nLOGIN FAIL, user not found (NULL)");
			return false;
		} else {
			if (user.isDeleted()) {
				UIUtils.showNotification("Your credentials have expired", UIUtils.NotificationType.INFO, 0);
				System.out.println("\nLOGIN FAIL, user: '" + user.getFullName() + "' set as 'deleted'");
				return false;
			}

			if (user.getCompany() == null) {
				UIUtils.showNotification("Company is NULL", UIUtils.NotificationType.ERROR);
				System.err.println("\nLOGIN FAIL, NULL company");
				return false;
			}
		}

		setCurrentSessionUser(user.getId());

		return true;
	}

	private static boolean loginRememberedUser() {
		Optional<Cookie> rememberMeCookie = getRememberMeCookie();

		if (rememberMeCookie.isPresent()) {
			String id = rememberMeCookie.get().getValue();
			String username = rememberedUsers.get(id);

			if (username != null) {
				return constructSessionData(null, username);
			}
		}

		return false;
	}

	private static Optional<Cookie> getRememberMeCookie() {
		Cookie[] cookies = getCurrentRequest().getCookies();
		if (cookies != null) {
			return Arrays.stream(cookies).filter(c -> c.getName().equals(COOKIE_NAME)).findFirst();
		}

		return Optional.empty();
	}

	private static void rememberUser(String username) {
		String id = new BigInteger(130, random).toString(32);

		System.out.println();
		System.out.println("New user to remember---");
		System.out.println("id: " + id);
		System.out.println("username: " + username);

		rememberedUsers.put(id, username);

		Cookie cookie = new Cookie(COOKIE_NAME, id);
		cookie.setPath("/");
		cookie.setMaxAge((int) TimeUnit.HOURS.toSeconds(24));
		VaadinService.getCurrentResponse().addCookie(cookie);
	}

	private static void deleteRememberMeCookie() {
		Cookie cookie = new Cookie(COOKIE_NAME, "");
		cookie.setPath("/");
		cookie.setMaxAge(0);
		VaadinService.getCurrentResponse().addCookie(cookie);
	}

	private static VaadinRequest getCurrentRequest() {
		VaadinRequest request = VaadinService.getCurrentRequest();
		if (request == null) {
			throw new IllegalStateException("No request bound to current thread.");
		}
		return request;
	}
}