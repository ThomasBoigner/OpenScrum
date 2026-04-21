package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.AssignDeveloperCommand
import at.fhtw.openscrum.scrum.application.command.AssignProductOwnerCommand
import at.fhtw.openscrum.scrum.application.command.AssignScrumMasterCommand
import at.fhtw.openscrum.scrum.application.dtos.DeveloperDto
import at.fhtw.openscrum.scrum.application.dtos.ProductOwnerDto
import at.fhtw.openscrum.scrum.application.dtos.ScrumMasterDto
import at.fhtw.openscrum.scrum.application.dtos.TeamMemberDto
import at.fhtw.openscrum.scrum.domain.model.teammember.Developer
import at.fhtw.openscrum.scrum.domain.model.teammember.DeveloperRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwnerRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMasterRepository
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class TeamMemberApplicationService(
    private val teamMemberRepository: TeamMemberRepository,
    private val developerRepository: DeveloperRepository,
    private val scrumMasterRepository: ScrumMasterRepository,
    private val productOwnerRepository: ProductOwnerRepository,
    private val log: Logger = LoggerFactory.getLogger(TeamMemberApplicationService::class.java),
) {
    fun getTeamMemberOfProject(
        projectId: UUID,
        username: String,
    ): TeamMemberDto? {
        log.debug("Trying to get team member with username {} in project with id {}", username, projectId)
        val teamMember = teamMemberRepository.findByProjectIdAndUsername(projectId, username)
        log.info(
            teamMember?.let { "Found team member $it" }
                ?: "Team member with username $username of project with project id $projectId could not be found",
        )
        return teamMember?.let { TeamMemberDto(it) }
    }

    fun getDevelopersOfProject(projectId: UUID): List<DeveloperDto> {
        log.debug("Trying to get all developers of project with id {}", projectId)
        val developers = developerRepository.findByProjectId(projectId)
        log.info("Found all ({}) developers of project with id {}", developers.size, projectId)
        return developers.map { DeveloperDto(it) }
    }

    fun getScrumMasterOfProject(projectId: UUID): ScrumMasterDto? {
        log.debug("Trying to get scrum master of project with id {}", projectId)
        val scrumMaster = scrumMasterRepository.findByProjectId(projectId)
        log.info(
            scrumMaster?.let { "Found scrum master $it" }
                ?: "Scrum master of project with project id $projectId could not be found",
        )
        return scrumMaster?.let { ScrumMasterDto(it) }
    }

    fun getProductOwnerOfProject(projectId: UUID): ProductOwnerDto? {
        log.debug("Trying to get product owner of project with id {}", projectId)
        val productOwner = productOwnerRepository.findByProjectId(projectId)
        log.info(
            productOwner?.let { "Found product owner $it" }
                ?: "Product owner of project with project id $projectId could not be found",
        )
        return productOwner?.let { ProductOwnerDto(it) }
    }

    @Transactional(readOnly = false)
    fun assignDeveloper(command: AssignDeveloperCommand): DeveloperDto {
        log.debug("Trying to assign developer with command: {}", command)

        val developer =
            Developer(
                teamMemberId = TeamMemberId(userId = command.userId, projectId = command.projectId),
                username = command.username,
                fullName = FullName(firstName = command.firstName, lastName = command.lastName),
            )

        log.info("Assigned developer {}", developer)
        return DeveloperDto(developerRepository.save(developer))
    }

    @Transactional(readOnly = false)
    fun assignScrumMaster(command: AssignScrumMasterCommand): ScrumMasterDto {
        log.debug("Trying to assign scrum master with command: {}", command)

        val scrumMaster =
            ScrumMaster(
                teamMemberId = TeamMemberId(userId = command.userId, projectId = command.projectId),
                username = command.username,
                fullName = FullName(firstName = command.firstName, lastName = command.lastName),
            )

        log.info("Assigned scrum master {}", scrumMaster)
        return ScrumMasterDto(scrumMasterRepository.save(scrumMaster))
    }

    @Transactional(readOnly = false)
    fun assignProductOwner(command: AssignProductOwnerCommand): ProductOwnerDto {
        log.debug("Trying to assign product owner with command: {}", command)

        val productOwner =
            ProductOwner(
                teamMemberId = TeamMemberId(userId = command.userId, projectId = command.projectId),
                username = command.username,
                fullName = FullName(firstName = command.firstName, lastName = command.lastName),
            )

        log.info("Assigned product owner {}", productOwner)
        return ProductOwnerDto(productOwnerRepository.save(productOwner))
    }
}
