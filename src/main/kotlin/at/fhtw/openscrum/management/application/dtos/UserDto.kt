package at.fhtw.openscrum.management.application.dtos

import at.fhtw.openscrum.management.domain.model.user.User
import java.util.UUID

data class UserDto(
    val userId: UUID,
    val username: String,
    var emailAddress: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val role: RoleDto,
    val deletable: Boolean = false,
) {
    constructor(user: User, deletable: Boolean = false) : this(
        user.userId.token,
        user.username,
        user.emailAddress.emailAddress,
        user.fullName.firstName,
        user.fullName.lastName,
        user.fullName.fullName,
        RoleDto.Companion.fromRole(user.role),
        deletable,
    )
}
