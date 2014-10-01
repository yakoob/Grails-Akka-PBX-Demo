package state

import audio.Audio
import utils.traits.Greeting

class Menu extends Voice implements Greeting {

    Audio noInputAudio
    Audio noMatchAudio
    Audio extensionNotFoundAudio
    Audio disconnectAudio

    Voice.TypeCode getTypeCode() {
        return Voice.TypeCode.MENU
    }

    static mapping = {

        greeting lazy: false
        noInputAudio lazy: false
        noMatchAudio lazy: false
        extensionNotFoundAudio lazy: false
        disconnectAudio lazy: false



    }


}
