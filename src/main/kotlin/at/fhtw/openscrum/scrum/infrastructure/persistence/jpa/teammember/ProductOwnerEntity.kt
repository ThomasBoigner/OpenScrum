package at.fhtw.openscrum.scrum.infrastructure.persistence.jpa.teammember

import at.fhtw.openscrum.scrum.domain.model.teammember.FullName
import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMember
import at.fhtw.openscrum.scrum.domain.model.teammember.TeamMemberId
import jakarta.persistence.Entity
import java.util.UUID

@Entity
class ProductOwnerEntity(
    id: Long? = null,
    userId: UUID,
    projectId: UUID,
    username: String,
    firstName: String,
    lastName: String,
) : TeamMemberEntity(id, userId, projectId, username, firstName, lastName) {
    constructor(productOwner: ProductOwner) : this(
        id = productOwner.id,
        userId = productOwner.teamMemberId.userId,
        projectId = productOwner.teamMemberId.projectId,
        username = productOwner.username,
        firstName = productOwner.fullName.firstName,
        lastName = productOwner.fullName.lastName,
    )

    constructor() : this(
        id = null,
        userId = UUID.randomUUID(),
        projectId = UUID.randomUUID(),
        username = "",
        firstName = "",
        lastName = "",
    )

    fun toProductOwner(): ProductOwner =
        ProductOwner(
            id = id,
            teamMemberId = TeamMemberId(userId = userId, projectId = projectId),
            username = username,
            fullName = FullName(firstName = firstName, lastName = lastName),
        )

    override fun toTeamMember(): TeamMember =
        ProductOwner(
            id = id,
            teamMemberId = TeamMemberId(userId = userId, projectId = projectId),
            username = username,
            fullName = FullName(firstName = firstName, lastName = lastName),
        )
}
