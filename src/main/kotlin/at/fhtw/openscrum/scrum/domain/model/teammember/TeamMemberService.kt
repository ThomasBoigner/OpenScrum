package at.fhtw.openscrum.scrum.domain.model.teammember

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

class TeamMemberService(
    private val teamMemberRepository: TeamMemberRepository,
    private val developerRepository: DeveloperRepository,
    private val scrumMasterRepository: ScrumMasterRepository,
    private val productOwnerRepository: ProductOwnerRepository,
    private val log: Logger = LoggerFactory.getLogger(TeamMemberService::class.java),
) {
    fun updateTeamMemberInformation(
        userId: UUID,
        username: String,
        firstName: String,
        lastName: String,
    ) {
        log.debug("Trying to update information of team members of user with id {}", userId)

        val teamMembers = teamMemberRepository.findAllByUserId(userId)
        val fullName = FullName(firstName = firstName, lastName = lastName)

        teamMembers.forEach { teamMember ->
            teamMember.updateInformation(username, fullName)
            when (teamMember) {
                is Developer -> developerRepository.save(teamMember)
                is ScrumMaster -> scrumMasterRepository.save(teamMember)
                is ProductOwner -> productOwnerRepository.save(teamMember)
            }
        }

        log.info("Updated information of {} team members of user with id {}", teamMembers.size, userId)
    }
}
