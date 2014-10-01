package state

import groovy.util.logging.Log4j

@Log4j
class Grammar {

    def fsm

    def event
    def fromState
    def toState

    def eventMatches = []
    def act

    Grammar(a_fsm) {
        fsm = a_fsm
    }

    def on(a_event) {
        a_event.each {
            eventMatches << it.canonicalName
        }
        this
    }

    def from(a_fromState) {
        fromState = a_fromState.canonicalName
        this
    }

    def to(a_toState) {
        toState = a_toState.canonicalName
        fsm.registerTransition(this)
        this
    }

    def isValid() {
        event && fromState && toState
    }

    public String toString() {
        "$event: $fromState=>$toState"
    }
}