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
	}

	private void constructSession() {
		session = Session.getInstance(prop, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("captain.gr3g@gmail.com", "Th1s1sMyP@ssw0rd");
			}
		});
	}

	public boolean constructAndSendMessage(String receiver, String recoveryLink) {
		try {
			message = new MimeMessage(session);
			message.setFrom(new InternetAddress("captain.gr3g@gmail.com"));
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
				"<ul>"+
					"<li>"+
					"You have requested to reset your password in " + ProjectConstants.PROJECT_NAME_FULL + " application.\n" +
					"</li>"+

					"<li>"+
					"You can reset your password from the following link:\n" +
					"</li>"+

					"<li>"+
					"<a>" + recoveryLink + "</a>" +
					"</li>"+

					"<li>"+
					"</li>"+
					"If you have not requested to reset your password, please ignore this message."+
				"</ul>";
		return msg;
	}


}
