package akka

import actor.SpeechActor
import akka.actor.ActorRef
import grails.transaction.Transactional

@Transactional(readOnly = true)
class SpeechActorService {

    def akkaService

    ActorRef actorRef

    public void setActorRef(ActorRef ref){
        actorRef = akkaService.actorOf(SpeechActor, "Speech")
    }

    public ActorRef getActorRef() {
        return actorRef
    }
}
