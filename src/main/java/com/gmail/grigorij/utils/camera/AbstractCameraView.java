package com.gmail.grigorij.utils.camera;

import com.vaadin.flow.component.html.Div;

import java.io.File;

public class AbstractCameraView extends Div implements HasDataReceiver {

	private Camera camera;
	private File latest;

	protected AbstractCameraView() {
		setSizeFull();

		camera = new Camera();
		camera.setReceiver(this);

		add(camera);
	}

	protected Camera getCamera() {
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
