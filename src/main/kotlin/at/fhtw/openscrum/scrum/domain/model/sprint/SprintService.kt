package at.fhtw.openscrum.scrum.domain.model.sprint

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

class SprintService(
    private val sprintRepository: SprintRepository,
    private val log: Logger = LoggerFactory.getLogger(SprintService::class.java),
) {
    fun initializeSprint(
        projectId: UUID,
        sprintLength: Long,
    ): Sprint {
        log.debug("Trying to initialize sprint of project {}", projectId)

        val sprint =
            Sprint(
                sprintId = SprintId(projectId = projectId),
                sprintNumber = sprintRepository.countByProjectId(projectId) + 1,
                sprintLength = sprintLength,
            )

        log.info("Initialized sprint {}", sprint)
        return sprintRepository.save(sprint)
    }
}
