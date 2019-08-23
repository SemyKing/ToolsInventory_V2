package com.gmail.grigorij.backend.database;

import com.gmail.grigorij.ApplicationServletContextListener;
import com.gmail.grigorij.backend.entities.EntityPojo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;


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


	public <T> T find(Class<T> c, Object primaryKey) {
		if (c == null || primaryKey == null) {
			return null;
		}

		EntityManager em = createEntityManager();
		try {
			return em.find(c, primaryKey);
		} catch (PersistenceException e) {
			throw e;
		} finally {
			em.close();
		}
	}

	public <T extends EntityPojo> T insert(T pojo) {
//		logger.debug( "Inserting new pojo:" + pojo.getClass().getName());
		System.out.println();
		System.out.println(this.getClass().getSimpleName() + " INSERT: " + pojo.getClass().getSimpleName());

		EntityManager em = createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(pojo);
			em.getTransaction().commit();
			System.out.println("INSERT SUCCESSFUL");
			return pojo;
		} catch (PersistenceException e) {
			System.err.println("INSERT ERROR");
			throw e;
		} finally {
			em.close();
		}
	}

	public <T> T merge(T pojo) {
		EntityManager em = createEntityManager();
		try {
			em.getTransaction().begin();
			T p = em.merge(pojo);
			em.flush();
			p = em.merge(p); // Related entities marked cascade-merge will become merged too.
			em.getTransaction().commit();
			return p;
		} catch (RollbackException e) {
			throw e;
		} finally {
			em.close();
		}
	}

	public <T> T update(T pojo) {
		System.out.println();
		System.out.println(this.getClass().getSimpleName() + " UPDATE: " + pojo.getClass().getSimpleName());

		EntityManager em = createEntityManager();
		try {
			em.getTransaction().begin();
			T p = em.merge(pojo);
			em.flush();
			p = em.merge(p); // Related entities marked cascade-merge will become merged too.
			em.getTransaction().commit();
			System.out.println("UPDATE SUCCESSFUL");
			return p;
		} catch (RollbackException e) {
			System.err.println("UPDATE ERROR");
			throw e;
		} finally {
			em.close();
		}
	}

	public void remove(EntityPojo pojo) {
		System.out.println();
		System.out.println(this.getClass().getSimpleName() + " DELETE");
		if (pojo == null || pojo.getId() <= 0 ) {
			System.err.println("ENTITY IS NULL OR ID <= 0");
			return;
		}

		System.out.println("DELETING: " + pojo.getClass().getSimpleName());

		EntityManager em = createEntityManager();
		try {
			em.getTransaction().begin();
			em.remove(em.find(pojo.getClass(), pojo.getId()));
			em.getTransaction().commit();
			System.out.println("DELETE SUCCESSFUL");
		} catch (PersistenceException e) {
			System.err.println("DELETE ERROR");
			throw e;
		} finally {
			em.close();
		}
	}
}
