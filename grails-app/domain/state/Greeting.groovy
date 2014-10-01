package state

class Greeting extends Voice implements utils.traits.Greeting {

    Voice.TypeCode getATypeCode() {
        return Voice.TypeCode.GREETING
    }

    static mapping = {
        greeting lazy: false
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
