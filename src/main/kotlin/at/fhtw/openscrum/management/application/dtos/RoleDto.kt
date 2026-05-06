package at.fhtw.openscrum.management.application.dtos

import at.fhtw.openscrum.management.domain.model.user.Role

enum class RoleDto(
    val displayName: String,
    val isManager: Boolean,
) {
    USER("User", false),
    MANAGER("Manager", true),
    ;

    companion object {
        fun fromRole(role: Role): RoleDto =
            when (role) {
                Role.USER -> USER
                Role.MANAGER -> MANAGER
            }
    }
}
