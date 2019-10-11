package com.gmail.grigorij.backend.entities.message;

import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.backend.enums.MessageType;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "messages")

@NamedQueries({
		@NamedQuery(
				name="getAllMessagesForUser",
				query="SELECT message FROM Message message WHERE message.recipientId = :user_id_var")
})
public class Message extends EntityPojo {

	@Enumerated(EnumType.STRING)
	private MessageType messageType;

	@Column(name = "message_header")
	private String messageHeader = "";

	@Column(name = "message_text")
	private String messageText = "";

	private String sender;

	private Long senderId;

	private Long recipientId;

	private Long toolId;

	private Date date;

	@Column(name = "message_read")
	private boolean messageRead = false;


	public Message() {
		this.date = new Date();
	}


	public MessageType getMessageType() {
		return messageType;
	}
	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
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

	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}

	public Long getSenderId() {
		return senderId;
	}
	public void setSenderId(Long senderId) {
		this.senderId = senderId;
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
}
