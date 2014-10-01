package state

import groovy.util.logging.Log4j
import utils.traits.Channel

@Log4j
trait ChannelState implements Channel {

    public FiniteStateMachine fsm = FiniteStateMachine.newInstance(None)


}