package com.gmail.grigorij;

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
		System.out.println("----------------------------THE SERVER IS STARTED----------------------------");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("----------------------------THE SERVER IS ENDED----------------------------");
		System.out.println("-------------------------------CLOSE DATABASE------------------------------");
		if ( entityManagerFactory != null && entityManagerFactory.isOpen()) {
			entityManagerFactory.close();
			entityManagerFactory = null;
		}
	}
}
