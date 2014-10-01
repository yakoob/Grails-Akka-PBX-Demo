package actor


import akka.event.Logging
import akka.event.LoggingAdapter
import grails.util.Holders
import message.SetVar
import message.Speak
import utils.traits.ObjectBinding


class SpeechActor extends BaseActor implements ObjectBinding {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this)

    def akkaService = Holders.applicationContext.getBean("akkaService")

    def freeSwitchActorService = Holders.applicationContext.getBean("freeSwitchActorService")

    @Override
    void onReceive(Object message) throws Exception {

        if (message instanceof Speak) {

            freeSwitchActorService.actorRef.tell(copyObject(message, new SetVar(key: "tts_engine", value: message.engine)), akkaService.actorNoSender())
            freeSwitchActorService.actorRef.tell(copyObject(message, new SetVar(key: "tts_voice", value: message.voice)), akkaService.actorNoSender())
            freeSwitchActorService.actorRef.tell(copyObject(message, new Speak(words: message.words)), akkaService.actorNoSender())

        }

    }

}
