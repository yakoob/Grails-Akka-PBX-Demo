package audio

import grails.transaction.Transactional
import grails.util.Holders
import groovy.util.logging.Log4j
import message.Shout
import message.Speak

@Log4j
@Transactional(readOnly = true)
class AudioService {

    def akkaService = Holders.applicationContext.getBean("akkaService")
    def speechActorService = Holders.applicationContext.getBean("speechActorService")

    def prepare(Shout message) {

        def file = new File("/var/audio/${message.audioId.toString()}.mp3")

        if( !file.exists() ) {
            def audioBinary = AudioBinary.findById(new Long(message.audioId))
            if (!audioBinary) {
                speechActorService.actorRef.tell(new Speak(channelUuid: message.channelUuid, words: "Audio ID ${message.audioId} NOT FOUND..." ), akkaService.actorNoSender())
                return
            } else {
                file.withOutputStream{ os -> os << new ByteArrayInputStream(audioBinary.fileBinary)?.bytes}
            }

        }

    }

}
