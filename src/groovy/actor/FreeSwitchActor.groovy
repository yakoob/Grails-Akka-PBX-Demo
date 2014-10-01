package actor

import akka.actor.ActorContext
import akka.actor.UntypedActor
import akka.event.Logging
import akka.event.LoggingAdapter
import message.Answer
import message.Bridge
import message.Hangup
import message.Originate
import message.SetVar
import message.Shout
import message.AcceptFax
import message.PlaybackTerminators
import message.RecordSession
import message.RingReady
import message.SendDtmf
import message.Speak
import message.StopRecordSession
import org.freeswitch.esl.client.transport.SendMsg
import org.jboss.netty.channel.Channel

class FreeSwitchActor extends BaseActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this)

    public static final String LINE_TERMINATOR = "\n";

    public Channel channel

    public FreeSwitchActor(Channel channel) {
        this.channel = channel
    }

    @Override
    void onReceive(Object message) throws Exception {

        if (message instanceof Channel){
            this.channel = message
            return
        }

        if (message instanceof SetVar) {

            api("uuid_setvar ${message.channelUuid} ${message.key} ${message.value}")

        } else if (message instanceof Answer) {

            bgapi("uuid_answer ${message.channelUuid}")

        } else if (message instanceof Speak) {

            SendMsg msg = new SendMsg(message.channelUuid)
            msg.addCallCommand("execute")
            msg.addExecuteAppName("speak")
            msg.addExecuteAppArg(message.words)
            msg.addEventLock()
            write(sendMsgParser(msg.msgLines))

        } else if (message instanceof Shout) {

            SendMsg msg = new SendMsg(message.channelUuid)
            if (message.audioId){
                msg.addCallCommand("execute")
                msg.addExecuteAppName("playback")
                msg.addExecuteAppArg("/var/audio/${message.audioId}.mp3")
                write(sendMsgParser(msg.msgLines))
            }

        } else if (message instanceof RecordSession){

            // bgapi("uuid_record ${message.channelUuid} start /var/audio/recordings/a_${message.channelUuid}.wav")

            SendMsg msg = new SendMsg(message.channelUuid)
            msg.addCallCommand("execute")
            msg.addExecuteAppName("record_session")
            msg.addExecuteAppArg("/var/audio/recordings/${message.channelUuid}.wav")
            write(sendMsgParser(msg.msgLines))


        } else if (message instanceof StopRecordSession){

            SendMsg msg = new SendMsg(message.channelUuid)
            msg.addCallCommand("execute")
            msg.addExecuteAppName("stop_record_session")
            msg.addExecuteAppArg(message.recordPath)
            write(sendMsgParser(msg.msgLines))

        } else if (message instanceof SendDtmf){

            sleep(7000)
            SendMsg msg = new SendMsg(message.channelUuid)
            msg.addCallCommand("execute")
            msg.addExecuteAppName("send_dtmf")
            msg.addExecuteAppArg(message.digits)
            msg.addEventLock()
            write(sendMsgParser(msg.msgLines))

        } else if (message instanceof RingReady) {

            SendMsg msg = new SendMsg(message.channelUuid)
            msg.addCallCommand("execute")
            msg.addExecuteAppName("ring_ready")
            write(sendMsgParser(msg.msgLines))

            msg = new SendMsg(message.channelUuid)
            msg.addCallCommand("execute")
            msg.addExecuteAppName("set")
            msg.addExecuteAppArg(message.ringReadyTone)
            write(sendMsgParser(msg.msgLines))

        } else if (message instanceof AcceptFax) {

            SendMsg msg = new SendMsg(message.channelUuid)
            msg.addCallCommand("execute")
            msg.addExecuteAppName("playback")
            msg.addExecuteAppArg("silence_stream://2000")
            write(sendMsgParser(msg.msgLines))

            msg = new SendMsg(message.channelUuid)
            msg.addCallCommand("execute")
            msg.addExecuteAppName("spandsp_start_fax_detect")
            msg.addExecuteAppArg("rxfax '${message.filePath}'")
            write(sendMsgParser(msg.msgLines))

        } else if (message instanceof PlaybackTerminators) {

            SendMsg msg = new SendMsg(message.channelUuid)
            msg.addCallCommand("execute")
            msg.addExecuteAppName("set")
            msg.addExecuteAppArg(message.terminators)
            write(sendMsgParser(msg.msgLines))

        } else if (message instanceof Hangup) {

            SendMsg msg = new SendMsg(message.channelUuid);
            msg.addCallCommand("execute")
            msg.addExecuteAppName("hangup")
            write(sendMsgParser(msg.msgLines))

        } else if (message instanceof Originate) {

            bgapi("originate {return_ring_ready=true,origination_uuid=${UUID.randomUUID().toString()},actor=${message.variableActor}}sofia/external/${message.ringToAddress} &park()")

        } else if (message instanceof Bridge) {

            bgapi("uuid_answer ${message.channelUuid}")
            bgapi("uuid_bridge ${message.channelUuid} ${message.bridgeChannelUuid}")

        } else {

            log.debug "message not handled, message: ${message.toString()}"

        }


    }


    public String sendMsgParser(List<String> commandLines) {
        StringBuilder sb = new StringBuilder();

        for (String line : commandLines) {
            sb.append(line);
            sb.append(LINE_TERMINATOR);
        }
        sb.append(LINE_TERMINATOR);
        return sb.toString();
    }

    def bgapi(String msg){
        write("bgapi $msg\n\n")
    }

    def api(String msg){
        return write("api $msg\n\n")
    }

    def write(String msg){
        log.info(msg);
        channel.write(msg)
    }

}