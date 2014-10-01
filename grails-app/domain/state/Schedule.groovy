package state

import com.google.ical.compat.jodatime.DateTimeIteratorFactory
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Minutes
import utils.time.TimeUtility

import java.text.ParseException

class Schedule extends Voice{

    String timeZone

    static hasMany = [scheduleItems: ScheduleItem]

    Voice.TypeCode getTypeCode(){
        return Voice.TypeCode.SCHEDULE
    }

    def Long getScheduleIdForDateAndTimeZone(DateTime currentDate, DateTimeZone timeZone) throws ParseException {

        // work with runtime DateTimeZone in UTC for relative equality
        DateTimeZone utcTimeZone = DateTimeZone.UTC;

        // setup localized call control runtime Date/Time values; localized for processing rules now
        DateTime currentDateTime =  TimeUtility.convertLocalizedUtcFormat(new DateTime(currentDate), timeZone, DateTimeZone.forID(this.getTimeZone()));

        // loop over the schedule items
        for (ScheduleItem ci : this.scheduleItems ){

            // setup date properties for evaluating occurrence
            DateTime itemStartDate = TimeUtility.convertUtcFormat(new DateTime(ci.getStartDateTime()));
            DateTime itemEndDate = TimeUtility.convertUtcFormat(new DateTime(ci.getEndDateTime()));
            DateTime itemUntilDate = TimeUtility.convertUtcFormat(new DateTime(ci.getExpiryDateTime()));

            // setup a property to measure this event duration in minutes
            Minutes minutesBetweenItemStartItemEnd = Minutes.minutesBetween(itemStartDate, itemEndDate);

            // set expiry date with the proper duration since the UI doesn't account for duration
            itemUntilDate = itemUntilDate.plusMinutes(minutesBetweenItemStartItemEnd.getMinutes());

            // process non recurring schedule items
            if (ci.getRepeatTypeCode().equals(ScheduleItem.RepeatTypeCode.NEVER)){
                if ( currentDateTime.isAfter(itemStartDate) && currentDateTime.isBefore(itemEndDate) ){
                    // log the event duration on the schedule item for picking a winner in the later routines
                    ci.setDuration( itemEndDate.getMillis() - itemStartDate.getMillis() );
                } else if (currentDateTime.isEqual(itemStartDate)) {
                    ci.setDuration( itemEndDate.getMillis() - itemStartDate.getMillis() );
                } else if (currentDateTime.isEqual(itemEndDate)) {
                    ci.setDuration( itemEndDate.getMillis() - itemStartDate.getMillis() );
                }
            }
            // process recurring schedule items
            else {
                // retrieve the iCal formatted RRule for processing with google-rfc
                String icalRruleString = ci.createRrule();
                // make sure the RRule is populated and ensure the Event expiration date is not exceeded
                if (icalRruleString != null && itemUntilDate.isAfter(currentDateTime) ) {
                    // iterate over all event occurrences in past few days
                    for (DateTime iDateTimeStart : DateTimeIteratorFactory.createDateTimeIterable(icalRruleString, itemStartDate.minusDays(3), utcTimeZone, true)){
                        // calculate the event duration based on the relative duration of the first occurrence
                        DateTime iDateTimeEnd = iDateTimeStart.plusMinutes(minutesBetweenItemStartItemEnd.getMinutes());
                        // make sure the event falls during the localized now datetime
                        if (currentDateTime.isAfter(iDateTimeStart) && currentDateTime.isBefore(iDateTimeEnd)) {

                            // check if this winning item does not have an exclusion
                            if (!hasExclusions(ci, currentDateTime)){
                                // log the event duration on the schedule item for picking a winner in the later routines
                                ci.setDuration( iDateTimeEnd.getMillis() - iDateTimeStart.getMillis() );
                            }
                            break;
                        }
                    }
                }
            }
        }

        // check the schedule items for duration and pick the item with shortest duration for the winner.
        ScheduleItem winningItem = null;
        for (ScheduleItem ci : this.scheduleItems ) {
            if (ci.getDuration() != null) {
                // if there is not winningItem populated, use the fist item with duration as winner
                if (winningItem == null){
                    winningItem = ci;
                }
                // if a later item has a shorter duration that current winner, overwrite with new winner
                else {
                    if ( ci.getDuration() < winningItem.getDuration() ){
                        winningItem = ci;
                    }
                }
            }
        }

        // by default use the ScheduleAction nextActionId
        Long scheduleId = this.nextStateId();

        // if there is a wining schedule item use the items schedule action instead of the schedules nextAction
        if (winningItem != null)
            scheduleId = winningItem.id();

        return scheduleId;
    }


    private boolean hasExclusions(ScheduleItem item, DateTime iDateTimeStart) {
        if (item.scheduleItemExclusions.size() > 0) {
            DateTime iDateStart = TimeUtility.convertDateNoTime(iDateTimeStart);
            Minutes minutesBetweenItemStartItemEnd = Minutes.minutesBetween(new DateTime(item.getStartDateTime()), new DateTime(item.getEndDateTime()));
            for (ScheduleItemExclusion exclusion : item.scheduleItemExclusions) {
                DateTime exDateStart = TimeUtility.convertDateNoTime(new DateTime(exclusion.getExclusionDate()));
                DateTime exDateEnd = TimeUtility.convertDateNoTime(exDateStart.plusMinutes(minutesBetweenItemStartItemEnd.getMinutes()));
                if (iDateStart.equals(exDateStart)) {
                    return true;
                }
                if (iDateStart.equals(exDateEnd)){
                    return true;
                }
                if (iDateStart.isAfter(exDateStart) && iDateStart.isBefore(exDateEnd)){
                    return true;
                }
            }
        }
        return false;
    }

}
