package state

import org.jadira.usertype.dateandtime.joda.PersistentDateTime
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import state.phone.ScheduleItemRecurrence

class ScheduleItem {

    enum RepeatTypeCode {NEVER, DAILY, WEEKLY, MONTHLY, YEARLY}

    /**
     * This is the equi``valent of 9999-12-31 23:59:59 GMT.
     * This is the MAX date we want to store in the DB for when an item goes "forever".
     */
    static final Long MAX_DATE = 253402300799000L

    /**
     * This is the MAX year that we store for items that don't expire.
     */
    static final int MAX_YEAR = 9999

    Voice callState
    String description
    DateTime startDateTime
    DateTime endDateTime
    DateTime expiryDateTime
    Boolean repeatMonday
    Boolean repeatTuesday
    Boolean repeatWednesday
    Boolean repeatThursday
    Boolean repeatFriday
    Boolean repeatSaturday
    Boolean repeatSunday
    Boolean endDate
    String color
    RepeatTypeCode repeatTypeCode
    List<ScheduleItemRecurrence> scheduleItemRecurrences  = new ArrayList<ScheduleItemRecurrence>()

    Long duration;

    static transients = ['MAX_DATE','MAX_YEAR','endDate', 'scheduleItemRecurrences', 'duration']

    static belongsTo = [schedule: Schedule]

    static hasMany = [scheduleItemExclusions: ScheduleItemExclusion]

    static mapping = {
        repeatTypeCode enumType: 'string'
        startDateTime type: PersistentDateTime
        endDateTime type: PersistentDateTime
        expiryDateTime type: PersistentDateTime
    }

    public String createRrule() {
        String rruleString = "";
        if (getExpiryDateTime() != null){
            DateTime expiryDate = new DateTime(getExpiryDateTime());
            rruleString = rruleString + ";UNTIL=" + expiryDate.toString(DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss'Z'"));
        }
        return rruleString;
    }

}
