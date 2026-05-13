package at.fhtw.openscrum.scrum.infrastructure.messaging

import at.fhtw.openscrum.scrum.domain.model.EventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class ScrumApplicationEventPublisher(
    private val events: ApplicationEventPublisher,
) : EventPublisher {
    override fun publishEvent(event: Any) {
        events.publishEvent(event)
    }
}
