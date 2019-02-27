package com.yhsoft.photoremember.util;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static java.lang.System.currentTimeMillis;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;
import static java.util.TimeZone.getDefault;

public class DateUtil {
    /**
     * Default photo bucket range is monthly.
     * It's possible to occur too many photos in one bucket if we use the annual range.
     */
    public static final int RANGE_YEAR = 0;
    public static final int RANGE_MONTH = 1;
    public static final int RANGE_DAY = 2;

    private static SimpleDateFormat sFormatter;
    private static SimpleDateFormat sFormatDay;
    private static SimpleDateFormat sFormatMonth;
    private static SimpleDateFormat sFormatYear;
    private static SimpleDateFormat sFormatYearPickMode;
    private static SimpleDateFormat sFormatOtherPickMode;
    public static long[] millis = new long[2];

    static {
        sFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        sFormatDay = new SimpleDateFormat("yyyy.MM.dd");
        sFormatMonth = new SimpleDateFormat("yyyy.MM");
        sFormatYear = new SimpleDateFormat("yyyy");
        sFormatYearPickMode = new SimpleDateFormat("MM-dd");
        sFormatter.setTimeZone(getDefault());
    }

    public static String getDateString(long millis, int range) {
        DateFormat formatter = null;
        Date d = new Date(millis);
        switch (range) {
            case RANGE_DAY:
                formatter = sFormatDay;
                break;
            case RANGE_MONTH:
                formatter = sFormatMonth;
                break;
            case RANGE_YEAR:
                formatter = sFormatYear;
                break;
        }
        if (formatter != null) {
            return formatter.format(d);
        } else {
            return null;
        }
    }

    public static String getDateStringInPickMode(long millis) {
        DateFormat formatter = sFormatYearPickMode;
        Date d = new Date(millis);
        if (formatter != null) {
            return formatter.format(d);
        } else {
            return null;
        }
    }

    public static int getCurrentDayInPickMode() {
        Calendar calendar = getInstance();
        calendar.clear();
        calendar.setTimeInMillis(currentTimeMillis());
        return calendar.get(DAY_OF_MONTH);
    }

    public static long getNextDateInMillis(long millis, int range) {
        Calendar calendar = getInstance();
        calendar.clear();
        calendar.setTimeInMillis(millis);

        switch (range) {
            case RANGE_YEAR:
                calendar.set(YEAR, (calendar.get(YEAR) + 1));
                break;
            case RANGE_MONTH:
                calendar.set(MONTH, (calendar.get(MONTH) + 1));
                break;
            case RANGE_DAY:
                calendar.set(DAY_OF_MONTH, (calendar.get(DAY_OF_MONTH) + 1));
                break;
        }

        return calendar.getTimeInMillis();
    }

    public static long getPrevDateInMillis(long millis, int range) {
        Calendar calendar = getInstance();
        calendar.clear();
        calendar.setTimeInMillis(millis);

        switch (range) {
            case RANGE_YEAR:
                calendar.set(YEAR, (calendar.get(YEAR) - 1));
                break;
            case RANGE_MONTH:
                calendar.set(MONTH, (calendar.get(MONTH) - 1));
                break;
            case RANGE_DAY:
                calendar.set(DAY_OF_MONTH, (calendar.get(DAY_OF_MONTH) - 1));
                break;
        }

        return calendar.getTimeInMillis();
    }

    public static long getDateLong(String dateString, int range) {
        DateFormat formatter = null;
        ParsePosition pos = new ParsePosition(0);
        switch (range) {
            case RANGE_DAY:
                formatter = sFormatDay;
                break;
            case RANGE_MONTH:
                formatter = sFormatMonth;
                break;
            case RANGE_YEAR:
                formatter = sFormatYear;
                break;
        }
        if (formatter != null) {
            try {
                Date datetime = formatter.parse(dateString, pos);
                if (datetime == null) {
                    return -1;
                }
                return datetime.getTime();
            } catch (IllegalArgumentException ex) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public static long getDateInMIllis(String dateString) {
        if (dateString == null) return -1;

        ParsePosition pos = new ParsePosition(0);
        try {
            Date datetime = sFormatter.parse(dateString, pos);
            if (datetime == null) return -1;
            return datetime.getTime();
        } catch (IllegalArgumentException ex) {
            return -1;
        }
    }

    public static int[] getDisplayDate(long millis) {
        Calendar calendar = getInstance();
        calendar.clear();

        calendar.setTimeInMillis(millis);
        int[] values = new int[3];
        values[0] = calendar.get(YEAR);
        values[1] = calendar.get(MONTH) + 1;
        values[2] = calendar.get(DAY_OF_MONTH);

        return values;
    }

}
