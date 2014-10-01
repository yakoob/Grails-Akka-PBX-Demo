package freeswitch

import akka.AppService
import event.Answered
import event.FaxComplete
import event.FaxDetected
import event.Hungup
import event.KeyPressed
import event.Parked
import grails.transaction.Transactional
import grails.util.Holders
import groovy.util.logging.Log4j
import org.freeswitch.esl.client.transport.event.EslEvent
import org.freeswitch.esl.client.transport.message.EslFrameDecoder
import org.freeswitch.esl.client.transport.message.EslHeaders
import org.freeswitch.esl.client.transport.message.EslMessage
import org.jboss.netty.bootstrap.ClientBootstrap
import org.jboss.netty.channel.*
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory
import org.jboss.netty.handler.codec.string.StringEncoder
import org.joda.time.DateTime
import utils.traits.ObjectBinding

import java.util.concurrent.Executors

@Transactional(readOnly = true)
class FreeSwitchConnectionService {

    def akkaService
    def freeSwitchActorService

    FreeSwitchPipelineFactory pipelineFactory = new FreeSwitchPipelineFactory();
    Channel channel

    def connect(String freeSwitchHost, int freeSwitchPort) {

        NioClientSocketChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool())

        final ClientBootstrap bootstrap = new ClientBootstrap(factory);
        bootstrap.setPipelineFactory( pipelineFactory )
        bootstrap.setOption("tcpNoDelay" , true)
        bootstrap.setOption("keepAlive", false)

        ChannelFuture future = bootstrap.connect( new InetSocketAddress( freeSwitchHost, freeSwitchPort ) )

        if(future.awaitUninterruptibly(1000)){

            if(future.isSuccess()){

                log.info("connected to FreeSwitch at $freeSwitchHost:$freeSwitchPort")
                this.channel = future.getChannel();
                freeSwitchActorService.setActorRef(akkaService.actorNoSender(), channel)

            } else {

                future.cancel()
                log.error("$future.cause",)
                log.error("failed to connect to FreeSwitch at $freeSwitchHost:$freeSwitchPort")

            }
        }
    }
}


class FreeSwitchPipelineFactory implements ChannelPipelineFactory {
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline()
        pipeline.addLast("encoder", new StringEncoder())
        pipeline.addLast("decoder",  new EslFrameDecoder(8092,true))
        pipeline.addLast("eventHandler", new EventHandler())
        pipeline.addLast("eventWrapper", new EventWrapper())
        return pipeline
    }
}


@Log4j
class EventHandler extends SimpleChannelUpstreamHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {

        def ga = Holders.applicationContext.getBean("grailsApplication")

        def im = e.message as EslMessage

        switch (im.contentType) {

            //ignore command/reply, unless it's an error, then log it
            case "command/reply":
                def reply = im.getHeaderValue(EslHeaders.Name.REPLY_TEXT)

                /*
                if (reply.startsWith("-ERR")) {
                    log.error(im)
                }
                */
                break

            //if pipe initialization requests authentication, provide it
            case "auth/request":
                //send password to freeswitch
                ctx.channel.write('auth '+ga.config.freeswitch.password+'\n\n')
                //tell freeswitch what events we are interested in
                ctx.channel.write('event plain HEARTBEAT CHANNEL_PARK CHANNEL_ANSWER CHANNEL_PROGRESS CHANNEL_PROGRESS_MEDIA DTMF PLAYBACK_STOP CHANNEL_HANGUP CHANNEL_HANGUP_COMPLETE CUSTOM spandsp::rxfaxnegociateresult spandsp::rxfaxresult\n\n')
                // ctx.channel.write('event plain ALL\n\n')
                break
            //send upstream so message can be wrapped and strong
            case "text/event-plain":
                ctx.sendUpstream(new UpstreamMessageEvent(ctx.channel, new EslEvent(im, false), e.remoteAddress))
                break

        }
    }

}


@Log4j
class EventWrapper extends SimpleChannelUpstreamHandler implements ObjectBinding {

    @Override
    public void messageReceived(ChannelHandlerContext ctx,  MessageEvent e){

        def akkaService = Holders.applicationContext.getBean("akkaService")
        def switchboardActor = Holders.applicationContext.getBean("switchBoardActorService").actorRef

        def event = e.message as EslEvent

        def eventName = event.eventName
        def subEventName = event.eventHeaders.get("Event-Subclass")

        log.debug("eventName: $eventName")
        // log.debug("subEventName: $subEventName")

        def fsEventMap = [:]
        fsEventMap.callerId = event.eventHeaders.get("Caller-Orig-Caller-ID-Number")
        fsEventMap.destination = event.eventHeaders.get("Caller-Destination-Number")
        fsEventMap.channelUuid = event.eventHeaders.get("Unique-ID")
        fsEventMap.variableActor = event.eventHeaders.get("variable_actor")
        fsEventMap.inbound = event.eventHeaders.get("Call-Direction") == "inbound" ? true : false
        fsEventMap.outbound = !fsEventMap.inbound
        fsEventMap.digit = event.eventHeaders.get("DTMF-Digit")
        fsEventMap.codec = event.getEventHeaders().get("write_codec")

        log.debug("DESTINATION: " + fsEventMap.destination)

        if (eventName == "CHANNEL_PARK") {

            log.info(" ")
            Parked parked = copyMap(fsEventMap, new Parked())
            switchboardActor.tell(parked, akkaService.actorNoSender())

        } else if (eventName == "CHANNEL_ANSWER") {

            Answered answered = copyMap(fsEventMap, new Answered())
            switchboardActor.tell(answered, akkaService.actorNoSender())

        } else if (eventName == "CUSTOM" && subEventName == "spandsp::rxfaxnegociateresult") {

            log.info("@@@ INCOMING FAX DETECTED @@@")
            FaxDetected faxDetected = copyMap(fsEventMap, new FaxDetected())
            switchboardActor.tell(faxDetected, akkaService.actorNoSender())

        } else if (eventName == "CUSTOM" && subEventName == "spandsp::rxfaxresult"){

            log.info("@@@ FAX COMPLETE @@@")
            FaxComplete faxComplete = copyMap(fsEventMap, new FaxComplete())
            switchboardActor.tell(faxComplete, akkaService.actorNoSender())

        } else if (eventName == "DTMF"){

            log.info("@@@ DTMF DIGIT: ${fsEventMap.digit} @@@")
            KeyPressed keyPressed = copyMap(fsEventMap, new KeyPressed())
            keyPressed.variableActor = "/user/${keyPressed.channelUuid}-${keyPressed.destination}"
            switchboardActor.tell(keyPressed, akkaService.actorNoSender())

        } else if (eventName == "HEARTBEAT") {

            AppService.LAST_HEARTBEAT = DateTime.now()

        } else if (eventName == "CHANNEL_HANGUP"){

            Hungup hungup = copyMap(fsEventMap, new Hungup())

            switchboardActor.tell(hungup, akkaService.actorNoSender())

        } else {

            log.debug("FreeSwitchConnection.EventWrapper() - Message not handled")

        }

    }

}

