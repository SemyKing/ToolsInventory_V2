package com.gmail.grigorij.ui.authentication;

import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.user.User;


/**
 *
 */
public class CurrentSession {

	private static CurrentSession mInstance;

	private CurrentSession() {
	}

	public static CurrentSession getInstance() {
		if (mInstance == null) {
			mInstance = new CurrentSession();
		}
		return mInstance;
	}


	private Company company;
	private User user;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
