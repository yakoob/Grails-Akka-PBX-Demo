package akka

import akka.actor.ActorRef
import akka.actor.ActorSelection
import akka.actor.ActorSystem
import akka.actor.Props
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Akka System in the webapp
 * (singleton) non transactional.
 */
class AkkaService {

	static transactional = false  // transactional behaviour not needed here ...

	private static ActorSystem system
	private static final String ACTOR_SYSTEM_DEFAULT_NAME = "BattleField"

	// sender actor null
	private static final ActorRef ACTOR_NO_SENDER = ActorRef.noSender()

	@PostConstruct
	void init() {
		log.info "initializing Akka ActorSystem: start ..."

		system = ActorSystem.create(ACTOR_SYSTEM_DEFAULT_NAME);

        log.info("ActorSystem: $system")

		log.info "initializing Akka ActorSystem: done."
	}

	@PreDestroy
	void destroy() {
		log.info "destroying Akka ActorSystem: start shutdown ..."

		system?.shutdown()
		system = null

		log.info "destroying Akka ActorSystem: done."
	}

	ActorSystem getSystem() {
		return system
	}

	Props props(Class clazz) {
		assert clazz != null

		Props props = Props.create(clazz)
		return props
	}

	ActorRef actorOf(Props props) {
		assert props != null

		assert system != null

		ActorRef actor = system.actorOf(props)
		return actor
	}


	ActorRef actorOf(Props props, String name) {
		assert props != null
		assert name != null

		assert system != null

		ActorRef actor = system.actorOf(props, name)
		return actor
	}

	ActorRef actorOf(Class clazz) {
		assert clazz != null

		Props props = props(clazz)
		assert props != null
		assert system != null

		ActorRef actor = system.actorOf(props)
		return actor
	}

	ActorRef actorOf(Class clazz, String name) {
		assert clazz != null
		assert name != null

		Props props = props(clazz)
		assert props != null
		assert system != null

		ActorRef actor = system.actorOf(props, name)
		return actor
	}

	ActorRef actorNoSender() {
		return ACTOR_NO_SENDER
	}




}
