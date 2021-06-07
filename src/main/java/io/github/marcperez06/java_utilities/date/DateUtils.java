package io.github.marcperez06.java_utilities.date;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String EUROPEAN_DATE_FORMAT = "dd/MM/yyyy";
    private static final String EUROPEAN_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final String AMERICAN_DATE_FORMAT = "MM/dd/yyyy";
    private static final String AMERICAN_DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
    
    private static final String EUROPEAN_DATE_FORMAT_FOR_FILE = "dd.MM.yyyy";
    private static final String EUROPEAN_DATE_TIME_FORMAT_FOR_FILE = "dd.MM.yyyy_HH.mm.ss";
    private static final String AMERICAN_DATE_FORMAT_FOR_FILE = "MM.dd.yyyy";
    private static final String AMERICAN_DATE_TIME_FORMAT_FOR_FILE = "MM.dd.yyyy_HH.mm.ss";
    
    private static final DateTimeFormatter ISO_DATE_TIME_MILLIS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'.'SSSZZZ");
    private static final DateTimeFormatter ISO_DATE_TIME_MILLIS_WITH_COLON = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'.'SSSxxx");
	
    private DateUtils() {}
    
    public static Date getDate() {
    	Date date = null;
    	try {
    		date = new Date();
    	} catch (Exception e) {
    		date = null;
    	}
    	return date;
    }
    
    public static Date getDate(Locale locale) {
    	Date date = null;
    	try {
    		Calendar calendar = Calendar.getInstance(locale);
        	date = calendar.getTime();
    	} catch (Exception e) {
    		date = null;
    	}
    	return date;
    }
    
    public static Date getDate(long miliseconds) {
    	Date date = null;
    	try {
        	date = new Date(miliseconds);
    	} catch (Exception e) {
    		date = null;
    	}
    	return date;
    }

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
    
    public static String getTodayDate(String format) {
    	return getFormatedDate(getDate(), format);
    }
    
    public static String getTodayDate(Locale locale, String format) {
    	return getFormatedDate(getDate(locale), format);
    }
    
    public static String getFormatedDate(Date date, String format) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    	return dateFormat.format(date);
    }
    
    public static Timestamp getCurrentTimestamp() {
		return new Timestamp(getCurrentTime());
	}
    
    public static long getCurrentTime() {
    	long time = -1;
    	try {
	    	Date today = new Date();
	    	time = today.getTime();
	    } catch (Exception e) {
			throw e;
		}
		return time;
	}
    
    public static long getCurrentTime(Locale locale) {
    	long time = -1;
    	try {
    		Calendar calendar = Calendar.getInstance(locale);
        	time = calendar.getTimeInMillis();
    	} catch (Exception e) {
    		throw e;
    	}
    	return time;
	}

}