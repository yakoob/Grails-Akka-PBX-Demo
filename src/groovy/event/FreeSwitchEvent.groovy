package event

import utils.traits.Channel


class FreeSwitchEvent implements Serializable, Channel {

    Boolean inbound = false
    Boolean outbound = false

}
