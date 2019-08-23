package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.message.Message;
import com.gmail.grigorij.backend.entities.transaction.Transaction;

import javax.persistence.NoResultException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;


public class MessageFacade {

	private static MessageFacade mInstance;
	private MessageFacade() {}
	public static MessageFacade getInstance() {
		if (mInstance == null) {
			mInstance = new MessageFacade();
		}
		return mInstance;
	}

	private List<Message> getAllMessagesForUser(long userId) {
		List<Message> messages;
		try {
			messages = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getAllMessagesForUser", Message.class)
					.setParameter("user_id_var", userId)
					.getResultList();
		} catch (NoResultException nre) {
			messages = null;
		}
		return messages;
	}

	private List<Message> getSortedList(List<Message> list, LocalDate start, LocalDate end) {

		final Date startDate = Date.valueOf(start);
		final Date endDate = Date.valueOf(end.plusDays(1));

		list.removeIf((Message message) -> message.getDate().before(startDate));
		list.removeIf((Message message) -> message.getDate().after(endDate));

		list.sort(Comparator.comparing(Message::getDate));

		return list;
	}


	public List<Message> getAllMessagesBetweenDates(LocalDate start, LocalDate end, long userId) {
		List<Message> messages = getAllMessagesForUser(userId);

		return getSortedList(messages, start, end);
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
