package com.gmail.grigorij.backend.database;

import com.gmail.grigorij.ApplicationServletContextListener;
import com.gmail.grigorij.backend.entities.EntityPojo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

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
		System.out.println("Inserting new pojo: " + pojo.getClass().getName());

		EntityManager em = createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(pojo);
			em.getTransaction().commit();
			System.out.println("pojo inserted");
			return pojo;
		} catch (PersistenceException e) {
			throw e;
		} finally {
			em.close();
		}
	}


	public <T> T update(T pojo) {
		System.out.println("Updating pojo: " + pojo.getClass().getName());

		EntityManager em = createEntityManager();
		try {
			em.getTransaction().begin();
			T p = em.merge(pojo);
			em.flush();
			p = em.merge(p); // Related entities marked cascade-merge will become merged too.
			em.getTransaction().commit();
			System.out.println("pojo updated");
			return p;
		} catch (RollbackException e) {
			throw e;
		} finally {
			em.close();
		}
	}


	public void remove(EntityPojo pojo) {
		System.out.println("deleting pojo: " + pojo);
		if (pojo == null || pojo.getId() <= 0 ) {
			return;
		}

		EntityManager em = createEntityManager();
		try {
			em.getTransaction().begin();
			em.remove(em.find(pojo.getClass(), pojo.getId()));
			em.getTransaction().commit();
			System.out.println("pojo deleted");
		} catch (PersistenceException e) {
			throw e;
		} finally {
			em.close();
		}
	}


	public <T> T find(Class<T> c, Object obj) {
		System.out.println("looking for pojo: " + c.getName() + ", obj: " + obj);

		EntityManager em = createEntityManager();
		try {
			return em.find(c, obj);
		} catch (PersistenceException e) {
			throw e;
		} finally {
			em.close();
		}
	}
}
