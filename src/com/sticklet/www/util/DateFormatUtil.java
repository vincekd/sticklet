package com.sticklet.www.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
	public static String formatDate(Date date) {
		if (date != null) {
			return (new SimpleDateFormat("MM/dd/yy h:mma")).format(date);
		}
		return null;
	}
}