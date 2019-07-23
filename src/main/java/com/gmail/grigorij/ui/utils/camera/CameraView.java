package com.gmail.grigorij.ui.utils.camera;

import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.OperationStatus;
import com.gmail.grigorij.utils.camera.AbstractCameraView;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CameraView extends AbstractCameraView {

	public CameraView() {
	}

	public void showPreview() {
		getCamera().showPreview();
	}

	public void takePicture() {
		getCamera().takePicture();
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
								status.onFail("Code not found in the image", UIUtils.NotificationType.INFO);
							} else {
								if (response.length() <= 0) {
									status.onFail("Code not found in the image", UIUtils.NotificationType.INFO);
								} else {
									status.onSuccess(response, UIUtils.NotificationType.SUCCESS);
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

	public void stop() {
		getCamera().stopCamera();
	}
}
