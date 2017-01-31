package nexnet.com.solution.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.text.TextUtils;

import com.google.common.base.Strings;

/**
 * A Helper class with various utilities related to date time 
 */
public class DateUtil {

	private final static String dateFormatISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	// hide constructor
	private DateUtil() {}
	/**
	 * Format elapsed time to H:MM:SS / HH:MM:SS / HHH:MM:SS / ... format
	 * use toElapsedTime instead
	 *  
	 * @param elapsedTime time in milliseconds
	 *            
	 * @return duration in H:MM:SS
	 */
	public static String formatElapsedTime(long elapsedTime) {
		StringBuilder buf = new StringBuilder(20);
		String sgn = "";

		if (elapsedTime < 0) {
			sgn = "-";
			elapsedTime = Math.abs(elapsedTime);
		}

		append(buf, sgn, 0, (elapsedTime / 3600000)); // Hours
		append(buf, ":", 2, ((elapsedTime % 3600000) / 60000));// Minutes
		append(buf, ":", 2, ((elapsedTime % 60000) / 1000));// Seconds
//		append(buf, ".", 3, (elapsedTime % 1000));// Milliseconds
		return buf.toString();
	}

	/** Append a right-aligned and zero-padded numeric elapsedTime to a `StringBuilder`. */
	static private void append(StringBuilder tgt, String pfx, int dgt, long elapsedTime) {
		tgt.append(pfx);
		if (dgt > 1) {
			int pad = (dgt - 1);
			for (long xa = elapsedTime; xa > 9 && pad > 0; xa /= 10) {
				pad--;
			}
			for (int xa = 0; xa < pad; xa++) {
				tgt.append('0');
			}
		}
		tgt.append(elapsedTime);
	}

	/**
	 * Get today in yyyy-MM-dd format
	 * 
	 * @return String in yyyy-MM-dd format, empty string on error
	 */
	public static String getNowString() {
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String retDate = "";
		try {
			retDate = formatter.format(now);
		} catch (Exception ex) {

		}
		return retDate;
	}


	/**
	 * parse the date to the age
	 * <p>
	 * eg : birthday 2000 - 7 - 30 current year : 2013 - 7 -30 <br>
	 * result age = 13
	 * 
	 * @param birthday a well formatted date..., i.e., 2000-7-30
	 * @return age in year
	 */
	public static int dobToAge(String birthday) {
		return dobToAge(birthday, Calendar.getInstance());
	}

	/**
	 * @see int dobToAge(String birthday)
	 */
	public static int dobToAge(String birthday, Calendar today) {

		if (Strings.isNullOrEmpty(birthday)) {
			//Log.d("birthday is null , return 0");

			return 0;
		}

		Calendar dob = Calendar.getInstance();

		String[] date = birthday.split("-");

		dob.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));

		int curYear = today.get(Calendar.YEAR);
		int curMonth = today.get(Calendar.MONTH);
		int curDay = today.get(Calendar.DAY_OF_MONTH);

		int year = dob.get(Calendar.YEAR);
		int month = dob.get(Calendar.MONTH);
		int day = dob.get(Calendar.DAY_OF_MONTH);

		int age = curYear - year;

		if (curMonth < month || (month == curMonth && curDay < day)) {
			age--;
		}

		return age;
	}

	public static long fromISO8601Date(String text, long defaultValue) {
		long time = defaultValue;

		if (!TextUtils.isEmpty(text)) {
			try {
				DateFormat dateFormat = new SimpleDateFormat(dateFormatISO8601, Locale.US);
				dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				time = dateFormat.parse(text).getTime();
			} catch (Exception e) {
				time = defaultValue;
			}
		}

		return time;
	}

	public static String formatDuration(long duration) {
		long durationInSec = (long) Math.ceil(duration / 1000d);
		return String.format(Locale.US, "%d:%02d:%02d", durationInSec / 3600, (durationInSec % 3600) / 60, (durationInSec % 60));
	}

}
