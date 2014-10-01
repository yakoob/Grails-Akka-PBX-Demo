package state

import org.jadira.usertype.dateandtime.joda.PersistentDateTime
import org.joda.time.DateTime

class ScheduleItemExclusion {

    DateTime exclusionDate

    static belongsTo = [scheduleItem: ScheduleItem]

    static constraints = {
        exclusionDate nullable: true
        scheduleItem nullable: true
    }

    static mapping = {
        exclusionDate type: PersistentDateTime
    }
}
