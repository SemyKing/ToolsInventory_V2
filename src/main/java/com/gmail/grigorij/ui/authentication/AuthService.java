package com.gmail.grigorij.ui.authentication;

import com.gmail.grigorij.backend.database.Database;
import com.gmail.grigorij.backend.entities.Company;
import com.gmail.grigorij.backend.entities.User;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

import javax.servlet.http.Cookie;

import java.util.Arrays;
import java.util.Optional;

import static com.vaadin.flow.server.VaadinSession.getCurrent;

/**
 *
 */
public class AuthService {

    private static final String COOKIE_NAME = "remember_me_cookie";
    private static final String SESSION_USERNAME = "session_username";


    public static boolean isAuthenticated() {
        return VaadinSession.getCurrent().getAttribute(SESSION_USERNAME) != null || loginRememberedUser();
    }


    public static boolean signIn(String username, String password, boolean rememberMe) {
        if (username == null || username.isEmpty())
            return false;

        if (password == null || password.isEmpty())
            return false;

        User currentUser = Database.getInstance().getUser(username, password);
        if (currentUser != null) {
            Company currentCompany = Database.getInstance().getCompanyById(currentUser.getCompany_id());

            if (currentCompany == null) {
                Notification.show("Company not found!");
                System.err.println("User not assigned to any company");
                return false;
            }

            VaadinSession.getCurrent().setAttribute(SESSION_USERNAME, username);

            CurrentSession.setUser(currentUser);
            CurrentSession.setCompany(currentCompany);


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
            UserService.removeRememberedUser(id);
            deleteRememberMeCookie();
        }

        getCurrent().getSession().invalidate();
        UI.getCurrent().getPage().reload();
    }



    private static boolean loginRememberedUser() {
        Optional<Cookie> rememberMeCookie = getRememberMeCookie();

        if (rememberMeCookie.isPresent()) {
            String id = rememberMeCookie.get().getValue();
            String username = UserService.getRememberedUser(id);

            if (username != null) {
                VaadinSession.getCurrent().setAttribute(SESSION_USERNAME, username);
                return true;
            }
        }

        return false;
    }


    private static Optional<Cookie> getRememberMeCookie() {
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
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
}