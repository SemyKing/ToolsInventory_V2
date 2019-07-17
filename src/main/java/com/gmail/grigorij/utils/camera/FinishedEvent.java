package com.gmail.grigorij.utils.camera;

import com.vaadin.flow.component.ComponentEvent;

public class FinishedEvent extends ComponentEvent<Camera> {

	private String mime;

	public FinishedEvent(Camera source, boolean fromClient, String mime) {
		super(source, fromClient);
		this.mime = mime;
	}

	public String getMime() {
			return this.mime;
		}
}
