package audio

class Audio {

    String uuid = UUID.randomUUID().toString()

    String description

    AudioTypeCode audioTypeCode
    ContextTypeCode contextTypeCode

    enum AudioTypeCode{SHARED,ACCOUNT}

    enum ContextTypeCode{ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, FAXACCEPTING,
    ERRORGENERALTRYAGAIN, CALLERDISCONNECTED, CALLERCONNECTING, PARTYCONNECTINGNEW,
    EXTENSIONLABEL, DISCONNECTGOODBYE, ERRORINVALIDSELECTION, IVRLABEL,
    ERRORINVALIDEXTENSION, PARTYDISCONNECTED, THEPARTYNOTANSWERING, FORWARDINGACCEPTCALL,
    RECONNECTING, RECONNECTINGCALLER, ERRORNUMBERDIALEDCANCELLED, ERRORNUMBERDIALEDDISABLED,
    ERRORNUMBERDIALEDPURCHASE, ERRORNUMBERDIALEDUNRECOGNIZED, ERRORNUMBERDIALEDSUSPENDEDTEMP,
    NAVIGATEBACKSTAR, VOICEMAILLEAVEMESSAGEPRESSONE, IVRLISTENVOICEMENUPRESSONE,
    IVRLISTENMESSAGEPRESSONE, BRANDTOLLFREEFORWARDING, NAVIGATELOGINPOUND,
    EXTENSIONREACHANOTHER, IVRREPEATPRESSNINE, VOICEMAILRECORDAGAINPRESSTWO,
    NAVIGATERETURNTOCALLERSTAR, MESSAGESENDPOUNDHANGUP, TRANSFERTOIVR, TRANSFERPROGRESS,
    TRANSFERSUCCESS, ERRORIDLE, VOICEMAILWHENFINISHEDPRESSPOUNDHANGUP, VOICEMAILGREETING,
    IVRGREETING, MESSAGESENT, ANNOUNCEMENTGREETING, RINGBACKTONE, HOLD}

    static mapping = {
        tablePerHierarchy false
        contextTypeCode enumType: "string"
        audioTypeCode enumType: "string"

    }

    static constraints = {
        description nullable: true
        audioTypeCode nullable: true
        contextTypeCode nullable: true
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
