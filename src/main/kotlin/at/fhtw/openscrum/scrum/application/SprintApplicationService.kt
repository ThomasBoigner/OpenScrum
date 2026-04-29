package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.InitializeSprintCommand
import at.fhtw.openscrum.scrum.application.dtos.SprintDto
import at.fhtw.openscrum.scrum.domain.model.sprint.Sprint
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class SprintApplicationService(
    private val sprintRepository: SprintRepository,
    private val log: Logger = LoggerFactory.getLogger(SprintApplicationService::class.java),
) {
    fun getSprintsOfProject(projectId: UUID): List<SprintDto> {
        log.debug("Trying to get all sprint from project with id {}", projectId)
        val sprints = sprintRepository.findSprintsByProjectId(projectId)
        log.info("Found all ({}) sprints of project with id {}", sprints.size, projectId)
        return sprints.map { SprintDto(it) }
    }

    fun initializeSprint(command: InitializeSprintCommand): SprintDto {
        log.debug("Trying to initialize sprint with command {}", command)

        val sprint =
            Sprint(
                sprintId = SprintId(projectId = command.projectId),
                sprintLength = command.sprintLength,
            )

        log.info("Initialized sprint {}", sprint)
        return SprintDto(sprintRepository.save(sprint))
    }
}
