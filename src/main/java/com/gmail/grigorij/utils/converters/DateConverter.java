package com.gmail.grigorij.utils.converters;

import java.util.Date;
import java.text.SimpleDateFormat;

public class DateConverter {

	private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
	private final static SimpleDateFormat dateWithTimeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	public static String toStringDate(Date date) {
		if (date == null) {
			return "";
		} else {
			try {
				return dateFormatter.format(date);
			} catch (Exception e) {
				System.out.println("Error converting Date: '" + date +"' to String");
				e.printStackTrace();
				return "";
			}
		}
	}

	public static String toStringDateWithTime(Date date) {
		if (date == null) {
			return "";
		} else {
			try {
				return dateWithTimeFormatter.format(date);
			} catch (Exception e) {
				System.out.println("Error converting Date: '" + date +"' to String");
				e.printStackTrace();
				return "";
			}
		}
	}

}
