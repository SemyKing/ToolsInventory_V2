package com.gmail.grigorij;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.communication.PushMode;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ApplicationServletContextListener implements ServletContextListener {

	private static final String PERSISTENCE_UNIT_NAME = "tools_inventory";
	private static EntityManagerFactory entityManagerFactory = null;


	public static EntityManagerFactory getEntityManagerFactory() {
		if ( entityManagerFactory == null ) {
			entityManagerFactory = Persistence.createEntityManagerFactory( PERSISTENCE_UNIT_NAME, null );
		}
//		if ( logger.isDebugEnabled()) logger.debug( "getEntityManagerFactory()" );
		return entityManagerFactory;
	}


	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("----------THE SERVER IS STARTED----------");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println();
		System.out.println("----------THE SERVER IS STOPPING");
		if ( entityManagerFactory != null && entityManagerFactory.isOpen()) {
			entityManagerFactory.close();
			entityManagerFactory = null;
		}
		System.out.println("----------DATABASE IS CLOSED");


		//Looks like because of '@Push' sessions are not destroyed when the server is being restarted, causing new Server Start fail

//		if (UI.getCurrent() != null) {
//			UI.getCurrent().access(() -> {
//				for (final UI ui : VaadinSession.getCurrent().getUIs()) {
//					if (ui != null) {
//						System.out.println("----------SET PushMode.DISABLED");
//						ui.access(() -> ui.getPushConfiguration().setPushMode(PushMode.DISABLED));
//					}
//				}
//			});
//		}
//
//
//
//		if (UI.getCurrent() != null) {
//			UI.getCurrent().access(() -> {
//				for (final UI ui : VaadinSession.getCurrent().getUIs()) {
//					if (ui != null) {
//						System.out.println("----------INVALIDATE SESSION");
//
//						ui.access(() -> {
//							ui.getSession().getSession().invalidate();
//							ui.getPage().reload();
//						});
//					}
//				}
//			});
//		}


		System.out.println("----------THE SERVER IS STOPPED----------");
	}
}
