package actor

import akka.actor.UntypedActor
import akka.event.Logging
import akka.event.LoggingAdapter

abstract class BaseActor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this)

    @Override
    void onReceive(Object message) throws Exception{
        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@ baseActor.onReceive called @@@@@@@@@@@@@@@@@@@@@@@@@@@@")
    }


}
