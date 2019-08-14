package com.gmail.grigorij.utils.camera;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.server.StreamReceiver;
import com.vaadin.flow.server.StreamVariable;
import com.vaadin.flow.shared.Registration;

import elemental.json.JsonFactory;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import elemental.json.impl.JreJsonFactory;

import java.io.OutputStream;
import java.util.Map;

@Tag("camera-element")
@HtmlImport("custom-components/camera-element.html")
public class Camera extends Component {

	Camera() {}

//	private JsonValue toJson(Map<String,Object> map, JsonFactory factory) {
//		JsonObject obj = factory.createObject();
//		for(Map.Entry<String,Object> entry: map.entrySet()) {
//			if(entry.getValue() instanceof Boolean) {
//				obj.put(entry.getKey(), factory.create((Boolean)entry.getValue()));
//			} else if(entry.getValue() instanceof Double) {
//				obj.put(entry.getKey(), factory.create((Double)entry.getValue()));
//			} else if(entry.getValue() instanceof Integer) {
//				obj.put(entry.getKey(), factory.create((Integer)entry.getValue()));
//			} else if(entry.getValue() instanceof String) {
//				obj.put(entry.getKey(), factory.create((String)entry.getValue()));
//			} else if(entry.getValue() instanceof Map) {
//				obj.put(entry.getKey(), toJson((Map<String,Object>)entry.getValue(), factory));
//			} else {
//				throw new IllegalArgumentException("Unsopported argument in options");
//			}
//		}
//		return obj;
//	}

	void setReceiver(DataReceiver receiver) {
		getElement().setAttribute("target", new StreamReceiver(
				getElement().getNode(), "camera", new CameraStreamVariable(receiver)));
	}

	private void fireFinishedEvent(String mime) {
		fireEvent(new FinishedEvent(this, true, mime));
	}

	public void stopCamera() {
//		getElement().callFunction("stopCamera");
		getElement().callJsFunction("stopCamera");
	}

	public void takePicture() {
//		getElement().callFunction("takePicture");
		getElement().callJsFunction("takePicture");
	}

	public void showPreview() {
//		getElement().callFunction("showPreview");
		getElement().callJsFunction("showPreview");
	}

	public Registration addFinishedListener(ComponentEventListener<FinishedEvent> listener) {
		return addListener(FinishedEvent.class, listener);
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