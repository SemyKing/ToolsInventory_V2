package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.message.Message;


public class MessageFacade {

	private static MessageFacade mInstance;
	private MessageFacade() {}
	public static MessageFacade getInstance() {
		if (mInstance == null) {
			mInstance = new MessageFacade();
		}
		return mInstance;
	}


	public boolean insert(Message message) {
		if (message == null) {
			System.err.println(this.getClass().getSimpleName() + " -> INSERT NULL MESSAGE");
			return false;
		}

		try {
			DatabaseManager.getInstance().insert(message);
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> MESSAGE INSERT FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean update(Message message) {
		if (message == null) {
			System.err.println(this.getClass().getSimpleName() + " -> UPDATE NULL MESSAGE");
			return false;
		}

		Message messageInDatabase = null;

		if (message.getId() != null) {
			messageInDatabase = DatabaseManager.getInstance().find(Message.class, message.getId());
		}

		try {
			if (messageInDatabase == null) {
				return insert(message);
			} else {
				DatabaseManager.getInstance().update(message);
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> MESSAGE UPDATE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean remove(Message message) {
		if (message == null) {
			System.err.println(this.getClass().getSimpleName() + " -> REMOVE NULL MESSAGE");
			return false;
		}

		Message messageInDatabase = null;

		if (message.getId() != null) {
			messageInDatabase = DatabaseManager.getInstance().find(Message.class, message.getId());
		}

		try {
			if (messageInDatabase != null) {
				DatabaseManager.getInstance().remove(message);
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> MESSAGE REMOVE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
