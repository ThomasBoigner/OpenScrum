package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.CancelSprintCommand
import at.fhtw.openscrum.scrum.application.command.CompleteSprintsCommand
import at.fhtw.openscrum.scrum.application.command.InitializeSprintCommand
import at.fhtw.openscrum.scrum.application.command.MoveSprintBacklogItemCommand
import at.fhtw.openscrum.scrum.application.command.PlanSprintCommand
import at.fhtw.openscrum.scrum.application.dtos.SprintBacklogItemDto
import at.fhtw.openscrum.scrum.application.dtos.SprintDto
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.productbacklogitem.ProductBacklogItemRepository
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintBacklogItemStatus
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintId
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintRepository
import at.fhtw.openscrum.scrum.domain.model.sprint.SprintService
import at.fhtw.openscrum.scrum.domain.model.teammember.DeveloperRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwnerRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMasterRepository
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
    private val productOwnerRepository: ProductOwnerRepository,
    private val scrumMasterRepository: ScrumMasterRepository,
    private val developerRepository: DeveloperRepository,
    private val productBacklogItemRepository: ProductBacklogItemRepository,
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

    fun getSprintBacklogItems(
        projectId: UUID,
        sprintId: UUID,
        sprintBacklogItemStatus: SprintBacklogItemStatus,
    ): List<SprintBacklogItemDto> {
        log.debug(
            "Trying to get sprint backlog items for sprint with project id {}, sprint id {} and sprint backlog item status {}",
            projectId,
            sprintId,
            sprintBacklogItemStatus,
        )
        val sprint =
            sprintRepository.findSprintBySprintId(SprintId(projectId, sprintId)) ?: throw IllegalArgumentException(
                "Could not find sprint with projectId $projectId and sprintId $sprintId",
            )
        val sprintBacklogItems =
            sprint.getSprintBacklogItems(sprintBacklogItemStatus).map {
                SprintBacklogItemDto(
                    it,
                    it.assignedDeveloper?.let { teamMemberId -> developerRepository.findByTeamMemberId(teamMemberId) },
                    sprint,
                )
            }
        log.info(
            "Found {} sprint backlog items of sprint with id {} that have status {}",
            sprintBacklogItems.size,
            sprintId,
            sprintBacklogItemStatus,
        )
        return sprintBacklogItems
    }

    @Transactional(readOnly = false)
    fun initializeSprint(command: InitializeSprintCommand): SprintDto {
        log.debug("Trying to initialize sprint with command {}", command)

        return SprintDto(sprintService.initializeSprint(command.projectId, command.sprintLength))
    }

    @Transactional(readOnly = false)
    fun planSprint(
        authenticatedUserUsername: String,
        command: PlanSprintCommand,
    ): SprintDto {
        log.debug("Trying to plan sprint with command {}", command)

        val sprint =
            sprintRepository.findSprintBySprintId(SprintId(command.projectId, command.sprintId))
                ?: throw IllegalArgumentException(
                    "Could not find sprint with projectId ${command.projectId} and sprintId ${command.sprintId}",
                )
        val productBacklogItems =
            command.productBacklogIds
                .map {
                    productBacklogItemRepository.findProductBacklogItemByProductBacklogItemId(
                        ProductBacklogItemId(projectId = command.projectId, productBacklogItemId = it),
                    ) ?: throw IllegalArgumentException(
                        "Could not find product backlog item with projectId ${command.projectId} and productBacklogItemId $it",
                    )
                }.toSet()
        val scrumMaster = scrumMasterRepository.findByProjectIdAndUsername(command.projectId, authenticatedUserUsername)

        sprint.planSprint(
            scrumMaster = scrumMaster,
            sprintGoal = command.sprintGoal,
            productBacklogItems = productBacklogItems,
        )

        log.info("Planned sprint {}", sprint)

        return SprintDto(sprintRepository.save(sprint))
    }

    @Transactional(readOnly = false)
    fun moveSprintBacklogItem(
        authenticatedUserUsername: String,
        command: MoveSprintBacklogItemCommand,
    ): SprintBacklogItemDto {
        log.debug("Trying to move sprint backlog item with command {}", command)

        val sprint =
            sprintRepository.findSprintBySprintId(SprintId(command.projectId, command.sprintId))
                ?: throw IllegalArgumentException(
                    "Could not find sprint with projectId ${command.projectId} and sprintId ${command.sprintId}",
                )

        val developer = developerRepository.findByProjectIdAndUsername(command.projectId, authenticatedUserUsername)

        val sprintBacklogItem =
            sprint.moveSprintBacklogItem(
                SprintBacklogItemId(command.projectId, command.sprintId, command.productBacklogItemId),
                command.moveDirection,
                developer,
            )

        sprintRepository.save(sprint)

        log.info("Moved sprint backlog item {}", sprintBacklogItem)

        return SprintBacklogItemDto(
            sprintBacklogItem,
            sprintBacklogItem.assignedDeveloper?.let {
                developerRepository.findByTeamMemberId(it)
            },
            sprint,
        )
    }

    @Transactional(readOnly = false)
    fun cancelSprint(
        authenticatedUserUsername: String,
        command: CancelSprintCommand,
    ): SprintDto {
        log.debug("Trying to cancel sprint with command {}", command)

        val sprint =
            sprintRepository.findSprintBySprintId(SprintId(command.projectId, command.sprintId))
                ?: throw IllegalArgumentException(
                    "Could not find sprint with projectId ${command.projectId} and sprintId ${command.sprintId}",
                )

        val productOwner =
            productOwnerRepository.findByProjectIdAndUsername(command.projectId, authenticatedUserUsername)

        sprint.cancelSprint(productOwner)

        log.info("Canceled sprint {}", sprint)

        return SprintDto(sprintRepository.save(sprint))
    }

    @Transactional(readOnly = false)
    fun completeSprints(command: CompleteSprintsCommand): List<SprintDto> {
        log.debug("Trying to complete all sprints that have ended before {}", command.date)

        val sprints = sprintRepository.findSprintsByEndDateBeforeAndStatusInProgressOrStatusNotPlanned(command.date)
        sprints.forEach { it.completeSprint() }

        log.info("Completed {} sprints", sprints.size)
        return sprints.map { SprintDto(sprintRepository.save(it)) }
    }
}
