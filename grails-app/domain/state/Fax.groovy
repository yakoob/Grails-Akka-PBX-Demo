package state

import account.Account

class Fax {
    enum TypeCode {
        ASSIGNED, DISABLED, UNASSIGNED
    }
    TypeCode typeCode = TypeCode.UNASSIGNED
    String description
    Account account

    enum FaxFormat {
        PDF, TIFF
    }
    FaxFormat faxFormat
    String password

    static mapping = {
        typeCode enumType: 'string'
        faxFormat enumType: 'string'
        tablePerHierarchy false
    }

    static constraints = {
        description()
        account nullable: true
        faxFormat nullable: true
        password nullable: true
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
