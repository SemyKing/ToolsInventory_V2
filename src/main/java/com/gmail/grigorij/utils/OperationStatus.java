package com.gmail.grigorij.utils;

import com.gmail.grigorij.ui.utils.UIUtils.NotificationType;

public interface OperationStatus {
	void onSuccess(String msg, NotificationType type);
	void onFail(String msg, NotificationType type);
}
