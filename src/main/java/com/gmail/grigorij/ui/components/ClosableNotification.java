package com.gmail.grigorij.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;

public class ClosableNotification extends Notification {

	public ClosableNotification() {}

	public static void showNotification(String msg, long delay, Position pos) {
		Notification notification = new Notification();

		Button closeBtn = new Button("Close");
		closeBtn.addClickListener(event -> notification.close());

		notification.add(new Label("msg"));
		notification.add(closeBtn);
		notification.setDuration((int) delay);
		notification.setPosition(pos);
		notification.open();
	}
}
