package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.recoverylink.RecoveryLink;

import javax.persistence.NoResultException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;


public class RecoveryLinkFacade {

	private static RecoveryLinkFacade mInstance;
	private RecoveryLinkFacade() {}
	public static RecoveryLinkFacade getInstance() {
		if (mInstance == null) {
			mInstance = new RecoveryLinkFacade();
		}
		return mInstance;
	}



	public RecoveryLink getRecoveryLinkByToken(String token) {
		RecoveryLink link;
		try {
			link = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getRecoveryLinkByToken", RecoveryLink.class)
					.setParameter("token_var", token)
					.getSingleResult();
		} catch (NoResultException nre) {
			link = null;
		}
		return link;
	}



	public boolean insert(RecoveryLink link) {
		System.out.println("RecoveryLink INSERT");
		if (link == null)
			return false;

		try {
			DatabaseManager.getInstance().insert(link);
		} catch (Exception e) {
			System.out.println("RecoveryLink INSERT fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("RecoveryLink INSERT successful");
		return true;
	}

	public boolean remove(RecoveryLink link) {
		System.out.println("RecoveryLink REMOVE");
		if (link == null) {
			System.err.println("RecoveryLink is NULL");
			return false;
		}

		if (link.getId() == null) {
			System.err.println("RecoveryLink ID is NULL");
			return false;
		}

		RecoveryLink linkInDatabase = DatabaseManager.getInstance().find(RecoveryLink.class, link.getId());
		System.out.println("linkInDatabase: " + linkInDatabase);

		try {
			if (linkInDatabase != null) {
				DatabaseManager.getInstance().remove(link);
			}
		} catch (Exception e) {
			System.out.println("RecoveryLink REMOVE fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("RecoveryLink REMOVE successful");
		return true;
	}
}
