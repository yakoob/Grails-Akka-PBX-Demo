package akka

import actor.FreeSwitchActor
import akka.actor.ActorRef
import akka.actor.Props
import grails.transaction.Transactional
import org.jboss.netty.channel.Channel

@Transactional(readOnly = true)
class FreeSwitchActorService {

    def akkaService

    ActorRef actorRef

    public void setActorRef(ActorRef ref, Channel channel){
        actorRef = akkaService.system.actorOf(Props.create(FreeSwitchActor.class, channel), "FreeSwitch");
    }

    public ActorRef getActorRef() {
        return actorRef
    }
}
