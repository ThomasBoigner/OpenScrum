package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.InitializeSprintCommand
import at.fhtw.openscrum.scrum.application.dtos.SprintDto
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintRepository
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class SprintApplicationService(
    private val sprintService: SprintService,
    private val sprintRepository: SprintRepository,
    private val log: Logger = LoggerFactory.getLogger(SprintApplicationService::class.java),
) {
    fun getSprintsOfProject(projectId: UUID): List<SprintDto> {
        log.debug("Trying to get all sprint from project with id {}", projectId)
        val sprints = sprintRepository.findSprintsByProjectId(projectId)
        log.info("Found all ({}) sprints of project with id {}", sprints.size, projectId)
        return sprints.map { SprintDto(it) }
    }

    fun getSprint(
        projectId: UUID,
        sprintId: UUID,
    ): SprintDto? {
        log.debug("Trying to get sprint of project with id {} and sprintId {}", projectId, sprintId)
        val sprint = sprintRepository.findSprintBySprintId(SprintId(projectId, sprintId))
        log.info(sprint?.let { "Found sprint $it" } ?: "Sprint with sprint id $projectId could not be found")
        return sprint?.let { SprintDto(it) }
    }

    fun initializeSprint(command: InitializeSprintCommand): SprintDto {
        log.debug("Trying to initialize sprint with command {}", command)

        return SprintDto(sprintService.initializeSprint(command.projectId, command.sprintLength))
    }
}
