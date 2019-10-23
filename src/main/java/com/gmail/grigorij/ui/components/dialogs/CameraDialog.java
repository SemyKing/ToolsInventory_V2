package com.gmail.grigorij.ui.components.dialogs;

import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.camera.CameraView;
import com.vaadin.flow.component.html.Span;

public class CameraDialog extends CustomDialog {

	private CameraView cameraView;
	private boolean cameraActive = false;


	public CameraDialog() {
		constructContent();
	}


	private void constructContent() {
		setCloseOnEsc(false);
		setCloseOnOutsideClick(false);

		setHeader(UIUtils.createH3Label("Code scanner"));

		cameraView = new CameraView();
		cameraView.addClickListener(imageClickEvent -> {
			if (cameraActive) {
				cameraView.takePicture();
			} else {
				cameraView.showPreview();
				cameraActive = true;
			}
		});

		Span cameraHint = new Span("Click on image to scan code");
		cameraHint.addClassName("camera-dialog-hint");

		getContent().add(cameraHint);
		getContent().add(cameraView);

		setCancelButton(null);

		getConfirmButton().setText("Close");
		getConfirmButton().addClickListener(e -> {
			cameraView.stop();
			this.close();
		});

		cameraActive = true;
	}

	public CameraView getCameraView() {
		return cameraView;
	}

	public void stopCamera() {
		cameraView.stop();
		cameraActive = false;
	}
}
