package at.fhtw.openscrum.scrum.domain.model

interface EventPublisher {
    fun publishEvent(event: Any)
}
