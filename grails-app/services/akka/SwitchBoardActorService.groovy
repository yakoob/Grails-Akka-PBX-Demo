package akka

import actor.SwitchBoardActor
import akka.actor.ActorRef
import grails.transaction.Transactional

@Transactional(readOnly = true)
class SwitchBoardActorService {

    def akkaService

    ActorRef actorRef

    public void setActorRef(ActorRef ref){
        actorRef = akkaService.actorOf(SwitchBoardActor, "SwitchBoard")
    }

    public ActorRef getActorRef() {
        return actorRef
    }
}
