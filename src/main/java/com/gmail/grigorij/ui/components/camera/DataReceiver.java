package com.gmail.grigorij.ui.components.camera;

import java.io.OutputStream;

@FunctionalInterface
public interface DataReceiver {
	OutputStream getOutputStream(String var1);
}
