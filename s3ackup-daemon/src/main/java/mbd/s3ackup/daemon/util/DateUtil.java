package mbd.s3ackup.daemon.util;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

public class DateUtil extends DateUtils {

	public static Date returnNewestDate(Date... dates) {
		Date rtnVal = null;
		for (Date dt : dates) {
			if (dt == null) {
				continue;
			}
			if (rtnVal == null) {
				rtnVal = dt;
			}
			if (dt.after(rtnVal)) {
				rtnVal = dt;
			}
		}
		return rtnVal;
	}

	public static String getTimeDifferenceAsString(long start, long end) {
		float f = (end - start) / 1000.0f;
		return String.format("%.3f seconds", f);
	}

	public static String getTimeDifferenceAsString(long t) {
		return getTimeDifferenceAsString(t, System.currentTimeMillis());
	}
}
