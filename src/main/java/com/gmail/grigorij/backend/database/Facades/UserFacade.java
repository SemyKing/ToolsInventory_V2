package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.user.User;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class UserFacade {

	private static UserFacade mInstance;
	private UserFacade() {}
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


	public boolean isUsernameUnique(String username) {
		for (User u : getAllUsers()) {
			if (u.getUsername().equals(username)) {
				return false;
			}
		}
		return true;
	}



	public boolean insert(User user) {
		if (user == null) {
			System.err.println(this.getClass().getSimpleName() + " -> INSERT NULL USER");
			return false;
		}

		try {
			DatabaseManager.getInstance().insert(user);
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> USER INSERT FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean update(User user) {
		if (user == null) {
			System.err.println(this.getClass().getSimpleName() + " -> UPDATE NULL USER");
			return false;
		}

		User userInDatabase = null;

		if (user.getId() != null) {
			userInDatabase = DatabaseManager.getInstance().find(User.class, user.getId());
		}

		try {
			if (userInDatabase == null) {
				return insert(user);
			} else
				DatabaseManager.getInstance().update(user);
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> USER UPDATE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean remove(User user) {
		if (user == null) {
			System.err.println(this.getClass().getSimpleName() + " -> REMOVE NULL USER");
			return false;
		}

		User userInDatabase = null;

		if (user.getId() != null) {
			userInDatabase = DatabaseManager.getInstance().find(User.class, user.getId());
		}

		try {
			if (userInDatabase != null) {
				DatabaseManager.getInstance().remove(user);
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> USER REMOVE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
