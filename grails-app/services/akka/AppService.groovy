package akka

import grails.transaction.Transactional
import org.joda.time.DateTime

@Transactional
class AppService {

    def grailsApplication
    def akkaService
    def switchBoardActorService
    def audioActorService
    def speechActorService
    def freeSwitchActorService
    def freeSwitchConnectionService

    public static DateTime LAST_HEARTBEAT = DateTime.now()

    def init() {
        if (!akkaService.system){
            akkaService.init()
        }
        createActors()
        freeSwitchConnectionService.connect(grailsApplication.config.freeswitch.host, new Integer(grailsApplication.config.freeswitch.port))
        LAST_HEARTBEAT = DateTime.now()
    }

    def destroy(){

        if (audioActorService.actorRef)
            akkaService.system.stop(audioActorService.actorRef)

        if (speechActorService.actorRef)
            akkaService.system.stop(speechActorService.actorRef)

        if (switchBoardActorService.actorRef)
            akkaService.system.stop(switchBoardActorService.actorRef)

        if (freeSwitchActorService.actorRef)
            akkaService.system.stop(freeSwitchActorService.actorRef)

        akkaService.destroy()

    }

    def createActors() {
        switchBoardActorService.setActorRef(akkaService.actorNoSender())
        speechActorService.setActorRef(akkaService.actorNoSender())
        audioActorService.setActorRef(akkaService.actorNoSender())
    }
}
