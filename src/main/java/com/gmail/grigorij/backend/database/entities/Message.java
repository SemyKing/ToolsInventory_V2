package com.gmail.grigorij.backend.database.entities;

import com.gmail.grigorij.utils.DateConverter;
import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Date;


@Entity
@Table(name = "messages")

@NamedQueries({
		@NamedQuery(name=Message.QUERY_ALL_BY_USER,
				query="SELECT message FROM Message message WHERE message.recipientId = :" + ProjectConstants.ID_VAR)
})
public class Message extends EntityPojo {

	public static final String QUERY_ALL = "get_all_messages";
	public static final String QUERY_ALL_BY_USER = "get_all_messages_by_user";


	private String subject = "";
	private String text = "";
	private String senderString = "";
	private User senderUser;
	private Long recipientId;
	private Long toolId;
	private Date date;
	private boolean messageRead = false;


	public Message() {
		this.date = new Date();
	}


	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public String getSenderString() {
		return senderString;
	}
	public void setSenderString(String senderString) {
		this.senderString = senderString;
	}

	public User getSenderUser() {
		return senderUser;
	}
	public void setSenderUser(User senderUser) {
		this.senderUser = senderUser;
	}

	public Long getRecipientId() {
		return recipientId;
	}
	public void setRecipientId(Long recipientId) {
		this.recipientId = recipientId;
	}

	public Long getToolId() {
		return toolId;
	}
	public void setToolId(Long toolId) {
		this.toolId = toolId;
	}

	public Date getDate() {
		return date;
	}

	public boolean isMessageRead() {
		return messageRead;
	}
	public void setMessageRead(boolean messageRead) {
		this.messageRead = messageRead;
	}


	public String getSender() {
		String s = "";

		if (senderUser != null) {
			s = senderUser.getFullName();
		} else {
			s = senderString;
		}

		return s;
	}

	public String getDateWithTimeString() {
		try {
			if (date == null) {
				return "";
			} else {
				return DateConverter.dateToStringWithTime(date);
			}
		} catch (Exception e) {
			return "";
		}
	}

}
