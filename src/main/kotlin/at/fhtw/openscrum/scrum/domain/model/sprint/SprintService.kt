package at.fhtw.openscrum.scrum.domain.model.sprint

import at.fhtw.openscrum.scrum.domain.util.Page
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

class SprintService(
    private val sprintRepository: SprintRepository,
    private val log: Logger = LoggerFactory.getLogger(SprintService::class.java),
) {
    fun getSprints(
        projectId: UUID,
        sprintNameQuery: String? = null,
        page: Int = 0,
        size: Int = 5,
    ): Page<Sprint> {
        require(page >= 0) { "Page number must not be negative!" }
        require(size > 0) { "Page size must be positive!" }
        return sprintRepository.findSprintsByProjectId(projectId, sprintNameQuery, page, size)
    }

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
