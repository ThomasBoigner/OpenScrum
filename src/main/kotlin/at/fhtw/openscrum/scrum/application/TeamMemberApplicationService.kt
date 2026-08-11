package at.fhtw.openscrum.scrum.application

import at.fhtw.openscrum.scrum.application.command.AssignDeveloperCommand
import at.fhtw.openscrum.scrum.application.command.AssignProductOwnerCommand
import at.fhtw.openscrum.scrum.application.command.AssignScrumMasterCommand
import at.fhtw.openscrum.scrum.application.command.UnassignDeveloperCommand
import at.fhtw.openscrum.scrum.application.command.UnassignProductOwnerCommand
import at.fhtw.openscrum.scrum.application.command.UnassignScrumMasterCommand
import at.fhtw.openscrum.scrum.application.command.UpdateTeamMemberInformationCommand
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

    @Transactional(readOnly = false)
    fun updateTeamMemberInformation(command: UpdateTeamMemberInformationCommand) {
        log.debug("Trying to update team member information with command: {}", command)

        val teamMembers = teamMemberRepository.findAllByUserId(command.userId)
        val fullName = FullName(firstName = command.firstName, lastName = command.lastName)

        teamMembers.forEach { teamMember ->
            when {
                teamMember.isDeveloper() -> {
                    developerRepository.save(
                        Developer(teamMember.id, teamMember.teamMemberId, command.username, fullName),
                    )
                }

                teamMember.isScrumMaster() -> {
                    scrumMasterRepository.save(
                        ScrumMaster(teamMember.id, teamMember.teamMemberId, command.username, fullName),
                    )
                }

                teamMember.isProductOwner() -> {
                    productOwnerRepository.save(
                        ProductOwner(teamMember.id, teamMember.teamMemberId, command.username, fullName),
                    )
                }
            }
        }

        log.info("Updated information of {} team members of user with id {}", teamMembers.size, command.userId)
    }

    @Transactional(readOnly = false)
    fun unassignDeveloper(command: UnassignDeveloperCommand) {
        log.debug("Trying to unassign developer with command: {}", command)
        developerRepository.delete(TeamMemberId(userId = command.userId, projectId = command.projectId))
        log.info("Unassigned developer with user id {} from project with id {}", command.userId, command.projectId)
    }

    @Transactional(readOnly = false)
    fun unassignScrumMaster(command: UnassignScrumMasterCommand) {
        log.debug("Trying to unassign scrum master with command: {}", command)
        scrumMasterRepository.delete(TeamMemberId(userId = command.userId, projectId = command.projectId))
        log.info("Unassigned scrum master with user id {} from project with id {}", command.userId, command.projectId)
    }

    @Transactional(readOnly = false)
    fun unassignProductOwner(command: UnassignProductOwnerCommand) {
        log.debug("Trying to unassign product owner with command: {}", command)
        productOwnerRepository.delete(TeamMemberId(userId = command.userId, projectId = command.projectId))
        log.info("Unassigned product owner with user id {} from project with id {}", command.userId, command.projectId)
    }
}
