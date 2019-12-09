package com.gmail.grigorij.utils.servlet;

import com.vaadin.flow.component.UI;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.HashMap;


@WebListener
public class ApplicationServletContextListener implements ServletContextListener {

	private static final String PERSISTENCE_UNIT_NAME = "tools_inventory";
	private static EntityManagerFactory entityManagerFactory = null;


	public static EntityManagerFactory getEntityManagerFactory() {
		if ( entityManagerFactory == null ) {
			entityManagerFactory = Persistence.createEntityManagerFactory( PERSISTENCE_UNIT_NAME, null );
		}
		return entityManagerFactory;
	}


	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("----------THE SERVER IS STARTED----------");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("----------THE SERVER IS STOPPING----------");

		if ( entityManagerFactory != null && entityManagerFactory.isOpen()) {
			entityManagerFactory.close();
			System.out.println("----------DATABASE IS CLOSED");
		}

		System.out.println("----------THE SERVER IS STOPPED----------");
	}

	private static HashMap<Long, UI> userUIs = new HashMap<>();

	public static HashMap<Long, UI> getUserUIs() {
		return userUIs;
	}
}
