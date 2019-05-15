package com.gmail.grigorij.backend.database;

import com.gmail.grigorij.ApplicationServletContextListener;
import com.gmail.grigorij.backend.entities.EntityPojo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

//DataFacade
public class DatabaseManager {

	private static DatabaseManager mInstance;

	private DatabaseManager() {}

	public static DatabaseManager getInstance() {
		if (mInstance == null) {
			mInstance = new DatabaseManager();
		}
		return mInstance;
	}


	public EntityManager createEntityManager() {
		EntityManagerFactory emf = ApplicationServletContextListener.getEntityManagerFactory();
		return emf.createEntityManager();
	}


	public <T extends EntityPojo> T insert(T pojo) {
//		logger.debug( "Inserting new pojo:" + pojo.getClass().getName());
		EntityManager em = createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(pojo);
			em.getTransaction().commit();
			return pojo;
		} catch (PersistenceException e) {
			throw e;
		} finally {
			em.close();
		}
	}


}
