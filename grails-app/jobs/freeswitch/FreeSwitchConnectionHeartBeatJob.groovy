package freeswitch

import akka.AppService
import freeswitch.FreeSwitchConnectionService
import groovy.util.logging.Log4j
import org.joda.time.DateTime


@Log4j
class FreeSwitchConnectionHeartBeatJob {

    def appService
    def akkaService

    static triggers = {
        // simple name:'freeSwitchHeartbeatTrigger', startDelay:5000, repeatInterval: 10000, repeatCount: -1
    }

    def execute() {

        /*
        if (AppService.LAST_HEARTBEAT.plusSeconds(20) < DateTime.now()){
            log.info("HEARTBEAT NOT DETECTED IN TIME... RECONNECT INITIATING")
            // todo save actors
            appService.destroy()
            appService.init()
        }*/


    }

}
