package akka

import actor.AudioActor
import akka.actor.ActorRef
import grails.transaction.Transactional

@Transactional(readOnly = true)
class AudioActorService {

    def akkaService

    ActorRef actorRef

    public void setActorRef(ActorRef ref){
        actorRef = akkaService.actorOf(AudioActor, "Audio")
    }

    public ActorRef getActorRef() {
        return actorRef
    }
}
