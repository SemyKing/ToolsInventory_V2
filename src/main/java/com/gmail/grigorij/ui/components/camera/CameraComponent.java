package com.gmail.grigorij.ui.components.camera;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.server.StreamReceiver;
import com.vaadin.flow.server.StreamVariable;
import com.vaadin.flow.shared.Registration;

import java.io.OutputStream;


@Tag("camera-element")
@JsModule("./src/camera-element.js")
public class CameraComponent extends Component {

	CameraComponent() {}

	void setReceiver(DataReceiver receiver) {
		getElement().setAttribute("target", new StreamReceiver(
				getElement().getNode(), "camera", new CameraStreamVariable(receiver)));
	}

	private void fireFinishedEvent(String mime) {
		fireEvent(new CameraFinishedEvent(this, true, mime));
	}

	public void stopCamera() {
		getElement().callJsFunction("stopCamera");
	}

	public void takePicture() {
		getElement().callJsFunction("takePicture");
	}

	public void showPreview() {
		getElement().callJsFunction("showPreview");
	}

	public Registration addFinishedListener(ComponentEventListener<CameraFinishedEvent> listener) {
		return addListener(CameraFinishedEvent.class, listener);
	}

	private class CameraStreamVariable implements StreamVariable {

		String mime;
		DataReceiver receiver;

		CameraStreamVariable(DataReceiver receiver) {
			this.receiver = receiver;
		}

		@Override
		public OutputStream getOutputStream() {
			return receiver.getOutputStream(mime);
		}

		@Override
		public boolean isInterrupted() {
			return false;
		}

		@Override
		public boolean listenProgress() {
			return false;
		}

		@Override
		public void onProgress(StreamingProgressEvent arg0) {}

		@Override
		public void streamingFailed(StreamingErrorEvent arg0) {}

		@Override
		public void streamingFinished(StreamingEndEvent arg0) {
			fireFinishedEvent(mime);
		}

		@Override
		public void streamingStarted(StreamingStartEvent arg0) {
			mime = arg0.getMimeType();
		}
	}
}
