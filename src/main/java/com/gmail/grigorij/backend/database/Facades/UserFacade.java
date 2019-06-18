package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.ui.utils.UIUtils;

import javax.persistence.NoResultException;
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


	public User findUserInDatabase(String username, String password) {
		User user;
		try {
			user = (User) DatabaseManager.getInstance().createEntityManager().createNamedQuery("User.findUserInDatabase")
					.setParameter("username", username)
					.setParameter("password", password)
					.getSingleResult();
		} catch (NoResultException nre) {
			user = null;
		}
		return user;
	}


	public User findUserInDatabaseByUsername(String username) {
		User user;
		try {
			user = (User) DatabaseManager.getInstance().createEntityManager().createNamedQuery("User.findUserInDatabaseByUsername")
					.setParameter("username", username)
					.getSingleResult();
		} catch (NoResultException nre) {
			user = null;
		}
		return user;
	}


	public List<User> listAllUsers() {
		List<User> users;
		try {
			users = DatabaseManager.getInstance().createEntityManager().createNamedQuery("User.listAllUsers", User.class)
					.getResultList();
		} catch (NoResultException nre) {
			users = null;
		}
		return users;
	}


	public List<User> listUsersByCompanyId(long companyId) {
		List<User> users;
		try {
			users = DatabaseManager.getInstance().createEntityManager().createNamedQuery("User.listUsersByCompanyId", User.class)
					.setParameter("companyId", companyId)
					.getResultList();
		} catch (NoResultException nre) {
			users = null;
		}
		return users;
	}


	public boolean insert(User user) {
		System.out.println();
		System.out.println("UserFacade -> insert");

		if (user == null)
			return false;

		try {
			DatabaseManager.getInstance().insert(user);
		} catch (Exception e) {
			UIUtils.showNotification("User INSERT fail", UIUtils.NotificationType.ERROR);
			e.printStackTrace();
			return false;
		}

		UIUtils.showNotification("User INSERT successful", UIUtils.NotificationType.SUCCESS);
		return true;
	}


	public boolean update(User user) {
		System.out.println();
		System.out.println("UserFacade -> update");

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
			UIUtils.showNotification("User UPDATE fail", UIUtils.NotificationType.ERROR);
			e.printStackTrace();
			return false;
		}

		UIUtils.showNotification("User UPDATE successful", UIUtils.NotificationType.SUCCESS);
		return true;
	}


	public boolean remove(User user) {
		System.out.println();
		System.out.println("UserFacade -> remove");

		if (user == null)
			return false;

		User userInDatabase = DatabaseManager.getInstance().find(User.class, user.getId());
		System.out.println("userInDatabase: " + userInDatabase);

		try {
			if (userInDatabase != null) {
				DatabaseManager.getInstance().remove(user);
			}
		} catch (Exception e) {
			UIUtils.showNotification("User REMOVE fail", UIUtils.NotificationType.ERROR);
			e.printStackTrace();
			return false;
		}

		UIUtils.showNotification("User REMOVE successful", UIUtils.NotificationType.SUCCESS);
		return true;
	}


}
