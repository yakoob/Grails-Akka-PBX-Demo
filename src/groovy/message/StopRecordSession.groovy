package message

class StopRecordSession extends Message {
    String recordPath = "/var/audio/recordings/$channelUuid"
}
