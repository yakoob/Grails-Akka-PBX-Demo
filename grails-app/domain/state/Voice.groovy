package state

import account.Account
import audio.Actor

class Voice {

    enum TypeCode {
        GREETING, MENU, SCHEDULE
    }

    TypeCode typeCode = TypeCode.GREETING

    String description
    Account account
    Actor audioActor
    Integer maxDurationMinutes

    Voice nextState
    Integer nextStateSeconds

    static mapping = {
        typeCode enumType: 'string'
        tablePerHierarchy false

        account lazy: false
        audioActor lazy: false
        nextState lazy: false
    }

    static constraints = {
        description nullable: true
        nextState nullable: true
        nextStateSeconds nullable: true
        maxDurationMinutes nullable: true
        audioActor nullable: true
        account nullable: true
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
