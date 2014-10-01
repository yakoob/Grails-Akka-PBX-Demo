package audio

import account.Account

class AudioAccount extends Audio {

    Account account

    Audio.AudioTypeCode getAudioTypeCode() {
        return Audio.AudioTypeCode.ACCOUNT
    }

    static constraints = {
        account nullable: true
    }

    static mapping = {
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
