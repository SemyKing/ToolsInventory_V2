package com.gmail.grigorij.ui.utils.scanners.qr;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.function.SerializableConsumer;

/**
 *
 */
@JavaScript("code-readers/qr/jsQR.js")
@JavaScript("code-readers/qr/camSupport.js")
@StyleSheet("code-readers/qr/jsQRCam.css")
public class QRReader extends Div {

	private SerializableConsumer<String> codeConsumer;

	public QRReader() {
		add(new Html("<div id='jsQRCamLoadingMessage'></div>"));
		add(new Html("<canvas id='jsQRCamCanvas' hidden style='width:100%'></canvas>"));
	}

	public QRReader(SerializableConsumer<String> codeConsumer) {
		this();
		this.codeConsumer = codeConsumer;
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		attachEvent.getUI().getPage().executeJavaScript("jsQRCam.init()");
	}

	@ClientCallable
	public void onClientCodeRead(String code) {
		if (codeConsumer != null) {
			codeConsumer.accept(code);
		}
	}

	public void onCode(SerializableConsumer<String> codeConsumer) {
		this.codeConsumer = codeConsumer;
	}

	public void reset() {
		getUI().ifPresent(ui -> ui.getPage().executeJavaScript("jsQRCam.reset()"));
	}

	public void stop() {
		getUI().ifPresent(ui -> ui.getPage().executeJavaScript("jsQRCam.stop()"));
	}
}

