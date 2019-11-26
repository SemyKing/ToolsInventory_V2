package com.gmail.grigorij.ui.components.camera;

import com.gmail.grigorij.utils.OperationStatus;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CameraView extends AbstractCameraView {

	public CameraView() {}

	public void showPreview() {
		getCamera().showPreview();
	}

	public void takePicture() {
		getCamera().takePicture();
	}

	public void stop() {
		getCamera().stopCamera();
	}

	public void onFinished(OperationStatus status) {
		getCamera().addFinishedListener(e -> {
			if (e != null) {
				String mime = e.getMime();

				if (mime.contains("image")) {
					File file = getLatest();

					if (file != null) {
						try {
							String response = decodeCodeInImage(file);

							if (response == null) {
								try {
									Thread.sleep(500);
								} catch (InterruptedException ex) {
									ex.printStackTrace();
								}
								status.onFail();
							} else {
								if (response.length() <= 0) {
									try {
										Thread.sleep(500);
									} catch (InterruptedException ex) {
										ex.printStackTrace();
									}
									status.onFail();
								} else {
									status.onSuccess(response);
								}
							}
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});
	}

	private String decodeCodeInImage(File imgFile) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(imgFile);
		LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		try {
			Result result = new MultiFormatReader().decode(bitmap);
			return result.getText();
		} catch (NotFoundException e) {
			return null;
		}
	}
}
