package audio


class Actor {

    String description;
    String languageCode;
    String countryCode;
    AudioActorTypeCode audioActorTypeCode;

    enum AudioActorTypeCode {
        MALE, FEMALE, NONE
    }

    static mapping = {
        audioActorTypeCode enumType: 'string'
    }

    static constraints = {
        languageCode nullable: true
        countryCode nullable: true
        audioActorTypeCode nullable: true
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
