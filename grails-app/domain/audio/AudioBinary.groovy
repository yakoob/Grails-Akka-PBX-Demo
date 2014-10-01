package audio

class AudioBinary {

    String uuid = UUID.randomUUID().toString()

    byte[] fileBinary

    static mapping = {
        fileBinary sqlType: "longblob"
    }

    static constraints = {
        uuid nullable: true
    }
}
