package com.gmail.grigorij.utils.email;

import com.gmail.grigorij.utils.ProjectConstants;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class Email {

	private Properties prop;
	private Session session;
	private Message message;


	public Email() {
		constructProperties();

		constructSession();
	}


	private void constructProperties() {
		prop = new Properties();
		prop.put("mail.smtp.host", "true");
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.sender.address", ""); //TODO: INSERT EMAIL
		prop.put("mail.sender.password", ""); //TODO: INSERT EMAIL PASSWORD
	}

	private void constructSession() {
		session = Session.getInstance(prop, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(prop.getProperty("mail.sender.address"), prop.getProperty("mail.sender.password"));
			}
		});
	}

	public boolean constructAndSendMessage(String receiver, String recoveryLink) {
		try {
			message = new MimeMessage(session);
			message.setFrom(new InternetAddress(prop.getProperty("mail.sender.address")));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
			message.setSubject("Password reset for" + ProjectConstants.PROJECT_NAME_FULL);

			String msg = getMessage(recoveryLink);

			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(msg, "text/html");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);

			message.setContent(multipart);

			Transport.send(message);
		} catch (MessagingException e) {
			System.out.println("EMAIL CONSTRUCTION / SENDING ERROR");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private String getMessage(String recoveryLink) {
		String msg =

				"<div style='text-align: center; font-size: medium;'>" +
						"<p>You have requested to reset your password for your <strong>" + ProjectConstants.PROJECT_NAME_FULL + "</strong> account. Please click the button below to reset it.</p>" +
						"<p>&nbsp;</p>" +
						"<a class='button' href='" + recoveryLink + "' target='_blank' rel='noopener' style='" + getButtonStyle() + "'>RESET PASSWORD</a>" +
						"<p>&nbsp;</p>" +
						"<strong>If you did not request to reset your password, please ignore this message.</strong>" +
						"<p>THIS IS AN AUTOMATED MESSAGE, PLEASE DO NOT REPLY.</p>" +
						"</div>";


		return msg;
	}

	private String getButtonStyle() {
		String style = "background-color: #3869D4;" +
				"border-top: 10px solid #3869D4;" +
				"border-right: 18px solid #3869D4;" +
				"border-bottom: 10px solid #3869D4;" +
				"border-left: 18px solid #3869D4;" +
				"display: inline-block;" +
				"color: #FFF;" +
				"text-decoration: none;" +
				"border-radius: 3px;" +
				"box-shadow: 0 2px 3px rgba(0, 0, 0, 0.16);" +
				"-webkit-text-size-adjust: none;" +
				"box-sizing: border-box;";
		return style;
	}
}
