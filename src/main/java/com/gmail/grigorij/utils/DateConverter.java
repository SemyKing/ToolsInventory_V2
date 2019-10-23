package com.gmail.grigorij.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DateConverter {

	private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
	private final static SimpleDateFormat dateFormatterWithTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	private final static DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private final static DateTimeFormatter  localDateFormatterWithTime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");



	public static String dateToString(Date date) {
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

	public static String dateToStringWithTime(Date date) {
		if (date == null) {
			return "";
		} else {
			try {
				return dateFormatterWithTime.format(date);
			} catch (Exception e) {
				System.out.println("Error converting Date: '" + date +"' to String with time");
				e.printStackTrace();
				return "";
			}
		}
	}

	public static String localDateToString(LocalDate date) {
		if (date == null) {
			return "";
		} else {
			try {
				return localDateFormatter.format(date);
			} catch (Exception e) {
				System.out.println("Error converting LocalDate: '" + date +"' to String");
				e.printStackTrace();
				return "";
			}
		}
	}
	public static String localDateToStringWithTime(LocalDate date) {
		if (date == null) {
			return "";
		} else {
			try {
				return localDateFormatterWithTime.format(date);
			} catch (Exception e) {
				System.out.println("Error converting LocalDate: '" + date +"' to String with time");
				e.printStackTrace();
				return "";
			}
		}
	}

}
