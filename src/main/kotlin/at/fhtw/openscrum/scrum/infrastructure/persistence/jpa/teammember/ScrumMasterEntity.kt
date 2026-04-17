package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ScrumMaster
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity
class ScrumMasterEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var userId: UUID,
    var projectId: UUID,
    var username: String,
    var firstName: String,
    var lastName: String,
) {
    constructor(scrumMaster: ScrumMaster) : this(
        id = scrumMaster.id,
        userId = scrumMaster.teamMemberId.userId,
        projectId = scrumMaster.teamMemberId.projectId,
        username = scrumMaster.username,
        firstName = scrumMaster.fullName.firstName,
        lastName = scrumMaster.fullName.lastName,
    )

    constructor() : this(
        id = null,
        userId = UUID.randomUUID(),
        projectId = UUID.randomUUID(),
        username = "",
        firstName = "",
        lastName = "",
    )

    fun toScrumMaster(): ScrumMaster =
        ScrumMaster(
            id = id,
            teamMemberId = TeamMemberId(userId = userId, projectId = projectId),
            username = username,
            fullName = FullName(firstName = firstName, lastName = lastName),
        )
}
