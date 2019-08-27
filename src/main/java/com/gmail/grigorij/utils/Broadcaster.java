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

	private static HashMap<Long, Consumer<String>> userBroadcasterList = new HashMap<>();

	public static synchronized Registration registerUser(long userId, Consumer<String> listener) {
		if (userBroadcasterList.get(userId) != null) {
			userBroadcasterList.replace(userId, listener);
		} else {
			userBroadcasterList.put(userId, listener);
		}

		return () -> {
			synchronized (Broadcaster.class) {
				userBroadcasterList.remove(userId);
			}
		};
	}


	public static synchronized void broadcastToUser(long userId, String message) {
		if (userBroadcasterList.get(userId) != null) {
			executor.execute(() -> userBroadcasterList.get(userId).accept(message));
		}
	}

	public static void removeBroadcasterForUser(long userId) {
		userBroadcasterList.remove(userId);
	}
}
