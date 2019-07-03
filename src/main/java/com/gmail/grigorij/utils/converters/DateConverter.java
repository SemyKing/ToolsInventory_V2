package com.gmail.grigorij.utils.converters;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class DateConverter {

	final static DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

	public static String LocalDateToString(LocalDate date) {
		if (date == null) {
			return "";
		} else {
			try {
				return formatter.format(date);
			} catch (Exception e) {
				System.out.println("Error converting LocalDate: '" + date +"' to String");
				e.printStackTrace();
				return "";
			}
		}
	}

	public static String DateToString(Date date) {
		if (date == null) {
			return "";
		} else {
			try {
				return formatter.format(date);
			} catch (Exception e) {
				System.out.println("Error converting Date: '" + date +"' to String");
				e.printStackTrace();
				return "";
			}
		}
	}

}
