package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.domain.model.sprint.SprintRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SprintApplicationService(
    private val sprintRepository: SprintRepository,
    private val log: Logger = LoggerFactory.getLogger(SprintApplicationService::class.java),
)
