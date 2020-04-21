package com.gmail.grigorij.ui.components.camera;

import com.vaadin.flow.component.html.Div;

import java.io.ByteArrayInputStream;
import java.io.File;

public class AbstractCameraView extends Div implements HasDataReceiver {

	private CameraComponent camera;
	private File latest;

	protected AbstractCameraView() {
		setSizeFull();

		camera = new CameraComponent();
		camera.setReceiver(this);

		add(camera);
	}

	protected CameraComponent getCamera() {
		return camera;
	}


	@Override
	public File getLatest() {
		return latest;
	}

	@Override
	public void setLatest(File file) {
		latest = file;
	}
}
