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
		if (link == null) {
			System.err.println(this.getClass().getSimpleName() + " -> INSERT NULL RECOVERY_LINK");
			return false;
		}

		try {
			DatabaseManager.getInstance().insert(link);
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> RECOVERY_LINK INSERT FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean remove(RecoveryLink link) {
		if (link == null) {
			System.err.println(this.getClass().getSimpleName() + " -> REMOVE NULL RECOVERY_LINK");
			return false;
		}

		RecoveryLink linkInDatabase = null;

		if (link.getId() != null) {
			linkInDatabase = DatabaseManager.getInstance().find(RecoveryLink.class, link.getId());
		}

		try {
			if (linkInDatabase != null) {
				DatabaseManager.getInstance().remove(link);
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> RECOVERY_LINK REMOVE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
