package at.fhtw.openscrum.management.application.dtos

import at.fhtw.openscrum.management.domain.model.user.Role
import at.fhtw.openscrum.management.domain.model.user.User
import java.util.UUID

class UserDto(
    val userId: UUID,
    var username: String,
    var emailAddress: String,
    val firstName: String,
    val lastName: String,
    var fullName: String,
    var role: Role,
) {
    constructor(user: User) : this(
        user.userId.token,
        user.username,
        user.emailAddress.emailAddress,
        user.fullName.firstName,
        user.fullName.lastName,
        user.fullName.fullName,
        user.role,
    )
}
