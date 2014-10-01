package actor

import event.FaxDetected
import event.Hungup
import event.KeyPressed
import event.Parked
import grails.util.Holders
import message.*
import scala.concurrent.duration.Duration
import state.All
import state.Greeting
import state.InboundChannelState
import state.Init
import state.Menu
import state.None
import utils.traits.ObjectBinding

import state.Fax
import phone.PhoneNumber

import java.util.concurrent.TimeUnit

class InboundChannelActor extends BaseActor implements ObjectBinding, InboundChannelState {

    def akkaService = Holders.applicationContext.getBean("akkaService")

    def audioActorService = Holders.applicationContext.getBean("audioActorService")
    def speechActorService = Holders.applicationContext.getBean("speechActorService")

    def freeSwitchActorService = Holders.applicationContext.getBean("freeSwitchActorService")


    Integer faxTimeout = 27
    boolean faxTimerStarted = false
    boolean faxDetected = false
    boolean faxEnabled = false

    boolean nextCallActionTimerStarted = false

    public InboundChannelActor(String channelUuid, String destination) {

        this.channelUuid = channelUuid
        this.destination = destination

        // record a transition for Event: "Parked and All" from State: "None" to State: "Greeting"
        state_ChannelState__fsm.record().on([Parked]).from(None).to(None).act = {

            log.info("on Parked from None -> None")
            state_ChannelState__fsm.goToState(state_ChannelState__fsm.currentValue)

        }

        // Initialize a Greeting
        state_ChannelState__fsm.record().on([Init]).from(Greeting).to(Greeting).act = { event ->
            // play greeting
            audioActorService.actorRef.tell(new Shout(channelUuid: channelUuid, audioId: state_ChannelState__fsm.currentValue.greeting.id), context.self())
            // start timer for next action
            akkaService.system.scheduler().scheduleOnce(Duration.create(state_ChannelState__fsm.currentValue.nextStateSeconds, TimeUnit.SECONDS),
                    new Runnable() {@Override public void run() {
                        state_ChannelState__fsm.goToState(new Init(), state_ChannelState__fsm.currentValue.nextState)
                    } }, akkaService.system.dispatcher())
        }

        // Play a menu Greeting
        state_ChannelState__fsm.record().on([Init]).from(Menu).to(Menu).act = { event ->
            // play menu greeting
            audioActorService.actorRef.tell(new Shout(channelUuid: channelUuid, audioId: state_ChannelState__fsm.currentValue.greeting.id), akkaService.actorNoSender())
        }

        // Wait for menu events or other state events
        state_ChannelState__fsm.record().on([KeyPressed]).from(Menu).to(Menu).act = { event ->

            speechActorService.actorRef.tell(new Speak(channelUuid: channelUuid, words: "dtmf ${event.digit}"), akkaService.actorNoSender())

        }

        state_ChannelState__fsm.record().on([Hungup]).from(All).to(All).act = { event ->

            context.stop(context.self())

        }

        configureActions()


    }

    @Override
    void onReceive(Object message) throws Exception {


        if (message instanceof Parked && !message.variableActor && message.inbound) {

            freeSwitchActorService.actorRef.tell(copyObject(message, new SetVar(key: "actor", value: self.path().toString())), context.self())
            freeSwitchActorService.actorRef.tell(copyObject(message, new Answer()), context.self())

            if (faxEnabled) {
                freeSwitchActorService.actorRef.tell(new AcceptFax(channelUuid: channelUuid, filePath: "/var/fax/${channelUuid}.tiff"), akkaService.actorNoSender())
                startFaxTimer()
            }

        } else if (message instanceof FaxDetected){

            faxDetected = true
            log.info("FAX DETECTED")

        }

        state_ChannelState__fsm.fire(message, state_ChannelState__fsm.currentState)

    }




    def configureActions(){

        def phoneNumber = PhoneNumber.findByE164(destination)


        if (!phoneNumber) {
            log.info( "phoneNumber $destination not found" )
            speechActorService.actorRef.tell(new Speak(channelUuid: channelUuid, words: "inbound dnis not in our system"), akkaService.actorNoSender())

            freeSwitchActorService.actorRef.tell(new Hangup(channelUuid: channelUuid), akkaService.actorNoSender())
        }


        if (phoneNumber) {

            if (phoneNumber.faxState && phoneNumber.faxState.typeCode == Fax.TypeCode.ASSIGNED)
                faxEnabled = true


            if (!phoneNumber.voiceState) {

                speechActorService.actorRef.tell(new Speak(channelUuid: channelUuid, words: "CallAction not detected"), context.self())

            } else {

                state_ChannelState__fsm.goToState(phoneNumber.voiceState)

            }

        }
    }


    def startFaxTimer(){

        if (faxEnabled && !faxTimerStarted){

            log.info(" FAX TIMER STARTED ")

            faxTimerStarted = true

            akkaService.system.scheduler().scheduleOnce(Duration.create(faxTimeout, TimeUnit.SECONDS),
                    new Runnable() {
                        @Override
                        public void run() {

                            def tts = ""

                            if (faxDetected) {

                                tts = "FAX DETECTED..."
                                log.info(tts)


                            } else {

                                tts = "FAX NOT DETECTED..."
                                log.info(tts)


                            }

                        }
                    }, akkaService.system.dispatcher()
            )
        }

    }


}
