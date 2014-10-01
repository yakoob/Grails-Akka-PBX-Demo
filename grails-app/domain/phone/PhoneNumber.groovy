package phone

import account.Account
import state.Fax
import state.Voice

class PhoneNumber {

    String appear
    String e164
    Account owner

    Voice voiceState
    Fax faxState

    static mapping = {
        dynamicUpdate true
        version true
        voiceState lazy: false
        faxState lazy: false
    }

    static constraints = {
        appear nullable: true
        e164 nullable: true
        owner nullable: true
        voiceState nullable: true
        faxState nullable: true
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
