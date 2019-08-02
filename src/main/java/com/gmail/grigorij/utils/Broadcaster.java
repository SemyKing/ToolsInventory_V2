package com.gmail.grigorij.utils;

import com.gmail.grigorij.backend.entities.user.User;
import com.vaadin.flow.shared.Registration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


/**
 * Allows to send messages to other users who are online
 *
 * --Currently sends messages to ALL online users
 */
public class Broadcaster {

	private static Executor executor = Executors.newSingleThreadExecutor();

	private static HashMap<Long, Consumer<String>> list = new HashMap<>();

//	private static LinkedList<Consumer<String>> listeners = new LinkedList<>();

//	public static synchronized Registration register( Consumer<String> listener) {
//		listeners.add(listener);
//
//		return () -> {
//			synchronized (Broadcaster.class) {
//				listeners.remove(listener);
//			}
//		};
//	}

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

//	public static synchronized void broadcast(String message) {
//		for (Consumer<String> listener : listeners) {
//			executor.execute(() -> listener.accept(message));
//		}
//	}



	public static synchronized void broadcastToUser(long userId, String message) {

		if (list.get(userId) != null) {
			executor.execute(() -> list.get(userId).accept(message));
		}
	}
}
