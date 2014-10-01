package audio

class AudioShared extends Audio {

    Actor audioActor

    Audio.AudioTypeCode getAudioTypeCode() {
        return Audio.AudioTypeCode.SHARED
    }

    static constraints = {
        audioActor nullable: true
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
