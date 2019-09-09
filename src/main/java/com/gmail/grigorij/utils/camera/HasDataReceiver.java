package com.gmail.grigorij.utils.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface HasDataReceiver extends DataReceiver {

	@Override
	default OutputStream getOutputStream(String mimeType) {
		if (mimeType != null) {
			String suffix;
			if (mimeType.contains("jpeg")) {
				suffix = ".jpeg";
			} else if (mimeType.contains("matroska")) {
				suffix = ".mkv";
			} else {
				suffix = ".file";
			}

			File latest = getLatest();
			if (latest != null) {
				latest.delete();
			}
			try {
				latest = File.createTempFile("camera", suffix);
				setLatest(latest);
				return new FileOutputStream(latest);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	File getLatest();

	void setLatest(File file);
}
