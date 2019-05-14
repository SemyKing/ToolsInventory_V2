package com.gmail.grigorij;

import com.gmail.grigorij.ui.authentication.AuthService;
import com.gmail.grigorij.ui.authentication.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.*;


/**
 * This class is used to listen to BeforeEnter event of all UIs in order to
 * check whether a user is signed in or not before allowing entering any page.
 * It is registered in a file named
 * com.vaadin.flow.server.VaadinServiceInitListener in META-INF/services.
 */
public class ViewInitListener implements VaadinServiceInitListener {

    private static int i = 0;

    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        serviceInitEvent.getSource().addUIInitListener(uiInitEvent -> uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {

            System.out.println();
            System.out.println("event:             " + i);
            System.out.println("entering:          " + enterEvent.getNavigationTarget().getName());
            System.out.println("isAuthenticated():  " + AuthService.isAuthenticated());
            System.out.println("LoginView.class:   " + LoginView.class.equals(enterEvent.getNavigationTarget()));
            i++;

            if (LoginView.class.equals(enterEvent.getNavigationTarget())) {
                UI.getCurrent().navigate("login");
            }



            if (!AuthService.isAuthenticated() && !LoginView.class.equals(enterEvent.getNavigationTarget())) {

                System.out.println("---> REROUTE TO LOGIN [->]");
                enterEvent.rerouteTo(LoginView.class);
            }
        }));
    }
}
