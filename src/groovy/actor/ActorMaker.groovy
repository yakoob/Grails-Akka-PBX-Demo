package actor

import actor.InboundChannelActor
import akka.actor.ActorRef
import akka.actor.Props
import grails.util.Holders


trait ActorMaker {

    def akkaService = Holders.applicationContext.getBean("akkaService")

    public ActorRef makeInboundChannelActor(String channelUuid, String destination){

        return akkaService.system.actorOf(Props.create(InboundChannelActor.class, channelUuid, destination), "$channelUuid-$destination");

    }


}