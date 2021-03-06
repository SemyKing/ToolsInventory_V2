package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.enums.permissions.PermissionLevel;
import com.gmail.grigorij.backend.database.enums.tools.ToolUsageStatus;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.component.notification.NotificationVariant;

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


	public List<User> getAllUsers() {
		List<User> users;
		try {
			users = DatabaseManager.getInstance().createEntityManager().createNamedQuery(User.QUERY_ALL, User.class)
					.getResultList();
		} catch (NoResultException nre) {
			users = null;
		}
		return users;
	}
	
	public List<User> getAllActiveUsers() {
		List<User> users;
		try {
			users = DatabaseManager.getInstance().createEntityManager().createNamedQuery(User.QUERY_ALL, User.class)
					.getResultList();
			users.removeIf(User::isDeleted);
		} catch (NoResultException nre) {
			users = null;
		}
		return users;
	}

	public List<User> getAllUsersInCompany(long companyId) {
		List<User> users;
		try {
			users = DatabaseManager.getInstance().createEntityManager().createNamedQuery(User.QUERY_ALL_BY_COMPANY, User.class)
					.setParameter(ProjectConstants.ID_VAR, companyId)
					.getResultList();
		} catch (NoResultException nre) {
			users = null;
		}
		return users;
	}

	public List<User> getAllActiveUsersInCompany(long companyId) {
		List<User> users;
		try {
			users = DatabaseManager.getInstance().createEntityManager().createNamedQuery(User.QUERY_ALL_BY_COMPANY, User.class)
					.setParameter(ProjectConstants.ID_VAR, companyId)
					.getResultList();
			users.removeIf(User::isDeleted);
		} catch (NoResultException nre) {
			users = null;
		}
		return users;
	}

	public User getUserById(Long id) {
		User user;
		try {
			user = DatabaseManager.getInstance().createEntityManager().createNamedQuery(User.QUERY_BY_ID, User.class)
					.setParameter(ProjectConstants.ID_VAR, id)
					.getSingleResult();
		} catch (NoResultException nre) {
			user = null;
		}
		return user;
	}

	public User getUserByUsername(String username) {
		User user;
		try {
			user = DatabaseManager.getInstance().createEntityManager().createNamedQuery(User.QUERY_BY_USERNAME, User.class)
					.setParameter(ProjectConstants.VAR1, username)
					.getSingleResult();
		} catch (NoResultException nre) {
			user = null;
		}
		return user;
	}

	public User getUserByEmail(String email) {
		User user;
		try {
			user = DatabaseManager.getInstance().createEntityManager().createNamedQuery(User.QUERY_BY_EMAIL, User.class)
					.setParameter(ProjectConstants.VAR1, email)
					.getSingleResult();
		} catch (NoResultException nre) {
			user = null;
		}
		return user;
	}

	public List<User> getSystemAdmins() {
		List<User> users;
		try {
			users = DatabaseManager.getInstance().createEntityManager().createNamedQuery(User.QUERY_ALL_BY_PERMISSION_LEVEL, User.class)
					.setParameter(ProjectConstants.VAR1, PermissionLevel.SYSTEM_ADMIN)
					.getResultList();
		} catch (NoResultException nre) {
			users = null;
		}
		return users;
	}



	public boolean isUsernameAvailable(String username) {
		for (User u : getAllUsers()) {
			if (u == null) {
				System.err.println("NULL USER IN DATABASE");
				return false;
			}

			if (u.getUsername().equals(username)) {
				return false;
			}
		}
		return true;
	}

	public boolean isEmailAvailable(String email) {
		for (User u : getAllUsers()) {
			if (u == null) {
				System.err.println("NULL USER IN DATABASE");
				return false;
			}

			if (u.getPerson() == null) {
				System.err.println("NULL PERSON IN DATABASE, USER ID: " + u.getId());
				return false;
			}

			if (u.getPerson().getEmail().equals(email)) {
				return false;
			}
		}
		return true;
	}

	public boolean handleUserStatusChange(long userId, boolean newStatus) {
		User user = UserFacade.getInstance().getUserById(userId);

		user.setDeleted(newStatus);

		// REMOVE USER FROM TOOLS
		if (newStatus) {
			List<Tool> toolsInUse = InventoryFacade.getInstance().getAllToolsByCurrentUserId(userId);

			for (Tool tool : toolsInUse) {
				tool.setCurrentUser(null);

				if (tool.getReservedUser() == null) {
					tool.setUsageStatus(ToolUsageStatus.FREE);
				} else {
					tool.setUsageStatus(ToolUsageStatus.RESERVED);
				}

				InventoryFacade.getInstance().update(tool);
			}

			List<Tool> toolsReserved = InventoryFacade.getInstance().getAllToolsByReservedUserId(userId);

			for (Tool tool : toolsReserved) {
				tool.setReservedUser(null);

				if (tool.getCurrentUser() == null) {
					tool.setUsageStatus(ToolUsageStatus.FREE);
				} else {
					tool.setUsageStatus(ToolUsageStatus.IN_USE);
				}

				InventoryFacade.getInstance().update(tool);
			}
		}

		if (!UserFacade.getInstance().update(user)) {
			UIUtils.showNotification("User status change failed for: " + user.getUsername(), NotificationVariant.LUMO_ERROR);
			return false;
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

	private boolean remove(User user) {
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
