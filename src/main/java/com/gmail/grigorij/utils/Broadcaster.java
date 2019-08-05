package com.gmail.grigorij.utils;

import com.vaadin.flow.shared.Registration;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


/**
 * Allows to send messages to other users who are online
 *
 */
public class Broadcaster {

	private static Executor executor = Executors.newSingleThreadExecutor();

	private static HashMap<Long, Consumer<String>> list = new HashMap<>();

	public static synchronized Registration registerUser(long userId, Consumer<String> listener) {
		if (list.get(userId) != null) {
			list.replace(userId, listener);
		} else {
			list.put(userId, listener);
		}

		return () -> {
			synchronized (Broadcaster.class) {
				list.remove(userId);
			}
		};
	}


	public static synchronized void broadcastToUser(long userId, String message) {

		if (list.get(userId) != null) {
			executor.execute(() -> list.get(userId).accept(message));
		}
	}
}
