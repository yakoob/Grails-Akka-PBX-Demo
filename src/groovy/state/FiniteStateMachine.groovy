package state

import groovy.util.logging.Log4j

@Log4j
class FiniteStateMachine {

    def transitions = []

    def initialState
    def previousState
    def previousValue
    def currentState
    def currentValue

    FiniteStateMachine(startingState) {
        assert startingState, "You need to provide an initial state"
        initialState = startingState
        currentState = startingState
    }

    def record() {
        Grammar.newInstance(this)
    }

    def reset() {
        currentState = initialState
    }

    def goToState(toState) {

        if (!toState)
            toState = None

        def state = toState.class.canonicalName

        log.info(" ")
        log.info("Transistion: ${this.currentState} => ${state}")
        log.info(" ")

        this.previousState = this.currentState
        this.currentState = state
        this.previousValue = this.currentValue
        this.currentValue = toState

        // initialize new state
        fire(new Init(), currentState)

    }


    def goToState(e, toState) {

        def event = e.class.canonicalName
        def state = toState.class.canonicalName

        if (transitions.find {

            it.event == event
            it.fromState == this.currentState
            it.toState == state

        }) {


            log.info(" ")
            log.info("Transistion on $event from ${this.currentState} => ${state}")
            log.info(" ")
            // from -> to transition
            fire(event, state)



            this.previousState = this.currentState
            this.currentState = state
            this.previousValue = this.currentValue
            this.currentValue = toState


            log.info(" ")
            log.info("Transistion on Init() from ${currentState} => ${currentState}")
            log.info(" ")
            // initialize new state
            fire(new Init(), currentState)

        } else {

            log.info("no record of transition found")

        }

    }

    def registerTransition(Grammar grammar) {

        grammar.eventMatches.each { event ->
            grammar.event = event
            transitions << grammar
        }


    }

    // TODO: clean this up now that its working...
    def fire(e, s) {

        def originalEvent = e

        def thisState = this.currentState

        if (e instanceof String)
            println(e)
        else
            e = e.class.canonicalName

        if (transitions.findAll{Grammar g -> g.event == e}.each { Grammar it ->

            def matcherEventGrammar
            def matcherEvent

            def matcherFromStateGrammar
            def matcherFromState

            def matcherToStateGrammar
            def matcherToState

            matcherEventGrammar = it.event.toString()
            matcherEvent = e.toString()

            matcherFromStateGrammar = it.fromState.toString()
            if (thisState instanceof String) {
                matcherFromState = thisState
            } else {
                matcherFromState = thisState.name
            }

            matcherToStateGrammar = it.toState.toString()
            if (s instanceof String) {
                matcherToState = s
            } else {
                matcherToState = s.name
            }

            if (matcherEventGrammar == null)
                return

            if (matcherFromState == null)
                return

            if (matcherToState == null)
                return

            if (matcherEventGrammar == matcherEvent && matcherFromStateGrammar == "state.All" && matcherToStateGrammar == "state.All") {

                it.act(originalEvent)

            } else if ( matcherEventGrammar == matcherEvent && matcherFromStateGrammar == matcherFromState && matcherToStateGrammar == matcherToState ){

                println "Grammer Action called on $it"
                it.act(originalEvent)

            }

        })

        return currentState
    }
}