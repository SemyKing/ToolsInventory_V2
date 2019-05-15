package com.gmail.grigorij.ui.authentication;

import com.gmail.grigorij.backend.database.Facades.CompanyFacade;
import com.gmail.grigorij.backend.database.Facades.UserFacade;
import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.user.AccessGroups;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.components.ClosableNotification;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

import javax.servlet.http.Cookie;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.vaadin.flow.server.VaadinSession.getCurrent;

/**
 *
 */
public class AuthService {

    private static final String COOKIE_NAME = "remember_me_cookie";
    private static final String SESSION_DATA = "SESSION_DATA";

//			getCurrentRequest().getWrappedSession().removeAttribute(SESSION_DATA);
//			getCurrentRequest().getWrappedSession().setAttribute(SESSION_DATA, currentSessionObj);
//          CurrentSession currentSession = (CurrentSession) getCurrentRequest().getWrappedSession().getAttribute(SESSION_DATA);


    public static boolean isAuthenticated() {
        return getCurrentRequest().getWrappedSession().getAttribute(SESSION_DATA) != null || loginRememberedUser();
    }


    static boolean signIn(String username, String password, boolean rememberMe) {
        if (username == null || username.isEmpty())
            return false;

        if (password == null || password.isEmpty())
            return false;


        User user = UserFacade.getInstance().findUserInDatabase(username, password);

        if (user != null) {

            constructSessionData(user.getUsername());

            if (rememberMe) {
                rememberUser(username);
            }

            return true;
        } else {
            return false;
        }
    }


    private static void constructSessionData(String username) {

        User user = UserFacade.getInstance().findUserInDatabaseByUsername(username);

        if (user != null) {
            if (user.isDeleted()) {
                System.out.println("------DELETED USER LOGIN------");
                System.out.println("Username: " + user.getUsername());

                ClosableNotification.showNotification("Your credentials have expired",
                        TimeUnit.MINUTES.toMillis(1), Notification.Position.TOP_CENTER);

                signOut();
            }
        } else {
            System.out.println("------USER IS NULL------");
            signOut();
        }

        Company company = null;

        if (user.getAccess_group() != AccessGroups.ADMIN.value()) {
            company = CompanyFacade.getInstance().findCompanyInDatabaseById(user.getCompany_id());

            if (company == null) {
                System.out.println("------COMPANY IS NULL------");
                System.out.println("------NON ADMIN USER SHOULD BE ASSIGNED TO COMPANY------");
                signOut();
            }
        }


        CurrentSession.getInstance().setUser(user);
        CurrentSession.getInstance().setCompany(company);

        System.out.println("------USER SET: " + CurrentSession.getInstance().getUser());
        System.out.println("------COMP SET: " + CurrentSession.getInstance().getCompany());

        getCurrentRequest().getWrappedSession().setAttribute(SESSION_DATA, CurrentSession.getInstance());
    }


    public static void signOut() {
        Optional<Cookie> cookie = getRememberMeCookie();
        if (cookie.isPresent()) {
            String id = cookie.get().getValue();
            UserService.removeRememberedUser(id);
            deleteRememberMeCookie();
        }

        getCurrentRequest().getWrappedSession().removeAttribute(SESSION_DATA);
        getCurrent().getSession().invalidate();
        UI.getCurrent().getPage().reload();
    }



    private static boolean loginRememberedUser() {
        Optional<Cookie> rememberMeCookie = getRememberMeCookie();

        if (rememberMeCookie.isPresent()) {
            String id = rememberMeCookie.get().getValue();
            String username = UserService.getRememberedUser(id);

            if (username != null) {
                constructSessionData(username);
                return true;
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
        String id = UserService.rememberUser(username);

        Cookie cookie = new Cookie(COOKIE_NAME, id);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30);
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