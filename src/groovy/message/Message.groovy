package message

import groovy.util.logging.Log4j
import utils.traits.Channel

@Log4j
class Message implements Serializable, Channel {
    def delegate
}
