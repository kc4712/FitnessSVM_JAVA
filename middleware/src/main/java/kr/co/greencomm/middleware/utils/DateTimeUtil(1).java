package kr.co.greencomm.middleware.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateTimeUtil {
	
	public static String simpleDateFormat(long currentTime){
		//return toDateFormat(currentTime, "yyyy-MM-dd HH:mm:ss");
		return toDateFormat(currentTime, "yyyy-MM-dd");
	}

	/**
	 * @param milliseconds the number of milliseconds since Jan. 1, 1970 GMT.
	 * @param pattern date format(ex. yyyyMMddHHmmss, yyyy.MM.dd HH:mm:ss, ...)
	 * @return
	 */
	public static String toDateFormat(long milliseconds, String pattern){
		Date date = new Date(milliseconds);
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		//ULog.d("DateTimeUtil", "date = " + date.toString());
		return dateFormat.format(date);
	}
}
