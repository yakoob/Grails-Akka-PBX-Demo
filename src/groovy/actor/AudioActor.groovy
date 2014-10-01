package actor

import akka.event.Logging
import akka.event.LoggingAdapter
import grails.util.Holders
import message.Shout

class AudioActor extends BaseActor {

    def akkaService = Holders.applicationContext.getBean("akkaService")
    def freeSwitchActorService = Holders.applicationContext.getBean("freeSwitchActorService")


    LoggingAdapter log = Logging.getLogger(getContext().system(), this)

	public AudioActor() {}

    @Override
    void onReceive(Object message) throws Exception {

        log.info(context.self().toString())

        if (message instanceof Shout){

            log.info("!!!!! AUDIO ACTOR $message.channelUuid")
            def audioService = Holders.applicationContext.getBean("audioService")
            audioService.prepare(message)
            freeSwitchActorService.actorRef.tell(message, akkaService.actorNoSender())

        }

    }

}