package com.gmail.grigorij.backend.database.Facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.user.User;

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
		User user = (User) DatabaseManager.getInstance().createEntityManager().createNamedQuery("User.findUserInDatabase")
				.setParameter("username", username)
				.setParameter("password", password)
				.getSingleResult();
		return user;
	}

	public User findUserInDatabaseByUsername(String username) {
		User user = (User) DatabaseManager.getInstance().createEntityManager().createNamedQuery("User.findUserInDatabaseByUsername")
				.setParameter("username", username)
				.getSingleResult();
		return user;
	}
}
