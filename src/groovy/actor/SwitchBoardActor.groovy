package actor

import akka.event.Logging
import akka.event.LoggingAdapter
import event.FreeSwitchEvent
import event.Parked
import grails.util.Holders
import message.Answer
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA

class SwitchBoardActor extends BaseActor implements ActorMaker {

    def akkaService = Holders.applicationContext.getBean("akkaService")

    def ctx = SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)

    LoggingAdapter log = Logging.getLogger(getContext().system(), this)

    public SwitchBoardActor() {
        log.debug("SwitchBoardActor() Initialized")
    }

    @Override
    void onReceive(Object message) throws Exception {

        /*
        if (message instanceof FreeSwitchEvent){
            message.inbound ? log.info("INBOUND Message") : log.info("OUTBOUND Message")
        }
        */

        if (message instanceof FreeSwitchEvent && message.variableActor) {

            getContext().system().actorSelection(message.variableActor).tell(message, context.self())

        } else if (message instanceof FreeSwitchEvent && message instanceof Parked && message.inbound) {

            makeInboundChannelActor(message.channelUuid, message.destination).tell(message, context.self())

        } else if (message instanceof FreeSwitchEvent && message instanceof Parked && message.outbound) {

            log.info("SwitchBoardActor outbound message Parked: ${message.toString()}")

        }  else if (message instanceof FreeSwitchEvent && message instanceof Answer && message.outbound) {

            log.info("SwitchBoardActor outbound message Parked: ${message.toString()}")

        } else {

            log.error "SwitchBoardActor: message not handled, message: ${message.toString()}"

        }

    }


}
