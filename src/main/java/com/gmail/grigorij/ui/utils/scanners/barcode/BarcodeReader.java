package com.gmail.grigorij.ui.utils.scanners.barcode;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.function.SerializableConsumer;

@JavaScript("code-readers/barcode/javascript-barcode-reader.js")
@JavaScript("code-readers/barcode/camSupport.js")
public class BarcodeReader extends Div {

	private SerializableConsumer<String> codeConsumer;

	public BarcodeReader() {
		add(new Html("<div id='barcodeCamLoadingMessage'></div>"));
		add(new Html("<canvas id='barcodeCamCanvas' hidden style='width:100%'></canvas>"));
	}

	public BarcodeReader(SerializableConsumer<String> codeConsumer) {
		this();
		this.codeConsumer = codeConsumer;
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		attachEvent.getUI().getPage().executeJavaScript("barcodeCam.init()");
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
		getUI().ifPresent(ui -> ui.getPage().executeJavaScript("barcodeCam.reset()"));
	}

	public void stop() {
		getUI().ifPresent(ui -> ui.getPage().executeJavaScript("barcodeCam.stop()"));
	}
}
