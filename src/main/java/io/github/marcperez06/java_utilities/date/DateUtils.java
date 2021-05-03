package io.github.marcperez06.java_utilities.date;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    private DateUtils() {}

    private final static DateTimeFormatter ISO_DATE_TIME_MILLIS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'.'SSSZZZ");
    private final static DateTimeFormatter ISO_DATE_TIME_MILLIS_WITH_COLON = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'.'SSSxxx");

    public static LocalDateTime parseFromISOTimeWithMillis(String date) {
		LocalDateTime parsedDate = null;
		if (date != null) {
			parsedDate = LocalDateTime.parse(date, ISO_DATE_TIME_MILLIS);
		}
		return parsedDate;
    }

    public static LocalDateTime parseFromISOTimeWithMillisAndColon(String date) {
		LocalDateTime parsedDate = null;
		if (date != null) {
			parsedDate = LocalDateTime.parse(date, ISO_DATE_TIME_MILLIS_WITH_COLON);
		}
		return parsedDate;
    }

    public static String toIsoTimeString(LocalDateTime date) {
        String stringDate = null;
        if (date != null) {
            stringDate = date.format(ISO_DATE_TIME_MILLIS);
        }

        return stringDate;
    }

    public static LocalDate parseFromShortFormat(String date) {
		LocalDate parsedDate = null;
		if (date != null) {
			parsedDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
		}
		return parsedDate;
    }
    
    public static String toShortFormatString(LocalDate date) {
        String stringDate = null;
        if (date != null) {
            stringDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        return stringDate;
    }
    
    public static Timestamp getCurrentTimestamp() {
		return new Timestamp(getCurrentTime());
	}
    
    public static long getCurrentTime() {
    	Date today = new Date();
    	return today.getTime();
	}
    
}