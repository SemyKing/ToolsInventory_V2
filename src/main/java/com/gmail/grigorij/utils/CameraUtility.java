package com.gmail.grigorij.utils;

import com.gmail.grigorij.ui.components.camera.CameraView;
import com.vaadin.flow.component.UI;

public class CameraUtility extends Thread {

	private final CameraView cameraView;
	private final UI ui;


	public CameraUtility(CameraView cameraView, UI ui) {
		this.cameraView = cameraView;
		this.ui = ui;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(2000);

			ui.access(() -> {
				cameraView.takePicture();
				ui.push();
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
