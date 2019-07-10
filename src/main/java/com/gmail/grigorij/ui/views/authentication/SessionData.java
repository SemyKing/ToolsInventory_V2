package com.gmail.grigorij.ui.views.authentication;

import com.gmail.grigorij.backend.entities.company.Company;
import com.gmail.grigorij.backend.entities.user.User;

public class SessionData {

	public SessionData() {}

	private User user;
	private Company company;


	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

//	public Company getCompany() {
//		return company;
//	}

//	public void setCompany(Company company) {
//		this.company = company;
//	}
}
