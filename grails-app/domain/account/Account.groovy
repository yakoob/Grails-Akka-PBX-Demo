package account

import audio.Actor
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class Account {

    Status status = Status.DEMO
    SubStatus subStatus = SubStatus.ACTIVE
    String timeZone
    Actor actor

    DateTime getLocalTime(){

        if(this.timeZone) {
            DateTime dt = new DateTime()
            DateTime localDt = dt.withZone(DateTimeZone.forID(this.timeZone));
            return localDt
        } else {
            return null
        }

    }

    enum Status {
        DEMO, TRIAL, PAID, CANCELLED
    }

    enum SubStatus {
        ACTIVE, SUSPENDED
    }

    static constraints = {
        actor nullable: true
        timeZone nullable: true
    }

    static mapping = {
        actor lazy: false
        status enumType: 'string'
        subStatus enumType: 'string'
        dynamicUpdate true
        version false
    }

    def beforeInsert() {
        throw RuntimeException("You must get written permission!!!")
    }

    def beforeDelete() {
        throw RuntimeException("You must get written permission!!!")
    }

    def beforeUpdate(){
        throw RuntimeException("You must get written permission!!!")
    }


}
