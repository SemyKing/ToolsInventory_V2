package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.user.User;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class UserFacade {

	private List<User> emptyList;

	private static UserFacade mInstance;
	private UserFacade() {
		emptyList = new ArrayList<>();
	}
	public static UserFacade getInstance() {
		if (mInstance == null) {
			mInstance = new UserFacade();
		}
		return mInstance;
	}

	public List<User> getAllUsers() {
		List<User> users;
		try {
			users = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getAllUsers", User.class)
					.getResultList();
		} catch (NoResultException nre) {
			users = null;
		}
		return users;
	}

	public User findUserInDatabase(String username, String password) {
		User user;
		try {
			user = (User) DatabaseManager.getInstance().createEntityManager().createNamedQuery("findUserInDatabase", User.class)
					.setParameter("username_var", username)
					.setParameter("password_var", password)
					.getSingleResult();
		} catch (NoResultException nre) {
			user = null;
		}
		return user;
	}

	public User getUserByUsername(String username) {
		User user;
		try {
			user = (User) DatabaseManager.getInstance().createEntityManager().createNamedQuery("getUserByUsername")
					.setParameter("username_var", username)
					.getSingleResult();
		} catch (NoResultException nre) {
			user = null;
		}
		return user;
	}

	public User getUserByEmail(String email) {
		User user;
		try {
			user = (User) DatabaseManager.getInstance().createEntityManager().createNamedQuery("getUserByEmail")
					.setParameter("email_var", email)
					.getSingleResult();
		} catch (NoResultException nre) {
			user = null;
		}
		return user;
	}




	public User getUserById(Long id) {
		User user;
		try {
			user = (User) DatabaseManager.getInstance().createEntityManager().createNamedQuery("getUserById")
					.setParameter("id_var", id)
					.getSingleResult();
		} catch (NoResultException nre) {
			user = null;
		}
		return user;
	}

	public List<User> getUsersInCompany(long companyId) {
		List<User> users;
		try {
			users = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getUsersInCompany", User.class)
					.setParameter("id_var", companyId)
					.getResultList();
		} catch (NoResultException nre) {
			users = null;
		}
		return users;
	}



	public boolean insert(User user) {
		System.out.println("User INSERT");
		if (user == null)
			return false;

		try {
			DatabaseManager.getInstance().insert(user);
		} catch (Exception e) {
			System.out.println("User INSERT fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("User INSERT successful");
		return true;
	}

	public boolean update(User user) {
		System.out.println("User UPDATE");
		if (user == null)
			return false;

		User userInDatabase = null;

		if (user.getId() != null) {
			userInDatabase = DatabaseManager.getInstance().find(User.class, user.getId());
		}

		System.out.println("userInDatabase: " + userInDatabase);

		try {
			if (userInDatabase == null) {
				return insert(user);
			} else
				DatabaseManager.getInstance().update(user);
		} catch (Exception e) {
			System.out.println("User UPDATE fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("User UPDATE successful");
		return true;
	}

	public boolean remove(User user) {
		System.out.println("User REMOVE");
		if (user == null)
			return false;

		User userInDatabase = DatabaseManager.getInstance().find(User.class, user.getId());
		System.out.println("userInDatabase: " + userInDatabase);

		try {
			if (userInDatabase != null) {
				DatabaseManager.getInstance().remove(user);
			}
		} catch (Exception e) {
			System.out.println("User REMOVE fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("User REMOVE successful");
		return true;
	}
}
