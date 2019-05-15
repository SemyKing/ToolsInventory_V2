package com.gmail.grigorij;

import com.gmail.grigorij.ui.authentication.AuthService;
import com.gmail.grigorij.ui.authentication.LoginView;
import com.vaadin.flow.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is used to listen to BeforeEnter event of all UIs in order to
 * check whether a user is signed in or not before allowing entering any page.
 * It is registered in a file named
 * com.vaadin.flow.server.VaadinServiceInitListener in META-INF/services.
 */
public class ApplicationServiceInitListener implements VaadinServiceInitListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceInitListener.class);

    private static int i = 0;

    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        serviceInitEvent.getSource().addUIInitListener(uiInitEvent -> uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {

//            if ( logger.isDebugEnabled()) logger.debug( "Entering view class: " + enterEvent.getNavigationTarget().getName());
//            if ( logger.isDebugEnabled()) logger.debug( "isAuthenticated():  " + AuthService.isAuthenticated());

            System.out.println();
            System.out.println( "Entering view class: " + enterEvent.getNavigationTarget().getName() );
            System.out.println( "isAuthenticated():  " + AuthService.isAuthenticated() );

            if (!AuthService.isAuthenticated() && !LoginView.class.equals(enterEvent.getNavigationTarget())) {

                System.out.println("---> REROUTE TO LOGIN [->]");
                enterEvent.rerouteTo(LoginView.class);
            }
        }));
    }


}
