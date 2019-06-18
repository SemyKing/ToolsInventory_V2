package com.gmail.grigorij.utils;

public interface OperationStatus {

	void onSuccess(String msg);
	void onFail(String msg);
}
