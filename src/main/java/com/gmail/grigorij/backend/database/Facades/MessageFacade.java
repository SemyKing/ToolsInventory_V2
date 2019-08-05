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
		System.out.println("Message INSERT");
		if (message == null)
			return false;

		try {
			DatabaseManager.getInstance().insert(message);
		} catch (Exception e) {
			System.out.println("Message INSERT fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("Message INSERT successful");
		return true;
	}


	public boolean update(Message message) {
		System.out.println("Message UPDATE");
		if (message == null)
			return false;

		Message messageInDatabase = null;

		if (message.getId() != null) {
			messageInDatabase = DatabaseManager.getInstance().find(Message.class, message.getId());
		}

		System.out.println("messageInDatabase: " + messageInDatabase);

		try {
			if (messageInDatabase == null) {
				return insert(message);
			} else {
				DatabaseManager.getInstance().update(message);
			}
		} catch (Exception e) {
			System.out.println("Message UPDATE fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("Message UPDATE successful");
		return true;
	}


	public boolean remove(Message message) {
		System.out.println("Message REMOVE");
		if (message == null)
			return false;

		Message messageInDatabase = DatabaseManager.getInstance().find(Message.class, message.getId());
		System.out.println("messageInDatabase: " + messageInDatabase);

		try {
			if (messageInDatabase != null) {
				DatabaseManager.getInstance().remove(message);
			}
		} catch (Exception e) {
			System.out.println("Message REMOVE fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("Message REMOVE successful");
		return true;
	}
}
