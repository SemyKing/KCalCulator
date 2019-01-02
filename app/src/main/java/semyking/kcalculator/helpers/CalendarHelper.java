package semyking.kcalculator.helpers;

import com.shrikanthravi.collapsiblecalendarview.data.Day;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarHelper {

    private final static SimpleDateFormat fullDateFormatter = new SimpleDateFormat("d.M.yyyy", Locale.GERMANY);
    private final static SimpleDateFormat shortDateFormatter = new SimpleDateFormat("d.M", Locale.GERMANY);

    public static Calendar getCalendar() {
        Calendar mCalendar = Calendar.getInstance(Locale.GERMAN);
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        return  mCalendar;
    }

    public static String formatFullDate(Date date) {
        return fullDateFormatter.format(date);
    }

    public static String formatShortDate(Date date) {
        return shortDateFormatter.format(date);
    }

    public static Date parseDate(String dateString) {
        Date date = new Date();
        try {
            date = fullDateFormatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static boolean dateBetween(Long milliSeconds, Calendar fromCal, Calendar toCal) {

        Calendar cal = getCalendar();
        cal.setTimeInMillis(milliSeconds);

        if (cal.getTime().equals(fromCal.getTime()) || cal.getTime().equals(toCal.getTime())) {
            return true;
        }
        return cal.getTime().after(fromCal.getTime()) && cal.getTime().before(toCal.getTime());
    }

    public static boolean dateInWeek(Long milliSeconds, int week) {
        boolean res = false;

        Calendar cal = getCalendar();
        cal.setTimeInMillis(milliSeconds);

        if (cal.get(Calendar.WEEK_OF_YEAR) == week)
            res = true;

        return res;
    }

    public static Date getDateFromMilliseconds(Long milliSeconds) {
        Calendar cal = getCalendar();
        cal.setTimeInMillis(milliSeconds);
        return cal.getTime();
    }

    public static int getWeekOfYear(Day day) {
        Calendar cal = getCalendar();
        cal.set(Calendar.YEAR, day.getYear());
        cal.set(Calendar.MONTH, day.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, day.getDay());
        return cal.get(Calendar.WEEK_OF_YEAR);
    }
}
