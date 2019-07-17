package com.gmail.grigorij.utils.camera;

import java.io.OutputStream;

@FunctionalInterface
public interface DataReceiver {
	OutputStream getOutputStream(String var1);
}
