package com.gmail.grigorij.utils;


public interface OperationStatus {
	void onSuccess(String message);
	void onFail();
}
