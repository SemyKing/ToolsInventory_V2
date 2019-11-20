package com.gmail.grigorij.ui.components.camera;

import com.vaadin.flow.component.ComponentEvent;

public class CameraFinishedEvent extends ComponentEvent<Camera> {

	private String mime;

	public CameraFinishedEvent(Camera source, boolean fromClient, String mime) {
		super(source, fromClient);
		this.mime = mime;
	}

	public String getMime() {
			return this.mime;
		}
}
