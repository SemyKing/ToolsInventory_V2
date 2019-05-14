package com.gmail.grigorij.ui.authentication;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class UserService {

	private static SecureRandom random = new SecureRandom();

	private static Map<String, String> rememberedUsers = new HashMap<>();

	public static String rememberUser(String username) {
		String randomId = new BigInteger(130, random).toString(32);

		System.out.println("New user to remember---");
		System.out.println("id: " + randomId);
		System.out.println("username: " + username);

		rememberedUsers.put(randomId, username);
		return randomId;
	}

	public static String getRememberedUser(String id) {

		System.out.println("getRememberedUser()");
		System.out.println("id: " + id);
		System.out.println("username: " + rememberedUsers.get(id));

		return rememberedUsers.get(id);
	}

	public static void removeRememberedUser(String id) {

		System.out.println("removeRememberedUser()");
		System.out.println("id: " + id);

		rememberedUsers.remove(id);
	}

}
