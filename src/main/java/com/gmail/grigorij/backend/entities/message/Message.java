package com.gmail.grigorij.backend.entities.message;

import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.entities.user.User;
import com.gmail.grigorij.backend.enums.MessageType;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "messages")
public class Message extends EntityPojo {

	@Enumerated(EnumType.STRING)
	private MessageType messageType;

	@OneToOne
	private User sender;

	@OneToOne
	private User recipient;

	@Column(name = "message_header")
	private String messageHeader = "";

	@Column(name = "message_text")
	private String messageText = "";

	@Temporal( TemporalType.TIMESTAMP )
	private Date date;

	@Column(name = "message_read")
	private boolean messageRead = false;

	private Long toolId;


	public Message() {
		this.date = new Date();
	}


	public MessageType getMessageType() {
		return messageType;
	}
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public User getSender() {
		return sender;
	}
	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getRecipient() {
		return recipient;
	}
	public void setRecipient(User recipient) {
		this.recipient = recipient;
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

	public Long getToolId() {
		return toolId;
	}
	public void setToolId(Long toolId) {
		this.toolId = toolId;
	}
}
