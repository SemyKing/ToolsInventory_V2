package com.gmail.grigorij.ui.components.dialogs;

import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.ui.utils.camera.CameraView;
import com.gmail.grigorij.utils.CameraUtility;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;


public class CameraDialog extends CustomDialog {

	private final CameraUtility cameraUtility;
	private CameraView cameraView;


	public CameraDialog() {
		cameraView = new CameraView();
		cameraUtility = new CameraUtility(cameraView, UI.getCurrent());

		setCloseOnEsc(false);
		setCloseOnOutsideClick(false);

		constructContent();
	}


	private void constructContent() {
		setHeader(UIUtils.createH3Label("Code Scanner"));

		cameraView.showPreview();

		getContent().add(cameraView);

		setConfirmButton(null);

		getCancelButton().setText("Close");
		getCancelButton().addClickListener(e -> {
			cameraView.stop();
			this.close();
		});
	}

	public CameraView getCameraView() {
		return cameraView;
	}

	@SuppressWarnings("deprecation")
	public void stop() {
		cameraUtility.stop();
		cameraView.stop();
	}

	public void initCamera() {
		cameraUtility.start();
	}
}
