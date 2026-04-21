package at.fhtw.openscrum.scrum.application.dtos

import at.fhtw.openscrum.scrum.domain.model.teammember.ProductOwner
import java.util.UUID

data class ProductOwnerDto(
    val userId: UUID,
    val projectId: UUID,
    val username: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
) {
    constructor(productOwner: ProductOwner) : this(
        userId = productOwner.teamMemberId.userId,
        projectId = productOwner.teamMemberId.projectId,
        username = productOwner.username,
        firstName = productOwner.fullName.firstName,
        lastName = productOwner.fullName.lastName,
        fullName = productOwner.fullName.fullName,
    )
}
