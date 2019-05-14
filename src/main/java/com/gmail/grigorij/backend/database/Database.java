package com.gmail.grigorij.backend.database;

import com.gmail.grigorij.backend.entities.Company;
import com.gmail.grigorij.backend.entities.User;

import javax.persistence.*;

public class Database {

	private static final String PERSISTENCE_UNIT_NAME = "tools_inventory";
	private EntityManagerFactory factory;
	private EntityManager em;


	private static Database ourInstance = new Database();
	public static Database getInstance() {
		return ourInstance;
	}

	private Database() {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = factory.createEntityManager();
	}



	private User user;

	public User getUser() {
		return user;
	}

	public void findUserInDatabase(String username, String password) {
		user = (User) em.createNamedQuery("User.findUserInDatabase")
				.setParameter("username", username)
				.setParameter("password", password)
				.getSingleResult();
	}









	public User getUser(String username, String password) {
		EntityManager em = factory.createEntityManager();

		// Begin a new local transaction so that we can persist a new entity
		em.getTransaction().begin();

		Query q = em.createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password");
		q.setParameter("username", username);
		q.setParameter("password", password);

		User user;

		try {
			user = (User) q.getSingleResult();
			user.setPassword("");
		} catch (NoResultException nre) {
			System.err.println("USER: " + username + " NOT FOUND IN DATABASE");
			user = null;
		} finally {
			em.close();
		}

		return user;
	}

	public Company getCompanyById(int company_id) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		Query q = em.createQuery("SELECT n FROM Company n WHERE n.id = :company_id");
		q.setParameter("company_id", company_id);

		Company company;

		try {
			company = (Company) q.getSingleResult();
		} catch (NoResultException nre) {
			System.err.println("COMPANY WITH ID: " + company_id + " NOT FOUND IN DATABASE");
			company = null;
		} finally {
			em.close();
		}

		return company;
	}


}
