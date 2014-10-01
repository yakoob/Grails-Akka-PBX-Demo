package utils.time

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class TimeUtility {

/**
 * Converts a DateTime object into UTC time zone.
 *
 * @param dateTime
 * @return
 */
    public static DateTime convertLocalizedUtcFormat(DateTime dateTime, DateTimeZone sourceDateTimeZone, DateTimeZone destinationDateTimeZone) {

        // setup localized server Date/Time values; localized for processing rules now
        DateTimeZone utcTimeZone = DateTimeZone.UTC;
        DateTime cdt = null;

        // utcTimeZone.getOffset()
        // check if the server date timezone is in same zone as data if so no need to convert it
        // this only considers the DayTime Rules of today by setting the offset mills of now
        if ( sourceDateTimeZone.getOffset( new DateTime().getMillis() ) == destinationDateTimeZone.getOffset( new DateTime().getMillis() ) ){
            cdt = dateTime;
        } else {
            cdt = dateTime.toDateTime(destinationDateTimeZone);
        }

        // format date as utc w/o converting it...
        cdt = new DateTime(cdt.year().get(), cdt.monthOfYear().get(), cdt.dayOfMonth().get(), cdt.hourOfDay().get(), cdt.minuteOfHour().get(), utcTimeZone);
        return cdt;
    }

    /**
     * Reformats a DateTime object into UTC time zone.
     *
     * @param dateTime
     * @return
     */
    public static DateTime convertUtcFormat(DateTime dateTime) {

        // format date as utc w/o converting it...
        DateTimeZone utcTimeZone = DateTimeZone.UTC;
        return new DateTime(dateTime.year().get(), dateTime.monthOfYear().get(), dateTime.dayOfMonth().get(), dateTime.hourOfDay().get(), dateTime.minuteOfHour().get(), utcTimeZone);
    }

    /**
     * Resets the "time" specific elements of a DateTime object to zeros.
     *
     * @param dateTime
     * @return
     */
    public static DateTime convertDateNoTime(DateTime dateTime) {

        // format date as utc w/o converting it...
        DateTimeZone utcTimeZone = DateTimeZone.UTC;
        return new DateTime(dateTime.year().get(), dateTime.monthOfYear().get(), dateTime.dayOfMonth().get(), 0, 0, 0, 0, utcTimeZone);
    }

    /**
     * Returns a Date object representing the first day of the current month.
     *
     * @return
     */
    public static Date getFirstDayOfMonthDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return new Date(calendar.getTimeInMillis());
    }

    /**
     * Returns a Long object representing the first day of the current month in milliseconds.
     *
     * @return
     */
    public static Long getFirstDayOfMonthLong() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * Returns a Date object representing the current day of the current month.
     *
     * @return
     */
    public static Date getCurrentDayOfMonthDate() {

        Calendar calendar = Calendar.getInstance();

        return new Date(calendar.getTimeInMillis());
    }

    /**
     * Returns a Long object representing the current day of the current month in milliseconds.
     *
     * @return
     */
    public static Long getCurrentDayOfMonthLong() {

        Calendar calendar = Calendar.getInstance();

        return calendar.getTimeInMillis();
    }

    /**
     * Returns a Date object representing the last day of the current month.
     *
     * @return
     */
    public static Date getLastDayOfMonthDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);

        return new Date(calendar.getTimeInMillis());
    }


}
