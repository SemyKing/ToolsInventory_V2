package com.gmail.grigorij.backend.entities.message;

import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.MessageType;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "messages")
//@NamedQueries({
//
//		@NamedQuery(
//				name="getMessagesByUserId",
//				query="SELECT message FROM Message message WHERE" +
//						" message.recipient IS NOT NULL AND" +
//						" message.recipient.id = :id_var")
//})
public class Message extends EntityPojo {

	@OneToOne
	private User recipient = null;

	@OneToOne
	private User sender = null;

	@Column(name = "message_header")
	private String messageHeader = "";

	@Column(name = "message_text")
	private String messageText = "";

	@Temporal( TemporalType.TIMESTAMP )
	private Date date;

	@Column(name = "message_read")
	private boolean messageRead = false;

	@OneToOne
	private InventoryEntity tool = null;

	@Enumerated(EnumType.STRING)
	private MessageType messageType;


	public Message() {
		this.date = new Date();
	}


	public User getRecipient() {
		return recipient;
	}
	public void setRecipient(User recipient) {
		this.recipient = recipient;
	}

	public User getSender() {
		return sender;
	}
	public void setSender(User sender) {
		this.sender = sender;
	}

	public String getMessageHeader() {
		return messageHeader;
	}
	public void setMessageHeader(String messageHeader) {
		this.messageHeader = messageHeader;
	}

	public String getMessageText() {
		return messageText;
	}
	public void setMessageText(String messageText) {
		this.messageText = messageText;
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

	public InventoryEntity getTool() {
		return tool;
	}
	public void setTool(InventoryEntity tool) {
		this.tool = tool;
	}

	public MessageType getMessageType() {
		return messageType;
	}
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
}
